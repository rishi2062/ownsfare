package com.example.ownsfare.Dao

import android.util.Log
import com.example.ownsfare.model.Post
import com.example.ownsfare.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private lateinit var documentReference: DocumentReference
    private val db = FirebaseFirestore.getInstance()
     val postCollection = db.collection("Post")
    private val auth = Firebase.auth
    val currTime = System.currentTimeMillis()
     @OptIn(DelicateCoroutinesApi::class)
     fun addPost(text : String)
     {
         val currentUSerId = auth.currentUser!!.uid
         GlobalScope.launch {
             val userDao = UserDao()
             val user = userDao.getUserById(currentUSerId).await().toObject(User::class.java)!! // await will help the code to complete its task and then we call toObject to get our user as it was a task before

             val post = Post(currTime.toString(),text,user,currTime)
             postCollection.document(currTime.toString()).set(post)
             val a = postCollection.document(currTime.toString()).id
//             postCollection.document(a).update("postId",a)

         }
     }

    fun updateId(){
        postCollection.addSnapshotListener{it ,e->
            if(it!=null){
                val documents = it.documents
                Log.d("CODEEEE",documents.toString())
                documents.forEach{
                    documentReference = postCollection.document(it.id)
                    documentReference.get().addOnSuccessListener {
                        if (it.exists()) {
                            val randomNumber = it.getString("postId")
                            Log.d("TAG ye hai Id -->", randomNumber.toString())
                           // documentReference.update("postId",randomNumber.toString())
                        }
                    }

                }
            }
            if(e!=null){
                Log.d("Error",e.toString())
        }

        }
    }
    fun getPostById(postId : String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }
    fun updateLike(postId : String){

        GlobalScope.launch {
            val currentUSerId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!! // await will help the code to complete its task and then we call toObject to get our user as it was a task before
            val isLiked = post.likedBy.contains(currentUSerId)
            if(isLiked)
            {
                post.likedBy.remove(currentUSerId)
            }else{
                post.likedBy.add(currentUSerId)
            }
            postCollection.document(postId).set(post)
        }

    }

}