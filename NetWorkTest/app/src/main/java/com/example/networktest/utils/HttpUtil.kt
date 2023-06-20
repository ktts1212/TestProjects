package com.example.networktest.utils

import androidx.compose.material3.contentColorFor
import com.example.networktest.HttpCallbackListener
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class HttpUtil {
    fun sendHttpRequest(address:String,listener:HttpCallbackListener){
        var connection:HttpURLConnection?=null
        try {
            val response=StringBuilder()
            val url= URL(address)
            connection=url.openConnection() as HttpURLConnection
            connection.connectTimeout=8000
            connection.readTimeout=8000
            val input=connection.inputStream
            val reader=BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    response.append(it)
                }
            }
            listener.onFinish(response.toString())
        }catch (e:Exception){
            e.printStackTrace()
            listener.onError(e)
        }finally {
            connection?.disconnect()
        }
    }

    fun sendOKHttpRequest(address:String,callback:okhttp3.Callback){
        val client=OkHttpClient()
        val request=Request.Builder()
            .url(address)
            .build()
        client.newCall(request).enqueue(callback)
    }
}