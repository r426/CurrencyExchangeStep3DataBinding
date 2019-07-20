package com.ryeslim.currencyexchangedatabinding

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyViewModel : ViewModel() {

    val euro = Currency(1000.toBigDecimal(), 0.toBigDecimal(), "EUR")
    val dollar = Currency(0.toBigDecimal(), 0.toBigDecimal(), "USD")
    val yen = Currency(0.toBigDecimal(), 0.toBigDecimal(), "JPY")
    var infoMessage = ""

    val currencies = arrayOf(euro, dollar, yen)

    var amountFrom = -1.toBigDecimal()
    var amountResult = 0.toBigDecimal()
    var indexFrom = -1
    var indexTo = -1
    var url = ""
    var thisCommission = 0.toBigDecimal()
    var numberOfOperations = 0


    fun makeUrl() {
        var url: String =
            "http://api.evp.lt/currency/commercial/exchange/$amountFrom-${currencies[indexFrom].currencyCode}/${currencies[indexTo].currencyCode}/latest"
    }

    fun getResultFromNetwork() {
        //go to url via retrofit/volley
        //and get result in JSON

        //some fake number
        amountResult = 50.toBigDecimal()
    }

    fun calculateValues() {
        currencies[indexFrom].balanceValue =
            currencies[indexFrom].balanceValue.minus(amountFrom).minus(thisCommission)

        currencies[indexTo].balanceValue = currencies[indexTo].balanceValue.plus(amountResult)

        currencies[indexFrom].commissionsValue = currencies[indexFrom].commissionsValue.plus(thisCommission)
    }

    fun calculateCommission() {
        if (numberOfOperations > 5) {
            thisCommission = zeroSevenPercent(amountFrom, 0.7.toBigDecimal())
        } else {
            thisCommission = 0.toBigDecimal()
        }
    }

    fun zeroSevenPercent(value: BigDecimal, percent: BigDecimal) = (value * percent / 100.toBigDecimal()).setScale(
        2, RoundingMode.HALF_EVEN
    )

    fun makeInfoMessage() {
        infoMessage = String.format(
            "You converted %.2f %s to %.2f %s. Commissions paid: %.2f %s",
            amountFrom,
            currencies[indexFrom].currencyCode,
            amountResult,
            currencies[indexTo].currencyCode,
            thisCommission,
            currencies[indexFrom].currencyCode
        )
    }
}