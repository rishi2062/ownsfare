package com.example.ownsfare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ownsfare.Dao.PostDao
import com.example.ownsfare.databinding.ActivityPostBinding
import com.example.ownsfare.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity(),IPostAdapter {
    private lateinit var binding: ActivityPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user1 : FirebaseUser
    private lateinit var postDao: PostDao
    private lateinit var documentReference: DocumentReference
    private lateinit var postid : String
    lateinit var adapter : PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        user1 = auth.currentUser!!
        val userId = auth.currentUser!!.uid
        postDao = PostDao()
        val postCollection = postDao.postCollection
        postid = postDao.currTime.toString()
        binding.add.setOnClickListener{
            val postIntent = Intent(this,CreatePostActivity::class.java)
            startActivity(postIntent)
        }
        val postDao = PostDao()
        val postsCollections = postDao.postCollection
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(recyclerViewOptions,this,this)
        binding.recyclerView.adapter = adapter
        //postDao.updateId()
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikedClicked(postId: String) {
        postDao = PostDao()
        postDao.updateLike(postId)
    }
}
