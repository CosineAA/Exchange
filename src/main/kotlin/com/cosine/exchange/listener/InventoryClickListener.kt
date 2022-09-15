package com.cosine.exchange.listener

import com.cosine.exchange.manager.TradeManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return
        val player = event.whoClicked as Player
        val trade = TradeManager.getTrade(player) ?: return
        if (inventory == player.openInventory.topInventory) {

        }
    }
}