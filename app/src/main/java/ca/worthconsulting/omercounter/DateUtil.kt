/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter

import android.annotation.SuppressLint

import timber.log.Timber

import java.text.SimpleDateFormat
import java.util.*

import kotlin.math.floor
import kotlin.math.roundToLong

object DateUtil {
    private const val DAYS = 86400000.0
    @SuppressLint("SimpleDateFormat")
    fun uTCtoLocal(utcDate : String ) : Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.parse(utcDate)
    }
    fun omerCount(todayDateStr : String,startDateStr : String ) : Long {
        val todaySunset = uTCtoLocal(todayDateStr)
        val timeRightNow =  Date()
        val startDate = uTCtoLocal(startDateStr)

        val diff: Long = (timeRightNow.time - startDate.time) + (timeRightNow.time - todaySunset.time)
        val omer = (floor ( diff / DAYS)).roundToLong() +1
        val cal = Calendar.getInstance()
        cal.time = startDate

        cal.add(Calendar.DAY_OF_YEAR, omer.toInt()-1)

        Timber.i("Raw Omer: $omer")
        Timber.i( "Start of Omer is: $startDate")
        Timber.i("Twilight Zone Start : ${cal.time}")
        Timber.i( "Time is: $timeRightNow")
        Timber.i( "Sunset is at : $todaySunset")
       return omer
    }
   @SuppressLint("SimpleDateFormat")
   fun getToday() : String {
       val date = Date()
       val dateOnly = SimpleDateFormat("yyyy-MM-dd")
       return dateOnly.format(date)

   }


}
