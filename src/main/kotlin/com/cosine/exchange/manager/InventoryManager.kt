package com.cosine.exchange.manager

import com.cosine.exchange.util.getPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.text.DecimalFormat
import java.util.UUID


class InventoryManager {

    fun createInventory(self: Player, target: Player): Inventory {
        val tradeInventory = Bukkit.createInventory(null, 54, "${target.name}님과의 거래")

        setItem("§f", Material.STAINED_GLASS_PANE, 15, 1, tradeInventory, 9, 13, 17, 22, 31, 36, 40, 44)
        setItem("§f", Material.STAINED_GLASS_PANE, 8, 1, tradeInventory, 14, 15, 16, 23, 24, 25, 32, 33, 34, 41, 42, 43)

        setItem("§f[ §6거래 준비 §f]", listOf("§f클릭 시 거래를 준비합니다."), Material.STAINED_GLASS_PANE, 14, 1, tradeInventory, 18)
        setItem("§f[ §a거래 수락 §f]", listOf("§f클릭 시 거래를 수락합니다."), Material.STAINED_GLASS_PANE, 14, 1, tradeInventory, 27)

        setItem("§f[ §6거래 준비 §f]", listOf("§f상대방의 거래 준비 상태입니다."), Material.STAINED_GLASS_PANE, 14, 1, tradeInventory, 26)
        setItem("§f[ §a거래 수락 §f]", listOf("§f상대방의 거래 수락 상태입니다."), Material.STAINED_GLASS_PANE, 14, 1, tradeInventory, 35)

        setItem("§f[ §e1만원 추가 §f]", listOf("§f클릭시 1만원을 추가합니다."), Material.GOLD_NUGGET, 0, 1, tradeInventory, 46)
        setItem("§f[ §e10만원 추가 §f]", listOf("§f클릭시 10만원을 추가합니다."), Material.GOLD_INGOT, 0, 1, tradeInventory, 47)
        setItem("§f[ §e100만원 추가 §f]", listOf("§f클릭시 100만원을 추가합니다."), Material.GOLD_BLOCK, 0, 1, tradeInventory, 48)

        skull(self, listOf("§e보내는 돈§f: 0원"), tradeInventory, 2)
        skull(target, listOf("§e보내는 돈§f: 0원"), tradeInventory, 6)

        return tradeInventory
    }

    fun refreshSendingMoney(money: Int, uuid: UUID, selfInventory: Inventory, partnerInventory: Inventory) {
        val cleanMoney = DecimalFormat("#,###").format(money)
        skull(getPlayer(uuid), listOf("§e보내는 돈§f: ${cleanMoney}원"), partnerInventory, 6)
        skull(getPlayer(uuid), listOf("§e보내는 돈§f: ${cleanMoney}원"), selfInventory, 2)
    }

    fun isFirstReady(state: Boolean, selfInventory: Inventory, partnerInventory: Inventory) {
        if (state) {
            setItem("§f[ §6거래 준비 §f]", listOf("§f클릭 시 거래를 준비합니다."), Material.STAINED_GLASS_PANE, 13, 1, selfInventory, 18)
            setItem("§f[ §6거래 준비 §f]", listOf("§f상대방의 거래 준비 상태입니다."), Material.STAINED_GLASS_PANE, 13, 1, partnerInventory, 26)
            return
        }
        setItem("§f[ §6거래 준비 §f]", listOf("§f클릭 시 거래를 준비합니다."), Material.STAINED_GLASS_PANE, 14, 1, selfInventory, 18)
        setItem("§f[ §6거래 준비 §f]", listOf("§f상대방의 거래 준비 상태입니다."), Material.STAINED_GLASS_PANE, 14, 1, partnerInventory, 25)
    }

    fun isSecondReady(state: Boolean, selfInventory: Inventory, partnerInventory: Inventory) {
        if (state) {
            setItem("§f[ §a거래 수락 §f]", listOf("§f클릭 시 거래를 수락합니다."), Material.STAINED_GLASS_PANE, 13, 1, selfInventory, 27)
            setItem("§f[ §a거래 수락 §f]", listOf("§f클릭 시 거래를 수락합니다."), Material.STAINED_GLASS_PANE, 13, 1, partnerInventory, 35)
            return
        }
        setItem("§f[ §a거래 수락 §f]", listOf("§f클릭 시 거래를 수락합니다."), Material.STAINED_GLASS_PANE, 14, 1, selfInventory, 27)
        setItem("§f[ §a거래 수락 §f]", listOf("§f클릭 시 거래를 수락합니다."), Material.STAINED_GLASS_PANE, 14, 1, partnerInventory, 35)
    }

    private fun setItem(display: String, lore: List<String>, material: Material, data: Short, stack: Int, inventory: Inventory, vararg slot: Int) {
        val item = ItemStack(material, stack, data)
        val meta = item.itemMeta
        meta.displayName = display
        meta.lore = lore
        item.itemMeta = meta
        slot.forEach { inventory.setItem(it, item) }
    }

    private fun setItem(display: String, material: Material, data: Short, stack: Int, inventory: Inventory, vararg slot: Int) {
        val item = ItemStack(material, stack, data)
        val meta = item.itemMeta
        meta.displayName = display
        item.itemMeta = meta
        slot.forEach { inventory.setItem(it, item) }
    }

    private fun skull(player: Player, lore: List<String>, inventory: Inventory, slot: Int) {
        val skull = ItemStack(Material.SKULL_ITEM, 1, SkullType.PLAYER.ordinal.toShort())
        val meta = skull.itemMeta as SkullMeta
        meta.owningPlayer = player
        meta.displayName = "§f§l" + player.name
        meta.lore = lore
        skull.itemMeta = meta
        inventory.setItem(slot, skull)
    }
}