package com.example.tmdbapp.ui.home

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbapp.MainActivity
import com.example.tmdbapp.R
import com.example.tmdbapp.model.PaginationScrollListener
import com.example.tmdbapp.model.Result
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home), RecyclerViewClickInterface {

    private lateinit var homeViewModel: HomeViewModel
     val viewModel: HomeViewModel by viewModels()
     var movieAdapter = moviesAdapter()
    var isFirstStart = true
    var isItSearch = false
    var page = 1
    var live_page = MutableLiveData<Int>()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var searchMovie = ""
    var movieList = ArrayList<Result>()
    lateinit var recyclerView: RecyclerView
    val isSwitched: Boolean = true


    internal lateinit var callback: OnHeadlineSelectedListener

    fun setOnHeadlineSelectedListener(callback: OnHeadlineSelectedListener) {
        this.callback = callback
    }



    interface OnHeadlineSelectedListener {
        fun onArticleSelected()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {

        })






        var button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            searchMovie = view.findViewById<EditText>(R.id.searchMovie).text.toString()
            isFirstStart = true
            isItSearch = true
            page = 2
            viewModel.showPage.value = 1
            live_page.postValue(page)
            viewModel.getSearchMovies(query = searchMovie)
            viewModel.showPage.value = 2

        }
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = movieAdapter
        movieAdapter.recyclerViewClickInterface = this
        movieAdapter.context = context

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Result>("unique_key")?.observe(
                viewLifecycleOwner) { result ->
            Log.d("sa", result.id.toString())
            val movie: Result? = movieAdapter.movies.find { it.id == result.id }

            var index = movieAdapter.movies.indexOf(movie)
            if (index != -1)
                movieAdapter.movies.set(index, result)
            movieAdapter.notifyDataSetChanged()



        }


        if (live_page.value != null)
            page = live_page.value!!

        if (!movieAdapter.isLinearLayout)
            recyclerView.layoutManager = GridLayoutManager(activity, 2)
        else
            recyclerView.layoutManager = LinearLayoutManager(activity)

        controlScroll()
        if (page < 2) {
            viewModel.showPage.value = 1
            viewModel.getPopularMovies()
            observeViewModel()
            page++
            live_page.postValue(page)
            viewModel.showPage.value = viewModel.showPage.value?.plus(1)
        }

    }

    fun controlScroll() {

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

        isLoading = false
        viewModel.getPopularMovies(page)
        page++
        live_page.postValue(page)
        viewModel.showPage.value = viewModel.showPage.value?.plus(1)

    }

    fun getMoreSearchItems() {
        Log.d("sayi",viewModel.showPage.value.toString())
        isLoading = false
        viewModel.getSearchMovies(page = page, query = searchMovie)
        page++
        live_page.postValue(page)
        viewModel.showPage.value = viewModel.showPage.value?.plus(1)

    }

    fun observeViewModel() {


        viewModel.showLiveData.observe(requireActivity()) {
            val preferences = context?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
            var stringHashSet = preferences?.getStringSet("favorites", HashSet<String>())
            var inSet = HashSet<String>(stringHashSet)
            it.forEach {

                if (inSet.contains(it.id.toString()))
                    it.isFavorite = true

            }

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


        viewModel.showError.observe(requireActivity()) {

            if (it == true) {
                page--
                Log.e("hata", page.toString())
                if (context != null)
                    Toast.makeText(context, "islem basarisiz", Toast.LENGTH_SHORT).show()
            }


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

            //first solution is sending the movie to details page
            val bundle = Bundle()
            bundle.putInt("id", movie.id!!)
            //  bundle.putSerializable("movie",viewModel.showLiveData.value)
            navController.navigate(R.id.action_navigation_home_to_detailFragment, bundle)


        }
    }


}