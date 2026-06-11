package com.memory.platform_specificfeatures.platform

import android.os.Build
import kotlin.math.sqrt

actual class DeviceInfo actual constructor() {
    actual fun getDeviceName(): String = Build.MODEL
    actual fun getManufacturer(): String = Build.MANUFACTURER
    actual fun getOsVersion(): String = "Android ${Build.VERSION.RELEASE}"
    actual fun getSdkVersion(): String = Build.VERSION.SDK_INT.toString()
    actual fun isTablet(): Boolean {
        val metrics = android.content.res.Resources.getSystem().displayMetrics
        val diag = sqrt(
            (metrics.widthPixels / metrics.xdpi).let { it * it } +
                    (metrics.heightPixels / metrics.ydpi).let { it * it }
        )
        return diag >= 7.0
    }
}