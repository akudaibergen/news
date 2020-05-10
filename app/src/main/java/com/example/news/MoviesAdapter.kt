package com.example.myapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.DetailActivity
import com.example.news.R

class MoviesAdapter(
    var context: Context,
    var moviesList: ArrayList<Movie>? = ArrayList()
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = moviesList?.size ?: 0

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(moviesList?.get(position))
    }

    inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?) {
            val title = view.findViewById<TextView>(R.id.title_movie)
            val voteMovie = view.findViewById<TextView>(R.id.rate_movie)
            val data = view.findViewById<TextView>(R.id.date_movie)
            val pictureMovie = view.findViewById<ImageView>(R.id.movie_pic)

            title.text = movie?.original_title
            voteMovie.text = movie?.vote_average.toString()
            data.text = movie?.release_date

            Glide.with(context).load(movie?.getPosterPath()).into(pictureMovie)

            view.setOnClickListener {
                val intent = Intent(context,DetailActivity::class.java)
                intent.putExtra("movie",movie)
                view.context.startActivity(intent)
            }
        }
    }

    open fun addList(movieList: List<Movie>?) {
        if (movieList != null) {
            moviesList?.addAll(movieList)
            notifyDataSetChanged()
        }
    }
}