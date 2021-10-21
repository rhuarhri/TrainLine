package com.rhuarhri.trainline.search_widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class SearchWidget {

    @Composable
    fun widget(viewModel: SearchWidgetViewModel, onSearch : () -> Unit) {

        val stationName : String = viewModel.state.stationName
        val date : String = viewModel.state.date
        val time : String = viewModel.state.time

        Box(modifier = Modifier.fillMaxWidth().background(Color.White), Alignment.TopCenter) {
            Column() {
                TextField(value = stationName, onValueChange = { name ->
                    viewModel.selectStation(name)
                }, label = {
                    Text("Station name")
                })
                TextField(value = date, onValueChange = { date ->
                    viewModel.selectDate(date)
                }, label = {
                    Text("Date of departure")
                })
                TextField(value = time, onValueChange = { time ->
                    viewModel.selectTime(time)
                }, label = {
                    Text("time of departure")
                })
                Button(onClick = onSearch,) {
                    Text("Search")
                }
            }
        }
    }
}

class SearchWidgetState (val stationName: String, val date : String, val time : String) {
    /*
    pro tip the state class should not be a data class as android compose will use the classes
    is equal to method. This is problematic as if the data in the class changes android compose
    will not be notified of the change as the class itself has not changed.
    solution 1: over ride the existing is equal to method
    solution 2: don't use a data class to store state
    */
}

class SearchWidgetViewModel : ViewModel() {
    /*
    Why does a widget have it's own view model
    this is because each widget has some logic associated with it
    this logic can be separated out into it's oen view model and helps
    keep the code separate from everything else
     */

    var state by mutableStateOf(SearchWidgetState("","",""))
    /*
    Why is there a state class instead of separate values as each value could have it's
    own state?
    The main reason is that all information is always up to date. It forces me to ensure all
    values are up to date as for every time I create a new SearchWidgetState instance I have
    to add the most up to date versions of the information the app / widget needs
     */

    fun selectStation(name: String) {
        val newState = SearchWidgetState(name, state.date, state.time)
        state = newState
    }

    fun selectDate(date : String) {
        val newState = SearchWidgetState(state.stationName, date, state.time)
        state = newState
    }

    fun selectTime(time : String) {
        val newState = SearchWidgetState(state.stationName, state.date, time)
        state = newState
    }

}

class SearchWidgetRepo {

}