package com.example.tmdbapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailedActor(@SerializedName("biography") var biography:String?,
                         @SerializedName("birthday") var birthday:String?,
                         @SerializedName("name") var name:String?,
                         @SerializedName("place_of_birth") var place_of_birth:String?,
                         @SerializedName("profile_path") var profile_path:String?,
) : Serializable