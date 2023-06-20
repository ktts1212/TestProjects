package com.example.networktest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.networktest.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sendRequestBtn.setOnClickListener {
            sendRequestWithOkHttp()
        }
    }

//    val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
//        isGranted->
//        if (isGranted){
//            sendRequestWithHttpURLConnection()
//        }else{
//            Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun sendRequestWithHttpURLConnection(){
        //开启线程发送网络请求
        thread {
            var connection:HttpURLConnection?=null
            try {
                val response=StringBuilder()
                val url=URL("https://www.baidu.com")
                connection=url.openConnection() as HttpURLConnection
                connection.connectTimeout=8000
                connection.readTimeout=8000
                val input=connection.inputStream
                //对输入流进行读取
                val reader=BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                    response.append(it)
                } }
                showResponse(response.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                connection?.disconnect()
            }
        }
    }

    private fun showResponse(response:String){
        runOnUiThread{
            binding.responseText.text=response
        }
    }

    private fun sendRequestWithOkHttp(){
        thread {
            try {
                val client=OkHttpClient()
                val request=Request.Builder()
                        //指定访问的服务器地址是计算机本机
                    .url("http://10.0.2.2/get_data.json")
                    .build()
                //获取返回的数据
                val response=client.newCall(request = request).execute()
                val responseData=response.body?.string()
                if (responseData!=null){
                    Log.d("MainActivity",responseData)
                    parseJSONWithJSONObject(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun parseJSONWithJSONObject(jsonData:String){
        try {
            Log.d("MainActivity",jsonData)
            val jsonArray=JSONArray(jsonData)
            for (i in 0 until jsonArray.length()){
                val jsonObject=jsonArray.getJSONObject(i)
                val id=jsonObject.getString("id")
                val name=jsonObject.getString("name")
                val version=jsonObject.get("version")
                Log.d("MainActivity","id is $id")
                Log.d("MainActivity","name is $name")
                Log.d("MainActivity","version is $version")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}