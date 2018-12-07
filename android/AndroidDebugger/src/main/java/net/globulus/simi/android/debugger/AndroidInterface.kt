package net.globulus.simi.android.debugger

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import net.globulus.simi.Debugger
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class AndroidInterface private constructor(private val mContext: Context) : Debugger.DebuggerInterface {

    private val mQueue = ArrayBlockingQueue<String>(10)
    private var mBoundActivity: DebuggerActivity? = null
    private var mLaunchingActivity = false
    private var mBuffer = StringBuilder()

    private fun launchActivity(command: String, text: String) {
        mLaunchingActivity = true
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(mContext, DebuggerActivity::class.java)
            intent.putExtra(command, text)
            mContext.startActivity(intent)
        }
    }

    internal fun setBoundActivity(activity: DebuggerActivity?) {
        mBoundActivity = activity
        if (mBoundActivity != null) {
            mBoundActivity!!.flush(mBuffer.toString())
            mLaunchingActivity = false
            mBuffer = StringBuilder()
        }
    }

    override fun flush(s: String) {
        if (mBoundActivity == null) {
            if (mLaunchingActivity) {
                mBuffer.append(s).append("\n")
            } else {
                launchActivity(DebuggerActivity.BUNDLE_PRINTLN, s)
            }
        } else {
            mBoundActivity!!.flush(s)
        }
    }

    override fun read(): String? {
        return null
    }

    override fun getInputQueue(): BlockingQueue<String> {
        return mQueue
    }

    override fun resume() {
        mBoundActivity?.finish()
    }

    companion object {

        private var sInstance: AndroidInterface? = null

        fun sharedInstance(context: Context): AndroidInterface {
            if (sInstance == null) {
                sInstance = AndroidInterface(context)
            }
            return sInstance!!
        }
    }
}
