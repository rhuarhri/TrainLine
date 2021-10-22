package com.rhuarhri.trainline.online

import com.rhuarhri.trainline.online.time_table_data.All
import com.rhuarhri.trainline.online.time_table_data.TimeTable
import com.rhuarhri.trainline.online.train_station_location_data.TrainStation
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Online {

    private val BASE_URL = "http://transportapi.com/v3/uk/"

    private fun setupRetrofit(url: String) : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
    }

    suspend fun getTimeTable(stationName: String = "SHF", date : String, time : String) : List<All> {
        val url = BASE_URL + "train/station/" + stationName + "/" + date + "/" + time + "/"
        println("base url is $url")
        val retrofitInterface = setupRetrofit(url).create(OnlineInterface::class.java)
        val timeTable = retrofitInterface.getTimeTable().await()

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

    suspend fun getStation() : TrainStation {
        val retrofitInterface = setupRetrofit(BASE_URL).create(OnlineInterface::class.java)


        return retrofitInterface.getPlace().await()
    }

    companion object {
        fun convertTime(hours : Int, minutes : Int) : String {
            val hourText = if (hours < 10) {
                "0$hours"
            } else {
                "$hours"
            }

            val minutesText = if (minutes < 10) {
                "0$minutes"
            } else {
                "$minutes"
            }
            return "$hourText:$minutesText"
        }

        fun covertDate(year: Int, month : Int, day : Int) : String {
            /*
            This is because the date format it YYYY-MM-DD
             */

            if (day == 0 || month == 0 || year == 0) {
                return ""
            }

            val monthText = if (month < 10) {
                "0$month"
            } else {
                "$month"
            }

            val dayText = if (day < 10) {
                "0$day"
            } else {
                "$day"
            }

            return "$year-$monthText-$dayText"
        }
    }
}

interface OnlineInterface {

    @GET("timetable.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&train_status=passenger")
    fun getTimeTable() : Call<TimeTable>

    @GET("places.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&type=train_station")
    fun getPlace() : Call<TrainStation>
}