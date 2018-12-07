package net.globulus.simi.android.debugger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EnvironmentAdapter(private val dumpSource: EnvironmentFragment.DumpSource,
                         private val listener: EnvironmentTapListener)
    : RecyclerView.Adapter<EnvironmentAdapter.PropertyViewHolder>() {

    private var items: List<Map.Entry<String, String>>? = null

    init {
        update()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        return PropertyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false))
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.update(items!![position], listener)
    }

    fun update() {
        items = dumpSource.update().entries.toList()
        notifyDataSetChanged()
    }

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.text_name)
        private val value: TextView = itemView.findViewById(R.id.text_value)

        fun update(prop: Map.Entry<String, String>, listener: EnvironmentTapListener) {
            name.text = prop.key
            name.setOnClickListener { listener.onItemTap(prop) }
            value.text = prop.value
        }
    }

    interface EnvironmentTapListener {
        fun onItemTap(prop: Map.Entry<String, String>)
    }
}
