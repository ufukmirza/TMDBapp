package com.example.tmdbapp.ui.actor


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.model.DetailedActor
import com.example.tmdbapp.util.ServiceManager
import kotlinx.coroutines.launch

class ActorViewModel : ViewModel(){
    var showLiveData= MutableLiveData<DetailedActor>()
    fun getActorDetails(id:Int){

        viewModelScope.launch {

            try {
                val result = ServiceManager.request.getActorDetail(id)
                showLiveData.postValue(result)
            } catch (e: Exception) {

                Log.e("hata", "service call error", e)
            }
        }


    }


}