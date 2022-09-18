package com.cosine.exchange.manager

import com.cosine.exchange.main.Exchange
import com.cosine.exchange.service.EconomyService
import org.bukkit.Bukkit
import java.util.*

class EconomyManager(private val plugin: Exchange) : EconomyService {

    override fun getPlayerMoney(uuid: UUID): Double {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        return plugin.economy.getBalance(offlinePlayer)
    }
    // 입금
    override fun depositPlayerMoney(uuid: UUID, money: Double) {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        plugin.economy.depositPlayer(offlinePlayer, money)
    }
    // 출금
    override fun withdrawPlayerMoney(uuid: UUID, money: Double) {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        plugin.economy.withdrawPlayer(offlinePlayer, money)
    }
}