/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Application Controller for managing Timber
 */
class ApplicationController : Application() {
    /**
     * Initializes the Timber log
     */
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}