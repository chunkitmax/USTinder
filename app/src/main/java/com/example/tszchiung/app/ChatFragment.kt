package com.example.tszchiung.app

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.tszchiung.app.adapter.ChatAdapter
import com.example.tszchiung.app.adapter.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatFragment : Fragment() {
    private var myUid: String? = null
    private var partnerUid: String? = null
    private var partnerUri: String? = null

    private lateinit var adapter: ChatAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private var myMsgListener: ValueEventListener? = null
    private var partnerMsgListener: ValueEventListener? = null

    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            partnerUid = it.getString("partnerUid")
            partnerUri = it.getString("partnerUri")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        mView = view

        mView.findViewById<RecyclerView>(R.id.chat_view).layoutManager =  LinearLayoutManager(context)
        adapter = ChatAdapter(context!!, listOf())
        mView.findViewById<RecyclerView>(R.id.chat_view).adapter = adapter

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            if (it.currentUser != null && it.currentUser!!.uid.isNotEmpty()) {
                mAuth = it
                mDatabase = FirebaseDatabase.getInstance().reference.child("user-messages")
                partnerMsgListener = mDatabase.child(it.currentUser!!.uid).child(partnerUid).orderByKey()
                        .addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {}
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                val partnerMsg = snapshot!!.children.map {
                                    listOf(it.child("time").value!! as String, it.child("body").value!! as String, false)
                                }
                                val messages = ArrayList<Message>()
                                partnerMsg.forEach {
                                    messages.add(Message(it[0] as String, it[1] as String, it[2] as Boolean))
                                }
                                adapter.addAll(messages, false)
//                                mView.findViewById<RecyclerView>(R.id.chat_view).layoutManager =  LinearLayoutManager(context)
//                                mView.findViewById<RecyclerView>(R.id.chat_view).invalidate()
                            }
                        })
                myMsgListener = mDatabase.child(partnerUid).child(it.currentUser!!.uid).orderByKey()
                        .addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {}
                            override fun onDataChange(snapshot2: DataSnapshot?) {
                                val myMsg = snapshot2!!.children.map {
                                    listOf(it.child("time").value!! as String, it.child("body").value!! as String, true)
                                }
                                val messages = ArrayList<Message>()
                                myMsg.forEach {
                                    messages.add(Message(it[0] as String, it[1] as String, it[2] as Boolean))
                                }
                                adapter.addAll(messages, true)
//                                mView.findViewById<RecyclerView>(R.id.chat_view).layoutManager =  LinearLayoutManager(context)
//                                mView.findViewById<RecyclerView>(R.id.chat_view).invalidate()
                            }
                        })
            }
        }

        view.findViewById<ImageButton>(R.id.send_btn).setOnClickListener {
            if (input_box.text.isNotEmpty()) {
                val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
                val msg = com.example.tszchiung.app.model.Message(input_box.text.toString(), time)
                val key = mDatabase.child(partnerUid).child(mAuth.currentUser!!.uid).push().key
                val updates = HashMap<String, Any>()
                updates[key] = msg
                mDatabase.child(partnerUid).child(mAuth.currentUser!!.uid).updateChildren(updates)
                input_box.setText("")
            }
        }

        return view
    }

    override fun onDestroyView() {
        mDatabase.child(partnerUid).child(mAuth.currentUser!!.uid).removeEventListener(myMsgListener)
        mDatabase.child(mAuth.currentUser!!.uid).child(partnerUid).removeEventListener(partnerMsgListener)
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(partnerUid: String, partnerUri: Uri) =
                ChatFragment().apply {
                    arguments = Bundle().apply {
                        putString("partnerUri", partnerUri.toString())
                        putString("partnerUid", partnerUid)
                    }
                }
    }
}
