package com.example.tmdbapp.ui.actor

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapp.R
import com.example.tmdbapp.ui.detail.DetailViewModel

class ActorFragment : Fragment(R.layout.fragment_actor) {

    lateinit var actorName: TextView
    lateinit var actorBiography: TextView
    lateinit var actorImage: ImageView
    lateinit var actorBirthday: TextView
    lateinit var actorPlaceOfBirth: TextView
    private val actorViewModel: ActorViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actorName=view.findViewById(R.id.textView3)
        actorImage=view.findViewById(R.id.imageView3)
        actorBiography=view.findViewById(R.id.textView5)
        actorBirthday=view.findViewById(R.id.textView6)
        actorPlaceOfBirth=view.findViewById(R.id.textView7)


   var id  = arguments?.getInt("id")
        if (id != null) {
            actorViewModel.getActorDetails(id)
        }
        observe()


    }


    fun observe(){

       actorViewModel.showLiveData.observe(requireActivity()){

           actorName.text=it.name
           actorBiography.append(it.biography)
           actorBirthday.append(it.birthday)
           actorPlaceOfBirth.append(it.place_of_birth)
           Glide.with(actorImage.context).load("https://image.tmdb.org/t/p/w500" + it.profile_path)
                   .apply(RequestOptions().placeholder(R.drawable.nophoto).error(R.drawable.nophoto))
                   .into(actorImage)



       }






    }

}