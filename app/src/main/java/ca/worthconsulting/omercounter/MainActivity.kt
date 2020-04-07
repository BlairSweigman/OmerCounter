/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import ca.worthconsulting.omercounter.locator.GpsUtils
import ca.worthconsulting.omercounter.locator.LocationViewModel
import ca.worthconsulting.omercounter.databinding.ActivityMainBinding
import ca.worthconsulting.omercounter.sunset.DatePoint
import ca.worthconsulting.omercounter.sunset.SunsetViewModel
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import kotlin.math.abs

/* MUST CHANGE THIS YEARLY!!*/
const val START_DATE = "2020-04-09"
const val END_DATE = "2020-05-28"
const val YEAR = "2020"

/* DON'T TOUCH THESE*/
const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101


class MainActivity : AppCompatActivity() {


    private lateinit var sunsetViewModel: SunsetViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var binding: ActivityMainBinding
    private var isGPSEnabled = false
    private var permGranted = false
    private var needSave = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        sunsetViewModel = ViewModelProviders.of(this).get(SunsetViewModel::class.java)
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@MainActivity.isGPSEnabled = isGPSEnable
            }
        })
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }


    private fun getDates(lat: Double, lng: Double) {

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)

        val currentYear = sharedPref.getString("Year", "")
        if (currentYear != YEAR) {
            showLoc(lat, lng)
            needSave = true
            //retrieves start and End Dates from Model
            with(sharedPref.edit()) {
                putString("START_DATE", START_DATE)
                putString("END_DATE", END_DATE)
                commit()
            }
            Timber.i( "Getting location from GPS")
            sunsetViewModel.getSunsetDate(lat, lng, START_DATE, DatePoint.START)
            sunsetViewModel.getSunsetDate(lat, lng, END_DATE, DatePoint.END)
        } else {
            needSave = false
            Timber.i( "retrieving times from preferences")
            sunsetViewModel.setStartSunset(sharedPref.getString("Sunset_Start", "")!!)
            sunsetViewModel.setEndUnset(sharedPref.getString("Sunset_End", "")!!)
            sunsetViewModel.calcOmer()
        }

        sunsetViewModel.getSunsetDate(lat, lng, DateUtil.getToday(), DatePoint.TODAY)
        sunsetViewModel.omerCount.observe(this, Observer {
            val omer = abs(it)
            //LAG B'OMER 33
            if (it <= 0) {
                binding.txtOmerTop.text = getString(R.string.will_start_in)
                binding.txtOmerBottom.text = getString(R.string.days)
                binding.txtOmer.setTextColor(ContextCompat.getColor(this, R.color.beforeOmer))
                binding.txtOmer.text = "$omer"
            } else {
                if (it <= 49) {
                    binding.txtOmer.text = "$omer"
                    binding.txtOmer.setTextColor(ContextCompat.getColor(this, R.color.inOmer))
                } else {
                    binding.txtOmerTop.text = getString(R.string.omer_counted)
                    binding.txtOmer.text = getString(R.string.loading)
                    binding.txtOmer.setTextColor(ContextCompat.getColor(this, R.color.afterOmer))
                }
            }

            val startToShow = DateUtil.uTCtoLocal(sunsetViewModel.startSunset.value?.sunset!!)
            val endToShow = DateUtil.uTCtoLocal(sunsetViewModel.endSunset.value?.sunset!!)
            binding.txtOmerStart.text = "$startToShow"
            binding.txtOmerEnd.text = "$endToShow"
            if (needSave) {
                with(sharedPref.edit()) {
                    clear()
                    putString("Year", YEAR)
                    putString("Sunset_Start", sunsetViewModel.startSunset.value?.sunset!!)
                    putString("Sunset_End", sunsetViewModel.endSunset.value?.sunset!!)
                    commit()
                }
                needSave = false
            }
        })
    }

    private fun resetLocation(): Boolean {
        binding.txtOmer.text = "--"
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            commit()
        }
        sunsetViewModel.clearToday()
        invokeLocationAction()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.reset_location) {
            return resetLocation()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    //Location:
    private fun invokeLocationAction() {
        Timber.i( "InvokeLocation")

        when {
            !isGPSEnabled -> Timber.e( "GPS NOT ENABLED")
            isPermissionsGranted() -> startLocationUpdate()
            shouldShowRequestPermissionRationale() ->
                Timber.i(

                    "Need permission"
                ) // latLong.text = getString(R.string.permission_request)

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        Timber.e( "startLocationUpdate")
        locationViewModel.getLocationData().observe(this, Observer {
            //latLong.text =  getString(R.string.latLong, it.longitude, it.latitude)
            getDates(it.latitude, it.longitude)
        })

    }

    private fun isPermissionsGranted() = permGranted ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                permGranted = true
                invokeLocationAction()
            }
        }
    }
    private fun showLoc(lat: Double,lng:Double) {

        Snackbar.make(binding.root,"Setting Location to %.2f lat. %.2f lng".format(lat,lng),Snackbar.LENGTH_SHORT).show()
    }
}



