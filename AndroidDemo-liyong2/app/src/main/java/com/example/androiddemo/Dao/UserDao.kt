package com.example.androiddemo.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.androiddemo.entities.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User):Long

    @Update
    fun updateUser(newUser:User)

    @Query("select * from User")
    fun loadAllUser():List<User>

    @Query("select * from User where username=:username and password=:password")
    fun queryUser(username:String,password:String)
}