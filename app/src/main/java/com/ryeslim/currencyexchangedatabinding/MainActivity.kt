package com.ryeslim.currencyexchangedatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.ryeslim.currencyexchangedatabinding.databinding.ActivityMainBinding
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private val mViewModel: CurrencyViewModel by lazy {
        ViewModelProviders.of(this).get(CurrencyViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setViews()
        binding.convertButton.setOnClickListener { manageConversion() }
    }

    fun amountFrom() {
        if (binding.amountFrom.text.toString().trim().length > 0)
            mViewModel.amountFrom =
                binding.amountFrom.text.toString().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        else mViewModel.amountFrom = -1.toBigDecimal()
    }

    fun currencyFrom() {
        mViewModel.indexFrom = when (binding.radioGroupFrom.getCheckedRadioButtonId()) {
            binding.fromEur.getId() -> 0
            binding.fromUsd.getId() -> 1
            binding.fromJpy.getId() -> 2
            else -> -1
        }
    }

    fun currencyTo() {
        mViewModel.indexTo = when (binding.radioGroupTo.getCheckedRadioButtonId()) {
            binding.toEur.getId() -> 0
            binding.toUsd.getId() -> 1
            binding.toJpy.getId() -> 2
            else -> -1
        }
    }

    fun manageConversion() {
        var errorMessage: String? = null

        mViewModel.numberOfOperations++

        amountFrom()
        currencyFrom()
        currencyTo()

        //Calculated here to make sure, in the next step,
        //that the funds are sufficient
        mViewModel.calculateCommission()

        //Error check
        if (mViewModel.amountFrom < 0.toBigDecimal()) {
            errorMessage = getString(R.string.enter_the_amount)
        } else if (binding.radioGroupFrom.getCheckedRadioButtonId() == -1
            || binding.radioGroupTo.getCheckedRadioButtonId() == -1
            || mViewModel.indexFrom == mViewModel.indexTo
        ) {
            errorMessage = getString(R.string.radio_button_error)
        } else if (mViewModel.amountFrom + mViewModel.thisCommission > mViewModel.currencies[mViewModel.indexFrom].balanceValue) {
            errorMessage = getString(R.string.insufficient_funds)
        }

        //Error message
        if (errorMessage != null) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            mViewModel.numberOfOperations--
        } else {
            //No errors
            mViewModel.makeUrl()
            mViewModel.getResultFromNetwork()
            mViewModel.calculateValues()
            mViewModel.makeInfoMessage()
            setViews()
        }
        return
    }

    fun setViews() {

        binding.apply {
            radioGroupFrom.clearCheck()
            radioGroupTo.clearCheck()
            amountFrom.text.clear()
            eurBalanceValue.text = String.format("%.2f", mViewModel.currencies[0].balanceValue)
            usdBalanceValue.text = String.format("%.2f", mViewModel.currencies[1].balanceValue)
            jpyBalanceValue.text = String.format("%.2f", mViewModel.currencies[2].balanceValue)
            eurCommissionsValue.text = String.format("%.2f", mViewModel.currencies[0].commissionsValue)
            usdCommissionsValue.text = String.format("%.2f", mViewModel.currencies[1].commissionsValue)
            jpyCommissionsValue.text = String.format("%.2f", mViewModel.currencies[2].commissionsValue)
            infoMessage.text = mViewModel.infoMessage
        }
    }
}
