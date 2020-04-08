/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter.network

import ca.worthconsulting.omercounter.sunset.Results

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.sunrise-sunset.org"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * Retrofit interface to the Sunrise-sunset API
 */
interface SunApiService {

    /**
     * gets the sunset bu utc for location/date
     * @param lat {Double} - Lattitude (Negative values south)
     * @param Log {Double} - Longitude (Negative values west)
     * @param date {String} - Date
     * @param formatted {int} - must be 0
     */
    @GET("/json")
    suspend fun getSunsetByDate(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("date") date: String,
        @Query("formatted") formatted: Int = 0
    ) : Response<Results>
}

/**
 * API connection
 */
object SunApi {
    val retrofitService: SunApiService by lazy { retrofit.create(
        SunApiService::class.java) }
}