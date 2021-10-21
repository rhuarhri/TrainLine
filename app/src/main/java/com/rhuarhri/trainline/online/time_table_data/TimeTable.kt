package com.rhuarhri.trainline.online.time_table_data

import com.rhuarhri.trainline.online.time_table_data.Departures

data class TimeTable(
    val date: String?,
    val departures: Departures?,
    val request_time: String?,
    val station_code: String?,
    val station_name: String?,
    val time_of_day: String?
)