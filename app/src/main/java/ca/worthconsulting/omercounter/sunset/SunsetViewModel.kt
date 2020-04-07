/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter.sunset


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.worthconsulting.omercounter.DateUtil
import ca.worthconsulting.omercounter.network.SunApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

enum class DatePoint { START, END,TODAY }
class SunsetViewModel : ViewModel() {
    private var omerShown = false

    private val _todaySunset = MutableLiveData<Sunset>()
    val todaySunset: LiveData<Sunset> get() = _todaySunset

    private val _startSunset = MutableLiveData<Sunset>()
    val startSunset: LiveData<Sunset> get() = _startSunset

    private val _endSunset = MutableLiveData<Sunset>()
    val endSunset: LiveData<Sunset> get() = _endSunset

    private val _omerCount = MutableLiveData<Int>()
    val omerCount : LiveData<Int> get() = _omerCount

    fun setStartSunset(date : String) {
        _startSunset.value = Sunset(date)
    }
    fun setEndUnset(date: String) {
        _endSunset.value = Sunset(date)
    }

    fun getSunsetDate(lat: Double, lng: Double, date: String, datePoint: DatePoint) {
        Timber.i("Date: $date for $datePoint")
        CoroutineScope(Dispatchers.IO).launch {
            val response = SunApi.retrofitService.getSunsetByDate(lat, lng, date)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        when (datePoint) {
                                DatePoint.START -> {_startSunset.value = response.body()?.results}
                                DatePoint.END -> { _endSunset.value = response.body()?.results}
                                DatePoint.TODAY ->{_todaySunset.value =response.body()?.results}
                        }
                    calcOmer()
                    }
                    else {
                        Timber.e( "API Returned: ${response.errorBody()} ")
                    }
                } catch (e: Exception) {
                    Timber.e( "Error: ${e.message} ")
                }
            }

        }
    }

    fun calcOmer() {
        if (_startSunset.value!=null && _endSunset.value!=null && _todaySunset.value!=null && !omerShown) {
            //Log.i("SunsetViewModel","I can calculate the Omer")
            omerShown=true
            _omerCount.value = DateUtil.omerCount(todaySunset.value!!.sunset,startSunset.value!!.sunset).toInt()
        }

    }
    fun clearToday() {
        _todaySunset.value = null
        omerShown = false
    }
}