package com.example.tmdbapp.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.util.ServiceManager
import kotlinx.coroutines.launch

class DetailViewModel: ViewModel() {

    var showLiveData= MutableLiveData<Result>()

    fun getMovieDetail(id:Int){

        viewModelScope.launch {

            try {
                val result = ServiceManager.request.getMovieDetail(id)

                showLiveData.postValue(result)
                Log.d("sa", result.original_title!!)
            } catch (e: Exception) {
                Log.e("hata", "service call error", e)
            }
        }


    }


}
