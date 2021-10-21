package com.rhuarhri.trainline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.rhuarhri.trainline.online.time_table_data.All
import com.rhuarhri.trainline.search_screen.SearchScreenViewModel
import com.rhuarhri.trainline.search_widget.SearchWidget
import com.rhuarhri.trainline.search_widget.SearchWidgetViewModel
import com.rhuarhri.trainline.time_table_widget.TimeTableWidget
import com.rhuarhri.trainline.time_table_widget.TimetableWidgetViewModel
import com.rhuarhri.trainline.ui.theme.TrainLineTheme

class MainActivity : ComponentActivity() {

    /*
    App id c9ef48df
    api key f7dc9efc73b6cd485d10835de81c6ed6
     */

    //lateinit var searchScreenViewModel: SearchScreenViewModel
    lateinit var searchWidgetViewModel: SearchWidgetViewModel
    lateinit var timetableWidgetViewModel: TimetableWidgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //searchScreenViewModel = ViewModelProvider(this).get(SearchScreenViewModel::class.java)
        //searchScreenViewModel.setup()

        searchWidgetViewModel = ViewModelProvider(this).get(SearchWidgetViewModel::class.java)
        timetableWidgetViewModel = ViewModelProvider(this).get(TimetableWidgetViewModel::class.java)

        setContent {
            TrainLineTheme {
                // A surface container using the 'background' color from the theme
                //Surface(color = MaterialTheme.colors.background) {

                TimeTableWidget().widget(state = timetableWidgetViewModel.state)

                SearchWidget().widget(searchWidgetViewModel, onSearch = {
                    //If I used only one view model then the code below would not be necessary
                    val stationName = searchWidgetViewModel.state.stationName
                    val date = searchWidgetViewModel.state.date
                    val time = searchWidgetViewModel.state.time
                    timetableWidgetViewModel.search(stationName, date, time)
                })
                //}
            }
        }
    }
}



