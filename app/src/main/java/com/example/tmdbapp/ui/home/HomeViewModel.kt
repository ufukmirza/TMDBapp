package com.example.tmdbapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.util.ServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var showLiveData=MutableLiveData<ArrayList<Result>>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getPopularMovies(page:Int=1){

        viewModelScope.launch {

                try {
                    val result = ServiceManager.request.getPopularSeries(page=page)
                    val showList = arrayListOf<Result>()
                    for (showResult in result.results) {
                        showList.add(showResult)
                    }
                    showLiveData.postValue(showList)
                    Log.d("sa", showList[0].original_title!!)
                } catch (e: Exception) {
                    Log.e("hata", "service call error", e)
                }
            }


    }


}