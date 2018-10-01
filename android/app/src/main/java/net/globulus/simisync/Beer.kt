package net.globulus.simisync

import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue

data class Beer(val guid: String, val date: Long, val brand: String, val quantity: Int) {
    companion object {
        fun fromList(prop: SimiValue): List<Beer> {
            val beers = mutableListOf<Beer>()
            val list = SimiMapper.fromArray(prop.`object`)
            for (item in list) {
                val map = item as Map<String, Any>
                beers.add(Beer(map["guid"] as String, ((map["date"] as Map<*, *>)["timestamp"] as Long),
                        ActiveSimi.eval("BeerApp", "brandForGuid", SimiValue.String(map["brand"] as String)).value.string,
                                (map["quantity"] as Double).toInt()))
            }
            return beers
        }
    }
}
