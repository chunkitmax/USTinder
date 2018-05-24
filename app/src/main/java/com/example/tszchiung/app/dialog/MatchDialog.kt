package com.example.tszchiung.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.bumptech.glide.Glide
import com.example.tszchiung.app.HomeActivity
import com.example.tszchiung.app.R
import kotlinx.android.synthetic.main.match_dialog.*


class MatchDialog(var activity: Activity, var listener: HomeActivity.MatchDialogListener)
    : Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen), android.view.View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.match_dialog)

        val uri = listener.getUri()
        Glide.with(activity).load(uri[0]).into(me)
        Glide.with(activity).load(uri[1]).into(partner)

        send_message.setOnClickListener(this)
        continue_swiping.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.send_message -> listener.fragmentTransaction()
            R.id.continue_swiping -> dismiss()
            else -> dismiss()
        }
    }
}