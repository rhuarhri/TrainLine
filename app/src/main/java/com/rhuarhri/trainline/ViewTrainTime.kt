package com.rhuarhri.trainline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.rhuarhri.trainline.data.ServiceInfo
import com.rhuarhri.trainline.data.Stop
import com.rhuarhri.trainline.ui.theme.TrainLineTheme
import com.rhuarhri.trainline.veiw_train_time_screen.ViewTrainTimeViewModel
import com.rhuarhri.trainline.veiw_train_time_screen.ViewTrainTimeViewModelFactory

class ViewTrainTime : ComponentActivity() {

    private lateinit var viewTrainTimeViewModel: ViewTrainTimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trainId = intent.getStringExtra("trainId")
        val date = intent.getStringExtra("date")

        viewTrainTimeViewModel = ViewModelProvider(this, ViewTrainTimeViewModelFactory(this.applicationContext))
            .get(ViewTrainTimeViewModel::class.java)
        if (trainId != null && date != null) {
            viewTrainTimeViewModel.setup(trainId, date)
        }

        setContent {
            TrainLineTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Train Time Table",
                                )
                            },
                        )
                    },
                    content = {
                        Column {
                            val serviceInfo by viewTrainTimeViewModel.serviceInfoState.observeAsState(
                                initial = ServiceInfo("", "", "", listOf())
                            )
                            Time(serviceInfo.date)
                            Route(serviceInfo.start, serviceInfo.end)
                            Stops(stops = serviceInfo.stops)
                        }
                    },
                )
            }
        }
    }

    @Composable
    fun Time(date : String) {
        Row(
            Modifier
                .height(100.dp)
                .fillMaxWidth(), Arrangement.Center) {
            Text(date)
        }
    }

    @Composable
    fun Route(start : String, end : String) {
        Column(
            Modifier
                .height(100.dp)
                .fillMaxWidth()) {
            Text("Route")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text(start)
                Text("to")
                Text(end)
            }
        }
    }

    @Composable
    fun Stops(stops : List<Stop>) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(items = stops) { item ->
                Stop(departAt = item.time, stationName = item.stationName)
            }
        }
    }

    @Composable
    fun Stop(departAt : String, stationName: String) {
        Row(
            Modifier
                .height(60.dp)
                .fillMaxWidth(), Arrangement.SpaceAround) {
            Text(departAt)
            Text(stationName)
        }
    }
}
