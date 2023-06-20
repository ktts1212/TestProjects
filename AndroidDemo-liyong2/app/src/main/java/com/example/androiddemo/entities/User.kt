package com.example.androiddemo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity
data class User(val username:String, val password:String, val age:Int, val gender:Char, val avatar: Blob, val nickname:String) {
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}