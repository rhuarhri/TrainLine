package com.rhuarhri.trainline.search_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhuarhri.trainline.online.time_table_data.All
import com.rhuarhri.trainline.online.time_table_data.ServiceTimetable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class SearchScreenState(var timetable: List<All>) {

}

class SearchScreenViewModel : ViewModel() {

    private val repo = SearchScreenRepo()

    //var timeTableList by mutableStateOf(mutableListOf<All>())

    var state by mutableStateOf(SearchScreenState(listOf()))

    fun setup() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getTrainList()

            println("result size is ${result.size}")

            withContext(Dispatchers.Main) {
                updateTable(result)
            }
        }

        //val result = runBlocking { repo.getTrainList() }

        //println("result size is ${result.size}")

        /*timeTableList.clear()
        for (foundTable in result) {
            timeTableList.add(foundTable)
        }*/
    }

    fun updateTable(result : List<All>) {

        val newState = SearchScreenState(result)
        //newState.timeTable = result
        /*timeTableList.clear()
        for (foundTable in result) {
            timeTableList.add(foundTable)
        }*/
        state = newState
        println("updated time table")
    }

}