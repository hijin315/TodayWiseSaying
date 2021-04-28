package com.jinny.wisesaying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }
    private val progress: ProgressBar by lazy {
        findViewById(R.id.progress)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initData()
    }

    private fun initViews() {
        viewPager.setPageTransformer { page, position ->
            when {
                position.absoluteValue >= 1.0F -> {
                    page.alpha = 0F
                }
                position == 0F -> {
                    page.alpha = 1F
                }
                else -> {
                    page.alpha = 1F - 2 * position.absoluteValue
                }
            }

        }
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            // 비동기로 세팅
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            progress.visibility = View.GONE
            if (it.isSuccessful) {
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameShow = remoteConfig.getBoolean("is_name_show")
                displayQuotes(quotes, isNameShow)
            }
        }
    }

    private fun parseQuotesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()
        for (index in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(index)
            obj?.let {
                jsonList = jsonList + it
            }
        }
        return jsonList.map {
            Quote(it.getString("quote"), it.getString("name"))
        }
    }

    private fun displayQuotes(quotes: List<Quote>, isNameShow: Boolean) {
        val adapter = QuotePageAdapter(quotes, isNameShow)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount / 2, false)
    }
}