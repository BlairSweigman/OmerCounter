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

/**
 * Utility objects for dates
 */
object DateUtil {
    private const val DAYS = 86400000.0 //day in milliseconds

    /**
     * Converts UTC time string to Local time
     * @param utcDate {String} UTC Time String from API
     * @return {date} Local Time
     */
    @SuppressLint("SimpleDateFormat")
    fun uTCtoLocal(utcDate : String ) : Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.parse(utcDate)
    }

    /**
     * Counts the Omer
     * @param todayDateStr {String} UTC Time of todays sunset
     * @param startDateStr {String} UTC Time of the start of the Omer
     * @return {Long} The Omer Count
     */

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

    /**
     * gets todays date in yyyy-MM-dd format
     * @return {String} the formatted date
     */
    @SuppressLint("SimpleDateFormat")
   fun getToday() : String {
       val date = Date()
       val dateOnly = SimpleDateFormat("yyyy-MM-dd")
       return dateOnly.format(date)

   }


}
