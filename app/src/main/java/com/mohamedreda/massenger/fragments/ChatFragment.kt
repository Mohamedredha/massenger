package com.mohamedreda.massenger.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mohamedreda.massenger.ChatActivity
import com.mohamedreda.massenger.ProfileActivity
import com.mohamedreda.massenger.R
import com.mohamedreda.massenger.model.User
import com.mohamedreda.massenger.recyclerview.ChatItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatFragment : Fragment() {

    //class start ....
    private lateinit var chatSection: Section

    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textViewTitle = activity?.findViewById(R.id.toolbar_text_view) as TextView
        textViewTitle.text = "Chats"
        val circleImageViewProfileImage =
            activity?.findViewById(R.id.circleImageView_profile_image) as ImageView
        circleImageViewProfileImage.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }

        //listening of chat ...
        addChatListener(::initRecyclerView)

        return inflater.inflate(R.layout.fragment_chat, container, false)

    }

    // listing of chats
    private fun addChatListener(onListen :(List<Item>) ->Unit) : ListenerRegistration {
      return  fireStoreInstance
          .collection("users")
          .document(FirebaseAuth.getInstance().currentUser!!.uid)
          .collection("sharedChat")
          .addSnapshotListener  { querySnapShot, firebaseFireStoreException ->
            if (firebaseFireStoreException != null)
            {
                return@addSnapshotListener
            }
              val items = mutableListOf<Item>()

              querySnapShot!!.documents.forEach {
                  items.add(ChatItem(it.id,it.toObject(User::class.java)!!, requireActivity()))
              }
              onListen(items)
        }
    }

    private fun initRecyclerView(item:List<Item>){
        chat_recyclerView.apply {
            layoutManager=LinearLayoutManager(activity)
            adapter=GroupAdapter<GroupieViewHolder>().apply {
              chatSection= Section(item)
                add(chatSection)
                setOnItemClickListener (onItemClick)
            }
        }
    }
    private val onItemClick = OnItemClickListener{ item, view->
        if (item is ChatItem){
            val intentChatActivity = Intent(activity, ChatActivity::class.java)
            intentChatActivity.putExtra("user_name",item.user.name)
            intentChatActivity.putExtra("profile_image",item.user.profileImage)
            intentChatActivity.putExtra("other_uid",item.uid)
            requireActivity().startActivity(intentChatActivity)
        }

    }

//    companion object {
//
//        fun newInstance(param1: String, param2: String) =
//            ChatFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}