package com.example.tszchiung.app

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.tszchiung.app.adapter.CardAdapter
import com.example.tszchiung.app.adapter.Partner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yuyakaido.android.cardstackview.CardStackView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val JUST_LOGIN = "justLogin"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private lateinit var cardView: CardStackView

    private lateinit var adapter: CardAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var mStorage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        view.findViewById<TextView>(R.id.greeting).text =
//                String.format("This is Home with justLogin=%b", justLogin)
        cardView = view.findViewById(R.id.card_view)
        adapter = CardAdapter(context!!)
        cardView.setAdapter(adapter)
        cardView.visibility = View.VISIBLE

        progressBar = view.findViewById(R.id.progress_bar)

        FirebaseAuth.getInstance()!!.addAuthStateListener(this)

        return view
    }

    override fun onAuthStateChanged(it: FirebaseAuth) {
        val user = it.currentUser
        if (user != null) {
            mStorage = FirebaseStorage.getInstance().reference
            getPartners(it.currentUser!!.email!!)
        } else {
            Toast.makeText(this@HomeFragment.context, "Please log in.", Toast.LENGTH_SHORT).show()
            activity!!.finish()
        }
        it.removeAuthStateListener(this)
    }

    fun getPartners(currentEmail: String) {
        FirebaseDatabase.getInstance()!!.reference.child("users")
                .orderByChild("gender")
                .equalTo("M") // TODO: should be according to user's preference
                .addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {}
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        if (snapshot != null) {
                            var partners = ArrayList<Partner>()
                            val taskList = ArrayList<Task<Uri>>()
                            val storgeRef = FirebaseStorage.getInstance()!!.reference.child("images")
                            for (child: DataSnapshot in snapshot!!.children) {
                                if (child.child("email").value as String != currentEmail) {
                                    partners.add(Partner(child.child("prefer").value!! as String,
                                            "${child.child("major").value!! as String} Year ${child.child("year").value as String}"))
                                    taskList.add(storgeRef.child("${child.child("username").value as String}.gif").downloadUrl)
                                }
                            }
                            Tasks.whenAllComplete(taskList)
                                    .addOnSuccessListener {
                                        val failedList = arrayListOf<Int>()
                                        it.forEachIndexed { i, task ->
                                            if (task.isSuccessful)
                                                partners[i].imageUri = task.result as Uri
                                            else
                                                failedList.add(i)
                                        }
                                        partners = partners.filterIndexed { index, partner ->
                                            failedList.indexOf(index) < 0 && partner.imageUri != null
                                        } as ArrayList<Partner>
                                        adapter.addAll(partners)
                                        progressBar.visibility = View.GONE
                                    }
                        } else {
                            TODO("error handling nedded")
                        }
                    }
                })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {}
    }
}
