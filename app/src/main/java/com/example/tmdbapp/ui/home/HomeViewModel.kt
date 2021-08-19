package com.example.tmdbapp.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.util.ServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query

class HomeViewModel : ViewModel() {

    var showLiveData=MutableLiveData<ArrayList<Result>>()
    var showError=MutableLiveData<Boolean>().apply {
        false
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    var showPage=MutableLiveData<Int>().apply {
        value=1
    }

    val text: LiveData<String> = _text




    fun getPopularMovies(page:Int=1){

        viewModelScope.launch {

                try {
                    val result = ServiceManager.request.getPopularSeries(page=showPage.value!!)
                    val showList = arrayListOf<Result>()
                    for (showResult in result.results) {
                        showList.add(showResult)
                    }
                    showError.postValue(false)
                    showLiveData.postValue(showList)
                    Log.e("hata", "girmemesi lazim")
                    Log.d("sa", showList[0].original_title!!)
                } catch (e: Exception) {
                    showPage.value= showPage.value?.minus(1)
                    showError.postValue(true)
                    Log.e("hata", "service call error", e)
                }
            }


    }

    fun getSearchMovies(page:Int=1,query: String){

        viewModelScope.launch {

            try {
                val result = ServiceManager.request.getSearchSeries(page=showPage.value!!,query= query)
                val showList = arrayListOf<Result>()
                for (showResult in result.results) {
                    showList.add(showResult)
                }
                showError.postValue(false)
                showLiveData.postValue(showList)
                Log.e("hata", "girmemesi lazim")
                Log.d("sa", showList[0].original_title!!)
            } catch (e: Exception) {
                showPage.value= showPage.value?.minus(1)
                showError.postValue(true)
                Log.e("hata", "service call error", e)

            }
        }


    }




}