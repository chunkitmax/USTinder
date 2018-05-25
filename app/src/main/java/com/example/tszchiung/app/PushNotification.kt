//package com.example.tszchiung.app
//
//import android.util.Log
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.support.v4.app.NotificationCompat
//
//class PushNotification : FirebaseMessagingService() {
//    val TAG = "Service"
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//        Log.d(TAG, "From: " + remoteMessage!!.from)
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
//
//        sendNotification(remoteMessage)
//        val intent = Intent(this@PushNotification, MessageActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        val rMN = remoteMessage.notification
//        intent.putExtra("message", rMN!!.body!!)
//        startActivity(intent)
//    }
//
//    private fun sendNotification(remoteMessage: RemoteMessage) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val rMN = remoteMessage.notification
//        val notificationBuilder = NotificationCompat.Builder(this)
//                .setContentText(rMN!!.body)
//                .setAutoCancel(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }
//}