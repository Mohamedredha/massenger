package com.mohamedreda.massenger.model

import java.util.*

data class TextMessage(
    val text: String,
    override val senderId: String,
    override val recipientId: String,
    override val data: Date,
    override val type: String=MessageType.TEXT
) : Message {
    constructor():this("","","", Date())

}
