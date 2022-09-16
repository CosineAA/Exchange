package com.cosine.exchange.service

import java.util.UUID

interface EconomyService {

    fun getPlayerMoney(uuid: UUID): Double

    fun depositPlayerMoney(uuid: UUID, money: Double)

    fun withdrawPlayerMoney(uuid: UUID, money: Double)
}