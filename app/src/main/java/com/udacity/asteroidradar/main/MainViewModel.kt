package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.io.IOException

enum class Filter {
    WEEK,
    TODAY,
    SAVED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getDatabase(application)
    private val repository = AsteroidRepository(database)

    private val _photo = MutableLiveData<PictureOfDay>()
    val photo: LiveData<PictureOfDay>
        get() = _photo

    private val _filter = MutableLiveData(Filter.WEEK)
    val asteroidList = _filter.switchMap { filter ->
        when (filter) {
            Filter.WEEK -> repository.asteroidAll
            Filter.TODAY -> repository.asteroidToday
            else -> repository.asteroidAll
        }
    }

    private val _selectedAsteroid = MutableLiveData<Asteroid?>()
    val selectedAsteroid: LiveData<Asteroid?>
        get() = _selectedAsteroid

    init {
        viewModelScope.launch {
            try {
                getPhoto()
                repository.getAsteroidNetwork()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getPhoto() {
        _photo.value = repository.getPictureOfTheDayNetwork()
    }

    fun onFilterChange(filter: Filter) {
        _filter.value = filter
    }

    fun asteroidClicked(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun navigateDone() {
        _selectedAsteroid.value = null
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}