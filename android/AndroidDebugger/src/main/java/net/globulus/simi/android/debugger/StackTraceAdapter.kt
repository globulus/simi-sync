package net.globulus.simi.android.debugger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.globulus.simi.Debugger

class StackTraceAdapter(private val items: List<Debugger.FrameDump>,
                        private val listener: StackTraceTapListener)
    : RecyclerView.Adapter<StackTraceAdapter.StackTraceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StackTraceViewHolder {
        return StackTraceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_stack_trace, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: StackTraceViewHolder, position: Int) {
        holder.update(items[position], position, listener)
    }

    class StackTraceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val line = itemView.findViewById<TextView>(R.id.line)

        fun update(frame: Debugger.FrameDump, pos: Int, listener: StackTraceTapListener) {
            line.text = frame.line
            (line.parent as View).setOnClickListener {
                listener.onItemTap(frame, pos)
            }
        }
    }

    interface StackTraceTapListener {
        fun onItemTap(item: Debugger.FrameDump, pos: Int)
    }
}
