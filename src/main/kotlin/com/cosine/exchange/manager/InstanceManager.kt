package com.cosine.exchange.manager

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

abstract class InstanceManager : JavaPlugin() {
    /**
     * 이코노미 객체입니다.
     */
    lateinit var economy: Economy

    /**
     * 아래는 모두 클래스 객체입니다.
     */
    lateinit var variableManager: VariableManager
    lateinit var inventoryManager: InventoryManager
}