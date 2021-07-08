package com.mohamedreda.massenger.recyclerview

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.mohamedreda.massenger.R
import com.mohamedreda.massenger.glide.GlideApp
import com.mohamedreda.massenger.model.ImageMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.sender_item_image_message.imageView_message_image
import kotlinx.android.synthetic.main.sender_item_image_message.textView_message_time


class SenderImageMessageItem(
    private val imageMessage: ImageMessage,
    private val messageId : String,
    val context:Context
):Item(){
    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.textView_message_time.text=android.text.format.DateFormat
            .format("hh:mm a",imageMessage.data).toString()

        if (imageMessage.imagePath.isNotEmpty()){
            GlideApp.with(context)
                .load(storageInstance.getReference(imageMessage.imagePath))
                .placeholder(R.drawable.ic_image_black_24)
                .into(viewHolder.imageView_message_image)}
    }

    override fun getLayout()= R.layout.sender_item_image_message


}