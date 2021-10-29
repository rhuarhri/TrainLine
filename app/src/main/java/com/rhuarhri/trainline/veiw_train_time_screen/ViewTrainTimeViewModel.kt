package com.rhuarhri.trainline.veiw_train_time_screen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.rhuarhri.trainline.data.ServiceInfo
import com.rhuarhri.trainline.data.Stop
import com.rhuarhri.trainline.online.Online
import kotlinx.coroutines.launch

class ViewTrainTimeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewTrainTimeViewModel(context) as T
    }

}

class ViewTrainTimeViewModel(context: Context) : ViewModel() {

    private val repo = ViewTrainTimeRepo(context)
    //var state by mutableStateOf(ServiceInfo("", "", "", listOf()))

    val serviceInfoState = repo.serviceInfoLiveData

    fun setup(trainId : String, date : String) {
        if (trainId.isNotBlank() && date.isNotBlank()) {
            viewModelScope.launch {
                repo.getServiceInfo(trainId, date)
            }
        }
    }
}

class ViewTrainTimeRepo(context : Context) {
    private val online = Online(context)

    val serviceInfoLiveData : LiveData<ServiceInfo> = Transformations.map(online.serviceInfoLiveData) { service ->

        if (service != null) {

            val serviceDate = service.date ?: ""

            val serviceStart = service.origin_name ?: ""
            val serviceEnd = service.destination_name ?: ""

            val serviceStops = mutableListOf<Stop>()

            if (service.stops != null) {
                for (foundStop in service.stops) {
                    if (foundStop.aimed_departure_time != null && foundStop.station_name != null) {
                        val newStop = Stop(foundStop.aimed_departure_time, foundStop.station_name)
                        serviceStops.add(newStop)
                    }
                }
            }
            ServiceInfo(serviceDate, serviceStart, serviceEnd, serviceStops)
        } else {
            ServiceInfo("", "", "", listOf())
        }
    }

    suspend fun getServiceInfo(trainId : String, date : String) {
        online.getServiceInfo(trainId, date)
        /*val service = online.getServiceInfo(trainId, date) ?: return ServiceInfo("", "", "", listOf())

        val serviceDate = service.date ?: ""

        val serviceStart = service.origin_name ?: ""
        val serviceEnd = service.destination_name ?: ""

        val serviceStops = mutableListOf<Stop>()

        if (service.stops != null) {
            for (foundStop in service.stops) {
                if (foundStop.aimed_departure_time != null && foundStop.station_name != null) {
                    val newStop = Stop(foundStop.aimed_departure_time, foundStop.station_name)
                    serviceStops.add(newStop)
                }
            }
        }

        return ServiceInfo(serviceDate, serviceStart, serviceEnd, serviceStops)*/
    }
}