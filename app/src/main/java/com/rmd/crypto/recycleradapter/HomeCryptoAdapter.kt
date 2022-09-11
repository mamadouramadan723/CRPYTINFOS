package com.rmd.crypto.recycleradapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmd.crypto.R
import com.rmd.crypto.constant.Constants
import com.rmd.crypto.databinding.RowHomeCryptoBinding
import com.rmd.crypto.model.HomeCrypto
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.util.*

class HomeCryptoAdapter : RecyclerView.Adapter<HomeCryptoAdapter.HomeCryptoViewHolder>() {
    private var cryptoCoins: List<HomeCrypto> = Collections.emptyList()

    private lateinit var binding: RowHomeCryptoBinding

    override fun getItemCount(): Int = cryptoCoins.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCryptoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_home_crypto, parent, false)
        return HomeCryptoViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: HomeCryptoViewHolder, position: Int) {
        binding = RowHomeCryptoBinding.bind(holder.itemView)

        val coin = cryptoCoins[position]

        holder.itemView.apply {
            val df = DecimalFormat("#.###")
            binding.coinNameAndSymbolTv.text =  "${coin.name} | ${coin.symbol}"
            binding.priceUsdTv.text = "$ ${df.format(coin.price)}"
            binding.oneHourVariationTv.text = "1h : ${df.format(coin.percent_change_1h)} %"
            binding.oneDayVariationTv.text = "24h : ${df.format(coin.percent_change_24h)} %"
            binding.oneWeekVariationTv.text = "7d : ${df.format(coin.percent_change_7d)} %"

            /**
             *  Picasso for async image loading
             */
            Picasso.get()
                .load(Constants.imageUrl + coin.symbol.toLowerCase() + ".png")
                .resize(150, 150).into(binding.coinIcon)

            /**
             *  Set color of percentage change textview to reflect
             *  if the percentage change was negative or positive
             *  But this doesn't respect the SOLID Principal so TODO
             */
            binding.oneHourVariationTv.setTextColor(
                Color.parseColor(
                    when {
                        coin.percent_change_1h < 0 -> "#ff0000"
                        else -> "#32CD32"
                    }
                )
            )

            binding.oneDayVariationTv.setTextColor(
                Color.parseColor(
                    when {
                        coin.percent_change_24h < 0 -> "#ff0000"
                        else -> "#32CD32"
                    }
                )
            )

            binding.oneWeekVariationTv.setTextColor(
                Color.parseColor(
                    when {
                        coin.percent_change_7d < 0 -> "#ff0000"
                        else -> "#32CD32"
                    }
                )
            )
        }
    }

    inner class HomeCryptoViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateData(cryptoCoins: List<HomeCrypto>) {
        this.cryptoCoins = cryptoCoins
        notifyDataSetChanged()
    }
}

