package com.rhuarhri.trainline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.lifecycle.ViewModelProvider
import com.rhuarhri.trainline.online.Online
import com.rhuarhri.trainline.search_widget.SearchWidget
import com.rhuarhri.trainline.search_widget.SearchWidgetViewModel
import com.rhuarhri.trainline.search_widget.SearchWidgetViewModelFactory
import com.rhuarhri.trainline.time_table_widget.TimeTableWidget
import com.rhuarhri.trainline.time_table_widget.TimeTableWidgetViewModelFactory
import com.rhuarhri.trainline.time_table_widget.TimetableWidgetViewModel
import com.rhuarhri.trainline.ui.theme.TrainLineTheme


class MainActivity : ComponentActivity() {

    lateinit var searchWidgetViewModel: SearchWidgetViewModel
    lateinit var timetableWidgetViewModel: TimetableWidgetViewModel

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchWidgetViewModel = ViewModelProvider(this, SearchWidgetViewModelFactory(this.applicationContext))
            .get(SearchWidgetViewModel::class.java)
        //searchWidgetViewModel.setupDropDownWidget()

        timetableWidgetViewModel = ViewModelProvider(this, TimeTableWidgetViewModelFactory(this.applicationContext))
            .get(TimetableWidgetViewModel::class.java)

        setContent {
            TrainLineTheme {
                
                Scaffold(topBar = {TopAppBar(
                    title = {
                        Text(
                            text = "Train Time Table",
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            searchWidgetViewModel.show()
                        }) {
                            Icon(Icons.Filled.Search, "")
                        }
                    },
                )},
                    content = {
                        TimeTableWidget().Widget(this, viewModel = timetableWidgetViewModel)
                    },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                                         searchForTimeTable()
                    }, content = {Icon(Icons.Filled.Refresh, "")})
                })

                SearchWidget().Widget(this, searchWidgetViewModel, onSearch = {
                    searchForTimeTable()
                })
            }
        }
    }

    private fun searchForTimeTable() {
        //If I used only one view model then the code below would not be necessary

        val day = searchWidgetViewModel.datePickerState.day
        val month = searchWidgetViewModel.datePickerState.month
        val year = searchWidgetViewModel.datePickerState.year

        val date : String = Online.covertDate(year, month, day)

        val hour = searchWidgetViewModel.timePickerState.hour
        val minutes = searchWidgetViewModel.timePickerState.minutes

        val time = Online.convertTime(hour, minutes)

        val stationLocation = searchWidgetViewModel.dropDownMenuState.selected

        val stationCode = if (stationLocation.code == "") {
            "SHF"
        } else {
            stationLocation.code
        }

        timetableWidgetViewModel.search(stationCode, date, time)
    }
}



