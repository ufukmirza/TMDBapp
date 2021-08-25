package com.example.tmdbapp.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapp.R
import com.example.tmdbapp.model.DBHelper
import com.example.tmdbapp.model.MovieActor
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.ui.home.moviesAdapter


class DetailFragment : Fragment(R.layout.fragment_detail),ActorClickInterface {


    lateinit var recyclerView: RecyclerView
    var actorsAdapter = CharactersAdapter()
    lateinit var movieName: TextView
    lateinit var movieDetail: TextView
    lateinit var movieImage: ImageView
    lateinit var movieVote: TextView
    lateinit var movieDate: TextView
    lateinit var favourite: CheckBox
    lateinit var characters: TextView
    var stringHashSet: Set<String>? = null
    var movieId: Int? = null
    var movie: Result? = null
    private val detailViewModel: DetailViewModel by viewModels()
    val db by lazy { DBHelper(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //shared preferencestaki favori id leri cekilir.

        val preferences = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        stringHashSet = preferences?.getStringSet("favorites", HashSet<String>())
        activity?.actionBar?.hide()




        movieName = view.findViewById(R.id.MovieName)
        movieDetail = view.findViewById(R.id.MovieDetail)
        movieImage = view.findViewById(R.id.imageView2)
        movieDate = view.findViewById(R.id.releaseDate)
        movieVote = view.findViewById(R.id.voteScore)
        favourite = view.findViewById(R.id.checkBox)
        characters = view.findViewById(R.id.characters)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_actors)
        recyclerView.adapter = actorsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        actorsAdapter.clickInterface = this


//eger detay sayfasinda filmin yildiz ikonuna basilirsa
//film zaten favori ise favorilerden cikarilir ve hem shared preferencesa hem de sql database den kaldirilir
//film favoride degilse favorilere alinir ve shared preferences ile sql database e kaydedilir.
//geri tusuna basilgidinda anasayfa ekrani uzerinde filmin favoriden cikarilip cikarilmamasinin gozukebilmesi icin findNavController kullanilir.



            favourite.setOnClickListener {

                if(movie!=null) {

                    var inSet = HashSet<String>(stringHashSet)
                    if (favourite.isChecked) {
                        inSet.add(movieId.toString())
                        preferences?.edit()?.putStringSet("favorites", inSet)!!.apply()
                        movie?.isFavorite = true
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("unique_key", movie)
                        Log.d("sa", stringHashSet?.size.toString())
                        db.insertData(movie!!)

                    } else {
                        movie?.isFavorite = false
                        inSet.remove(movieId.toString())
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("unique_key", movie)
                        preferences?.edit()?.putStringSet("favorites", inSet)!!.apply()
                        db.deleteData(movieId!!)
                    }

                }

            }



        //eger film ana sayfa ekranindan cekiliyorsa (favoriler sayfasindan cekilmiyorsa) film detaylari api uzerinden saglanir.
        //eger film favori sayfasindan cekiliyorsa  film detaylari secilen filmin database deki bilgileri uzerinden saglanir

        if (arguments?.getBoolean("isFavoriteFragment", false) == false) {
            detailViewModel.getMovieDetail(arguments?.getInt("id")!!)
            observe()
        } else if (arguments?.getBoolean("isFavoriteFragment", false) == true) {

            var moviex = arguments?.getSerializable("movie") as Result
            moviex.let {
                movie = it
                movieId = it.id
                movieDetail.text = it.overview
                favourite.isChecked = true
                movieName.text = it.original_title
                movieDate.append(it.release_date)
                movieVote.append(it.vote_average.toString())
                Glide.with(movieImage.context).load("https://image.tmdb.org/t/p/w500" + it.backdrop_path)

                        .apply(RequestOptions().placeholder(R.drawable.nophoto).error(R.drawable.nophoto))
                        .into(movieImage)
            }

        }


    }

    //film detaylari api uzerinden cekildiginde detay sayfasi uzerine aktarilir
    //film detaylari api uzerinden cekilirken bir hata ile karsilasilirsa kullaniciya hata mesaji sunulur.

    fun observe() {

        detailViewModel.showLiveData.observe(requireActivity()) {

//            for(i in it.credits?.cast!!){
//
//                Log.d("sa",i.name.toString())
//                characters.append(" ${i.name}, ")
//            }


            var sonraDuzelt = Result(it.original_title, it.overview, it.release_date, it.vote_average, it.backdrop_path, it.id, it.isFavorite)
            movie = sonraDuzelt
            movieId = it.id
            movieDetail.text = it.overview

            movieName.text = it.original_title
            movieDate.text="release date: "
            movieVote.text="vote average: "
            movieDate.append(it.release_date)
            movieVote.append(it.vote_average.toString())
            Glide.with(movieImage.context).load("https://image.tmdb.org/t/p/w500" + it.backdrop_path)
                    .apply(RequestOptions().placeholder(R.drawable.nophoto).error(R.drawable.nophoto))
                    .into(movieImage)

            if (stringHashSet!!.contains(movieId.toString())) {
                favourite.isChecked = true

            }

            actorsAdapter.apply {
                actors = it.credits!!.cast as ArrayList<MovieActor>
                notifyDataSetChanged()
            }


        }
        detailViewModel.showError.observe(requireActivity()) {

            if (it == true) {
                if (context != null)
                    Toast.makeText(context, "islem basarisiz", Toast.LENGTH_SHORT).show()
            }


        }


    }

    override fun onItemClick(actor: MovieActor) {

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {

            val bundle = Bundle()
            actor.id?.let { bundle.putInt("id", it) }
            navController.navigate(R.id.action_detailFragment_to_actorFragment, bundle)

        }


    }

}