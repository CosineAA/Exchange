package com.cosine.exchange.manager

import com.cosine.exchange.service.VariableService
import com.cosine.exchange.util.Variable
import java.util.*

class VariableManager : VariableService {

    override fun hasTrader(self: UUID): Boolean {
        return Variable.traders.containsKey(self)
    }
}