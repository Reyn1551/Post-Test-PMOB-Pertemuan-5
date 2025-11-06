package com.reynaldi.posttestpertemuan5

import android.os.Bundle
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

        postDao.getAllPosts().observe(this) {
            posts -> adapter.updatePosts(posts)
        }

        // Setup Stories RecyclerView
        binding.rvStories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val stories: List<Story> = listOf(
            Story(username = "reynaldi_123", profileImage = R.drawable.ic_profile_1),
            Story(username = "pengguna_dua", profileImage = R.drawable.ic_profile_1),
            Story(username = "tiga_pengguna", profileImage = R.drawable.ic_profile_1),
            Story(username = "user_empat", profileImage = R.drawable.ic_profile_1),
            Story(username = "lima_pengguna", profileImage = R.drawable.ic_profile_1),
            Story(username = "enam_pengguna", profileImage = R.drawable.ic_profile_1),
            Story(username = "tujuh_pengguna", profileImage = R.drawable.ic_profile_1)
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
        }
    }
}
