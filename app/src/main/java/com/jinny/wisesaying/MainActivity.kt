package com.jinny.wisesaying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData(){
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            // 비동기로 세팅
            remoteConfigSettings{
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful){
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameShow  = remoteConfig.getBoolean("is_name_show")
                displayQuotes(quotes,isNameShow)
            }
        }
    }
    private fun parseQuotesJson(json:String):List<Quote>{
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()
        for(index in 0 until jsonArray.length()){
            val obj = jsonArray.getJSONObject(index)
            obj?.let {
                jsonList = jsonList + it
            }
        }
        return jsonList.map {
            Quote(it.getString("quote"),it.getString("name"))
        }
    }
    private fun displayQuotes(quotes:List<Quote>,isNameShow:Boolean){
        viewPager.adapter = QuotePageAdapter(quotes,isNameShow)
    }
}