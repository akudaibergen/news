package com.example.news

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapp.Movie

class DetailActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var nameofMovie: TextView
    private lateinit var plotSynopsis: TextView
    private lateinit var userRating: TextView
    private lateinit var releaseDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var toolbar: Toolbar
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        toolbar = findViewById(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        textView = findViewById(R.id.toolbar_title_detail)
        toolbar?.navigationIcon = ContextCompat.getDrawable(this,R.drawable.ic_chevron_left_black_24dp)
        toolbar?.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        nameofMovie = findViewById(R.id.title_of_movies)
        plotSynopsis = findViewById(R.id.description)
        userRating = findViewById(R.id.rate_movie_user)
        releaseDate = findViewById(R.id.date_release)
        imageView = findViewById(R.id.movie_pic_detail)
        initIntent()

    }

    private fun initIntent() {
        try {
            movie = intent.extras?.getSerializable("movie") as Movie

            val thumbnail = movie?.getPosterPath()
            val movieName = movie?.original_title
            val synopsis = movie?.overview
            val rating = movie?.vote_average.toString()
            val sateOfRelease = movie?.release_date

            Glide.with(this)
                .load(thumbnail)
                .into(imageView)


            nameofMovie.text = movieName
            plotSynopsis.text = synopsis
            userRating.text = rating
            releaseDate.text = sateOfRelease
            toolbar.title = "News " + movie?.original_title
        }
        catch (e: Exception) {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
