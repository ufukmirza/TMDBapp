package com.example.tmdbapp.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapp.R
import com.example.tmdbapp.model.PaginationScrollListener
import com.example.tmdbapp.model.Result


class HomeFragment : Fragment(R.layout.fragment_home) ,RecyclerViewClickInterface {

    private lateinit var homeViewModel: HomeViewModel
    public val viewModel: HomeViewModel by viewModels()
    public var movieAdapter = moviesAdapter()
    var isFirstStart = true
    var isItSearch = false
    var page = 2
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var searchMovie = ""
    var movieList = ArrayList<Result>()
    lateinit var recyclerView :RecyclerView
    val isSwitched: Boolean = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //    textView.text = it
        })


        var button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            searchMovie = view.findViewById<EditText>(R.id.searchMovie).text.toString()
            isFirstStart = true
            isItSearch = true
            page = 2
            viewModel.getSearchMovies(query = searchMovie)

        }
         recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = movieAdapter
        movieAdapter.recyclerViewClickInterface=this
        //  recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        movieAdapter.isLinearLayout=false
        controlScroll()
        viewModel.getPopularMovies()
        observeViewModel()



    }

    fun controlScroll(){

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
                if (isItSearch == false)
                    getMoreItems()


                if (isItSearch == true)
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
        viewModel.getSearchMovies(page = page, query = searchMovie)
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


override  fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

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
                controlScroll()
                true
            }
            R.id.changeListLayout -> {
                movieAdapter.isLinearLayout = true
                recyclerView.layoutManager = LinearLayoutManager(activity)
                // movieAdapter.onCreateViewHolder(LinearLayoutManager(activity) as ViewGroup,1)
                movieAdapter.notifyDataSetChanged()
                controlScroll()
                true
            }

            else -> true
        }

    }

    override fun onItemClick(movie: Result) {

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {
            val bundle = Bundle()
        //    bundle.putString("link", "http://yourlink.com/policy")
            bundle.putSerializable("Movie", movie) // Serializable Object
            navController.navigate(R.id.action_navigation_home_to_detailFragment,bundle)


        }
    }

}