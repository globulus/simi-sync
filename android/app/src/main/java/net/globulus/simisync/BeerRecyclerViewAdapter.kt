package net.globulus.simisync


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_beer.view.*
import net.globulus.simisync.BeerFragment.OnListFragmentInteractionListener
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [Beer] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class BeerRecyclerViewAdapter(
        private val mValues: List<Beer>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<BeerRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Beer
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_beer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mDateView.text = SimpleDateFormat("MM/dd, HH:mm", Locale.getDefault()).format(Date(item.date.toLong()))
        holder.mBrandView.text = item.brand
        holder.mQuantityView.text = item.quantity.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mDateView: TextView = mView.date
        val mBrandView: TextView = mView.brand
        val mQuantityView: TextView = mView.quantity

        override fun toString(): String {
            return super.toString() + " '" + mDateView.text + "'" + " " + mBrandView.text + " " + mQuantityView.text
        }
    }

}
