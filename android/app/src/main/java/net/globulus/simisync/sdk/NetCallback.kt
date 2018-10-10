package net.globulus.simisync.sdk

import net.globulus.simi.api.*

class NetCallback(private val success: (SimiValue) -> Unit, private val error: (SimiValue) -> Unit) {
    fun getSuccessCallable(): SimiValue.Callable {
        return SimiValue.Callable(object : SimiCallable {
            override fun call(interpreter: BlockInterpreter?,
                              environment: SimiEnvironment?,
                              arguments: MutableList<SimiProperty>?,
                              ethrow: Boolean): SimiProperty? {
                success(arguments!![0].value)
                return null
            }

            override fun arity(): Int {
                return 1
            }
        }, "success", null)
    }
    fun getErrorCallable(): SimiValue.Callable {
        return SimiValue.Callable(object : SimiCallable {
            override fun call(interpreter: BlockInterpreter?,
                              environment: SimiEnvironment?,
                              arguments: MutableList<SimiProperty>?,
                              rethrow: Boolean): SimiProperty? {
                error(arguments!![0].value)
                return null
            }

            override fun arity(): Int {
                return 1
            }
        }, "error", null)
    }
}