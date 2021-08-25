package com.example.tmdbapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DetailedResult(@SerializedName("original_title") var original_title:String?,
                  @SerializedName("overview") var overview:String?,
                  @SerializedName("release_date") var release_date:String?,
                  @SerializedName("vote_average") var vote_average:Double?,
                  @SerializedName("backdrop_path") var backdrop_path:String?,
                  @SerializedName("id") var id:Int?,
                  @SerializedName("credits") var credits:cast?,
                  var isFavorite:Boolean? =false,
) : Serializable