package com.pmob.postapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pmob.postapp.databinding.FragmentAddPostBinding

/**
 * BottomSheetDialogFragment untuk Tambah dan Edit Postingan.
 * Ini adalah dialog yang muncul dari bawah.
 */
class AddPostFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private var editingPost: Post? = null
    private var postIdToEdit: Int = -1

    private lateinit var postDao: PostDao
    private lateinit var appExecutors: AppExecutor

    // ActivityResultLauncher untuk memilih gambar
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
        
        // Inisialisasi DB dan Executor
        postDao = PostDatabase.getDatabase(requireContext()).postDao()
        appExecutors = AppExecutor()
        
        // Ambil ID postingan jika dalam mode edit
        postIdToEdit = arguments?.getInt(EXTRA_POST_ID, -1) ?: -1

        if (postIdToEdit != -1) {
            // Mode Edit
            loadPostData(postIdToEdit)
        } else {
            // Mode Tambah
            binding.tvDialogTitle.text = "Tambah Post Baru"
            binding.btnSave.text = "Simpan"
        }

        setupClickListeners()
        return binding.root
    }

    // Memuat data postingan jika dalam mode Edit
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
        // Tombol Tambah Gambar
        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*") // Buka pemilih gambar
        }

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            savePost()
        }
    }

    private fun savePost() {
        val username = binding.etUsername.text.toString().trim()
        val caption = binding.etCaption.text.toString().trim()

        // Validasi input (seperti di screenshot 4)
        if (username.isEmpty() || caption.isEmpty() || currentImageUri == null) {
            Toast.makeText(context, "Isi semua kolom dulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Tentukan gambar profil secara acak (untuk contoh)
        val profileResId = if (username.contains("intan", true)) {
            R.drawable.ic_profile_1
        } else {
            R.drawable.ic_profile_2
        }

        appExecutors.diskIO.execute {
            if (editingPost == null) {
                // Mode Tambah: Buat postingan baru
                val newPost = Post(
                    username = username,
                    caption = caption,
                    imageUri = currentImageUri.toString(),
                    profileImageResId = profileResId
                )
                postDao.insert(newPost)
                
                // Tampilkan Toast di Main Thread
                appExecutors.mainThread.execute {
                    Toast.makeText(context, "Post ditambahkan", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Mode Edit: Perbarui postingan yang ada
                val updatedPost = editingPost!!.copy(
                    username = username,
                    caption = caption,
                    imageUri = currentImageUri.toString(),
                    profileImageResId = profileResId
                )
                postDao.update(updatedPost)
                
                // Tampilkan Toast di Main Thread
                appExecutors.mainThread.execute {
                    // Seperti di screenshot 8
                    Toast.makeText(context, "Post diperbarui", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Tutup dialog
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

        // Fungsi untuk membuat instance fragment (baik untuk tambah baru atau edit)
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
