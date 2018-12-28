package id.co.eltisia.kyranatest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.kotlinpermissions.KotlinPermissions
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.DefaultPrinter
import com.mazenrashed.printooth.data.Printable
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Bluetooth
import com.mazenrashed.printooth.utilities.PrintingCallback

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger {
    val TAG = "LOGkyrana"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Printooth.init(this)
        Printooth.printer().printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                info("Connected ${Printooth.hasPairedPrinter()}")
            }

            override fun printingOrderSentSuccessfully() {
                alert {
                    alert("Print Berhasil", "Silahkan cek printer") {
                        yesButton { toast("ok") }
                    }.show()
                }
            }  //printer was received your printing order successfully.

            override fun connectionFailed(error: String) {
                info("$TAG connect error $error")
            }

            override fun onError(error: String) {
                info("$TAG printer error $error")
            }

            override fun onMessage(message: String) {
                info("$TAG message $message")
            }
        }

        btn_print.setOnClickListener {
            var printables = ArrayList<Printable>().apply {
                add(
                    Printable.PrintableBuilder()
                        .setImage(R.mipmap.ic_test, resources)
                        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
                        .build()
                )
                add(
                    Printable.PrintableBuilder()
                        .setText(txt_print.text.toString())
                        .setAlignment(DefaultPrinter.ALLIGMENT_CENTER)
                        .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                        .build()
                )
            }
            Printooth.printer().print(printables)
        }
        fab.setOnClickListener { view ->
            KotlinPermissions.with(this)
                .permissions(Manifest.permission.BLUETOOTH_ADMIN)
                .permissions(Manifest.permission.BLUETOOTH)
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .onAccepted {
                    toast("accepted")
                    startActivityForResult(
                        Intent(this, ScanningActivity::class.java),
                        ScanningActivity.SCANNING_FOR_PRINTER
                    )
                }
                .onDenied {

                }
                .ask()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            info(Printooth.getPairedPrinter())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
