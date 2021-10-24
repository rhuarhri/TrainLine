package com.rhuarhri.trainline.online.train_service_data

data class Service(
    val category: String?,
    val date: String?,
    val destination_name: String?,
    val headcode: String?,
    val mode: String?,
    val `operator`: String?,
    val operator_name: String?,
    val origin_name: String?,
    val request_time: String?,
    val service: String?,
    val stop_of_interest: Any?,
    val stops: List<Stop>?,
    val time_of_day: Any?,
    val toc: Toc?,
    val train_status: String?,
    val train_uid: String?
)