package com.reynaldi.posttestpertemuan5.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.reynaldi.posttestpertemuan5.Post
import com.reynaldi.posttestpertemuan5.R
import com.reynaldi.posttestpertemuan5.databinding.ItemPostBinding

class PostAdapter(
    private var listPost: MutableList<Post>,
    private val listener: PostItemListener
) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface PostItemListener {
        fun onEditClick(post: Post)
        fun onDeleteClick(post: Post)
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.ivPostProfile.setImageResource(post.profileImageResId)
            if (post.imageUri.isNotEmpty()) {
                binding.ivPostImage.setImageURI(Uri.parse(post.imageUri))
            } else {
                binding.ivPostImage.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder
            }
            binding.tvPostUsername.text = post.username
            binding.tvPostCaption.text = post.caption

            binding.ivPostOptions.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.post_options_menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            listener.onEditClick(post)
                            true
                        }
                        R.id.menu_delete -> {
                            listener.onDeleteClick(post)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(listPost[position])
    }

    override fun getItemCount(): Int = listPost.size

    fun updatePosts(posts: List<Post>) {
        listPost.clear()
        listPost.addAll(posts)
        notifyDataSetChanged()
    }
}
