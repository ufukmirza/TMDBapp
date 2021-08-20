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


    //değiskenlerin oluşturulmasi

    private lateinit var homeViewModel: HomeViewModel
    val viewModel: HomeViewModel by viewModels()
    var movieAdapter = moviesAdapter()
    var isFirstStart = true
    var isItSearch = false
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var searchMovie = ""
    var movieList = ArrayList<Result>()
    lateinit var recyclerView: RecyclerView
    val isSwitched: Boolean = true


    //activity ile fragmentin haberlesmesi icin interface eklenmesi

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


//arama butonuna basildiginda klavye otomatik kapanir ve viewModelde aranilan filmle ilgili bilgileri çekmek için fonksiyon cagirilir

        var button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            searchMovie = view.findViewById<EditText>(R.id.searchMovie).text.toString()
            isFirstStart = true
            isItSearch = true
            viewModel.showPage.value = 1
            viewModel.getSearchMovies(query = searchMovie)
            viewModel.showPage.value = 2

        }

        //olusturulan recyclerview  fragment üzerindeki id ile eslenir ve adapterler baglanir.

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = movieAdapter
        movieAdapter.recyclerViewClickInterface = this
        movieAdapter.context = context


        //detay sayfasindan geri tusuna basildigi takdirde eger detay sayfasında film favoriye alinir ve ya cikarilirsa anasayfa üzerinde
        //filmin favori durumu guncellenir.

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Result>("unique_key")?.observe(
                viewLifecycleOwner) { result ->
            Log.d("sa", result.id.toString())
            val movie: Result? = movieAdapter.movies.find { it.id == result.id }

            var index = movieAdapter.movies.indexOf(movie)
            if (index != -1)
                movieAdapter.movies.set(index, result)
            movieAdapter.notifyDataSetChanged()


        }

        //eger ana ekran linear ve ya grid yapiya gecirilirse telefon donduruldugunde aynni yapida kalmasi saglanir

        if (!movieAdapter.isLinearLayout)
            recyclerView.layoutManager = GridLayoutManager(activity, 2)
        else
            recyclerView.layoutManager = LinearLayoutManager(activity)

        //recycler view üzerinde scroll kontrol fonksiyonunun olusturulmasi
        controlScroll()

        //eger uygulamada kullanıcı arama sayfasında degilse ve anasayfaya ilk defa eristiyse populer filmlerin ilk sayfası cekilir.
        if (page < 2) {
            viewModel.showPage.value = 1
            viewModel.getPopularMovies()
            observeViewModel()
            page++
            viewModel.showPage.value = 2
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
                // ana sayfa ekranında asagi kaydirirken eger kullanici arama sonuclarini gormuyorsa populer filmler cekilmeye devam eder.
                if (isItSearch == false)
                    getMoreItems()

// ana sayfa ekranında asagi kaydirirken eger kullanici arama sonuclarini goruyorsa aranan filmler cekilmeye devam eder.
                if (isItSearch == true)
                    getMoreSearchItems()
            }
        })

    }

    //populer filmlerin api uzerinden siradaki sayfadan cekilmesi

    fun getMoreItems() {

        isLoading = false
        viewModel.getPopularMovies(page)
        page++
        viewModel.showPage.value = viewModel.showPage.value?.plus(1)

    }

    //aranan filmlerin api uzerinden siradaki sayfadan cekilmesi

    fun getMoreSearchItems() {
        Log.d("sayi", viewModel.showPage.value.toString())
        isLoading = false
        viewModel.getSearchMovies(page = page, query = searchMovie)
        page++
        viewModel.showPage.value = viewModel.showPage.value?.plus(1)

    }

    //viewmodel uzerinde data degisikligi olursa data cekilir ve  bu fonkiyon calisir duruma gelir.

    fun observeViewModel() {


        //film datalari geldigi takdirde ilk olarak sharedPreferences uzerindeki
        //favori film idleri cekilir ve cekilen film id leri ile favori idler karsilastirilir.
        //eger ayni id ye sahipler ise film favori değiskeni true yapilarak filmin favori isaretlemesi yapilmasi saglanir.
        //eger filmler cekildiginde anasayfa ilk defa cagirildiysa filmler anasayfadaki filmler degisikligine aktarilir
        //rger filmler cekildiginde anasayfa ilk defa cagirilmadiysa filmler anasayfadaki filmler degisikligine eklenir.

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

        //film cekilmesi sirasinda herhangi bir hata gerceklestiyse kullaniciya bir mesaj yoluyla bildirilir.


        viewModel.showError.observe(requireActivity()) {

            if (it == true) {
                page--
                Log.e("hata", page.toString())
                if (context != null)
                    Toast.makeText(context, "islem basarisiz", Toast.LENGTH_SHORT).show()
            }


        }


    }


    //kullanicinin grid ve linear layout gecislerinin saglanabilmesi icin menu olusturulur.

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.top_bar_menu, menu)

    }


    //kullanici menude grid layouta basarsa gride linear layouta basarsa linear yapiya gecer.

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
    //ana sayfa uzerinde filme basildigi takdirde filmin id si detay sayfasına gonderilerek
    // servis endpointaracılığı ile film detayları API üzerinden çekilir ve detay sayfasinda gosterilir.

    override fun onItemClick(movie: Result) {

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {

            val bundle = Bundle()
            bundle.putInt("id", movie.id!!)
            navController.navigate(R.id.action_navigation_home_to_detailFragment, bundle)


        }
    }


}