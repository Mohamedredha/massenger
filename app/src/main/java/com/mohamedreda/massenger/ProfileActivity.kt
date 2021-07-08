@file:Suppress("DEPRECATION")

package com.mohamedreda.massenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mohamedreda.massenger.glide.GlideApp
import com.mohamedreda.massenger.model.User
import kotlinx.android.synthetic.main.activity_profileactivity.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileActivity : AppCompatActivity() {
    companion object{
      const val  RC_SELECT_IMAGE =2
    }
    private val mAuth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var userName : String

    private val fireStorageInstance:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef : DocumentReference
    get() = fireStorageInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserStorageRef:StorageReference
    get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profileactivity)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor= Color.WHITE
        }

        btn_sign_out.setOnClickListener {
            mAuth.signOut()

            val intentSignInActivity = Intent(this@ProfileActivity,SignInActivity::class.java)
            intentSignInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentSignInActivity)
            finish()


        }
        circleImageView_profile_image.setOnClickListener {
            val myIntentImage = Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage,"Select Image"),RC_SELECT_IMAGE)
        }

        setSupportActionBar(profile_toolBar)
        supportActionBar?.title="Me"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserInfo{user ->
           userName = user.name
            textView_user_name.text=user.name

            if (user.profileImage.isNotEmpty()){
            GlideApp.with(this)
                .load(storageInstance.getReference(user.profileImage))
                .placeholder(R.drawable.ic_account_circle)
                .into(circleImageView_profile_image)}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SELECT_IMAGE  && resultCode== Activity.RESULT_OK
            && data!==null && data.data!==null ){

                progress_profile.visibility= View.VISIBLE

            circleImageView_profile_image.setImageURI(data.data)
            val selectedImagePath = data.data
            val selectedImageBmp = getBitmap(this.contentResolver,selectedImagePath)
            val outPutStream= ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,20,outPutStream)
            val selectedImageBytes=outPutStream.toByteArray()

             uploadProfileImage(selectedImageBytes){path->

                 val userFieldMap = mutableMapOf<String,Any>()
                 userFieldMap["name"]=userName
                 userFieldMap["profileImage"]=path
                 currentUserDocRef.update(userFieldMap)
             }

        }
    }

    private fun uploadProfileImage(selectedImageBytes: ByteArray , onSuccess : (imagePath:String)->Unit) {
      val ref =  currentUserStorageRef.child("ProfilePictures/${UUID.nameUUIDFromBytes(selectedImageBytes)}")
       ref.putBytes(selectedImageBytes).addOnCompleteListener {
           if (it.isSuccessful){
               onSuccess(ref.path)
               progress_profile.visibility= View.GONE
           }
               else{
               Toast.makeText(this@ProfileActivity,
                   "Error:${it.exception?.message.toString()}",
                   Toast.LENGTH_SHORT)
                   .show()
           }
       }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           android.R.id.home->{
               finish()
               return true
           }
       }
        return false
    }
    private fun getUserInfo(onComplete : (User)->Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }
}