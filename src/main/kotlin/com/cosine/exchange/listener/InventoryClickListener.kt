package com.cosine.exchange.listener

import com.cosine.exchange.main.Exchange
import com.cosine.exchange.main.Exchange.Companion.prefix
import com.cosine.exchange.manager.TradeManager
import com.cosine.exchange.service.InstanceService
import com.cosine.exchange.util.getPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.UUID

class InventoryClickListener(private val instance: InstanceService) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return
        val player = event.whoClicked as Player
        val uuid = player.uniqueId
        val trade = TradeManager.getTrade(player) ?: return
        trade.synchronizationInventory(uuid) // 인벤토리 동기화
        if (inventory == player.openInventory.topInventory) {
            if (trade.getClickAllowedSlot(uuid).contains(event.rawSlot)) {
                if (event.rawSlot == 18) setFirstReadyState(uuid, trade)
                if (event.rawSlot == 27) setSecondReadyState(uuid, trade)
                if (event.rawSlot == 46) setSendingMoney(uuid, 10000, trade, event)
                if (event.rawSlot == 47) setSendingMoney(uuid, 100000, trade, event)
                if (event.rawSlot == 48) setSendingMoney(uuid, 1000000, trade, event)
            } else {
                event.currentItem ?: return
                event.isCancelled = true
            }
        }
    }
    private fun setSendingMoney(uuid: UUID, money: Int, trade: TradeManager, event: InventoryClickEvent) {
        if (event.click == ClickType.LEFT) {
            if (instance.economyManager.getPlayerMoney(uuid) < money) {
                getPlayer(uuid).sendMessage("$prefix 돈이 부족합니다.")
                return
            }
            trade.addSendingMoney(uuid, money)
        }
        if (event.click == ClickType.RIGHT) {
            if (trade.getSendingMoney(uuid) < money) return
            trade.subtractSendingMoney(uuid, money)
        }
    }
    private fun setFirstReadyState(self: UUID, trade: TradeManager) {
        if (!trade.getFirstReady(self)) {
            trade.setFirstReady(self, true) // 1차 준비 완료
            return
        }
        trade.setFirstReady(self, false) // 본인 1차 준비 해제
        trade.setSecondReady(self, false) // 본인 2차 준비 해제
    }
    private fun setSecondReadyState(self: UUID, trade: TradeManager) {
        if (!trade.getFirstReady(self)) {
            getPlayer(self).sendMessage("$prefix 아직 1차 준비를 하지 않았습니다.")
            return
        }
        val partner = trade.getTradingPartner(self)
        if (trade.getFirstReady(partner)) {
            getPlayer(self).sendMessage("$prefix 상대가 아직 1차 준비를 하지 않았습니다.")
            return
        }
        trade.setSecondReady(self, false)
        isPartnerSecondReady(self, partner, trade)
    }
    private fun isPartnerSecondReady(self: UUID, partner: UUID, trade: TradeManager) {
        if (trade.getSecondReady(partner)) return
        trade.successExchange(self)
    }
}