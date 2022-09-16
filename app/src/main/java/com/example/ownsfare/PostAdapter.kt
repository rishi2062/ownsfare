package com.example.ownsfare

//import com.bumptech.glide.Glide
//import com.example.social.model.Post
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ownsfare.Dao.PostDao
import com.example.ownsfare.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener : IPostAdapter,private val context: Context) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {

    private lateinit var auth: FirebaseAuth
    private lateinit var user1 : FirebaseUser
    private lateinit var postDao: PostDao
    private lateinit var documentReference: DocumentReference
    private lateinit  var postid : String
    private lateinit var link : String
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.findViewById<ImageView>(R.id.userImage)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val createdAt = itemView.findViewById<TextView>(R.id.createdAt)
        val postTitle = itemView.findViewById<TextView>(R.id.postTitle)
        val likeButton = itemView.findViewById<ImageView>(R.id.likeButton)
        val likeCount = itemView.findViewById<TextView>(R.id.likeCount)
        val btnShare = itemView.findViewById<Button>(R.id.btnShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false))
        view.likeButton.setOnClickListener{
            listener.onLikedClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }

        return view
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
       // val item = model
        holder.userName.text = model.createdBy.displayName
        holder.postTitle.text = model.text
        postid = model.postId
       // holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        //holder.likeCount.text = model.likedBy.size.toString()
       // Glide.with(holder.userImage.context).load(model.createdBy.photoUrl).circleCrop().into(holder.userImage)
        auth = Firebase.auth
        val currUser = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currUser)
        user1 = auth.currentUser!!
        val userId = auth.currentUser!!.uid
        postDao = PostDao()
        val postCollection = postDao.postCollection
//        postCollection.addSnapshotListener{it ,e->
//            if(it!=null){
//                val documents = it.documents
//                Log.d("CODEEEE",documents.toString())
//                documents.forEach{
//                    documentReference = postCollection.document(it.id)
//                    documentReference.get().addOnSuccessListener {
//                        if (it.exists()) {
//                            val randomNumber = it.getString("postId")
//                            Log.d("TAG ye hai Id -->", randomNumber.toString() + " " + position)
//
//                            link = "https://www.example.com/posts/?postid=${randomNumber.toString()}"
//                            Log.d("kassd",link + " " + position)
//                            // documentReference.update("postId",randomNumber.toString())
//                        }
//                    }
//
//                }
//            }
//            if(e!=null){
//                Log.d("Error",e.toString())
//            }
//
//        }
//        val currTime = System.currentTimeMillis()
//        postid = postDao.currTime.toString()
//        documentReference = postCollection.document(postid)
//        documentReference.get().addOnSuccessListener {
//            if (it.exists()) {
//                val randomNumber = it.getString("postId")
//                postid = randomNumber.toString()
//
//                Log.d("TAG ye hai code fjd", randomNumber.toString())
//            }
//        }
        //postid ="ABCD"
        link = "https://www.example.com/posts/?postid=${model.postId}"
        Log.d("TAG ye code link ->>", link)
        Log.d("TAG ye hai code fjd", postid + " " + position)
        val LinkText = "https://www.example.com/posts/?postid=${postid}"
        Log.d("Maindjffdfg",LinkText)
        Log.e("main", "create link")
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(LinkText))
            .setDomainUriPrefix("https://vasukamownsfare.page.link") // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.ios").build())
            .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                .setTitle("My Post Link")
                .setDescription("This is my Post")
                .setImageUrl(Uri.parse("https://media-exp2.licdn.com/dms/image/C4E0BAQHGKwTe30paaw/company-logo_200_200/0/1625508309927?e=2147483647&v=beta&t=eGK_ct5hrDl5Eu78gWHgepHqGmGFjBr8GTYVgwvlsvw"))
                .build())
            .buildDynamicLink()

        val dynamicLinkUri = dynamicLink.uri

        holder.btnShare.setOnClickListener{
            Log.e("main", "Long Refer " + dynamicLink.uri)

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
            context.startActivity(intent)

        }

    }
//    private fun generateLink() {
//
//    }
}
interface IPostAdapter{
    fun onLikedClicked(postId : String)
}
