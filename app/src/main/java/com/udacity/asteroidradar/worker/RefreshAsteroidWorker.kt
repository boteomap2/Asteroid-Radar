package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshAsteroidWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME = "RefreshAsteroidWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.getAsteroidNetwork()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}