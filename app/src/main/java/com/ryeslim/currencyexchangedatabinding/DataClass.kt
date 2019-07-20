package com.ryeslim.currencyexchangedatabinding

import java.math.BigDecimal

data class Currency(var balanceValue: BigDecimal, var commissionsValue: BigDecimal, val currencyCode: String)