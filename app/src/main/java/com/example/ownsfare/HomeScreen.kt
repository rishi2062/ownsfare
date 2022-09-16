package com.example.ownsfare

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ownsfare.Dao.PostDao
import com.example.ownsfare.databinding.ActivityHomeScreenBinding
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentReference


const val TAG: String = "HomeScreen"
class HomeScreen : AppCompatActivity() {
    private lateinit var binding : ActivityHomeScreenBinding
    private lateinit var postDao: PostDao
    private lateinit var documentReference: DocumentReference
    lateinit var signIn : SignInActivity
   // var referId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

       postDao = PostDao()
       val postsCollections = postDao.postCollection
       val f = intent.getStringExtra("postid")
       Log.d("Path data ye -- ",f.toString())

       //val postId = deepLink.getQueryParameter("postid").toString()
       documentReference = postsCollections.document(f.toString())
       documentReference.get().addOnSuccessListener {
           if (it.exists()) {
               binding.postTitle.text = it.getString("text").toString()
               binding.userName.text = it.getString("createdBy.displayName").toString()

           }
       }
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    Log.i("HomeScreen", "my refer link " + deepLink.toString())

                }

                if (deepLink != null) {
                    val j = intent.getStringExtra("postid")
                    Log.d("Path data ye -- ", j.toString())

                    val postId = deepLink.getQueryParameter("postid").toString()
                    documentReference = postsCollections.document(j.toString())
                    documentReference.get().addOnSuccessListener {
                        if (it.exists()) {
                            val randomNumber = it.getString("countRef")
                            binding.postTitle.text = it.getString("text").toString()
                            binding.userName.text = it.getString("displayName").toString()

                        }
                    }
                    // Log.d("TAG",deepLink.getQueryParameter("postid").toString())
                }


                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(
                this
            ) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

    }
}