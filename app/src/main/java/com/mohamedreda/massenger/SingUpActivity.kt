package com.mohamedreda.massenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mohamedreda.massenger.model.User
import kotlinx.android.synthetic.main.activity_sing_up.*
import kotlinx.android.synthetic.main.activity_singin.*

class SingUpActivity : AppCompatActivity(), TextWatcher {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef: DocumentReference
        get() = fireStoreInstance.document("users/${mAuth.currentUser?.uid.toString()}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        //send info to firebase when click on button sing up
        btn_sing_up.setOnClickListener {

            val name = edit_text_name_sing_up.text.toString().trim()
            val email = edit_text_email_sing_up.text.toString().trim()
            val password = edit_text_password_sign_up.text.toString().trim()

            if (name.isEmpty()) {
                edit_text_name_sing_up.error = "name required"
                edit_text_name_sing_up.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                edit_text_email_sing_up.error = "email required"
                edit_text_email_sing_up.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email_sing_up.error = "please enter a valid email"
                edit_text_email_sing_up.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                edit_text_password_sign_up.error = "6 char required  "
                edit_text_password_sign_up.requestFocus()
                return@setOnClickListener
            }

            createNewAccount(name, email, password)
        }


        edit_text_name_sing_up.addTextChangedListener(this)
        edit_text_email_sing_up.addTextChangedListener(this)
        edit_text_password_sign_up.addTextChangedListener(this)
    }

    // this fun for create ne account

    private fun createNewAccount(name: String, email: String, password: String) {

        progres_sign_up.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            val newUser = User(name , "")
            currentUserDocRef.set(newUser)
            if (task.isSuccessful) {
                progres_sign_up.visibility = View.GONE

                val intentMainActivity = Intent(this@SingUpActivity, MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMainActivity)

            } else {
                progres_sign_up.visibility = View.GONE

                Toast.makeText(this@SingUpActivity, task.exception?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        //sing up button will be enable when write info
        btn_sing_up.isEnabled = edit_text_name_sing_up.text.trim().isNotEmpty()
                && edit_text_email_sing_up.text.trim().isNotEmpty()
                && edit_text_password_sign_up.text.trim().isNotEmpty()
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}
