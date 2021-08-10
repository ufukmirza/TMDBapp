package com.example.tmdbapp.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.tmdbapp.R
import com.example.tmdbapp.model.Result
import com.google.android.material.bottomnavigation.BottomNavigationView


class DetailFragment : Fragment(R.layout.fragment_detail) {

    lateinit var movieName: TextView
    lateinit var movieDetail: TextView
    lateinit var movieImage: ImageView
    lateinit var movieVote: TextView
    lateinit var movieDate: TextView
    lateinit var favourite: CheckBox
    var stringHashSet:Set<String>?=null
    var movieId: Int? = null
    var movie: Result? = null
    private val detailViewModel: DetailViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val preferences = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
         stringHashSet= preferences?.getStringSet("favorites", HashSet<String>())
        val navBar: BottomNavigationView = activity?.findViewById(R.id.nav_view)!!
        //   navBar.visibility=View.INVISIBLE
        activity?.actionBar?.hide()

        movieName = view.findViewById(R.id.MovieName)
        movieDetail = view.findViewById(R.id.MovieDetail)
        movieImage = view.findViewById(R.id.imageView2)
        movieDate = view.findViewById(R.id.releaseDate)
        movieVote = view.findViewById(R.id.voteScore)
        favourite = view.findViewById(R.id.checkBox)


        favourite.setOnClickListener {
            var inSet = HashSet<String>(stringHashSet)
            if (favourite.isChecked) {

               inSet.add(movieId.toString())
                preferences?.edit()?.putStringSet("favorites",inSet)!!.apply()
              Log.d("sa", stringHashSet?.size.toString())
            }
              else{
                  inSet.remove(movieId.toString())
                preferences?.edit()?.putStringSet("favorites", stringHashSet)!!.apply()
                }



        }
        /*
   movie= arguments?.getSerializable("Movie") as Result?
movieDetail.text=movie!!.overview
        movieName.text=movie!!.original_title
        Glide.with(movieImage.context).load("https://image.tmdb.org/t/p/w500" + movie!!.backdrop_path)
                // .override(450,450)
                //    .centerInside()
                .into(movieImage)
                */

        detailViewModel.getMovieDetail(arguments?.getInt("id")!!)
        observe()


    }

    fun observe()  {

        detailViewModel.showLiveData.observe(requireActivity()) {
            movieId = it.id
            movieDetail.text = it.overview
            // movieDetail.text="\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nsa"
            movieName.text = it.original_title
            movieDate.append(it.release_date)
            movieVote.append(it.vote_average.toString())
            Glide.with(movieImage.context).load("https://image.tmdb.org/t/p/w500" + it.backdrop_path!!)
                    // .override(450,450)
                    //    .centerInside()
                    .into(movieImage)

            if(stringHashSet!!.contains(movieId.toString())) {
                favourite.isChecked = true

            }

        }


    }
}