package com.cosine.exchange.manager

import com.cosine.exchange.manager.InstanceManager.Companion.economy
import com.cosine.exchange.service.EconomyService
import org.bukkit.Bukkit
import java.util.*

object EconomyManager : EconomyService {

    override fun getPlayerMoney(uuid: UUID): Double {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        return economy.getBalance(offlinePlayer)
    }
    // 입금
    override fun depositPlayerMoney(uuid: UUID, money: Double) {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        economy.depositPlayer(offlinePlayer, money)
    }
    // 출금
    override fun withdrawPlayerMoney(uuid: UUID, money: Double) {
        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
        economy.withdrawPlayer(offlinePlayer, money)
    }
}