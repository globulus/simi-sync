package net.globulus.simisync

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*
import net.globulus.easyprefs.EasyPrefs
import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue
import net.globulus.simisync.sdk.NetCallback

class ListActivity : AppCompatActivity(), BeerFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivityForResult(Intent(this, AddActivity::class.java), 1);
        }
    }

    override fun onListFragmentInteraction(item: Beer) {
        AlertDialog.Builder(this)
                .setMessage(R.string.delete_entry)
                .setPositiveButton(R.string.yes) { dialog, which ->
                    val callback = NetCallback({ response ->
                        val f = fragment as BeerFragment
                        f.update(Beer.fromList(response))
                    }, { response ->
                        println(SimiMapper.fromSimiValue(response))
                    })
                    ActiveSimi.eval("BeerApp", "delete", SimiValue.String(item.guid),
                            SimiValue.String(EasyPrefs.getCookie(this)),
                            callback.getSuccessCallable(), callback.getErrorCallable())
                }
                .setNegativeButton(R.string.no, null)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            EasyPrefs.clearAll(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            (fragment as BeerFragment).fetchData()
        }
    }

}
