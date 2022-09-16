package com.example.ownsfare.Dao

import com.example.ownsfare.model.User
import com.example.ownsfare.model.referCode
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")
    val codeCollection = db.collection("referCode")

    @OptIn(DelicateCoroutinesApi::class)
    fun addUser(user: User?) {
        user?.let {
            GlobalScope.launch {
                userCollection.document(user.id).set(it)
            }

        }
    }
    fun addEarning(user1: User?) {
        user1?.let {
            GlobalScope.launch {
                it.refEarning?.let { it1 -> userCollection.document(user1.id).set(it1) }
            }

        }
    }
    fun addCode(code: referCode?) {
        code?.let {
            GlobalScope.launch {
                codeCollection.document(code.uid).set(it)
            }

        }
    }

    fun getId(user: User) : Task<DocumentSnapshot>? {
       return user.countRef?.let { userCollection.document(it).get() }
       // return userCollection.do
    }

    fun getUserById(uId : String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }

//   val getId = getUserById(uId).getResult()
//    val i = getId.get("countRef")

}

