package com.example.news

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapp.Movie
import com.example.myapp.MoviesAdapter
import com.example.myapp.RetrofitService
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MoviesAdapter
    private lateinit var movieList: List<Movie>
    private var listMovie: List<Movie>? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: Toolbar
    private lateinit var textView: TextView
    private lateinit var nestedScroll: NestedScrollView
    private var movieDao: MovieDao? = null
    private val job = Job()
    private lateinit var progressBar: ProgressBar
    private val PAGE_START = 0
    private var currentPage = PAGE_START
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var moviesInstance: ArrayList<Movie>? = ArrayList()
    private var start = 0
    private var end = 0

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        nestedScroll = findViewById(R.id.nest_main_scroll)
        toolbar = findViewById(R.id.toolbar)
        textView = findViewById(R.id.toolbar_title)
        textView.text = "News"
        recyclerView = findViewById(R.id.recycler_view)

        movieDao = MovieDatabase.getDatabase(context = this).movieDao()
        if (this.resources.configuration.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = linearLayoutManager
        }
        swipeRefreshLayout = findViewById(R.id.main_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            refreshViews()
        }

        ViewCompat.setNestedScrollingEnabled(recyclerView, false)
        nestedScroll.viewTreeObserver?.addOnScrollChangedListener {
            val holder = nestedScroll.getChildAt(0) as ViewGroup
            for (i in 0 until holder.childCount) {
                //Pull the pagination recyclerview child
                if (holder.getChildAt(i).id == recyclerView.id) {
                    recyclerView = holder.getChildAt(i) as RecyclerView
                    break
                }
            }
            recyclerView.let {
                if (it.bottom - (nestedScroll.height + nestedScroll.scrollY) == 0 ){
                    if (((currentPage+1)*10)<listMovie!!.size){
                        currentPage++
                        Toast.makeText(this@MainActivity, "News are loading ", Toast.LENGTH_SHORT).show()
                        loadNextPage()
                    }
                    else{
                        Toast.makeText(this@MainActivity, "There is no new posts ", Toast.LENGTH_SHORT).show()
                    }

            }

        }
        }
        refreshViews()
    }

    private fun refreshViews() {
        currentPage = 0
        end = 0
        start = 0
        progressBar.visibility = View.GONE
        movieList = ArrayList<Movie>()
        movieAdapter = MoviesAdapter(this, movieList as ArrayList<Movie>)
        movieAdapter = this.applicationContext?.let { MoviesAdapter(it, movieList as ArrayList<Movie>) }!!
        if (this.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 1)
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
        recyclerView.adapter = movieAdapter
        movieAdapter.notifyDataSetChanged()
        getPostsCoroutine()
    }

    private fun getPostsCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            listMovie = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getPopularMovieListCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    if (response.isSuccessful) {
                        val result = response.body()?.results
                        if (!result.isNullOrEmpty()) {
                            movieDao?.insertAll(result)
                            moviesInstance?.addAll(result)

                        }
                        result
                    } else {
                        movieDao?.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getAll() ?: emptyList()
                }
            }
            getFirstPosts()
        }
    }

    private fun getFirstPosts() {
        movieAdapter.addList(listMovie?.subList(0, 10))
        swipeRefreshLayout.isRefreshing = false
    }

    private fun loadNextPage() {
        progressBar.visibility = View.VISIBLE
        start = currentPage * 10
        end = (currentPage + 1) * 10
        if(end >= listMovie!!.size)
        {
            end = listMovie!!.size
        }
        Handler().postDelayed({
            movieAdapter.addList(listMovie?.subList(start,end))
            movieAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }, 4000)
    }
}

