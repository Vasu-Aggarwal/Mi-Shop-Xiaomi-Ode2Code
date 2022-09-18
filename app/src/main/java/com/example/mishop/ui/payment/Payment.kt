package com.example.mishop.ui.payment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mishop.MainActivity
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Payment : AppCompatActivity(), PaymentResultListener {

    val user = Firebase.auth.currentUser
    private var Paymentdetails = ArrayList<String>()
    private lateinit var mref: DatabaseReference
    private lateinit var cartProductList: ArrayList<cartItemModel>
    val db = FirebaseFirestore.getInstance()

    //for pdf
    private val PERMISSION_REQUEST_CODE = 200
    var pageHeight = 2010
    var pagewidth = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_payment)
        Checkout.preload(applicationContext)
        val bundle = intent.extras
        Paymentdetails = bundle?.getStringArrayList("details")!!

        //permissions for writing (one time)
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
        startPayment()
    }

    private fun startPayment() {
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_BL0EVhKnx7IvGU")

        try {
            val options = JSONObject()
            options.put("name","Xiaomi Ode2Code")
            options.put("description","You are buying Xiaomi Products")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc")
            options.put("currency","INR")
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            if(Paymentdetails.size == 7){
                options.put("amount", Paymentdetails[6].toInt()*100)
            }
            else{
                options.put("amount", Paymentdetails[5].toInt()*100)//pass amount in currency subunits
            }


            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email", Paymentdetails[2])
            prefill.put("name", Paymentdetails[0])

            prefill.put("contact", Paymentdetails[1])

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Success Payment, ID: $p0", Toast.LENGTH_SHORT).show()
        txtStatus.text = "Payment Status: Successful"
        cartProductList = arrayListOf()
        getCartData(p0!!)
        uploadOrderDetails(p0)

        generatePDF(p0)

        btn.setOnClickListener {
            //if payment is successful then empty the cart for next customer
            emptyCart()

            val file = File(getExternalFilesDir(null)?.absolutePath, "$p0.pdf")
            sendMessage(file)

            //go to home
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //end all the activities
            startActivity(intent)
        }
    }

    private fun emptyCart() {
        mref = Firebase.database.getReference("Orders").child(
            SharedPref(this).getString(Constants.POS_ID)!!)

        mref.removeValue()
    }

    private fun uploadOrderDetails(id: String) {

        var orderDetails: HashMap<String, String?>
        if(Paymentdetails.size == 7){

            orderDetails = hashMapOf(
                "total_amount" to Paymentdetails[6],
                "user_address" to Paymentdetails[5],
                "operator_id" to user?.uid,
                "user_email" to Paymentdetails[2],
                "user_phone" to Paymentdetails[1],
                "order_id" to id,
                "mode_of_com" to Paymentdetails[3],
                "user_name" to Paymentdetails[0],
                "mode_of_del" to Paymentdetails[4]
            )
        }

        else{
            orderDetails = hashMapOf(
                "total_amount" to Paymentdetails[5],
                "operator_id" to user?.uid,
                "user_email" to Paymentdetails[2],
                "user_phone" to Paymentdetails[1],
                "order_id" to id,
                "mode_of_com" to Paymentdetails[3],
                "user_name" to Paymentdetails[0],
                "mode_of_del" to Paymentdetails[4]
            )
        }

        db.collection("AllOrders")
            .document(SharedPref(this).getString(Constants.STORE_NAME)!!)
            .collection("Orders")
            .document(id)
            .set(orderDetails, SetOptions.merge())
    }

    private fun getCartData(id: String) {
        mref = Firebase.database.getReference("Orders").child(
            SharedPref(this).getString(Constants.POS_ID)!!)

        mref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (carti in snapshot.children){
                        val cart = carti.getValue(cartItemModel::class.java)
                        cartProductList.add(cart!!)
                    }

                    cartProductList.forEach {
                        val product_details = hashMapOf(
                            "category" to it.category.toString(),
                            "id" to it.id,
                            "name" to it.name.toString(),
                            "price" to it.price,
                            "quantity" to it.quantity.toString()
                        )

                        db.collection("AllOrders")
                            .document(SharedPref(this@Payment).getString(Constants.STORE_NAME)!!)
                            .collection("Orders")
                            .document(id)
                            .collection("Products")
                            .document(it.id)
                            .set(product_details, SetOptions.merge())
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Failed because of $p1", Toast.LENGTH_SHORT).show()
//        Log.d("rea", "onPaymentError: $p1")
        txtStatus.text = "Payment Status: Failure"
    }

    private fun generatePDF(id: String) {


        val pdfDocument = PdfDocument()

        // three variables for paint "paint" is used
        // for text and we will use "title"
        // for titles in our PDF file.
        // draw for drawing
        val paint = Paint()
        val title = Paint()
        val draw = Paint()

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        val mypageInfo = PageInfo.Builder(pagewidth, pageHeight, 1).create()

        // below line is used for setting
        // start page for our PDF file.
        val myPage = pdfDocument.startPage(mypageInfo)

        // creating a variable for canvas
        // from our page of PDF.
        val canvas: Canvas = myPage.canvas

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.textSize = 60f
        title.textAlign = Paint.Align.CENTER
        // below line is sued for setting color
        // of our text inside our PDF file.
        title.color = ContextCompat.getColor(this, R.color.black)

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("INVOICE", pagewidth/2f, 80f, title)


        //Left side customer details
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 30f
        paint.color = ContextCompat.getColor(this, R.color.black)
        canvas.drawText("Customer Name : ${Paymentdetails[0]}", 20f, 200f, paint)
        canvas.drawText("Contact No : ${Paymentdetails[1]}", 20f, 250f, paint)
        canvas.drawText("Email ID : ${Paymentdetails[2]}", 20f, 300f, paint)
        canvas.drawText("Order ID : $id", 20f, 350f, paint)

        //Right side invoice details
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Invoice No : $id", 1180f, 200f, paint)
        canvas.drawText("Operator ID : ${user?.uid}", 1180f, 250f, paint)

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("---------------------------------------------------------------------------------------------------", pagewidth/2f, 450f, paint)

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("This is to confirm that " , 30f, 520f, paint)
        paint.color = ContextCompat.getColor(this, R.color.orange)
        canvas.drawText("Mr/Mrs ${Paymentdetails[0]} ",330f, 520f, paint)
        paint.color = ContextCompat.getColor(this, R.color.black)
        canvas.drawText("successfully made the purchase with", 640f, 520f, paint)
        canvas.drawText("Xiaomi Pvt. Ltd of Rs. ${Paymentdetails[5]} with Order Id - $id.", 30f, 570f, paint)
        pdfDocument.finishPage(myPage)

        // below line is used to set the name of
        // our PDF file and its path.
        Log.d("path", "generatePDF: "+getExternalFilesDir(null)?.absolutePath)
        val file = File(getExternalFilesDir(null)?.absolutePath, "$id.pdf")
        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(FileOutputStream(file))

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(
                this,
                "PDF file generated successfully.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            // below line is used
            // to handle error
            e.printStackTrace()
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close()
    }

    private fun sendMessage(file: File) {

//        val uri: Uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, "Congratulations on your Product")
        intent.type = "text/plain"
//        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setPackage("com.whatsapp")
        startActivity(intent)
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 =
            ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}