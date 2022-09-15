package com.cosine.exchange.service

import org.bukkit.entity.Player

interface ExchangeService {

    fun beginExchange(self: Player, target: Player)

    fun acceptExchange()

    fun refuseExchange()
}