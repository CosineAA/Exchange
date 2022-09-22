package com.cosine.exchange.service

import com.cosine.exchange.manager.EconomyManager
import com.cosine.exchange.manager.InventoryManager
import com.cosine.exchange.manager.VariableManager
import net.milkbowl.vault.economy.Economy

interface InstanceService {

    val economy: Economy

    val variableManager: VariableManager

    val inventoryManager: InventoryManager

    val economyManager: EconomyManager
}

//abstract class InstanceManager : JavaPlugin() {
//
//    companion object {
//        /**
//         * 이코노미 객체입니다.
//         */
//        lateinit var economy: Economy
//
//        const val prefix = "§f§l[ §6§l거래 §f§l]§f"
//    }
//
//    /**
//     * 아래는 모두 클래스 객체입니다.
//     */
//    lateinit var variableManager: VariableManager
//    lateinit var inventoryManager: InventoryManager
//}