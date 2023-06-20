package com.example.camerademo.utils


import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log


class NetUtil: ConnectivityManager.NetworkCallback(){

    companion object{
        private var instance:NetUtil?=null

        @Synchronized
        fun getInstance():NetUtil{
            if (instance!=null){
                instance= NetUtil()
            }
            return instance!!
        }
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.d("AAAAAA","您现在可以上网")
    }

    //当网络状态修改时调用
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)){
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                //使用当时wifi网络
                Log.d("AAAAAA","当前使用wifi上网")
            }else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                Log.d("AAAAAA","使用数据流量上网")
            }else{
                Log.d("AAAAAA","使用未知网络，蓝牙或vpn")
            }
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Log.d("AAAAAA","网络连接已断开")
    }

    override fun onUnavailable() {
        super.onUnavailable()
        Log.d("AAAAAA","无法连接到网络")
    }
}