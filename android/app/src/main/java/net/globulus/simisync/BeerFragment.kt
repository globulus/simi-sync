package net.globulus.simisync

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [BeerFragment.OnListFragmentInteractionListener] interface.
 */
class BeerFragment : Fragment() {

    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    var items = mutableListOf<Beer>()
    var list: RecyclerView? = null
    var swipeRefresh: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_beer_list, container, false)
        list = view.findViewById(R.id.list)
        with(list!!) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(view.context)
                else -> GridLayoutManager(view.context, columnCount)
            }
            adapter = BeerRecyclerViewAdapter(items, listener)

        }
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh?.setOnRefreshListener { fetchData() }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
        fetchData()
    }

    internal fun fetchData() {
        val callback = NetCallback({response ->
            update(Beer.fromList(response))
        }, {response ->
            println(SimiMapper.fromSimiValue(response))
        })
        ActiveSimi.eval("BeerApp", "get", SimiValue.String(EasyPrefs.getCookie(context)),
                callback.getSuccessCallable(), callback.getErrorCallable())
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun update(values: List<Beer>) {
        activity?.runOnUiThread {
            items.clear()
            items.addAll(values)
            list?.adapter?.notifyDataSetChanged()
            swipeRefresh?.isRefreshing = false
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Beer)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                BeerFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
