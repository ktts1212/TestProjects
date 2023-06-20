package com.example.camerademo

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.camerademo.databinding.ActivityMainBinding
import java.io.File
import java.lang.Exception


class MainActivity : ComponentActivity() {
    companion object{
        val TAKE_PHOTO=1
        val CHOOSE_PHOTO=2
    }

    var imageUri:Uri?=null
    private var permissionsList=ArrayList<String>()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.avatarPhoto.setOnClickListener {
            checkTakePhotoPermissions()
        }
        binding.choosePhoto.setOnClickListener {
            checkChoosePhotoPermissions()
        }
        intent=Intent(this,NetworkStateService::class.java)
        startService(intent)
    }

    fun checkTakePhotoPermissions(){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.CAMERA)
            }
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (!permissionsList.isEmpty()){
                requestPermissionLauncher.launch(
                    arrayOf(
                        permissionsList[0],
                        permissionsList[1],
                        permissionsList[2]
                    )
                )
            }else{
                permissionsList.clear()
                call()
            }
    }

    fun checkChoosePhotoPermissions(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            permissionsList.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!permissionsList.isEmpty()){
            requestPermissionLauncher.launch(
                arrayOf(
                    permissionsList[0],
                    permissionsList[1],
                    permissionsList[2]
                )
            )
        }else{
            permissionsList.clear()
            openAlbum()
        }
    }

    fun openAlbum(){
        val intent=Intent("android.intent.action.GET_CONTENT")
        intent.type="image/*"
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions:Map<String,Boolean>->
        permissions.entries.forEach {
            if (it.value==false){
                Toast.makeText(this,"你没有此权限${it.key}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun call(){
        //创建Flie对象，存储拍照后的图片
        val outputImage=File(externalCacheDir,"output_image.jpg")
        try {
            if (outputImage.exists()){
                outputImage.delete()
            }
            outputImage.createNewFile()
        }catch (e:Exception){
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT<24){
            imageUri=Uri.fromFile(outputImage)
        }else{
            imageUri=FileProvider.getUriForFile(this,"com.example.camerademo.fileProvider",outputImage)
        }

        //启动相机功能
        val intent=Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        startActivityForResult(intent, TAKE_PHOTO)
        //launcher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
           TAKE_PHOTO->if (resultCode== RESULT_OK){
               try {
                   val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))
                   binding.avatarPhoto.setImageBitmap(bitmap)
               }catch (e:Exception){
                   e.printStackTrace()
               }
           }
            CHOOSE_PHOTO->if (resultCode== RESULT_OK){
                if (Build.VERSION.SDK_INT>=19){
                    if (data!=null){
                        handleImageOnKitKat(data)
                    }
                }
            }
       }

    }

//    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        if (it.data!=null&&it.resultCode== RESULT_OK){
//            try {
//                //显示图片
//                val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))
//                binding.avatarPhoto.setImageBitmap(bitmap)
//            }catch (e:Exception){
//                e.printStackTrace()
//            }
//        }else{
//            Toast.makeText(this,"call error",Toast.LENGTH_SHORT).show()
//        }
//    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding.avatarPhoto.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show()
        }
    }



    //4.4后 判断封装情况
    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        Log.d("TAG", "handleImageOnKitKat: uri is $uri")
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":".toRegex()).toTypedArray()[1] // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        displayImage(imagePath) // 根据图片路径显示图片
    }

    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri, null)
        displayImage(imagePath)
    }


}