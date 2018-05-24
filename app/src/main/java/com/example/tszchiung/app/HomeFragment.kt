package com.example.tszchiung.app

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.example.tszchiung.app.adapter.CardAdapter
import com.example.tszchiung.app.adapter.Partner
import com.example.tszchiung.app.model.Info
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment(), FirebaseAuth.AuthStateListener, CardStackView.CardEventListener, View.OnClickListener {

    private lateinit var adapter: CardAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabase: DatabaseReference

    private lateinit var curUserObj: Info
    private lateinit var mView: View

    private var email2uid = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mView = view
//        view.findViewById<TextView>(R.id.greeting).text =
//                String.format("This is Home with justLogin=%b", justLogin)
        adapter = CardAdapter(context!!)
        val cardView = view.findViewById<CardStackView>(R.id.card_view)
        cardView.setAdapter(adapter)
        cardView.visibility = View.VISIBLE

        view.findViewById<ImageButton>(R.id.skip).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.like).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.superlike).setOnClickListener(this)

        FirebaseAuth.getInstance()!!.addAuthStateListener(this)

        cardView.setCardEventListener(this)

        return view
    }

    override fun onAuthStateChanged(it: FirebaseAuth) {
        val user = it.currentUser
        if (user != null) {
            mAuth = it
            mStorage = FirebaseStorage.getInstance().reference.child("images")
            mDatabase = FirebaseDatabase.getInstance().reference.child("users")
            getPartners(it.currentUser!!.email!!)
        } else {
            Toast.makeText(this@HomeFragment.context, "Please log in.", Toast.LENGTH_SHORT).show()
            activity!!.finish()
        }
        it.removeAuthStateListener(this)
    }

    fun getPartners(currentEmail: String) {
        FirebaseDatabase.getInstance()!!.reference.child("users")
                .addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {}
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        var partners = ArrayList<Partner>()
                        val taskList = ArrayList<Task<Uri>>()
                        val userObj = snapshot!!.children.filter {
                            it.key == mAuth.currentUser!!.uid
                        }[0]
                        curUserObj = userObj.getValue(Info::class.java)!!
                        val targetMajor = userObj.child("major").value!! as String
                        val ratedUserList = ArrayList<String>()
                        listOf("skip", "like", "super").forEach {
                            if (userObj.hasChild(it)) {
                                ratedUserList += userObj.child(it).children.map { it.key }
                            }
                        }
                        val ratedUserSet = ratedUserList.toSet()
                        email2uid.clear()
                        snapshot.children.filter {
                            ratedUserSet.indexOf(it.key) < 0 &&
                                    it.hasChild("major") &&
                                    it.child("major").value!! as String == targetMajor &&
                                    it.key != mAuth.currentUser!!.uid
                        }.forEach {
                            email2uid[it.child("email").value as String] = it.key
                            partners.add(Partner(it.child("prefer").value!! as String,
                                    "${it.child("major").value!! as String} Year ${it.child("year").value as String}",
                                    it.child("email").value!! as String))
                            taskList.add(mStorage.child("${it.child("username").value as String}.${it.child("ext").value as String}").downloadUrl)
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
                                    adapter.clear()
                                    adapter.addAll(partners)
                                    mView.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
                                }
                    }
                })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.skip -> card_view.swipe(Point(0, 0), SwipeDirection.Left)
            R.id.like -> card_view.swipe(Point(0, 0), SwipeDirection.Right)
            R.id.superlike -> card_view.swipe(Point(0, 0), SwipeDirection.Top)
        }
    }

    override fun onCardDragging(percentX: Float, percentY: Float) {}

    override fun onCardSwiped(direction: SwipeDirection?) {
        if (adapter.count > 0) {
            val partner = adapter.getItem(0)
            val map = HashMap<String, Any>()
            val category = when (direction) {
                SwipeDirection.Left -> "skip"
                SwipeDirection.Right -> "like"
                SwipeDirection.Top -> "super"
                SwipeDirection.Bottom -> ""
                else -> ""
            }
            if (category.isNotEmpty()) {
                map["$category/${email2uid[partner.email]}"] = true
                mDatabase.child(mAuth.uid!!).updateChildren(map)
            }
            if (category == "like" || category == "super") {
                mDatabase.child(email2uid[partner.email])
                        .addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {}
                            override fun onDataChange(snapshot: DataSnapshot?) {
                                if (snapshot!!.child("like/${mAuth.uid!!}").exists() ||
                                                snapshot.child("super/${mAuth.uid!!}").exists()) {
                                    mStorage.child("${curUserObj.username}.${curUserObj.ext}")
                                            .downloadUrl
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    (activity as HomeActivity).showMatchDialog(listOf(it.result, partner.imageUri!!))
                                                } else {
                                                    Toast.makeText(activity, "Cannot get profile picture of current user",
                                                            Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                }
                            }
                        })
            }
        }
    }

    override fun onCardReversed() {}

    override fun onCardMovedToOrigin() {}

    override fun onCardClicked(index: Int) {}

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {}
    }
}
