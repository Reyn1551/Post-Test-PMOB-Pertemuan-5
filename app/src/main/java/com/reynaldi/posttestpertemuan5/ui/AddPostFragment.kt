package com.reynaldi.posttestpertemuan5.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reynaldi.posttestpertemuan5.AppExecutor
import com.reynaldi.posttestpertemuan5.Post
import com.reynaldi.posttestpertemuan5.PostDao
import com.reynaldi.posttestpertemuan5.PostDatabase
import com.reynaldi.posttestpertemuan5.R
import com.reynaldi.posttestpertemuan5.databinding.FragmentAddPostBinding

class AddPostFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private var editingPost: Post? = null
    private var postIdToEdit: Int = -1

    private lateinit var postDao: PostDao
    private lateinit var appExecutors: AppExecutor

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImageUri = it
                binding.ivImagePreview.setImageURI(it)
                binding.ivImagePreview.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)

        postDao = PostDatabase.getDatabase(requireContext()).postDao()
        appExecutors = AppExecutor()

        postIdToEdit = arguments?.getInt(EXTRA_POST_ID, -1) ?: -1

        if (postIdToEdit != -1) {
            loadPostData(postIdToEdit)
        } else {
            binding.tvDialogTitle.text = "Tambah Post Baru"
            binding.btnSave.text = "Simpan"
        }

        setupClickListeners()
        return binding.root
    }

    private fun loadPostData(postId: Int) {
        binding.tvDialogTitle.text = "Edit Post"
        binding.btnSave.text = "Perbarui"

        appExecutors.diskIO.execute {
            editingPost = postDao.getPostById(postId)
            appExecutors.mainThread.execute {
                editingPost?.let { post ->
                    binding.etUsername.setText(post.username)
                    binding.etCaption.setText(post.caption)
                    if (post.imageUri.isNotEmpty()) {
                        currentImageUri = Uri.parse(post.imageUri)
                        binding.ivImagePreview.setImageURI(currentImageUri)
                        binding.ivImagePreview.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            savePost()
        }
    }

    private fun savePost() {
        val username = binding.etUsername.text.toString().trim()
        val caption = binding.etCaption.text.toString().trim()

        if (username.isEmpty() || caption.isEmpty() || currentImageUri == null) {
            Toast.makeText(context, "Isi semua kolom dulu", Toast.LENGTH_SHORT).show()
            return
        }

        val profileResId = if (username.contains("reynaldi", true)) {
            R.drawable.ic_profile_1
        } else {
            R.drawable.ic_profile_2
        }

        appExecutors.diskIO.execute {
            if (editingPost == null) {
                val newPost = Post(
                    username = username,
                    caption = caption,
                    imageUri = currentImageUri.toString(),
                    profileImageResId = profileResId
                )
                postDao.insert(newPost)

                appExecutors.mainThread.execute {
                    Toast.makeText(context, "Post ditambahkan", Toast.LENGTH_SHORT).show()
                }
            } else {
                val updatedPost = editingPost!!.copy(
                    username = username,
                    caption = caption,
                    imageUri = currentImageUri.toString(),
                    profileImageResId = profileResId
                )
                postDao.update(updatedPost)

                appExecutors.mainThread.execute {
                    Toast.makeText(context, "Post diperbarui", Toast.LENGTH_SHORT).show()
                }
            }

            appExecutors.mainThread.execute {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPostFragment"
        const val EXTRA_POST_ID = "extra_post_id"

        fun newInstance(postId: Int? = null): AddPostFragment {
            val fragment = AddPostFragment()
            postId?.let {
                val args = Bundle()
                args.putInt(EXTRA_POST_ID, it)
                fragment.arguments = args
            }
            return fragment
        }
    }
}