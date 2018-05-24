package com.example.tszchiung.app

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.tszchiung.app.adapter.Friend
import com.example.tszchiung.app.adapter.FriendAdapter
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class MessageFragment : Fragment() {

    private lateinit var adapter: FriendAdapter

    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        mView = view
//        adapter = FriendAdapter(context!!, listOf())
//        mView.findViewById<RecyclerView>(R.id.friend_view).invalidate()

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            if (it.currentUser != null && it.currentUser!!.uid.isNotEmpty()) {
                val userRef = FirebaseDatabase.getInstance().reference.child("users")
                userRef.child(it.currentUser!!.uid).child("friends")
                        .addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {}
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                                    override fun onCancelled(error: DatabaseError?) {}
                                    override fun onDataChange(userSnapshot: DataSnapshot?) {
                                        if (userSnapshot != null) {
                                            val taskList = ArrayList<Task<Uri>>()
                                            val storage = FirebaseStorage.getInstance().reference.child("images")
                                            val friendList = snapshot!!.children.map {
                                                val targetUserObj = userSnapshot.child(it.key)
                                                taskList.add(storage.child("${targetUserObj.child("username").value!!}.${targetUserObj.child("ext").value!!}").downloadUrl)
                                                Friend(userSnapshot.child(it.key).child("prefer").value!! as String, it.key)
                                            }
                                            Tasks.whenAllComplete(taskList).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    it.result.forEachIndexed { index, it ->
                                                        if (it.isSuccessful) {
                                                            friendList[index].imageUri = it.result as Uri
                                                        }
                                                    }
                                                    mView.findViewById<RecyclerView>(R.id.friend_view).layoutManager =  LinearLayoutManager(context)
                                                    adapter = FriendAdapter(context!!, friendList.filter {
                                                        it.imageUri != null
                                                    })
                                                    mView.findViewById<RecyclerView>(R.id.friend_view).adapter = adapter
                                                    mView.findViewById<RecyclerView>(R.id.friend_view).invalidate()
                                                }
                                            }
                                        }
                                    }
                                })
                            }
                        })
            } else {
                Toast.makeText(this@MessageFragment.context, "Please log in.", Toast.LENGTH_SHORT).show()
                activity!!.finish()
            }
        }
        (activity as HomeActivity).findViewById<TextView>(R.id.title).text = "Messages"

//        view.findViewById<RecyclerView>(R.id.friend_view).setOnClickListener {
//            it.
//        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                MessageFragment().apply {}
    }
}
