package com.example.ownsfare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ownsfare.Dao.UserDao
import com.example.ownsfare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var signIn : SignInActivity
    private lateinit var userDao: UserDao
    private lateinit var user : String
    private lateinit var auth: FirebaseAuth
    private lateinit var user1 : FirebaseUser
    private var url : Uri? = null
    private lateinit var documentReference: DocumentReference
    private lateinit var documentReference1: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnShare.setOnClickListener {
            generateLink()

        }

        binding.addPost.setOnClickListener{
            val intent = Intent(this,PostActivity::class.java)
            startActivity(intent)
        }
        auth = Firebase.auth
        user1 = auth.currentUser!!
        val userId = auth.currentUser!!.uid
        userDao = UserDao()
        val postsCollections = userDao.userCollection

        signIn = SignInActivity()


        //val docRef = db.collection("cities").document("BJ")
        val okay = intent.getStringExtra("referal").toString()
        Log.d("TAG ---", okay)
        documentReference = postsCollections.document(userId)
        documentReference.get().addOnSuccessListener {
            if (it.exists()) {
                val randomNumber = it.getString("countRef")
                user = randomNumber.toString()
                binding.ivRefCode.text = randomNumber.toString()
                Log.d("TAG ye hai", randomNumber.toString())
            }
        }

        val query = postsCollections.whereEqualTo("countRef", okay).get()
            .addOnSuccessListener { documents ->

//                if (documents.isEmpty) {
//                    if(!okay.equals(null)){
//                        Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show()
//                    }
//                    documentReference.get().addOnSuccessListener {
//                        if (it.exists()) {
//                            val randomNumber4 = it.getLong("refEarning")
//                            // var a = randomNumber4.toString()
//                            //binding.ivRefCode.text = randomNumber.toString()
//                            Log.d("TAG ye hai -g-g-g-g-g--", randomNumber4.toString())
//                        }
//                    }
//
//                }
                for (document in documents) {
                    if (document.exists()) {
                        Log.d("Code",document.id)
                        documentReference1 = postsCollections.document(document.id)
                        documentReference1.get().addOnSuccessListener{
                            if(it.exists()){
                                val getSenderId = it.getLong("refEarning")
                                val a = getSenderId?.plus(5000)
                                documentReference1.update("refEarning",a)
                            }
                        }

//                   user2.refEarning = user2.refEarning?.plus(2000)
                        documentReference.get().addOnSuccessListener {
                            if (it.exists()) {
                                val getRecieverId = it.getLong("refEarning")
                                val b = getRecieverId?.plus(2000)
                                //binding.ivRefCode.text = randomNumber.toString()
                                Log.d("TAG ye hai -h-h-h-h-g--", getRecieverId.toString())
                                Log.d("TAG ye hai -h-h-h-h-d--", b.toString())
                                documentReference.update("refEarning", b)
                            }
                        }


                        //       userDao.addEarning(user2)


                    }
                    Log.d(TAG, document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        //binding.referAmount.setText("2000 VAT")
        documentReference.get().addOnSuccessListener {
            if (it.exists()) {
                val c = it.getLong("refEarning")

                binding.referAmount.text = c.toString()
            }
        }



    }


    private fun generateLink(){
//        //String parameters
        val LinkText = "https://www.example.com/?refid=$user&uid=${user1.uid}"
//        val shareLinkText : String = "https://vasukamownsfare.page.link/?" +
//                "link = //www.example.com/?refid=$user"+
//                "&apn=" + packageName +
//                "&st=" + "MyReferLink" +
//                "&sd=" + "20 Rewards" +
//                "&si=" + "https://media-exp2.licdn.com/dms/image/C4E0BAQHGKwTe30paaw/company-logo_200_200/0/1625508309927?e=2147483647&v=beta&t=eGK_ct5hrDl5Eu78gWHgepHqGmGFjBr8GTYVgwvlsvw"

        Log.e("main", "create link")
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(LinkText))
            .setDomainUriPrefix("https://vasukamownsfare.page.link") // Open links with this app on Android
            .setAndroidParameters(
                AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(IosParameters.Builder("com.example.ios").build())
            .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                .setTitle("My Referal Link")
                .setDescription("Refer and Earn VATS")
                .setImageUrl(Uri.parse("https://media-exp2.licdn.com/dms/image/C4E0BAQHGKwTe30paaw/company-logo_200_200/0/1625508309927?e=2147483647&v=beta&t=eGK_ct5hrDl5Eu78gWHgepHqGmGFjBr8GTYVgwvlsvw"))
                .build())
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri
        //https://ownsfare.page.link?apn=com.vasukam.ownsfare&ibi=com.example.ios&link=https%3A%2F%2Fwww.example.com%2F
        Log.e("main", "Long Refer " + dynamicLink.uri)

        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            longLink = dynamicLink.uri
        }.addOnSuccessListener {
            url = it.shortLink
        }.addOnFailureListener {
            Log.e("Error","error getting link!")
        }

         Log.d("TAGGGSGG", url.toString())

        //Intent for sharing
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url.toString())
        startActivity(intent)



    }


}


