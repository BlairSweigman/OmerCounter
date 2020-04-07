/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */

package ca.worthconsulting.omercounter.sunset

import com.squareup.moshi.Json

data class Results (@Json(name="results")val results: Sunset,
                    @Json(name="status") val status: String)