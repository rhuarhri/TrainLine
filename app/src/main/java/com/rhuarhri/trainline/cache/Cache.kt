package com.rhuarhri.trainline.cache

import androidx.room.*
import com.rhuarhri.trainline.data.DateTime
import com.rhuarhri.trainline.data.Station
import com.rhuarhri.trainline.data.TimeTable
import java.sql.Date
import java.time.ZoneOffset

class Cache(private val db : AppDatabase) {

    fun addStation(station : Station) {
        val stationEntity = StationEntity(station.code, station.name)
        val placeDAO = db.getDao()
        placeDAO.addStation(stationEntity)
    }

    fun getAllStations() : List<Station> {
        val placeDAO = db.getDao()
        val stationEntities = placeDAO.getAll()
        val stationList = mutableListOf<Station>()
        for (entity in stationEntities) {
            stationList.add(Station(name = entity.name, code = entity.stationCode))
        }
        return stationList
    }

    fun addTimeTable(platform: String, departTime : String, start : Station, end : Station, date : DateTime) {
        val dao = db.getDao()
        val timeTableEntity = TimeTableEntity(platform = platform, departAt = departTime,
            start = start.name, stationCode = start.code, end = end.name, date = date.toLong())

        dao.addTimeTable(timeTable = timeTableEntity)
    }

    fun getTimeTable(stationCode: String, date : DateTime) : List<TimeTable>{
        val dao = db.getDao()
        val startDate = date.toLong()

        val endDate = date.getDateTime().plusDays(1).toEpochSecond(ZoneOffset.UTC)

        val timeTableEntityList = dao.getTimeTable(stationCode, startDate, endDate)

        val timeTableList = mutableListOf<TimeTable>()
        for (entity in timeTableEntityList) {
            val dateTime = DateTime()
            dateTime.fromLong(entity.date)

            val timeTable = TimeTable(platform = entity.platform, departAt = entity.departAt,
                start = entity.start, destination = entity.end, trainId = "", date = dateTime.getDateString())
            timeTableList.add(timeTable)
        }

        return timeTableList
    }

    fun addStop(station : Station, time : String) {
        val dao = db.getDao()

    }
}

@Entity(tableName = "timeTable")
class TimeTableEntity(
    @PrimaryKey(autoGenerate = true) var id : Int = 0,
    @ColumnInfo(name = "platform") val platform : String,
    @ColumnInfo(name = "departAt") val departAt : String,
    @ColumnInfo(name = "startStation") val start : String,
    @ColumnInfo(name = "stationCode") val stationCode: String,
    @ColumnInfo(name = "endStation") val end : String,
    @ColumnInfo(name = "date") val date : Long
)

@Entity(tableName = "station")
class StationEntity(
    @PrimaryKey val stationCode : String,
    @ColumnInfo(name = "name") val name : String
)

@Entity(tableName = "stop")
class StopEntity(
    @PrimaryKey(autoGenerate = true) var id : Int = 0,
    @ColumnInfo(name = "stationName") val stationName : String,
    @ColumnInfo(name = "stationCode") val stationCode : String,
    @ColumnInfo(name = "time") val time : String,
    @ColumnInfo(name = "serviceId") val serviceId : String
)

@Dao
interface DatabaseInterface {

    //Station database code
    @Query("SELECT * FROM station")
    fun getAll(): List<StationEntity>

    @Insert
    fun addStation(station : StationEntity)

    @Query("DELETE FROM station")
    fun deleteAll()

    //time table database code
    @Insert
    fun addTimeTable(timeTable : TimeTableEntity)

    @Query("SELECT * FROM timeTable WHERE stationCode = :code AND date BETWEEN :startDate AND :endDate")
    fun getTimeTable(code : String, startDate : Long, endDate : Long) : List<TimeTableEntity>

    //service database code
    @Insert
    fun addStop(stop : StopEntity)

    @Query("SELECT * FROM stop WHERE serviceId = :serviceId")
    fun getStops(serviceId: String)

}

@Database(entities = [StationEntity::class, TimeTableEntity::class, StopEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DatabaseInterface
}
