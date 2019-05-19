package com.barron9.phone

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
import android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_dialer.*


class DialerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        phoneNumberInput.setText(intent?.data?.schemeSpecificPart)
        phoneNumberInput.setShowSoftInputOnFocus(false);
        parseNumber()
    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        buttoncall.setOnClickListener { makeCall() }
        deletebutton.setOnClickListener {
            phoneNumberInput.text = Editable.Factory.getInstance().newEditable("")
        }

        phoneNumberInput.setOnEditorActionListener { _, _, _ ->
            makeCall()
            true
        }

    }

    private fun makeCall() {

        if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
            val uri = "tel:${phoneNumberInput.text}".toUri()
            startActivity(Intent(Intent.ACTION_CALL, uri))
        } else {
            requestPermissions(this, arrayOf(CALL_PHONE), REQUEST_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION && PERMISSION_GRANTED in grantResults) {
            makeCall()
        }
    }

    private fun offerReplacingDefaultDialer() {

        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            Intent(ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                .let(::startActivity)
        }


    }

    private fun parseNumber() {
        button0.setOnClickListener { phoneNumberInput.append("0") }
        button1.setOnClickListener { phoneNumberInput.append("1") }
        button2.setOnClickListener { phoneNumberInput.append("2") }
        button3.setOnClickListener { phoneNumberInput.append("3") }
        button4.setOnClickListener { phoneNumberInput.append("4") }
        button5.setOnClickListener { phoneNumberInput.append("5") }
        button6.setOnClickListener { phoneNumberInput.append("6") }
        button7.setOnClickListener { phoneNumberInput.append("7") }
        button8.setOnClickListener { phoneNumberInput.append("8") }
        button9.setOnClickListener { phoneNumberInput.append("9") }
        button11.setOnClickListener { phoneNumberInput.append("#") }
        button10.setOnClickListener { phoneNumberInput.append("*") }


    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }
}
