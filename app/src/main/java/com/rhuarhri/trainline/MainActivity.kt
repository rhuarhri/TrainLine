package com.rhuarhri.trainline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.rhuarhri.trainline.online.time_table_data.All
import com.rhuarhri.trainline.search_screen.SearchScreenViewModel
import com.rhuarhri.trainline.ui.theme.TrainLineTheme

class MainActivity : ComponentActivity() {

    /*
    App id c9ef48df
    api key f7dc9efc73b6cd485d10835de81c6ed6
     */

    lateinit var searchScreenViewModel: SearchScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchScreenViewModel = ViewModelProvider(this).get(SearchScreenViewModel::class.java)
        searchScreenViewModel.setup()

        setContent {
            TrainLineTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            var size = searchScreenViewModel.state.timetable
                            println("size is ${size.size}")
                        }) {
                            Text("Button")
                        }
                        timeTableListView(timeTable = searchScreenViewModel.state.timetable)

                    }
                }
            }
        }
    }
}

@Composable
fun timeTableListView(timeTable : List<All>) {
    LazyColumn(Modifier.fillMaxWidth().height(300.dp)) {
        items(items = timeTable) { item ->
            val platform = item.platform.toString()
            val departAt = item.aimed_departure_time.toString()
            val start = item.origin_name.toString()
            val destination = item.destination_name.toString()
            //println("platform $platform, departAt $departAt, start $start, destination $destination")
            timeTableItem(platform = platform, departAt = departAt, start = start, destination = destination)
        }
    }
}

@Composable
fun timeTableItem(platform: String, departAt: String, start: String, destination: String) {

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
