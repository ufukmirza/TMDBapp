package com.example.tmdbapp.network

import com.example.tmdbapp.model.ApiResponse
import com.example.tmdbapp.model.DetailedActor
import com.example.tmdbapp.model.DetailedResult
import com.example.tmdbapp.model.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("discover/movie?")
    suspend fun getPopularSeries(@Query("api_key") api_key: String = "b23c2cdf7348ebdcb9093c3b6215c216",
                                 @Query("sort_by") sort_by: String = "popularity.desc",
                                 @Query("page") page: Int): ApiResponse

    @GET("search/movie?")
    suspend fun getSearchSeries(@Query("api_key") api_key: String = "b23c2cdf7348ebdcb9093c3b6215c216",
                                @Query("query") query: String,
                                @Query("page") page: Int): ApiResponse

    @GET("movie/{id}?")
    suspend fun getMovieDetail(
            @Path("id") id: Int?,
            @Query("api_key") api_key: String = "b23c2cdf7348ebdcb9093c3b6215c216",
            @Query("append_to_response")credits:String="credits"
    ):DetailedResult

    @GET("person/{id}?")
    suspend fun getActorDetail(
            @Path("id") id: Int?,
            @Query("api_key") api_key: String = "b23c2cdf7348ebdcb9093c3b6215c216",
    ):DetailedActor

}