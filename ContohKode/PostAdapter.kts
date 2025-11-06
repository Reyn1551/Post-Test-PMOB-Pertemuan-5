package com.pmob.postapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.pmob.postapp.databinding.ItemPostBinding

class PostAdapter(
    private val postList: List<Post>,
    // Lambda function untuk menangani klik pada tombol opsi (edit/delete)
    private val onOptionClick: (Post, View) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, onOptionClick: (Post, View) -> Unit) {
            binding.tvPostUsername.text = post.username
            binding.tvPostCaption.text = post.caption
            binding.ivPostProfile.setImageResource(post.profileImageResId)

            // Muat gambar dari URI
            if (post.imageUri.isNotEmpty()) {
                binding.ivPostImage.setImageURI(Uri.parse(post.imageUri))
            }

            // Set listener untuk tombol opsi
            binding.ivPostOptions.setOnClickListener {
                onOptionClick(post, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position], onOptionClick)
    }
}
