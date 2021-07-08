package com.mohamedreda.massenger.model

import java.util.*

interface Message {

    val senderId: String
    val recipientId: String
    val data: Date
    val type: String
}