package com.rhuarhri.trainline.online.train_service_data

data class Stop(
    val aimed_arrival_date: Any?,
    val aimed_arrival_time: Any?,
    val aimed_departure_date: String?,
    val aimed_departure_time: String?,
    val aimed_pass_date: Any?,
    val aimed_pass_time: Any?,
    val platform: String?,
    val station_code: String?,
    val station_name: String?,
    val stop_type: String?,
    val tiploc_code: String?
)