package com.mohamedreda.massenger.recyclerview

import android.content.Context
import com.mohamedreda.massenger.R
import com.mohamedreda.massenger.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.sende_item_text_message.*


class SenderTextMessageItem(
    private val textMessage: TextMessage,
    val messageId : String,
    val context:Context
):Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.text_view_message.text= textMessage.text
        viewHolder.text_view_time.text=android.text.format.DateFormat.format("hh:mm a",textMessage.data).toString()

    }

    override fun getLayout(): Int {
        return R.layout.sende_item_text_message}

}