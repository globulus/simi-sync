package net.globulus.simi.android.debugger

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_debugger.*
import net.globulus.simi.ActiveSimi
import net.globulus.simi.Debugger
import java.io.Serializable
import java.util.concurrent.BlockingQueue

class DebuggerActivity : AppCompatActivity(), Debugger.DebuggerInterface {

    companion object {
        const val BUNDLE_PRINTLN = "println"
    }

    private var mWatcher: Debugger.DebuggerWatcher? = null
    private var mCallStackShown = true
    private val mStackTrace: List<Debugger.FrameDump>
        get() = (if (mCallStackShown) mWatcher?.callStack else mWatcher?.lineStack) ?: emptyList()

    private val mEnvFragments: MutableSet<EnvironmentFragment?> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debugger)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mWatcher = ActiveSimi.getDebuggerWatcher()
        AndroidInterface.sharedInstance(this).setBoundActivity(this)

        val println = intent.extras.getString(BUNDLE_PRINTLN)
        if (println != null) {
            flush(println)
        }

        container.adapter = PageAdapter(supportFragmentManager)
        tabs.setupWithViewPager(container)

        stackTrace.adapter = StackTraceAdapter(mStackTrace, object: StackTraceAdapter.StackTraceTapListener {
            override fun onItemTap(item: Debugger.FrameDump, pos: Int) {
                sendCommand("i $pos")
            }
        })

        update()

//        input.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                val inputText = input.text.toString()
//                input.setText("")
//                inputQueue.put(inputText)
//                return@OnEditorActionListener true
//            }
//            false
//        })

        btnContinue.setOnClickListener { sendCommand("y") }
        btnEvaluate.setOnClickListener {
            val input = EditText(this)
            input.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            AlertDialog.Builder(this)
                    .setTitle(R.string.evaluate)
                    .setMessage(R.string.input_expression_to_evaluate)
                    .setView(input)
                    .setPositiveButton(R.string.evaluate) { dialog, which ->
                        sendCommand("e ${input.text}")
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
        btnStepInto.setOnClickListener { sendCommand("n") }
        btnStepover.setOnClickListener { sendCommand("v") }
        btnAdd.setOnClickListener { sendCommand("a") }
        btnRemove.setOnClickListener { sendCommand("r") }
        btnExceptions.setOnClickListener { sendCommand("x") }
    }

    override fun finish() {
        AndroidInterface.sharedInstance(this).setBoundActivity(null)
        super.finish()
    }

    override fun flush(s: String?) {
        runOnUiThread {
            if (s?.contains("BREAKPOINT") == true || s?.contains("EXCEPTION") == true) {
                output.setText(s)
            } else {
                Toast.makeText(this, s, Toast.LENGTH_LONG).show()
            }
            update()
        }
    }

    override fun read(): String? {
        return null
    }

    override fun getInputQueue(): BlockingQueue<String> {
        return AndroidInterface.sharedInstance(this).inputQueue
    }

    override fun resume() {
        // This will never be invoked
    }

    private fun update() {
        stackTrace.adapter?.notifyDataSetChanged()
        for (fragment in mEnvFragments) {
            fragment?.update()
        }
    }

    private fun sendCommand(command: String) {
        inputQueue.put(command)
    }

    inner class PageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            val fragment = when (position) {
                0 -> EnvironmentFragment.newInstance(FocusDumpSource(mWatcher), object: EnvironmentAdapter.EnvironmentTapListener {
                    override fun onItemTap(prop: Map.Entry<String, String>) {
                       sendCommand("w ${prop.key}")
                    }
                })
                1 -> EnvironmentFragment.newInstance(GlobalDumpSource(mWatcher), object: EnvironmentAdapter.EnvironmentTapListener {
                    override fun onItemTap(prop: Map.Entry<String, String>) {
                        sendCommand("w ${prop.key}")
                    }
                })
                2 -> EnvironmentFragment.newInstance(WatchDumpSource(mWatcher), object: EnvironmentAdapter.EnvironmentTapListener {
                    override fun onItemTap(prop: Map.Entry<String, String>) {

                    }
                })
                else -> null
            }
            mEnvFragments.add(fragment)
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return getString(when (position) {
                0 -> R.string.focus_env
                1 -> R.string.global_env
                2 -> R.string.watch
                else -> 0
            })
        }
    }

    private abstract class WatcherDumpSource(protected val watcher: Debugger.DebuggerWatcher?): EnvironmentFragment.DumpSource, Serializable

    private class FocusDumpSource(watcher: Debugger.DebuggerWatcher?) : WatcherDumpSource(watcher) {
        override fun update(): Map<String, String> {
            return watcher?.focusFrame?.fullEnvironment ?: emptyMap()
        }
    }

    private class GlobalDumpSource(watcher: Debugger.DebuggerWatcher?) : WatcherDumpSource(watcher) {
        override fun update(): Map<String, String> {
            return watcher?.globalEnvironment ?: emptyMap()
        }
    }

    private class WatchDumpSource(watcher: Debugger.DebuggerWatcher?) : WatcherDumpSource(watcher) {
        override fun update(): Map<String, String> {
            return watcher?.watch ?: emptyMap()
        }
    }
}
