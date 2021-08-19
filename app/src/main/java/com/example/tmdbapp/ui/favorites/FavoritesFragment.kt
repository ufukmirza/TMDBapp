package com.example.tmdbapp.ui.favorites

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapp.R
import com.example.tmdbapp.model.DBHelper
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.ui.home.RecyclerViewClickInterface
import com.example.tmdbapp.ui.home.moviesAdapter

class FavoritesFragment : Fragment(R.layout.fragment_favorites),RecyclerViewClickInterface {

    val viewModel: FavoritesViewModel by viewModels()
    val db by lazy { DBHelper(requireContext())  }
    lateinit var recyclerView: RecyclerView
    var movieAdapter = moviesAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        recyclerView=view.findViewById(R.id.recyclerView)
        recyclerView.adapter=movieAdapter
        movieAdapter.recyclerViewClickInterface = this
        recyclerView.layoutManager =   if(movieAdapter.isLinearLayout==false) GridLayoutManager(activity, 2) else  LinearLayoutManager(activity)
        viewModel.getFavorites(db)
        observe()


    }

    fun observe(){


        viewModel.favoriteMovies.observe(requireActivity()){

            movieAdapter.movies=it
            movieAdapter.notifyDataSetChanged()


        }

    }

    override fun onItemClick(movie: Result) {

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {

            //first solution is sending the movie to details page
            val bundle = Bundle()
            bundle.putBoolean("isFavoriteFragment", true)
            bundle.putSerializable("movie",movie)
            navController.navigate(R.id.action_navigation_notifications_to_detailFragment, bundle)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.top_bar_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.changeGridLayout -> {
                movieAdapter.isLinearLayout = false
                recyclerView.layoutManager = GridLayoutManager(activity, 2)
                //  movieAdapter.onCreateViewHolder(GridLayoutManager(activity, 2) as ViewGroup,2)
                movieAdapter.notifyDataSetChanged()
                true
            }
            R.id.changeListLayout -> {
                movieAdapter.isLinearLayout = true
                recyclerView.layoutManager = LinearLayoutManager(activity)
                // movieAdapter.onCreateViewHolder(LinearLayoutManager(activity) as ViewGroup,1)
                movieAdapter.notifyDataSetChanged()
                true
            }

            else -> true
        }

    }

}