package com.example.tmdbapp.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tmdbapp.R
import com.example.tmdbapp.model.MovieActor
import com.example.tmdbapp.model.Result
import com.example.tmdbapp.model.cast
import com.example.tmdbapp.ui.home.RecyclerViewClickInterface
import com.example.tmdbapp.ui.home.moviesAdapter

class CharactersAdapter : RecyclerView.Adapter<CharactersAdapter.MyViewHolder>() {

    var actors = ArrayList<MovieActor>()
    var clickInterface:ActorClickInterface?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val context = parent.context
        val view: View
        view = LayoutInflater.from(context).inflate(R.layout.actor_item, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.bind(actors.get(position))
        holder.itemView.setOnClickListener {

            clickInterface?.onItemClick(actors.get(position))


        }
    }


    override fun getItemCount() = actors?.size ?: 0


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val imageView: ImageView = itemView.findViewById(R.id.actorImage)
        private val textView: TextView = itemView.findViewById(R.id.actorName)
        private val textView2: TextView = itemView.findViewById(R.id.actorCharacters)


        fun bind(actor: MovieActor) {
            //imageDrawable)

            textView.text = actor.name
            textView2.text = actor.character

            Glide.with(imageView.context).load("https://image.tmdb.org/t/p/w500" + actor.profile_path)
                    .apply(RequestOptions().placeholder(R.drawable.nophoto).error(R.drawable.nophoto))
                    .into(imageView)

        }

    }

}
