package com.risako070310.webapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.api.load
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson: Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://api.github.com").addConverterFactory(GsonConverterFactory.create(gson)).build()

        val userService: UserService = retrofit.create(UserService::class.java)

        requestButton.setOnClickListener {
            runBlocking(Dispatchers.IO){
                kotlin.runCatching {
                    userService.getUser("risako070310")
                }
            }.onSuccess {
                avatarImageView.load(it.avatarUrl)
                nameTextView.text = it.name
                userIdTextView.text = it.userId
                followingTextView.text = "Following: " + it.following.toString()
                followersTextView.text = "Followers: " + it.followers.toString()
            }.onFailure {
                Toast.makeText(this, "失敗", Toast.LENGTH_LONG).show()
            }
        }
    }
}

data class User (
    val name:String,
    @SerializedName("login") val userId: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val following: Int,
    val followers: Int
)

interface UserService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String) : User
}