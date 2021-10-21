package com.rhuarhri.trainline.online.time_table_data

data class All(
    val aimed_arrival_time: Any?,
    val aimed_departure_time: String?,
    val aimed_pass_time: Any?,
    val category: String?,
    val destination_name: String?,
    val mode: String?,
    val `operator`: String?,
    val operator_name: String?,
    val origin_name: String?,
    val platform: String?,
    val service: String?,
    val service_timetable: ServiceTimetable?,
    val source: String?,
    val train_uid: String?
)