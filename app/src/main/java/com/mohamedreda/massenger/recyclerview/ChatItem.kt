@file:Suppress("DEPRECATION")

package com.mohamedreda.massenger.recyclerview

import android.content.Context
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mohamedreda.massenger.R
import com.mohamedreda.massenger.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.recycler_view_item.*

class ChatItem(
    val uid: String,
    val user: User,
    val context: Context
) : Item() {

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestoreInstant : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef : DocumentReference
    get() = firestoreInstant.document("users/$uid")

     override fun bind(viewHolder: GroupieViewHolder, position: Int) {

//
//        viewHolder.item_name_textView.text = user.name
//        viewHolder.item_time_textView.text = "Time"
//        viewHolder.item_last_message_textView.text = "last message..."
//
//        if (user.profileImage.isNotEmpty()) {
//            Glide
//                .with(context)
//                .load(storageInstance.getReference(user.profileImage))
//                .into(viewHolder.item_circle_imageView)
//        } else {
//            viewHolder.item_circle_imageView.setImageResource(R.drawable.ic_account_circle)
//        }
         getCurrentUser{

         }
     }

    private fun getCurrentUser(onComplete: (User)->Unit) {
    }


    override fun getLayout(): Int {
        return R.layout.recycler_view_item
    }

}