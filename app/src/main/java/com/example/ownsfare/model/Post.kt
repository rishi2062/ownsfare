package com.example.ownsfare.model

class Post(
    val postId : String = "",
    var text : String? = null,
    var createdBy : User  = User(),
    var createdAt : Long = 0L,
    var likedBy : ArrayList<String> = ArrayList())
