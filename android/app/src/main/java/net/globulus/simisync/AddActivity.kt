package net.globulus.simisync

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.content_add.*
import net.globulus.easyprefs.EasyPrefs
import net.globulus.simi.ActiveSimi
import net.globulus.simi.SimiMapper
import net.globulus.simi.api.SimiValue
import net.globulus.simisync.sdk.NetCallback
import java.util.*

class AddActivity : AppCompatActivity() {

    private var brands: List<Map<*, *>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        brands = SimiMapper.fromArray(ActiveSimi.eval("BeerApp", "brands").value.`object`) as List<Map<*, *>>
        println(brands)
        brand.adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, brands?.map { it["name"] as String })

        date.setOnClickListener { v ->
            val calendar = Calendar.getInstance()
            calendar.time = (v.tag as? Date) ?: Date()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { dialog, m, y, d ->v
                calendar.set(y, m, d)
                v.tag = calendar.time
                (v as Button).text = v.tag.toString()
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add) {
            val callback = NetCallback({ response ->
                setResult(Activity.RESULT_OK)
                finish()
            }, { response ->
                finish()
            })
            ActiveSimi.eval("BeerApp", "put",
                    SimiValue.Number((date.tag as Date).time.toDouble()),
                    SimiValue.String(brands!![brand.selectedItemPosition]["guid"] as String),
                    SimiValue.Number(quantity.text.toString().toDouble()),
                    SimiValue.String(EasyPrefs.getCookie(this)),
                    callback.getSuccessCallable(),
                    callback.getErrorCallable())
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
