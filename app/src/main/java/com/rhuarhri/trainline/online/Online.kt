package com.rhuarhri.trainline.online

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.rhuarhri.trainline.online.time_table_data.TimeTable
import com.rhuarhri.trainline.online.train_service_data.Service
import com.rhuarhri.trainline.online.train_station_location_data.TrainStation
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.util.concurrent.TimeUnit

class Online(private val context: Context) {
    /*
    This class only needs context for the basic cache solution
     */

    private val BASE = "http://transportapi.com/v3/uk/"

    private fun setupRetrofit() : Retrofit {
        return Retrofit.Builder()
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE)
            .build()
    }

    suspend fun getTimeTable(stationName: String = "SHF", date : String = "", time : String = "") : TimeTable? {
        //val url = "" + BASE + "train/station/" + stationName + "/" + date + "/" + time + "/"
        return try {
            val retrofitInterface = setupRetrofit().create(OnlineInterface::class.java)
            val response = retrofitInterface.getTimeTable(stationName,date,time).awaitResponse()
            if (response.isSuccessful == true) {
                response.body()
            } else {
                null
            }
        } catch (e : Exception) {
            null
        }
    }

    suspend fun getServiceInfo(trainId: String, date : String) : Service? {
        if (trainId.isBlank() && date.isBlank()) {
            return null
        }

        //val url = "$BASE/train/service/train_uid:$trainId/$date/"
        return try {
            val retrofitInterface = setupRetrofit().create(OnlineInterface::class.java)
            val response = retrofitInterface.getServiceInfo(trainId, date).awaitResponse()
            if (response.isSuccessful == true) {
                response.body()
            } else {
                null
            }
        } catch (e : Exception) {
            null
        }
    }

    suspend fun getStation() : TrainStation? {
        return try {
        val retrofitInterface = setupRetrofit().create(OnlineInterface::class.java)
            val response = retrofitInterface.getPlace().awaitResponse()
            if (response.isSuccessful == true) {
                response.body()
            } else {
                null
            }
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

    private fun getHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .cache(getCache())
            .addNetworkInterceptor(getNetworkInterceptor())
            .addInterceptor(getOfflineInterceptor())
            .build()
    }

    private fun getCache() : Cache {
        val cacheSize : Long = (5 * 1024 * 1024).toLong() //5 MB
        return Cache(File(context.cacheDir, "appCache"), cacheSize)
    }

    private fun getNetworkInterceptor() : Interceptor {
        // this ensures that if the user decides to refresh the app. then the app
        // will have the option to get the data from the cache instead of the network if the
        // cache is new enough
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request())

                val cacheControl = CacheControl.Builder()
                    .maxAge(1, TimeUnit.MINUTES)
                    .build()

                return response.newBuilder()
                    .removeHeader("Pragma") //this could tell the request not to use caching in some case
                    .removeHeader("Cache-Control") //this would be some default cache control
                    .header("Cache-Control", cacheControl.toString())
                    .build()
            }

        }
    }

    private fun getOfflineInterceptor() : Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request())

                if (checkNetworkConnection() == false) {
                    /*this runs every time a request for data is made
                    * but since that this interceptor is only interesting in getting information
                    * from the cache it needs to first check that there is a internet connection
                    * and from there know if the data should come from the cache*/
                    val cacheControl = CacheControl.Builder()
                        .maxAge(1, TimeUnit.DAYS)
                        .build()

                    return response.newBuilder()
                        .removeHeader("Pragma") //this could tell the request not to use caching in some case
                        .removeHeader("Cache-Control") //this would be some default cache control
                        .header("Cache-Control", cacheControl.toString())
                        .build()
                } else {
                    return response
                }
            }

        }
    }

    private fun checkNetworkConnection() : Boolean {
        val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capability = cm.getNetworkCapabilities(cm.activeNetwork)

        val connected = if (capability != null) {
            capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            false
        }

        return connected
    }
}

interface OnlineInterface {

    @GET("train/station/{name}/{date}/{time}/timetable.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&train_status=passenger")
    fun getTimeTable(@Path(value = "name") name : String,
                     @Path(value = "date") date : String,
                     @Path(value = "time") time : String) : Call<TimeTable>

    @GET("train/service/train_uid:{id}/{date}/timetable.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6")
    fun getServiceInfo(@Path(value = "id") trainId: String, @Path(value = "date") date : String) : Call<Service>

    @GET("places.json?app_id=c9ef48df&app_key=f7dc9efc73b6cd485d10835de81c6ed6&type=train_station")
    fun getPlace() : Call<TrainStation>
}