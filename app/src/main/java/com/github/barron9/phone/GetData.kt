package com.github.barron9.phone

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface GetData {

    @GET("/brngdtl")
    fun get(@Query("g") gsmnumber: String): Observable<JsonObject>
}