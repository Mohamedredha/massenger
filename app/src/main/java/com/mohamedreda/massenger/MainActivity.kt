package com.mohamedreda.massenger

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mohamedreda.massenger.fragments.ChatFragment
import com.mohamedreda.massenger.fragments.MoreFragment
import com.mohamedreda.massenger.fragments.PeopleFragment
import com.mohamedreda.massenger.glide.GlideApp
import com.mohamedreda.massenger.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val mChatFragment = ChatFragment()
    private val mPeopleFragment = PeopleFragment()
    private val mMoreFragment = MoreFragment()
    private val fireStoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storageInstance:FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fireStoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)

                if (user!!.profileImage.isNotEmpty()){
                    GlideApp.with(this).load(storageInstance.getReference(user.profileImage))
                        .into(circleImageView_profile_image)
                }else{circleImageView_profile_image.setImageResource(R.drawable.ic_account_circle)}
            }
        setSupportActionBar(toolbar_main)
        supportActionBar?.title=""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            window.statusBarColor=Color.WHITE
        }

        bottomNavigationView_main.setOnNavigationItemSelectedListener(this@MainActivity)
        setFragment(mChatFragment)


       /* btn_log_out.setOnClickListener {
            mAuth.signOut()
            val intentSignInActivity = Intent(this@MainActivity,SignInActivity::class.java)
            intentSignInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentSignInActivity)
        }*/
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_chat_item -> {
                setFragment(mChatFragment)
                return true
            }
            R.id.navigation_people_item -> {
                setFragment(mPeopleFragment)
                return true

            }
            R.id.navigation_more_item -> {
                setFragment(mMoreFragment)
                return true
            }


        }
        return true
    }

    private fun setFragment(fragment: Fragment) {
        val fr = supportFragmentManager.beginTransaction()
        fr.replace(R.id.coordinatorLayout_main_content,fragment)
        fr.commit()

    }
}
