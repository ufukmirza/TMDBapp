package com.example.tmdbapp.ui.favorites

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tmdbapp.model.DBHelper
import com.example.tmdbapp.model.Result

class FavoritesViewModel : ViewModel() {

    var favoriteMovies = MutableLiveData<ArrayList<Result>>()
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text


    fun getFavorites(db: DBHelper) {

        var movies = ArrayList<Result>()
        movies = db.readData()
        favoriteMovies.postValue(movies)

    }

}