package com.rmd.crypto.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rmd.crypto.R
import com.rmd.crypto.databinding.FragmentHomeBinding
import com.rmd.crypto.model.HomeCrypto
import com.rmd.crypto.recycleradapter.HomeCryptoAdapter
import org.json.JSONException


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeCryptoAdapter: HomeCryptoAdapter
    private lateinit var layoutManagerHomeCrypto: LinearLayoutManager


    private var cryptoCoins: MutableList<HomeCrypto> = arrayListOf()
    private var myCoinsSymbols: MutableList<String> = arrayListOf(
        "LUNC",
        "QUACK",
        "USTC",
        "ATOM",
        "XEN",
        "CHAMPS",
        "APT",
        "BNB",
        "OSMO"
    )
    private  var myCoinsName : MutableList<String> = arrayListOf(
        "Terra Classic",
        "TerraClassicUSD",
        "Cosmos",
        "XEN Crypto",
        "RichQUACK.com",
        "FIFA Champs",
        "Aptos",
        "BNB",
        "Osmosis"
    )
    private val apiUrl =
        "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=LUNC,QUACK,ATOM,USTC,XEN,CHAMPS,APT,BNB,OSMO"
    //"https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=100&convert=USD"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        //LayoutManager for recyclerview
        layoutManagerHomeCrypto = LinearLayoutManager(context)
        layoutManagerHomeCrypto.orientation = LinearLayoutManager.VERTICAL

        homeCryptoAdapter = HomeCryptoAdapter()
        binding.homeCryptoRvw.layoutManager = layoutManagerHomeCrypto
        binding.homeCryptoRvw.adapter = homeCryptoAdapter

        binding.swipeLayout.setOnRefreshListener {
            Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show()
            refresh()
        }
        getCoins()
    }

    private fun getCoins() {

        // creating a variable for request queue.
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            apiUrl,
            null,
            Response.Listener { response ->
                try {
                    // on below line we are getting data from our response
                    // and setting it in variables
                    // extracting data from json.

                    val dataObject = response?.getJSONObject("data")

                    if (dataObject != null) {
                        cryptoCoins.clear()

                        for (symbol in myCoinsSymbols){
                            val dataArray = dataObject.getJSONArray(symbol)

                            for (i in 0 until dataArray.length()){
                                val cryptoCoin = HomeCrypto()

                                val dataObj = dataArray.getJSONObject(i)
                                val name = dataObj.getString("name")
                                if (name in myCoinsName){
                                    val quote = dataObj.getJSONObject("quote")
                                    val usd = quote.getJSONObject("USD")

                                    // adding all data to our array list.
                                    cryptoCoin.id = dataObj.getString("id")
                                    cryptoCoin.name = dataObj.getString("name")
                                    cryptoCoin.symbol = symbol
                                    cryptoCoin.price = usd.getDouble("price")
                                    cryptoCoin.percent_change_1h = usd.getDouble("percent_change_1h")
                                    cryptoCoin.percent_change_24h = usd.getDouble("percent_change_24h")
                                    cryptoCoin.percent_change_7d = usd.getDouble("percent_change_7d")

                                    cryptoCoins.add(cryptoCoin)
                                }
                            }
                        }

                        //notifying adapter on data change.

                        val stringBuilder = StringBuilder()
                        stringBuilder.append("$cryptoCoins")
                        Log.d("+++---coins : ", "$stringBuilder")
                        homeCryptoAdapter.updateData(cryptoCoins.sortedBy { it.percent_change_1h })
                    }

                    //ipInfos.ip = response.getString("ip")
                } catch (e: JSONException) {
                    // handling json exception.
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "Something went amiss. Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener { // displaying error response when received any error.
                Toast.makeText(
                    context,
                    "Something went amiss. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                // in this method passing headers as
                // key along with value as API keys.
                val headers: HashMap<String, String> = HashMap()
                headers["X-CMC_PRO_API_KEY"] = resources.getString(R.string.coinMarketCapApiKey)
                // at last returning headers
                return headers
            }
        }

        // calling a method to add our
        // json object request to our queue.
        queue.add(jsonObjectRequest)
    }

    private fun refresh() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            parentFragmentManager.beginTransaction().detach(this).commitNow()
            parentFragmentManager.beginTransaction().attach(this).commitNow()
            binding.swipeLayout.isRefreshing = false
        } else {
            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
            binding.swipeLayout.isRefreshing = false
        }
    }

}