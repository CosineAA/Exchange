package com.cosine.exchange.service

import java.util.UUID

interface VariableService {

    fun hasTrader(self: UUID) : Boolean
}