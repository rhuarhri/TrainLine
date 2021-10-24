package com.rhuarhri.trainline

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.rhuarhri.trainline.search_widget.SearchWidgetViewModel
import com.rhuarhri.trainline.ui.theme.TrainLineTheme
import com.rhuarhri.trainline.veiw_train_time_screen.Stop
import com.rhuarhri.trainline.veiw_train_time_screen.ViewTrainTimeViewModel

class ViewTrainTime : ComponentActivity() {

    lateinit var viewTrainTimeViewModel: ViewTrainTimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trainId = intent.getStringExtra("trainId")
        val date = intent.getStringExtra("date")

        viewTrainTimeViewModel = ViewModelProvider(this).get(ViewTrainTimeViewModel::class.java)
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
                        Column() {
                            time(viewTrainTimeViewModel.state.date)
                            route(viewTrainTimeViewModel.state.start, viewTrainTimeViewModel.state.end)
                            stops(stops = viewTrainTimeViewModel.state.stops)
                        }
                    },
                )
            }
        }
    }

    @Composable
    fun time(date : String) {
        Row(Modifier.height(100.dp).fillMaxWidth(), Arrangement.Center) {
            Text(date)
        }
    }

    @Composable
    fun route(start : String, end : String) {
        Column(Modifier.height(100.dp).fillMaxWidth()) {
            Text("Route")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text(start)
                Text("to")
                Text(end)
            }
        }
    }

    @Composable
    fun stops(stops : List<Stop>) {
        LazyColumn(Modifier.fillMaxSize(),) {
            items(items = stops) { item ->
                stop(departAt = item.time, stationName = item.stationName)
            }
        }
    }

    @Composable
    fun stop(departAt : String, stationName: String) {
        Row(Modifier.height(60.dp).fillMaxWidth(), Arrangement.SpaceAround) {
            Text(departAt)
            Text(stationName)
        }
    }
}
