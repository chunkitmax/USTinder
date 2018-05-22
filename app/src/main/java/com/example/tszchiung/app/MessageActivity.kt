package com.example.tszchiung.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_message.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.database.ChangeEventListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.app.AlertDialog

class MessageActivity : AppCompatActivity() {

    private val TAG = "MessageActivity"
    private val REQUIRED = "Required"

    private var user: FirebaseUser? = null

    private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null
    private var mMessageListener: ChildEventListener? = null

    private var mAdapter: FirebaseRecyclerAdapter<Message, MessageViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageReference = FirebaseDatabase.getInstance().getReference("messages")
        user = FirebaseAuth.getInstance().currentUser

        firebaseListenerInit()
        // to be put in home page activity **********************************************************************
        val intent = intent
        val message = intent.getStringExtra("message")
        if(!message.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                    .setTitle("Notification")
                    .setMessage(message)
                    .setPositiveButton("Ok", { dialog, which -> }).show()
        }
        // to be put in home page activity **********************************************************************
        btnSend.setOnClickListener {
            submitMessage()
            edtSentText.setText("")
        }

        btnBack.setOnClickListener {
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        rcvListMessage.setHasFixedSize(true)
        rcvListMessage.layoutManager = layoutManager

        val query = mMessageReference!!.limitToLast(8)

        val options = FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message::class.java)
                .setLifecycleOwner(this)
                .build()

        mAdapter = object : FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            /*override fun populateViewHolder(viewHolder: MessageViewHolder?, model: Message?, position: Int) {
                viewHolder!!.bindMessage(model)
            }

            override fun onChildChanged(type: ChangeEventListener.EventType, snapshot: DataSnapshot?, index: Int, oldIndex: Int) {
                super.onChildChanged(type, snapshot, index, oldIndex)
                rcvListMessage.scrollToPosition(index)
            }*/
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                return MessageViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message, parent, false))
            }

            protected override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
                holder.bindMessage(model)
            }

            override fun onDataChanged() {
                rcvListMessage.scrollToPosition(itemCount-1)
            }
        }
        rcvListMessage.adapter = mAdapter
    }

    private fun firebaseListenerInit() {

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                val message = dataSnapshot!!.getValue(Message::class.java)
                Log.e(TAG, "onChildAdded:" + message!!.body)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.e(TAG, "onChildChanged:" + dataSnapshot!!.key)
                val message = dataSnapshot.getValue(Message::class.java)
                Toast.makeText(this@MessageActivity, "onChildChanged: " + message!!.body, Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot!!.key)
                val message = dataSnapshot.getValue(Message::class.java)
                Toast.makeText(this@MessageActivity, "onChildRemoved: " + message!!.body, Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot!!.key)
                val message = dataSnapshot.getValue(Message::class.java)
                Toast.makeText(this@MessageActivity, "onChildMoved: " + message!!.body, Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.e(TAG, "postMessages:onCancelled", databaseError!!.toException())
                Toast.makeText(this@MessageActivity, "Failed to load Message.", Toast.LENGTH_SHORT).show()
            }
        }
        mMessageReference!!.addChildEventListener(childEventListener)
        mMessageListener = childEventListener
    }

    override fun onStop() {
        super.onStop()
        if (mMessageListener != null) {
            mMessageReference!!.removeEventListener(mMessageListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*if(mAdapter != null) {
            mAdapter!!.cleanup()
        }*/
    }

    private fun submitMessage() {
        val body = edtSentText.text.toString()

        if (TextUtils.isEmpty(body)) {
            edtSentText.error = REQUIRED
            return
        }
        mDatabase!!.child("users").child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                if (user == null) {
                    Log.e(TAG, "onDataChange: User data is null!")
                    Toast.makeText(this@MessageActivity, "onDataChange: User data is null!", Toast.LENGTH_SHORT).show()
                    return
                }
                writeNewMessage(body)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Failed to read user!")
            }
        })
    }

    private fun writeNewMessage(body: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        val message = Message(getUsernameFromEmail(user!!.email), body, time)

        val messageValues = message.toMap()
        val childUpdates = HashMap<String, Any>()

        val key = mDatabase!!.child("messages").push().key

        childUpdates.put("/messages/" + key, messageValues)
        childUpdates.put("/user-messages/" + user!!.uid + "/" + key, messageValues)

        mDatabase!!.updateChildren(childUpdates)
    }

    private fun getUsernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
}