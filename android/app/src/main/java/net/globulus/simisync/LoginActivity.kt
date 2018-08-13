package net.globulus.simisync

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue
import java.io.File
import java.net.URL


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptLogin() }
        ActiveSimi.setImportResolver(object : ActiveSimi.ImportResolver {
            override fun useApiClassName(p0: String?): Boolean {
                return false
            }

            override fun readFile(path: String?): String {
                return application.assets.open(path).bufferedReader().use { it.readText() }
            }

            override fun resolve(s: String?): URL {
//                var loc = s!!
//                if (loc.startsWith('.')) {
//                    loc = loc.substring(1)
//                }
//                if (!loc.startsWith('/')) {
//                    loc = "/$loc"
//                }
//                return Paths.get("android_asset$loc").toUri().toURL()
                val fileName = s!!.substring(s.lastIndexOf('/') + 1)
                val libMain = File(getDir("libs", 0), fileName)
                return libMain.toURL()
            }
//            override fun getClassLoader(path: String): ClassLoader? {
//                return null
////                var s = path
////                if (s.startsWith("./")) {
////                    s = s.substring(2)
////                }
////                val buffSize = 8 * 1024
////                val fileName = s.substring(s.lastIndexOf('/') + 1)
////                val dexInternalStoragePath = File(getDir("dex", Context.MODE_PRIVATE), fileName)
////                dexInternalStoragePath.parentFile.mkdirs()
////                dexInternalStoragePath.createNewFile()
////                var bis: BufferedInputStream?
////                var dexWriter: OutputStream?
////                try {
////                    bis = BufferedInputStream(assets.open(s))
////                    dexWriter = BufferedOutputStream(FileOutputStream(dexInternalStoragePath))
////                    val buf = ByteArray(buffSize)
////                    var len: Int
////                    do {
////                        len = bis.read(buf, 0, buffSize)
////                        if (len <= 0) {
////                            break
////                        }
////                        dexWriter.write(buf, 0, len)
////                    } while (true)
////                    dexWriter.close()
////                    bis.close()
////                } catch (e: IOException) {
////                    e.printStackTrace()
////                }
////                val optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE)
//////                val command = arrayOf("--dex", "--output=$dexInternalStoragePath", "--min-sdk-version=26", "$dexInternalStoragePath")
//////                com.android.dx.command.Main.main(command)
////                val classLoader = MyDexClassLoader(dexInternalStoragePath.absolutePath,
////                        optimizedDexOutputPath.absolutePath, null,
////                        classLoader)
////                return classLoader
//            }
        })
        ActiveSimi.load("SimiSync.simi")
        ActiveSimi.eval("SimiSync", "configure", SimiValue.String("http://10.0.2.2:8888"), SimiValue.Number(1.0))
        val callback = NetCallback({response ->
            println(SimiMapper.fromObject(response).toString())
        }, {response ->
            println(SimiMapper.fromObject(response).toString())
        })
        try {
            ActiveSimi.eval("SimiSync", "sync", callback.getSuccessCallable(), callback.getErrorCallable())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            val creds = SimiValue.Object(SimiMapper.toObject(mapOf("email" to SimiValue.String(emailStr), "password" to SimiValue.String(passwordStr)), true))
            val callback = NetCallback({response ->
                println(SimiMapper.fromObject(response).toString())
            }, {response ->
                println(SimiMapper.fromObject(response).toString())
            })
            ActiveSimi.eval("ClientTasks", "postLogin", creds, callback.getSuccessCallable(), callback.getErrorCallable())
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}
