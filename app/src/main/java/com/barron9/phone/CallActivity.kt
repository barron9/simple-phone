package com.barron9.phone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_call.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    var client: GetData = startclient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data.schemeSpecificPart
    }

    override fun onStart() {
        super.onStart()

        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }
        upbutton.setOnClickListener {
            putdata(true, number)
            downbutton.isVisible = false
            upbutton.isVisible = false
        }
        downbutton.setOnClickListener {
            putdata(false, number)
            upbutton.isVisible = false
            downbutton.isVisible = false

        }
        loadData(number as String)
        OngoingCall.state
            .subscribe(::updateUi)

            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe {
                // finish()
            }
            .addTo(disposables)


    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {

        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n"
        phonenumber.text = "$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }

    private fun loadData(gsm: String) {

        disposables?.add(
            client.get(gsm)
                .onErrorReturn { throwable -> JsonObject() }
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { throwable -> JsonObject() }
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse)
        )

    }

    private fun putdata(vote: Boolean, gsm: String) {
        disposables?.add(
            client.put(gsm, vote)
                .onErrorReturn { throwable -> JsonObject() }
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { throwable -> JsonObject() }
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse)
        )

    }

    private fun startclient(): GetData {

        return Retrofit.Builder()
            .baseUrl("https://6p6s.com/callyapi/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(GetData::class.java)
    }

    private fun handleResponse(cryptoList: JsonObject) {

        Timber.tag("gsmresponse").e(cryptoList?.toString())
        callInfo.text = callInfo.text
        positives.text = cryptoList.get("positives")?.asString
        negatives.text = cryptoList.get("negatives")?.asString

    }

}
