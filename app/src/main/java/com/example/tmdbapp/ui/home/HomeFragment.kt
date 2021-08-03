package com.example.tmdbapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapp.R
import com.example.tmdbapp.model.PaginationScrollListener
import com.example.tmdbapp.model.Result

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: HomeViewModel
    public val viewModel: HomeViewModel by viewModels()
    public var movieAdapter = moviesAdapter()
    var isFirstStart = true
    var isItSearch=false
    var page = 2
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var searchMovie=""
    var movieList = ArrayList<Result>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //    textView.text = it
        })


        var button=view.findViewById<Button>(R.id.button)
        button.setOnClickListener{
            searchMovie=view.findViewById<EditText>(R.id.searchMovie).text.toString()
            isFirstStart=true
            isItSearch=true
            page=2
            viewModel.getSearchMovies(query = searchMovie)

        }
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getPopularMovies()
        observeViewModel()


        recyclerView.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {


            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {

                isLoading = true
                //you have to call loadmore items to get more data
                if(isItSearch==false)
                getMoreItems()


                if(isItSearch==true)
                    getMoreSearchItems()
            }
        })


    }

    fun getMoreItems() {
        //after fetching your data assuming you have fetched list in your
        // recyclerview adapter assuming your recyclerview adapter is
        //rvAdapter
        //  after getting your data you have to assign false to isLoading
        isLoading = false
        viewModel.getPopularMovies(page)
        page++

    }

    fun getMoreSearchItems() {

        isLoading = false
        viewModel.getSearchMovies(page=page,query = searchMovie)
        page++

    }

    fun observeViewModel() {


        viewModel.showLiveData.observe(requireActivity()) {

            if (isFirstStart == true) {
                isFirstStart = false
                movieAdapter.apply {
                    movieList = it
                    movies = it
                    notifyDataSetChanged()
                }
            } else {

                movieAdapter.addData(
                        it
                )

            }
        }


    }


}