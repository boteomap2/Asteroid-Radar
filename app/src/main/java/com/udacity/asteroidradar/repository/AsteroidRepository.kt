package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroidAll: LiveData<List<Asteroid>> = database.asteroidDao.getAll()
    val asteroidToday: LiveData<List<Asteroid>> = database.asteroidDao.getToday(getToday())

    suspend fun getAsteroidNetwork() {
        val result = NasaApi.retrofitService.getAsteroid()
        database.asteroidDao.clearAll()
        database.asteroidDao.insertAll(parseAsteroidsJsonResult(JSONObject(result)))
    }

    suspend fun getPictureOfTheDayNetwork(): PictureOfDay {
        return NasaApi.retrofitService.getPictureOfDay()
    }

    private fun getToday(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

}