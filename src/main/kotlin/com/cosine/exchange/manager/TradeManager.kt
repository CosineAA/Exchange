package com.cosine.exchange.manager

import com.cosine.exchange.main.Exchange
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*


/**
 * self = 본인
 * target = 상대
 */
class TradeManager(self: Player, target: Player) {

    companion object {
        /**
         * 활성화 되어있는 모든 거래 목록입니다.
         */
        private val activeTrades: MutableList<TradeManager> = ArrayList<TradeManager>()

        /**
         * 해당 플레이어가 거래 중인지 확인합니다.
         * (activeTrades에서 traders를 가져와 체크합니다.)
         */
        fun getTradeState(player: Player): TradeManager? {
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
    private val sendMoney = intArrayOf(0, 0)

    /**
     * 한 플레이어의 거래 준비와 수락 상태를 나타냅니다.
     */
    private val readyAndAccept = arrayOf(false, false)

    /**
     * 거래 현황입니다.
     * false = 준비 상태
     * true = 수락 상태
     */
    private var status: Boolean = false

    /**
     * 메인 클래스의 이코노미 사본 객체입니다.
     */
    private val economy = Exchange.economy

    init {
        traders[0] = self.uniqueId
        traders[1] = target.uniqueId

        tradingInventories[0] = Exchange.inventoryManager.createInventory(self, target)
        tradingInventories[1] = Exchange.inventoryManager.createInventory(target, self)

        activeTrades.add(this)

        self.closeInventory()
        target.closeInventory()

        self.openInventory(tradingInventories[0])
        target.openInventory(tradingInventories[1])
    }
}