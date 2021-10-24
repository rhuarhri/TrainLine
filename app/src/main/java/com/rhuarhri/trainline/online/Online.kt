package com.rhuarhri.trainline.online

import com.rhuarhri.trainline.online.time_table_data.TimeTable
import com.rhuarhri.trainline.online.train_service_data.Service
import com.rhuarhri.trainline.online.train_station_location_data.TrainStation
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Online {

    private val BASE = "http://transportapi.com/v3/uk/"

    private fun setupRetrofit(url: String) : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
    }

    suspend fun getTimeTable(stationName: String = "SHF", date : String = "", time : String = "") : TimeTable? {
        val url = "" + BASE + "train/station/" + stationName + "/" + date + "/" + time + "/"
        return try {
            val retrofitInterface = setupRetrofit(url).create(OnlineInterface::class.java)
            retrofitInterface.getTimeTable().await()
        } catch (e : Exception) {
            null
        }
    }

    suspend fun getServiceInfo(trainId: String, date : String) : Service? {
        if (trainId.isBlank() && date.isBlank()) {
            return null
        }

        val url = "$BASE/train/service/train_uid:$trainId/$date/"
        return try {
            val retrofitInterface = setupRetrofit(url).create(OnlineInterface::class.java)
            retrofitInterface.getServiceInfo().await()
        } catch (e : Exception) {
            null
        }
    }

    suspend fun getStation() : TrainStation? {
        return try {
        val retrofitInterface = setupRetrofit(BASE).create(OnlineInterface::class.java)

        retrofitInterface.getPlace().await()
        } catch (e : Exception) {
            null
        }
    }

    companion object {
        /*Why here
          This is because the code below is linked to the querying of the REST full api
          which is handled in the online class.
        */

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

    @GET("timetable.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6")
    fun getServiceInfo() : Call<Service>

    @GET("places.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&type=train_station")
    fun getPlace() : Call<TrainStation>
}