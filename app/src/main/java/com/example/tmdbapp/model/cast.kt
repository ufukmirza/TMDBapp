package com.example.tmdbapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class cast(@SerializedName("cast") var cast:List<MovieActor>?,
) : Serializable