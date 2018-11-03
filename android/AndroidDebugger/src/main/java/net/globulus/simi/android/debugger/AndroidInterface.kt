package net.globulus.simi.android.debugger

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import net.globulus.simi.Debugger

class AndroidInterface private constructor(private val mContext: Context) : Debugger.DebuggerInterface {

    private val mLock = Object()
    private var mBoundActivity: DebuggerActivity? = null
    private var mLaunchingActivity = false
    private var mBuffer = StringBuilder()

    private fun launchActivity(command: String, text: String) {
        mLaunchingActivity = true
        val a = Looper.myLooper() == Looper.getMainLooper()
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(mContext, DebuggerActivity::class.java)
            intent.putExtra(command, text)
            mContext.startActivity(intent)
        }
    }

    internal fun setBoundActivity(activity: DebuggerActivity?) {
        mBoundActivity = activity
        if (mBoundActivity != null) {
            mBoundActivity!!.print(mBuffer.toString())
            mLaunchingActivity = false
            mBuffer = StringBuilder()
        }
    }

    override fun print(s: String) {
        if (mBoundActivity == null) {
            if (mLaunchingActivity) {
                mBuffer.append(s)
            } else {
                launchActivity(DebuggerActivity.BUNDLE_PRINT, s)
            }
        } else {
            mBoundActivity!!.print(s)
        }
    }

    override fun println(s: String) {
        if (mBoundActivity == null) {
            if (mLaunchingActivity) {
                mBuffer.appendln(s)
            } else {
                launchActivity(DebuggerActivity.BUNDLE_PRINTLN, s)
            }
        } else {
            mBoundActivity!!.println(s)
        }
    }

    override fun read(): String? {
        if (mBoundActivity == null) {
            return null
        }
        return mBoundActivity!!.read()
    }


    override fun getLock(): Object {
        return mLock
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
