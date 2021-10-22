package com.rhuarhri.trainline.time_table_widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhuarhri.trainline.online.Online
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimeTableWidget {

    @Composable
    fun widget(state: TimeTableWidgetState) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(items = state.timeTable) { item ->
                timeTableItem(platform = item.platform, departAt = item.departAt,
                    start = item.start, destination = item.destination)
            }
        }
    }

    @Composable
    private fun timeTableItem(platform: String, departAt: String, start: String, destination: String) {

        Column(modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()) {
            Row(
                Modifier
                    .weight(2f)
                    .fillMaxWidth(), Arrangement.SpaceAround) {
                Text(text = platform, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = departAt, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), Arrangement.End) {
                Text(start, modifier = Modifier.padding(PaddingValues(start = 10.dp, end = 10.dp)))
                Text(destination, modifier = Modifier.padding(PaddingValues(start = 10.dp, end = 10.dp)))
            }
        }

    }
}

class TimeTableWidgetState(val timeTable : List<TimeTableItem>) {

}

class TimetableWidgetViewModel : ViewModel() {

    var state by mutableStateOf(TimeTableWidgetState(listOf()))
    private val repo = TimeTableWidgetRepo()

    fun search(stationName: String, date : String, time : String) {

        viewModelScope.launch(Dispatchers.IO) {
            val timeTable = repo.searchForTimeTable(stationName, date, time)

            println("got result is ${timeTable.size}")
            /*
               I think the viewModelScope defaults to the main thread, and there
               is not any real benefit to running the coroutine on the IO thread
               however this work just fine
            */
            withContext(Dispatchers.Main) {
                val newState = TimeTableWidgetState(timeTable)
                state = newState
            }
        }
    }

}

data class TimeTableItem(val platform: String, val departAt: String, val start : String, val destination: String)

class TimeTableWidgetRepo {

    private val online = Online()

    suspend fun searchForTimeTable(stationName: String = "SHF", date : String = "", time : String = "")
    : List<TimeTableItem> {
        val found = online.getTimeTable(stationName, date, time)

        val timeTable = mutableListOf<TimeTableItem>()

        for (item in found) {
            var platform = item.platform
            val departAt = item.aimed_departure_time
            val start = item.origin_name
            val destination = item.destination_name

            /*
            I think because the places.json file / functionality of the transport api is still a
            working progress.
            See
            https://developer.transportapi.com/docs?raml=https://transportapi.com/v3/raml/transportapi.raml##uk_places_json
            maybe causing the platform variable to be null
            Why? because the app gets a list of train stations from the places.json file.
            The code of the train station is used to find the time table of the train station, which
            contains a null value for the platform.
            Another possibility is that a train station with a null value platform means that it has
            only one platform.
            either way if the platform value is null replace with NA
             */

            if (platform == null) {
                platform = "NA"
            }

            if (departAt != null && start != null && destination != null) {
                /*
                removing all null data and getting only the most useful data
                 */
                val timeTableItem = TimeTableItem(platform = platform, departAt = departAt, start = start, destination = destination)
                timeTable.add(timeTableItem)
            }
        }

        return timeTable
    }
}