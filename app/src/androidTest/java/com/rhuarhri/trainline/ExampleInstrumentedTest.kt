package com.rhuarhri.trainline

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rhuarhri.trainline.cache.Cache
import com.rhuarhri.trainline.cache.DatabaseInterface
import com.rhuarhri.trainline.cache.AppDatabase
import com.rhuarhri.trainline.cache.TimeTableEntity
import com.rhuarhri.trainline.data.DateTime
import com.rhuarhri.trainline.data.Station
import com.rhuarhri.trainline.online.time_table_data.TimeTable
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rhuarhri.trainline", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class DatabaseTests {
    private lateinit var dao: DatabaseInterface
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        dao = db.getDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (this::db.isInitialized == true) {
            db.close()
        }
    }

    @Test
    @Throws(Exception::class)
    fun stationDatabaseTest() {
        val station = Station(name = "test", code = "1")
        val cache = Cache(db)

        cache.addStation(station = station)

        val stations = cache.getAllStations()

        assertEquals("has result", false, stations.isNullOrEmpty())

        assertEquals("database data", "test", stations.first().name)
    }

    @Test
    @Throws(Exception::class)
    fun getTimeTableListTest() {

        val station1 = Station(name = "Nottingham", code = "not")
        val station2 = Station(name = "Derby", code = "dby")

        val dateTime = DateTime()
        dateTime.fromDate(2021, 10, 26, 12, 0)

        val cache = Cache(db)
        cache.addTimeTable("1", "13:00", station1, station2, dateTime)
        cache.addTimeTable("1", "13:00", station2, station1, dateTime)
        cache.addTimeTable("1", "13:30", station1, station2, dateTime)

        //get all time table items that start at a station and are available after the date
        val timeTableList = cache.getTimeTable(station1.code, dateTime)

        assertEquals("check time table size", 2, timeTableList.size)
    }
}