package com.rhuarhri.trainline.time_table_widget

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import com.rhuarhri.trainline.ViewTrainTime
import com.rhuarhri.trainline.data.TimeTable
import com.rhuarhri.trainline.online.Online
import com.rhuarhri.trainline.online.time_table_data.All
import kotlinx.coroutines.launch

class TimeTableWidget {

    @Composable
    fun Widget(context : Context, viewModel : TimetableWidgetViewModel) {
        val timeTable by viewModel.timeTableSate.observeAsState(initial = listOf())
        LazyColumn(Modifier.fillMaxSize(),) {
            items(items = timeTable) { item ->
                TimeTableItem(context = context, platform = item.platform, departAt = item.departAt,
                    start = item.start, destination = item.destination, trainId = item.trainId, date = item.date)
            }
        }
    }

    @Composable
    private fun TimeTableItem(context : Context, platform: String, departAt: String,
                              start: String, destination: String, trainId: String, date: String) {

        Column(modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, ViewTrainTime::class.java)
                intent.putExtra("trainId", trainId)
                intent.putExtra("date", date)
                context.startActivity(intent)
            }) {
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

class TimeTableWidgetViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    /*
    This is a factory class. It is used to deal with the complexity of creating a view model.
    If the view model did not have to use context then this factory class would not exist.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimetableWidgetViewModel(context) as T
    }
}

class TimetableWidgetViewModel(context: Context) : ViewModel() {

    private val repo = TimeTableWidgetRepo(context)

    val timeTableSate = repo.timeTableListLiveData

    fun search(stationName: String, date : String, time : String) {

        viewModelScope.launch {
            repo.searchForTimeTable(stationName, date, time)
        }

        /*viewModelScope.launch(Dispatchers.IO) {
            val timeTable = repo.searchForTimeTable(stationName, date, time)

            println("got result is ${timeTable.size}")
            *//*
               I think the viewModelScope defaults to the main thread, and there
               is not any real benefit to running the coroutine on the IO thread
               however this work just fine
            *//*
            withContext(Dispatchers.Main) {
                val newState = TimeTableWidgetState(timeTable)
                state = newState
            }
        }*/
    }

}



class TimeTableWidgetRepo(context: Context) {

    private val online = Online(context)

    val timeTableListLiveData : LiveData<List<TimeTable>> =
        Transformations.map(online.currentTimeTable) { found ->

            val timeTable = mutableListOf<TimeTable>()

            if (found != null) {
                val all = if (found.departures != null) {
                    if (found.departures.all != null) {
                        found.departures.all
                    } else {
                        listOf<All>()
                    }
                } else {
                    listOf<All>()
                }

                for (item in all) {
                    var platform = item.platform
                    val departAt = item.aimed_departure_time
                    val start = item.origin_name
                    val destination = item.destination_name

                    val trainId = item.train_uid ?: ""
                    val trainDate = found.date ?: ""

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
                        val timeTableItem = TimeTable(
                            platform = platform, departAt = departAt, start = start,
                            destination = destination, trainId = trainId, date = trainDate
                        )
                        timeTable.add(timeTableItem)
                    }
                }
            }

        timeTable
    }

    suspend fun searchForTimeTable(stationName: String = "SHF", date : String = "", time : String = "") {
        online.getTimeTable(stationName, date, time)
    }
}