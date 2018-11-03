package net.globulus.simi.android.debugger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_debugger.*
import net.globulus.simi.Debugger

class DebuggerActivity : AppCompatActivity(), Debugger.DebuggerInterface {

    companion object {
        const val BUNDLE_PRINT = "print"
        const val BUNDLE_PRINTLN = "println"
    }

    private var mInput: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debugger)

        AndroidInterface.sharedInstance(this).setBoundActivity(this)

        val print = intent.extras.getString(BUNDLE_PRINT)
        if (print != null) {
            print(print)
        } else {
            val println = intent.extras.getString(BUNDLE_PRINTLN)
            if (println != null) {
                println(println)
            }
        }

        input.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mInput = input.text.toString()
                input.setText("")
                synchronized(lock) {
                    lock.notify()
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun finish() {
        AndroidInterface.sharedInstance(this).setBoundActivity(null)
        super.finish()
    }

    override fun print(s: String?) {
        runOnUiThread {
            output.append(s)
        }
    }

    override fun println(s: String?) {
        runOnUiThread {
            output.append(s + "\n")
        }
    }

    override fun read(): String? {
        val inputCopy = mInput
        mInput = null
        return inputCopy
    }

    override fun getLock(): Object {
        return AndroidInterface.sharedInstance(this).lock
    }

    override fun resume() {
        // This will never be invoked
    }
}
