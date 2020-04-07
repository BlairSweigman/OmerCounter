/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter.locator

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}
