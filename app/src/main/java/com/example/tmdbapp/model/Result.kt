package com.example.tmdbapp.model

import com.google.gson.annotations.SerializedName

data class Result(@SerializedName("original_title")val original_title:String?,
                  @SerializedName("overview")val overview:String?,
                  @SerializedName("release_date")val release_date:String?,
                  @SerializedName("vote_average")val vote_average:Double?,
                  @SerializedName("backdrop_path")val backdrop_path:String?,
                  )