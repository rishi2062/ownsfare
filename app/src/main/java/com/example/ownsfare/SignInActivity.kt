package com.example.ownsfare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ownsfare.Dao.UserDao
import com.example.ownsfare.databinding.ActivitySignInBinding
import com.example.ownsfare.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

//const val referId = MainActivity().getRandomString(6)
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    var referCode = ""
    private val RC_SIGN_IN: Int = 123
    private val TAG = "SignInActivity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UserDao
    private lateinit var documentReference: DocumentReference
    private lateinit var parts : ArrayList<String>
    lateinit var home : HomeScreen
    // private lateinit var user1 : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        binding.signInButton.setOnClickListener {
            signIn()
        }
        home = HomeScreen()
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.i("SignInScreen", "my refer link " + deepLink.toString())

                }

                if (deepLink != null) {
                    Log.d("TAG", deepLink.getQueryParameter("refid").toString())

                    binding.editRefer.setText(deepLink.getQueryParameter("refid").toString())
                    val path = deepLink.getPath()
                    Log.d("Path",path.toString())
                    if(path.equals("/posts/")){
                        val intent = Intent(this, HomeScreen::class.java);
                        intent.putExtra("postid", deepLink.getQueryParameter("postid").toString())
                        Log.d("Path j -- ",deepLink.getQueryParameter("postid").toString())
                        startActivity(intent)
                    }

//                    if (path != null) {
//                        parts = path.split("/") as ArrayList<String>
//                    }
//                    if (parts.get(1) == "posts") {
//                        val intent = Intent(this, HomeScreen::class.java);
//                        intent.putExtra("postid", deepLink.getQueryParameter("postid").toString());
//                        startActivity(intent);
//                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
        Log.d(TAG, "firebaseAuthWithGoogle:")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            Log.d(TAG, "firebaseAuthWithGoogleaayaaaya:")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
            binding.vasukamLogo.visibility = View.GONE
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.signInButton.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.editRefer.visibility = View.GONE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val user = auth.user
            withContext(Dispatchers.Main) {
                updateUI(user)
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            val referal = getRandomString(6)
            userDao = UserDao()
            val postsCollections = userDao.userCollection
            Log.d("TAG code -8-8--",referal)
            Log.d("TAG UID",user.uid)
            Log.d("TAG codaa",binding.editRefer.text.toString())
            val query1 = postsCollections.whereEqualTo("id",user.uid).get().addOnSuccessListener{
                if(it.isEmpty){
                    val query = postsCollections.whereEqualTo("countRef",referal).get().addOnSuccessListener {
                            documents ->
                        if(documents.isEmpty){

                            Log.d("TAG code---lk--",referal)
                            val user = User(user.uid, user.displayName,referal,0)
                            val usersDao = UserDao()
                            usersDao.addUser(user)
                            val mainActivityIntent = Intent(this, MainActivity::class.java).putExtra("referal",binding.editRefer.text.toString())
                            startActivity(mainActivityIntent)
                            finish()
                        }
                        for (document in documents) {
                            if(document.exists()){
                                Log.d("TAG code -9-9--",referal)
                                updateUI(user)
                                // binding.referAmount.setText("4000 VAT")
                            }
                            else{
                                Log.d("TAG code -10-9--",referal)



                            }
                            Log.d(com.example.ownsfare.TAG, document.id)
                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.w(com.example.ownsfare.TAG, "Error getting documents: ", exception)
                        }
                }else{
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()
                }
            }
                .addOnFailureListener { exception ->
                    Log.w(com.example.ownsfare.TAG, "Error getting documents: ", exception)
                }

            Log.d("TAG ---->>",referCode)

    }
        else {
            binding.signInButton.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

        }
    }

      fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }




}