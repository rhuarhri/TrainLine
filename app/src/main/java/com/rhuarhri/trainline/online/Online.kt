package com.rhuarhri.trainline.online

import com.rhuarhri.trainline.online.time_table_data.All
import com.rhuarhri.trainline.online.time_table_data.TimeTable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Online {

    private val BASE_URL = "http://transportapi.com/v3/uk/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    /*companion object {
        val BASE_URL = "http://transportapi.com/v3/uk/"

        fun create() : OnlineInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(OnlineInterface::class.java)

        }
    }*/



    /*suspend fun test() {
        var result = Online.create().get().await()
        println("retrofit result is ${result.departures.all.size}")
        for (name in result.departures.all)
        println("names ${name.operator_name}")
    }*/

    suspend fun getTimeTable() : List<All> {
        val retroInterface = retrofit.create(OnlineInterface::class.java)
        val timeTable = retroInterface.get().await()

        if (timeTable.departures != null) {
            if (timeTable.departures.all != null) {
                return timeTable.departures.all
            } else {
                return listOf<All>()
            }
        } else {
            return listOf<All>()
        }
    }

}

interface OnlineInterface {

    @GET("train/station/SHF///timetable.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&train_status=passenger")
    fun get() : Call<TimeTable>

}