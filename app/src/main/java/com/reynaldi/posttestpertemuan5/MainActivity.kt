package com.reynaldi.posttestpertemuan5

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.reynaldi.posttestpertemuan5.databinding.ActivityMainBinding
import com.reynaldi.posttestpertemuan5.ui.AddPostFragment
import com.reynaldi.posttestpertemuan5.ui.PostAdapter
import com.reynaldi.posttestpertemuan5.ui.StoryAdapter

class MainActivity : AppCompatActivity(), PostAdapter.PostItemListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var postDao: PostDao
    private lateinit var appExecutors: AppExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postDao = PostDatabase.getDatabase(this).postDao()
        appExecutors = AppExecutor()

        val adapter = PostAdapter(mutableListOf(), this)
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = adapter

        postDao.getAllPosts().observe(this) { posts ->
            if (posts.isEmpty()) {
                appExecutors.diskIO.execute {
                    postDao.insert(Post(username = "intan_dwi", caption = "Max Verstappen GAS!!!", imageUri = "android.resource://com.reynaldi.posttestpertemuan5/drawable/post_image_1", profileImageResId = R.drawable.people))
                    postDao.insert(Post(username = "minda_04", caption = "Jumat jumawa, Minggu Bela Sungkawaa️", imageUri = "android.resource://com.reynaldi.posttestpertemuan5/drawable/post_image_2", profileImageResId = R.drawable.people2))
                    postDao.insert(Post(username = "rubi_community", caption = "Lewis Ngawilton", imageUri = "android.resource://com.reynaldi.posttestpertemuan5/drawable/post_image_3", profileImageResId = R.drawable.people1))
                }
            }
            adapter.updatePosts(posts)
        }

        // Setup Stories RecyclerView
        binding.rvStories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val stories: List<Story> = listOf(
            Story(username = "Reynaldi", profileImage = R.drawable.people),
            Story(username = "Galih", profileImage = R.drawable.people1),
            Story(username = "rubi_community", profileImage = R.drawable.people2),
            Story(username = "rizka", profileImage = R.drawable.people3),
            Story(username = "amel", profileImage = R.drawable.people4),
            Story(username = "Fulan", profileImage = R.drawable.people5),
            Story(username = "Fulano", profileImage = R.drawable.people4)
        )
        val storyAdapter = StoryAdapter(stories)
        binding.rvStories.adapter = storyAdapter

        binding.fabAddPost.setOnClickListener {
            val addPostFragment = AddPostFragment.newInstance()
            addPostFragment.show(supportFragmentManager, AddPostFragment.TAG)
        }
    }

    override fun onEditClick(post: Post) {
        val addPostFragment = AddPostFragment.newInstance(post.id)
        addPostFragment.show(supportFragmentManager, AddPostFragment.TAG)
    }

    override fun onDeleteClick(post: Post) {
        appExecutors.diskIO.execute {
            postDao.delete(post)
            runOnUiThread {
                Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
