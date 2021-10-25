package com.rhuarhri.trainline.data


import java.time.LocalDateTime
import java.time.ZoneOffset

class DateTime() {

    //this is the only reason that the app has to have a sdk have greater than 25
    private var dateTime = LocalDateTime.now()

    fun fromDate(year : Int, month : Int, day : Int, hour : Int, minutes : Int) {
        dateTime = LocalDateTime.of(year, month, day, hour, minutes)
    }

    fun fromLong(date : Long) {
        dateTime = LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
    }

    fun getDateString() : String {
        /*
            This is because the date format it YYYY-MM-DD
             */

        if (dateTime.dayOfMonth == 0 || dateTime.month.value == 0 || dateTime.year == 0) {
            return ""
        }

        val monthText = if (dateTime.month.value < 10) {
            "0${dateTime.month.value}"
        } else {
            "${dateTime.month.value}"
        }

        val dayText = if (dateTime.dayOfMonth < 10) {
            "0${dateTime.dayOfMonth}"
        } else {
            "${dateTime.dayOfMonth}"
        }

        return "${dateTime.year}-$monthText-$dayText"
    }

    fun getTimeString() : String {
        val hourText = if (dateTime.hour < 10) {
            "0${dateTime.hour}"
        } else {
            "${dateTime.hour}"
        }

        val minutesText = if (dateTime.minute < 10) {
            "0${dateTime.minute}"
        } else {
            "${dateTime.minute}"
        }
        return "$hourText:$minutesText"
    }

    fun getDateTime() : LocalDateTime{
        return dateTime
    }

    fun toLong() : Long {
        return dateTime.toEpochSecond(ZoneOffset.UTC)
    }

    /*
    The min and max range
     *
    fun getMinRange() : Long {
        return dateTime.toEpochSecond(ZoneOffset.UTC)
    }

    fun getMaxRange() : Long {
        val max = dateTime
        return max.plusDays(1).toEpochSecond(ZoneOffset.UTC)
    }*/
}