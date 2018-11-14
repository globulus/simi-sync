package net.globulus.simi.android.debugger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_debugger.*
import net.globulus.simi.Debugger
import java.util.concurrent.BlockingQueue

class DebuggerActivity : AppCompatActivity(), Debugger.DebuggerInterface {

    companion object {
        const val BUNDLE_PRINTLN = "println"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debugger)

        AndroidInterface.sharedInstance(this).setBoundActivity(this)

        val println = intent.extras.getString(BUNDLE_PRINTLN)
        if (println != null) {
            flush(println)
        }

        input.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = input.text.toString()
                input.setText("")
                queue.put(inputText)
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun finish() {
        AndroidInterface.sharedInstance(this).setBoundActivity(null)
        super.finish()
    }

    override fun flush(s: String?) {
        runOnUiThread {
            output.append(s)
        }
    }

    override fun read(): String? {
        return null
    }

    override fun getQueue(): BlockingQueue<String> {
        return AndroidInterface.sharedInstance(this).queue
    }

    override fun resume() {
        // This will never be invoked
    }
}
