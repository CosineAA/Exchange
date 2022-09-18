package com.cosine.exchange.main

import com.cosine.exchange.command.UserCommand
import com.cosine.exchange.listener.InventoryClickListener
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

    private var econ: Economy? = null

    override val economy: Economy get() = econ ?: throw java.lang.Exception("이코노미가 없습니다.")
    override val variableManager: VariableManager by lazy { VariableManager() }
    override val inventoryManager: InventoryManager by lazy { InventoryManager() }
    override val economyManager: EconomyManager by lazy { EconomyManager(this) }

    override fun onEnable() {
        logger.info("거래 플러그인 활성화")

        if (!setupEconomy()) {
            logger.info("Vault 플러그인이 없어서 플러그인이 비활성화되었습니다.")
            server.pluginManager.disablePlugin(this)
            return
        }

        getCommand("거래").executor = UserCommand(this)
        server.pluginManager.registerEvents(InventoryClickListener(this), this)
    }

    override fun onDisable() {
        logger.info("거래 플러그인 비활성화")
    }

    private fun setupEconomy(): Boolean {
        server.pluginManager.getPlugin("Vault") ?: return false
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        econ = rsp.provider
        return true
    }
}