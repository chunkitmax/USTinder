package com.example.tszchiung.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.tszchiung.app.adapter.CardAdapter
import com.example.tszchiung.app.adapter.Partner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var justLogin: Boolean? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var cardView: CardStackView

    private lateinit var adapter: CardAdapter
    private lateinit var progressBar: ProgressBar

    private var mStorage: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            justLogin = it.getBoolean(JUST_LOGIN)
        }
        mStorage = FirebaseStorage.getInstance().reference
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

        FirebaseAuth.getInstance()!!.signInWithEmailAndPassword("cklauah@connect.ust.hk", "123456")
                .addOnCompleteListener { task ->
                    run {
                        if (task.isSuccessful) {
                            getPartners()
                        } else {
                            // TODO: what if authentication failed
                        }
                    }
                }

        return view
    }

    fun getPartners(): List<Partner> {
        var partners = ArrayList<Partner>()
        var pictureList = arrayListOf("chris.png", "girl.png")
        var nameList = arrayListOf("Mary Lau", "Selena Yip")
        var infoList = arrayListOf("IELM Year 3", "GBUS Year 3")
        var taskList = ArrayList<Task<Uri>>()
        pictureList.forEachIndexed { _, s ->
            run {
                taskList.add(mStorage!!.child(s).downloadUrl)
            }
        }
        val allSuccessTask = Tasks.whenAllSuccess<Uri>(taskList)
        allSuccessTask.addOnSuccessListener {
            it.forEachIndexed { i, uri ->
                adapter!!.add(Partner(nameList[i], infoList[i], uri))
            }
            progressBar.visibility = View.GONE
        }
//        partners.add(Partner("Christy Lam", "FINA Year3"))
//        partners.add(Partner("Cindy Wong", "ECON Year3"))
        return partners
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /**
         * No interaction between HomeActivity and HomeFragment so far
         */
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(justLogin: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(JUST_LOGIN, justLogin)
                }
            }
    }
}
