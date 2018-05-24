package com.example.tszchiung.app

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {
    private var myUid: String? = null
    private var partnerUid: String? = null
    private var partnerUri: String? = null

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
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        mView = view

        FirebaseAuth.getInstance()!!.addAuthStateListener {
            if (it.currentUser != null && it.currentUser!!.uid.isNotEmpty()) {
                val msgRef = FirebaseDatabase.getInstance().reference.child("user-messages")
                msgRef.child(it.currentUser!!.uid)
                        .addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {}
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                msgRef.child(partnerUid)
                                        .addListenerForSingleValueEvent(object: ValueEventListener {
                                            override fun onCancelled(error: DatabaseError?) {}
                                            override fun onDataChange(snapshot2: DataSnapshot?) {
                                                val myMsg = snapshot2!!.children.filter {
                                                    it.child("author").value!! as String == myUid
                                                }.map {
                                                    it.child("body").value!! as String
                                                }
                                                val partnerMsg = snapshot!!.children.filter {
                                                    it.child("author").value!! as String == partnerUid
                                                }.map {
                                                    it.child("body").value!! as String
                                                }
                                            }
                                        })
                            }
                        })
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
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
