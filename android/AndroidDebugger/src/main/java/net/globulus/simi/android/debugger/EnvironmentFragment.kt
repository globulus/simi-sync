package net.globulus.simi.android.debugger

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.Serializable

class EnvironmentFragment(private val listener: EnvironmentAdapter.EnvironmentTapListener) : Fragment() {

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater,
                        container: ViewGroup?,
                        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_environment, container, false)
        recyclerView = rootView.findViewById(R.id.recycler_environment)
        val source = arguments?.getSerializable(PROPERTIES) as DumpSource
        recyclerView?.adapter = EnvironmentAdapter(source, listener)
        return rootView
    }

    fun update() {
        val adapter = (recyclerView?.adapter as EnvironmentAdapter)
        adapter.update()
    }

    companion object {
        const val PROPERTIES = "properties"

        fun <T> newInstance(source: T, listener: EnvironmentAdapter.EnvironmentTapListener): EnvironmentFragment where T : DumpSource, T : Serializable {
            val fragment = EnvironmentFragment(listener)
            val args = Bundle()
            args.putSerializable(PROPERTIES, source)
            fragment.arguments = args
            return fragment
        }
    }

    interface DumpSource {
        fun update(): Map<String, String>
    }
}
