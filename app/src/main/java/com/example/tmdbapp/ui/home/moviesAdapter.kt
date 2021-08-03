package com.example.tmdbapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbapp.R
import com.example.tmdbapp.model.PaginationScrollListener
import com.example.tmdbapp.model.Result

class moviesAdapter : RecyclerView.Adapter<moviesAdapter.MyViewHolder>() {


    var movies=ArrayList<Result>()
    // var liveMovies= MutableLiveData<ArrayList<Show>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        /*
        holder.itemView.setOnClickListener() { view ->

            characters?.removeAt(position)
            liveCharaters.postValue(characters)
            notifyDataSetChanged()

        }*/
         holder.bind(movies.get(position))

    }


    fun addData(listItems: ArrayList<Result>) {
        var size = this.movies.size
        this.movies.addAll(listItems)
        var sizeNew = this.movies.size
        notifyItemRangeChanged(size, sizeNew)
        notifyDataSetChanged()
    }


    override fun getItemCount() = movies?.size ?: 0




    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private  val textView2: TextView =itemView.findViewById(R.id.textView2)
        private val textView4: TextView = itemView.findViewById(R.id.textView4)

        fun bind(movie: Result) {
            //imageDrawable)
            textView.text = movie.original_title
            textView2.text= movie.release_date
            textView4.text=movie.vote_average.toString()
            Glide.with(imageView.context).load("https://image.tmdb.org/t/p/w500"+movie.backdrop_path)
              .override(450,450)
            //    .centerInside()
                .into(imageView)
            // imageView.setImageResource(R.)
        }

    }

}