package com.example.tmdbapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MovieActor(@SerializedName("name") var name:String?,
                  @SerializedName("character") var character:String?,
                  @SerializedName("profile_path") var profile_path:String?,
                      @SerializedName("id") var id:Int?,

) : Serializable