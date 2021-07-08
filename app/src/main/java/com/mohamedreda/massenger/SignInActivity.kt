package com.mohamedreda.massenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sing_up.*
import kotlinx.android.synthetic.main.activity_singin.*

@Suppress("LABEL_NAME_CLASH")
class SignInActivity : AppCompatActivity() , TextWatcher {

    private val mAuth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singin)

        edit_text_email_sing_in.addTextChangedListener(this@SignInActivity)
        edit_text_password_sign_in.addTextChangedListener(this@SignInActivity)


        btn_create_account.setOnClickListener {  val intent = Intent(this,SingUpActivity::class.java)
            startActivity(intent)
        }

            btn_sing_in.setOnClickListener {
                val email= edit_text_email_sing_in.text.toString()
                val password=edit_text_password_sign_in.text.toString()

                if (email.isEmpty()){
                    edit_text_email_sing_in.error="email required"
                    edit_text_email_sing_in.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edit_text_email_sing_in.error="please enter a valid email"
                    edit_text_email_sing_in.requestFocus()
                    return@setOnClickListener
                }
                if(password.length<6){
                    edit_text_password_sign_in.error="6 char required  "
                    edit_text_password_sign_in.requestFocus()
                    return@setOnClickListener
                }
                signIn(email,password)

            }




    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser?.uid!=null){
            val intentMainActivity=Intent(this@SignInActivity,MainActivity::class.java)
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentMainActivity)
        }
    }

    private fun signIn(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { Task->
            if (Task.isSuccessful){
                val intentMainActivity=Intent(this@SignInActivity,MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMainActivity)
            }else{
                Toast.makeText(this@SignInActivity,Task.exception?.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        btn_sing_in.isEnabled= edit_text_email_sing_in.text.toString().trim().isNotEmpty() &&
                edit_text_password_sign_in.text.toString().trim().isNotEmpty()
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}
