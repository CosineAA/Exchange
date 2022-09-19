package com.cosine.exchange.manager

import com.cosine.exchange.main.Exchange
import com.cosine.exchange.main.Exchange.Companion.prefix
import com.cosine.exchange.util.getPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * self = 본인
 * target = 상대
 */
class TradeManager(private val instance: Exchange, self: Player, target: Player) {

    companion object {
        /**
         * 활성화 되어있는 모든 거래 목록입니다.
         */
        private val activeTrades: MutableList<TradeManager> = ArrayList<TradeManager>()

        /**
         * 해당 플레이어가 거래 중인지 확인합니다.
         * (activeTrades에서 traders를 가져와 체크합니다.)
         */
        fun getTrade(player: Player): TradeManager? {
            for (trade in activeTrades) {
                if (trade.traders[0] == player.uniqueId || trade.traders[1] == player.uniqueId) {
                    return trade
                }
            }
            return null
        }
    }

    /**
     * 거래를 하는 두 플레이어입니다.
     */
    private val traders: Array<UUID?> = arrayOfNulls<UUID?>(2)

    /**
     * 두 플레이어의 거래 인벤토리입니다.
     */
    private val tradingInventories: Array<Inventory?> = arrayOfNulls<Inventory?>(2)

    /**
     * 두 플레이어가 보내는 돈입니다.
     */
    private val sendingMoney = doubleArrayOf(0.0, 0.0)

    /**
     * 두 플레이어의 1차 거래 상태를 나타냅니다.
     */
    private val isFirstReady = arrayOf(false, false)

    /**
     * 두 플레이어의 2차 거래 상태를 나타냅니다.
     */
    private val isSecondReady = arrayOf(false, false)

    /**
     * 미리 객체를 생성하여 계속 생성하는 것을 방지합니다.
     */
    private val glass = ItemStack(Material.STAINED_GLASS_PANE, 1, 8)

    /**
     * 생성자 호출로 거래를 시작합니다.
     */
    init {
        traders[0] = self.uniqueId
        traders[1] = target.uniqueId

        tradingInventories[0] = instance.inventoryManager.createInventory(self, target)
        tradingInventories[1] = instance.inventoryManager.createInventory(target, self)

        activeTrades.add(this)

        self.closeInventory()
        target.closeInventory()

        self.openInventory(tradingInventories[0])
        target.openInventory(tradingInventories[1])
    }

    // (14 15 16 23 24 25 32 33 34 41 42 43)
    // 14 = (9 * 1) + 5
    // 15 = (9 * 1) + 6
    // 16 = (9 * 1) + 7
    // 23 = (9 * 2) + 5
    // 23 = (9 * 2) + 6
    // 23 = (9 * 2) + 7
    /**
     * 상대 거래 물품을 본인 인벤토리에 동기화합니다.
     */
    fun synchronizationInventory(uuid: UUID) {
        if (traders[0]!! == uuid) {
            setItemFunction(uuid, tradingInventories[0]!!)
        } else {
            setItemFunction(uuid, tradingInventories[1]!!)
        }
    }

    /**
     * 물품 동기화 이벤트의 공통 구문입니다.
     */
    private fun setItemFunction(uuid: UUID, inventory: Inventory) {
        val partnerInventory = getInventoryOfPartner(uuid)
        var line = 1
        for (loop: Int in 0..2) {
            if (line == 5) break
            val units = (5 + loop)
            val slot = (9 * line) + units
            val partnerItem = partnerInventory.getItem(slot) ?: glass
            inventory.setItem(slot - 4, partnerItem)
            if (units == 7) line++
        }
    }

    /**
     * 거래 물품과 돈을 보냅니다.
     */
    fun successExchange(self: UUID) {
        val partner = getTradingPartner(self)
        val selfInventory = getPlayer(self).openInventory.topInventory
        val partnerInventory = getInventoryOfPartner(self)

        getPlayer(self).apply {
            sendMessage("$prefix 거래가 완료되었습니다.")
            closeInventory()
            getTradingItem(partnerInventory).forEach { inventory.addItem(it) }
        }
        getPlayer(partner).apply {
            sendMessage("$prefix 거래가 완료되었습니다.")
            closeInventory()
            getTradingItem(selfInventory).forEach { inventory.addItem(it) }
        }

        instance.economyManager.depositPlayerMoney(self, getSendingMoney(partner))
        instance.economyManager.depositPlayerMoney(partner, getSendingMoney(self))

        activeTrades.remove(this)
    }

    /**
     * 거래를 취소합니다.
     */
    fun cancelExchange(self: UUID) {
        val partner = getTradingPartner(self)
        val selfInventory = getPlayer(self).openInventory.topInventory
        val partnerInventory = getInventoryOfPartner(self)

        getPlayer(self).apply {
            sendMessage("$prefix 거래가 취소되었습니다.")
            closeInventory()
            getTradingItem(selfInventory).forEach { inventory.addItem(it) }
        }
        getPlayer(partner).apply {
            sendMessage("$prefix 거래가 취소되었습니다.")
            closeInventory()
            getTradingItem(partnerInventory).forEach { inventory.addItem(it) }
        }

        instance.economyManager.withdrawPlayerMoney(self, getSendingMoney(self))
        instance.economyManager.withdrawPlayerMoney(partner, getSendingMoney(partner))

        activeTrades.remove(this)
    }

    /**
     * 거래 물품을 반환합니다.
     */
    fun getTradingItem(inventory: Inventory): List<ItemStack> {
        val list = mutableListOf<ItemStack>()
        var line = 1
        for (loop: Int in 0..2) {
            if (line == 5) break
            val units = (5 + loop)
            val slot = (9 * line) + units
            list.add(inventory.getItem(slot - 4))
            if (units == 7) line++
        }
        return list
    }

    /**
     * 거래하는 상대를 반환합니다.
     */
    fun getTradingPartner(uuid: UUID): UUID {
        return if (traders[0]!! == uuid) {
            traders[1]!!
        } else {
            traders[0]!!
        }
    }

    /**
     * 거래하는 상대 플레이어의 인벤토리를 반환합니다.
     */
    fun getInventoryOfPartner(uuid: UUID): Inventory {
        return if (traders[0]!! == uuid) {
            tradingInventories[1]!!
        } else {
            tradingInventories[0]!!
        }
    }

    /**
     * 플레이어의 1번째 거래 상태를 반환합니다.
     */
    fun getFirstReady(uuid: UUID): Boolean {
        return if (traders[0]!! == uuid) {
            isFirstReady[0]
        } else {
            isFirstReady[1]
        }
    }

    /**
     * 플레이어의 2번째 거래 상태를 반환합니다.
     */
    fun getSecondReady(uuid: UUID): Boolean {
        return if (traders[0]!! == uuid) {
            isSecondReady[0]
        } else {
            isSecondReady[1]
        }
    }

    /**
     * 플레이어의 1번째 거래 상태를 설정합니다.
     */
    fun setFirstReady(uuid: UUID, boolean: Boolean) {
        if (traders[0]!! == uuid) {
            isFirstReady[0] = boolean
            refreshFirstTradeState(boolean, 0, 1)
        } else {
            isFirstReady[1] = boolean
            refreshFirstTradeState(boolean, 1, 0)
        }
    }

    /**
     * 1번째 거래 상태를 설정하는 공통 구문입니다.
     */
    private fun refreshFirstTradeState(boolean: Boolean, who: Int, who2: Int) {
        tradingInventories[who]?.let { selfInventory ->
            tradingInventories[who2]?.let { partnerInventory ->
                instance.inventoryManager.isFirstReady(boolean, selfInventory, partnerInventory)
            }}
    }

    /**
     * 플레이어의 2번째 거래 상태를 설정합니다.
     */
    fun setSecondReady(uuid: UUID, boolean: Boolean) {
        if (traders[0]?.equals(uuid) == true) {
            isSecondReady[0] = boolean
            refreshSecondTradeState(boolean, 0 ,1)
        } else {
            isSecondReady[1] = boolean
            refreshSecondTradeState(boolean, 1 ,0)
        }
    }

    /**
     * 2번째 거래 상태를 설정하는 공통 구문입니다.
     */
    private fun refreshSecondTradeState(boolean: Boolean, who: Int, who2: Int) {
        tradingInventories[who]?.let { selfInventory ->
            tradingInventories[who2]?.let { partnerInventory ->
                instance.inventoryManager.isSecondReady(boolean, selfInventory, partnerInventory)
            }}
    }

    /**
     * 보내는 돈을 반환합니다.
     */
    fun getSendingMoney(uuid: UUID): Double {
        return if (traders[0]?.equals(uuid) == true) {
            sendingMoney[0]
        } else {
            sendingMoney[1]
        }
    }

    /**
     * 돈을 설정하는데 0보다 이하인지 체크하는 구문입니다.
     */
    fun checkSendingMoneyLimit(uuid: UUID, money: Int): Boolean {
        val doubleMoney = money.toDouble()
        return if (traders[0]?.equals(uuid) == true) {
            sendingMoney[0] + money <= doubleMoney
        } else {
            sendingMoney[1] + money <= doubleMoney
        }
    }

    /**
     * 보내는 돈을 늘립니다.
     */
    fun addSendingMoney(uuid: UUID, money: Int) {
        val doubleMoney = money.toDouble()
        instance.economyManager.depositPlayerMoney(uuid, doubleMoney)
        if (traders[0]?.equals(uuid) == true) {
            sendingMoney[0] += doubleMoney
            refreshSendingMoney(0, 1)
        } else {
            sendingMoney[1] += doubleMoney
            refreshSendingMoney(1, 0)
        }
    }
    /**
     * 보내는 돈을 줄입니다.
     */
    fun subtractSendingMoney(uuid: UUID, money: Int) {
        val doubleMoney = money.toDouble()
        instance.economyManager.withdrawPlayerMoney(uuid, doubleMoney)
        if (traders[0]?.equals(uuid) == true) {
            sendingMoney[0] -= doubleMoney
        } else {
            sendingMoney[1] -= doubleMoney
        }
    }

    /**
     * 돈을 보내는 공통 구문입니다.
     */
    private fun refreshSendingMoney(who: Int, who2: Int) {
        traders[who]?.let { self ->
            tradingInventories[who]?.let { selfInventory ->
                tradingInventories[who2]?.let { partnerInventory ->
                    instance.inventoryManager.refreshSendingMoney(
                        sendingMoney[who].toInt(), self, selfInventory, partnerInventory)
                }}}
    }

    /**
     * 거래 상태를 확인 후 클릭 가능한 슬롯을 반환합니다.
     */
    fun getClickAllowedSlot(uuid: UUID): List<Int> {
        return if (getFirstReady(uuid)) {
            listOf()
        } else {
            listOf(10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39)
        }
    }
}