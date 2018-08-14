package net.globulus.simisync

import net.globulus.simi.api.BlockInterpreter
import net.globulus.simi.api.SimiCallable
import net.globulus.simi.api.SimiProperty
import net.globulus.simi.api.SimiValue

class NetCallback(private val success: (SimiValue) -> Unit, private val error: (SimiValue) -> Unit) {
    fun getSuccessCallable(): SimiValue.Callable {
        return SimiValue.Callable(object : SimiCallable {
            override fun call(p0: BlockInterpreter?, p1: MutableList<SimiProperty>?, p2: Boolean): SimiProperty? {
                success(p1!![0].value)
                return null
            }

            override fun arity(): Int {
                return 1
            }
        }, "success", null)
    }
    fun getErrorCallable(): SimiValue.Callable {
        return SimiValue.Callable(object : SimiCallable {
            override fun call(p0: BlockInterpreter?, p1: MutableList<SimiProperty>?, p2: Boolean): SimiProperty? {
                success(p1!![0].value)
                return null
            }

            override fun arity(): Int {
                return 1
            }
        }, "error", null)
    }
}