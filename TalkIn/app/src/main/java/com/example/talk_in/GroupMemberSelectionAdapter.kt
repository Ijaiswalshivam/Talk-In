import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.talk_in.R
import com.example.talk_in.User
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class GroupMemberSelectionAdapter(val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<GroupMemberSelectionAdapter.UserViewHolder>() {

    // Track selected users
    private val selectedUsers: ArrayList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        var name = currentUser.name?.trim() ?: ""
        if (name.length > 25) {
            name = "${name.substring(0, 25)}..."
        }

        holder.textName.text = name

        // Toggle the visibility of selection_icon and update selectedUsers list on item click
        holder.itemView.setOnClickListener {
            if (selectedUsers.contains(currentUser)) {
                selectedUsers.remove(currentUser)
                holder.selection_icon.visibility = View.GONE
            } else {
                selectedUsers.add(currentUser)
                holder.selection_icon.visibility = View.VISIBLE
            }
        }

        var aboutUser = currentUser.aboutMe.toString().trim()
        if (aboutUser.length > 30) {
            aboutUser = "${aboutUser.substring(0,30)}..."
        }
        holder.aboutUserTextView.text = aboutUser

        currentUser.uid?.let { setProfileImage(it, holder) }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.txt_name)
        val userProfileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        val selection_icon: ImageView = itemView.findViewById(R.id.selection_icon)
        val aboutUserTextView: TextView = itemView.findViewById(R.id.txt_last_message)
    }

    private fun setProfileImage(uid: String, holder: UserViewHolder) {
        val storageReference = FirebaseStorage.getInstance().reference.child("user_profile_images/$uid.jpg")

        try {
            val localFile = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.resources, bitmap)
                    circularBitmapDrawable.isCircular = true
                    holder.userProfileImage.setImageDrawable(circularBitmapDrawable)
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileImage", "Failed to load profile image for UID: $uid", exception)
                    holder.userProfileImage.setImageResource(R.drawable.user_profile_icon)
                }
        } catch (e: IOException) {
            Log.e("ProfileImage", "Error creating temp file for UID: $uid", e)
            holder.userProfileImage.setImageResource(R.drawable.user_profile_icon)
        }
    }

    // Get the list of selected users
    fun getSelectedUsers(): List<User> {
        return selectedUsers
    }
}