package net.globulus.simisync

import net.globulus.easyprefs.annotation.Pref
import net.globulus.easyprefs.annotation.PrefClass

@PrefClass(staticClass = false, origin = true)
class PrefUtil {
    @Pref(nullable = true) var cookie: String? = null
}
