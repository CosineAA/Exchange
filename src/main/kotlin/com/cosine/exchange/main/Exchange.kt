package com.cosine.exchange.main

import com.cosine.exchange.command.UserCommand
import com.cosine.exchange.listener.InventoryClickListener
import com.cosine.exchange.manager.InstanceManager
import com.cosine.exchange.manager.InventoryManager
import com.cosine.exchange.manager.VariableManager
import net.milkbowl.vault.economy.Economy

class Exchange : InstanceManager() {

    override fun onLoad() {
        super.instance = this
    }

    override fun onEnable() {
        logger.info("거래 플러그인 활성화")

        if (!setupEconomy()) {
            logger.info("Vault 플러그인이 없어서 플러그인이 비활성화되었습니다.")
            server.pluginManager.disablePlugin(this)
            return
        }

        inventoryManager = InventoryManager()
        variableManager = VariableManager()

        getCommand("거래").executor = UserCommand()
        server.pluginManager.registerEvents(InventoryClickListener(), this)
    }

    override fun onDisable() {
        logger.info("거래 플러그인 비활성화")
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return false
        economy = rsp.provider
        return true
    }
}