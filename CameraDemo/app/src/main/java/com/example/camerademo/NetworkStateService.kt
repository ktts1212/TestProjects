package com.example.camerademo

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import com.example.camerademo.utils.NetUtil

class NetworkStateService : Service() {
    var connectionmgr:ConnectivityManager?=null
    var receiver:BroadcastReceiver?=null
    var info:NetworkInfo?=null
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter=IntentFilter()
        receiver=NetworkConnectChangedReceiver()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

   inner class NetworkConnectChangedReceiver:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
//                connectionmgr= getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//                var request=NetworkRequest.Builder().build()
//                connectionmgr?.registerNetworkCallback(request,NetUtil.getInstance())
            val action=intent?.action
            if (action==ConnectivityManager.CONNECTIVITY_ACTION){
                Log.d("AAAAAA","网络状态发生变化")
                connectionmgr=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                info=connectionmgr?.activeNetworkInfo
                if (info!=null&& info!!.isAvailable()){
                    val name=info?.typeName
                    Log.d("AAAAAA","当前网络名称"+name)
                }else{
                    Log.d("AAAAAA","没有网络")
                }
            }
        }
    }
}