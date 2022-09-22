package com.cosine.exchange

import com.cosine.exchange.command.UserCommand
import com.cosine.exchange.listener.InventoryClickListener
import com.cosine.exchange.listener.InventoryCloseListener
import com.cosine.exchange.manager.EconomyManager
import com.cosine.exchange.service.InstanceService
import com.cosine.exchange.manager.InventoryManager
import com.cosine.exchange.manager.VariableManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

class Exchange : JavaPlugin(), InstanceService {

    companion object {
        const val prefix = "§f§l[ §6§l거래 §f§l]§f"
    }

    override lateinit var economy: Economy
    override lateinit var variableManager: VariableManager
    override lateinit var inventoryManager: InventoryManager
    override lateinit var economyManager: EconomyManager

    override fun onEnable() {
        logger.info("거래 플러그인 활성화")

        if (!setupEconomy()) {
            logger.info("Vault 플러그인이 없으면 플러그인이 작동하지 않습니다.")
        }

        variableManager = VariableManager()
        inventoryManager = InventoryManager()
        economyManager = EconomyManager(this)

        getCommand("거래").executor = UserCommand(this)
        server.pluginManager.registerEvents(InventoryClickListener(this), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
    }

    override fun onDisable() {
        logger.info("거래 플러그인 비활성화")
    }

    private fun setupEconomy(): Boolean {
        server.pluginManager.getPlugin("Vault") ?: return false
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.provider
        return true
    }
}