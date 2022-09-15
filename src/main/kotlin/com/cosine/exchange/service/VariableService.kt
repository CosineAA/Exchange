package com.cosine.exchange.service

import java.util.UUID

interface VariableService {

    fun hasTrader(self: UUID): Boolean

    fun addTrader(self: UUID, target: UUID)

    fun isAccepted(self: UUID): Boolean

    fun deleteExchange(self: UUID)
}