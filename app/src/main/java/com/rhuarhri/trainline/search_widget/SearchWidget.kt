package com.rhuarhri.trainline.search_widget

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhuarhri.trainline.online.Online
import kotlinx.coroutines.launch
import java.util.*

class SearchWidget {

    @ExperimentalAnimationApi
    @Composable
    fun widget(context: Context, viewModel: SearchWidgetViewModel, onSearch : () -> Unit) {

        AnimatedVisibility(visible = viewModel.state.visible,) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White), Alignment.TopCenter
            ) {
                Column() {
                    dropDownWidget(viewModel = viewModel)

                    datePicker(context = context, viewModel = viewModel)
                    timePicker(context = context, viewModel = viewModel)

                    Button(onClick = {
                                     viewModel.hide()
                        onSearch.invoke()
                    },) {
                        Text("Search")
                    }
                }
            }
        }
    }

    @Composable
    private fun inputDisplay(title: String, data: String, onClick : () -> Unit) {
        Column(
            Modifier
                .height(90.dp)
                .fillMaxWidth()
                .clickable { onClick.invoke() }
                .border(2.dp, MaterialTheme.colors.primary)) {
            Text(modifier = Modifier
                .weight(1f)
                .fillMaxWidth().padding(5.dp), text = title)
            Text(modifier = Modifier
                .weight(2f)
                .fillMaxWidth().padding(5.dp), text = data, fontSize = 20.sp)
        }
    }

    @Composable
    private fun dropDownWidget(viewModel: SearchWidgetViewModel) {
        inputDisplay(title = "Station", data = viewModel.dropDownMenuState.selected.name, onClick = {
            viewModel.expandDropDown()
        })
        DropdownMenu(expanded = viewModel.dropDownMenuState.dropDownExpanded,
            onDismissRequest = { viewModel.minimiseDropDown() }) {
            for (item in viewModel.dropDownMenuState.dropDownItems) {
                DropdownMenuItem(onClick = {viewModel.selectDropDownItem(item)}) {
                    Text(item.name)
                }
            }
        }

    }

    @Composable
    fun datePicker(context : Context, viewModel: SearchWidgetViewModel) {
        val currentYear = viewModel.datePickerState.year
        val currentMonth = viewModel.datePickerState.month
        val currentDay = viewModel.datePickerState.day
        val datePicker = DatePickerDialog(context, { datePicker, day, month, year ->
            viewModel.selectDate(day, month, year)
        }, currentYear, currentMonth, currentDay)

        inputDisplay(title = "Date", data = "$currentDay/$currentMonth/$currentYear", onClick = {datePicker.show()})
    }

    @Composable
    fun timePicker(context: Context, viewModel: SearchWidgetViewModel) {
        val currentHour = viewModel.timePickerState.hour
        val currentMinutes = viewModel.timePickerState.minutes

        val timePicker = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            viewModel.selectTime(hour, minute)
        }, currentHour, currentMinutes, true)

        inputDisplay(title = "Time", data = "$currentHour:$currentMinutes", onClick = {timePicker.show()})
    }
}

class SearchWidgetState(val visible : Boolean)

/*
    pro tip the state class should not be a data class as android compose will use the classes
    is equal to method. This is problematic as if the data in the class changes android compose
    will not be notified of the change as the class itself has not changed.
    solution 1: over ride the existing is equal to method
    solution 2: don't use a data class to store state
    */
class SearchWidgetDropDownState(val dropDownExpanded : Boolean = false,
                                     val dropDownItems : List<Place> = listOf(),
                                     val selected : Place)

class SearchWidgetDatePickerState(val day : Int, val month : Int, val year : Int)

class SearchWidgetTimePickerState(val hour : Int, val minutes : Int)

class SearchWidgetViewModel : ViewModel() {
    private val repo = SearchWidgetRepo()

    /*
    Why does a widget have it's own view model
    this is because each widget has some logic associated with it
    this logic can be separated out into it's oen view model and helps
    keep the code separate from everything else
     */

    var state by mutableStateOf(SearchWidgetState(true))

    var dropDownMenuState by mutableStateOf(SearchWidgetDropDownState(false,
        listOf<Place>(), Place("", "")))

    private val calendar = Calendar.getInstance()

    var datePickerState by mutableStateOf(SearchWidgetDatePickerState(day = calendar[Calendar.DAY_OF_MONTH],
        month = calendar[Calendar.MONTH], year = calendar[Calendar.YEAR]))

    var timePickerState by mutableStateOf(SearchWidgetTimePickerState(hour = calendar[Calendar.HOUR],
        minutes = calendar[Calendar.MINUTE]))

    /*
    Why is there a state class instead of separate values as each value could have it's
    own state?
    The main reason is that all information is always up to date. It forces me to ensure all
    values are up to date as for every time I create a new SearchWidgetState instance I have
    to add the most up to date versions of the information the app / widget needs
     */

    /*fun selectStation(name: String) {
        val newState = SearchWidgetState(name, state.date, state.time,)
        state = newState
    }*/

    fun selectDate(day : Int, month: Int, year : Int) {
        val newDatePickerState = SearchWidgetDatePickerState(day, month, year)
        datePickerState = newDatePickerState
    }

    fun selectTime(hour : Int, minutes : Int) {
        val newTimePickerState = SearchWidgetTimePickerState(hour, minutes)
        timePickerState = newTimePickerState
    }

    fun setupDropDownWidget() {
        viewModelScope.launch {
            val places = repo.getPlaces()
            val selected : Place = if (places.isEmpty()) {
                places.first()
            } else {
                Place("", "")
            }

            val newDropDownState = SearchWidgetDropDownState(false, places, selected)
            dropDownMenuState = newDropDownState
        }
    }

    fun selectDropDownItem(item : Place) {
        val newDropDownState = SearchWidgetDropDownState(false, dropDownMenuState.dropDownItems, item)
        dropDownMenuState = newDropDownState
    }

    fun expandDropDown() {
        val newDropDownState = SearchWidgetDropDownState(true,
            dropDownMenuState.dropDownItems, dropDownMenuState.selected)
        dropDownMenuState = newDropDownState
    }

    fun minimiseDropDown() {
        val newDropDownState = SearchWidgetDropDownState(false,
            dropDownMenuState.dropDownItems, dropDownMenuState.selected)
        dropDownMenuState = newDropDownState
    }

    fun show() {
        val newState = SearchWidgetState(true)
        state = newState
    }

    fun hide() {
        val newState = SearchWidgetState(false)
        state = newState
    }

}

data class Place(val name : String, val code : String) {
    /*This is used to filter out unnecessary information*/
}

class SearchWidgetRepo {
    private val online = Online()

    suspend fun getPlaces() : List<Place> {
        val trainStation = online.getStation()

        val places = mutableListOf<Place>()
        if (trainStation.member != null) {

            for (stationInfo in trainStation.member) {
                val name = stationInfo.name
                val code = stationInfo.station_code

                if (name != null && code != null) {
                    val place = Place(name, code)
                    places.add(place)
                }
            }
        }

        return places
    }
}