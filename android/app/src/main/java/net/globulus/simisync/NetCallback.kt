package net.globulus.simisync

import net.globulus.simi.api.*

class NetCallback(private val success: (SimiObject) -> Unit, private val error: (SimiObject) -> Unit) {
    fun getSuccessCallable(): SimiValue.Callable {
        return SimiValue.Callable(object : SimiCallable {
            override fun call(p0: BlockInterpreter?, p1: MutableList<SimiProperty>?, p2: Boolean): SimiProperty? {
                success(p1!![0].value.`object`)
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
                success(p1!![0].value.`object`)
                return null
            }

            override fun arity(): Int {
                return 1
            }
        }, "error", null)
    }
}