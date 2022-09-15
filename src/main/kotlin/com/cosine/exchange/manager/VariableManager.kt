package com.cosine.exchange.manager

import com.cosine.exchange.service.VariableService
import com.cosine.exchange.util.Variable
import java.util.*

class VariableManager : VariableService {

    override fun hasTrader(self: UUID): Boolean {
        return Variable.traders.containsKey(self)
    }

    override fun addTrader(self: UUID, target: UUID) {
        Variable.traders[self] = target
    }

    override fun isAccepted(self: UUID): Boolean {
        return Variable.acceptOrRefuse[self] == 2
    }

    override fun deleteExchange(self: UUID) {
        Variable.traders.remove(self)
        Variable.acceptOrRefuse.remove(self)
    }
}