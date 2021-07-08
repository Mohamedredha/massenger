package com.mohamedreda.massenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mohamedreda.massenger.glide.GlideApp
import com.mohamedreda.massenger.model.ImageMessage
import com.mohamedreda.massenger.model.Message
import com.mohamedreda.massenger.model.MessageType
import com.mohamedreda.massenger.model.TextMessage
import com.mohamedreda.massenger.recyclerview.RecipientImageMessageItem
import com.mohamedreda.massenger.recyclerview.RecipientTextMessageItem
import com.mohamedreda.massenger.recyclerview.SenderImageMessageItem
import com.mohamedreda.massenger.recyclerview.SenderTextMessageItem
import com.xwray.groupie.GroupAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var mCurrentChannelId: String
    private val storageInstant: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    private val cruetImageRf: StorageReference
        get() = storageInstant.reference


    private val messageAdapter by lazy { GroupAdapter<com.xwray.groupie.GroupieViewHolder>() }

    private val chatChannelCollectionRef = fireStoreInstance.collection("chatChannels")

    //vars
    private var mRecipientId = ""

    private var mCurrentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    //on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = Color.WHITE
        }

        //هنا يتم استقبال اسم الشخص الذي سوف تتم الدردشة معه و صورته الشخصية و رقم التعريف الخاص به
        val userName = intent.getStringExtra("user_name")
        textView_user_name.text = userName
        val profileImage = intent.getStringExtra("profile_image")
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        mRecipientId = intent.getStringExtra("other_uid")

        fab_send_image.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage, "select image"), 2)


        }

        //هذه يتم استدعاء الدالة المسؤلة عن انشاء قناة دردشة
        createChatChannel { channelId ->
            mCurrentChannelId = channelId
            getMessages(channelId)
            imageView_send.setOnClickListener {
                val text = editText_message.text.toString()
                if (text.isNotEmpty()) {
                    val messageSend = TextMessage(
                        text,
                        mCurrentUserId,
                        mRecipientId,
                        Calendar.getInstance().time
                    )

                    sentMessage(channelId, messageSend)
                    editText_message.setText("")
                } else {
                    Toast.makeText(this@ChatActivity, "empty", Toast.LENGTH_SHORT).show()
                }

            }
        }
        recyclerView3.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }

        if (profileImage.isNotEmpty()) {
            GlideApp.with(this@ChatActivity)
                .load(storageInstant.getReference(profileImage))
                .into(circleImageView_profile_picture)
        } else {
            circleImageView_profile_picture.setImageResource(R.drawable.ic_profile_image_view)
        }
        image_view_back.setOnClickListener {
            finish()
        }
    }

    private fun sentMessage(channelId: String, message: Message) {
        chatChannelCollectionRef.document(channelId).collection("messages").add(message)
    }

    private fun createChatChannel(onComplete: (channelId: String) -> Unit) {


        fireStoreInstance.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
            .document(mRecipientId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document["channelId"] as String)
                    return@addOnSuccessListener
                }
                val newChatChannel = fireStoreInstance.collection("users").document()

                fireStoreInstance.collection("users")
                    .document(mRecipientId)
                    .collection("sharedChat")
                    .document(mCurrentUserId)
                    .set(mapOf("channelId" to newChatChannel.id))
                fireStoreInstance.collection("users")
                    .document(mCurrentUserId)
                    .collection("sharedChat")
                    .document(mRecipientId)
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel.id)
            }
    }

    private fun getMessages(channelId: String) {
        val query = chatChannelCollectionRef.document(channelId).collection("messages")
            .orderBy("date", Query.Direction.DESCENDING)
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException->
            messageAdapter.clear()

            querySnapshot!!.documents.forEach { document ->
                if (document["type"] == MessageType.TEXT) {
                    val textMessage = document.toObject(TextMessage::class.java)
                    if (textMessage?.senderId == mCurrentUserId) {
                        messageAdapter.add(
                            SenderTextMessageItem(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )
                    } else {
                        messageAdapter.add(
                            RecipientTextMessageItem(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this@ChatActivity
                            )
                        )
                    }


                } else {
                    if (document["type"] == MessageType.IMAGE){
                        val imageMessage = document.toObject(ImageMessage::class.java)
                        if (imageMessage?.senderId == mCurrentUserId) {
                            messageAdapter.add(
                                SenderImageMessageItem(
                                    document.toObject(ImageMessage::class.java)!!,
                                    document.id,
                                    this@ChatActivity
                                )
                            )
                        } else {
                            messageAdapter.add(
                                RecipientImageMessageItem(
                                    document.toObject(ImageMessage::class.java)!!,
                                    document.id,
                                    this@ChatActivity
                                )
                            )
                        }

                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
            val selectedImageBytes = outputStream.toByteArray()
            uploadImage(selectedImageBytes) { path ->
                val imageMessage =
                    ImageMessage(path, mCurrentUserId, mRecipientId, Calendar.getInstance().time)
                // chatChannelCollectionRef.document(mCurrentChannelId).collection("messages").add(imageMessage)
                sentMessage(mCurrentChannelId, imageMessage)
            }

        }
    }

    private fun uploadImage(selectedImageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = cruetImageRf
            .child(
                "${
                    FirebaseAuth.getInstance()
                        .currentUser!!.uid
                }/images${UUID.nameUUIDFromBytes(selectedImageBytes)}"
            )
        ref.putBytes(selectedImageBytes)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(ref.path)
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
    }


}