package com.example.tmdbapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tmdbapp.R
import com.example.tmdbapp.model.Result

class DetailFragment : Fragment(R.layout.fragment_detail) {

lateinit  var movieName:TextView
lateinit  var movieDetail:TextView
    lateinit  var movieImage:ImageView
    var movie:Result? =null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity?.actionBar?.hide()

         movieName=view.findViewById(R.id.MovieName)
        movieDetail=view.findViewById(R.id.MovieDetail)
        movieImage=view.findViewById(R.id.imageView2)

   movie= arguments?.getSerializable("Movie") as Result?
movieDetail.text=movie!!.overview
        movieName.text=movie!!.original_title
        Glide.with(movieImage.context).load("https://image.tmdb.org/t/p/w500" + movie!!.backdrop_path)
                // .override(450,450)
                //    .centerInside()
                .into(movieImage)
    }
}