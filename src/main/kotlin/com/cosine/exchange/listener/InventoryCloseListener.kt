package com.cosine.exchange.listener

import com.cosine.exchange.manager.TradeManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseListener() : Listener {

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val uuid = player.uniqueId
        val trade = TradeManager.getTrade(player) ?: return
        val partner = trade.getTradingPartner(uuid)
        if (trade.getSecondReady(uuid) && trade.getSecondReady(partner)) return // 둘 다 2차 레디 상태 확인
        trade.cancelExchange(uuid)
    }
}