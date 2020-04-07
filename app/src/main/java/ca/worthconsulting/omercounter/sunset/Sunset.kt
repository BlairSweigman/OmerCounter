/*
 * Copyright (c) 2020. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Blair Sweigman <blair.sweigman@gmail.com>
 */
package ca.worthconsulting.omercounter.sunset

import com.squareup.moshi.Json

data class Sunset(
    @Json(name="sunset") val sunset: String
)