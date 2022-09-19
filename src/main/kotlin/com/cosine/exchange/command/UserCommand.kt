package com.cosine.exchange.command

import com.cosine.exchange.main.Exchange
import com.cosine.exchange.main.Exchange.Companion.prefix
import com.cosine.exchange.manager.TradeManager
import com.cosine.exchange.service.ExchangeService
import com.cosine.exchange.service.InstanceService
import com.cosine.exchange.util.sendMessages
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class UserCommand(private val instance: InstanceService) : CommandExecutor, ExchangeService {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (args.isEmpty()) {
                help(player)
                return false
            }
            when (args[0]) {
                "신청" -> {
                    if (args.size == 1) {
                        player.sendMessage("$prefix 닉네임을 적어주세요.")
                        return false
                    }
                    val target = Bukkit.getPlayer(args[1])
                    if (Bukkit.getPlayer(args[1]) == null) {
                        player.sendMessage("$prefix 해당 플레이어가 오프라인입니다.")
                        return false
                    }
                    if (instance.variableManager.hasTrader(player.uniqueId) || TradeManager.getTrade(player) != null) {
                        player.sendMessage("$prefix 이미 거래 중인 상태입니다.")
                        return false
                    }
                    if (instance.variableManager.hasTrader(target.uniqueId) || TradeManager.getTrade(target) != null) {
                        player.sendMessage("$prefix 해당 플레이어는 이미 거래를 진행 중입니다.")
                        return false
                    }
                    beginExchange(player, target)
                }
                "수락" -> acceptExchange(player)
                "거절" -> refuseExchange(player)
            }
        }
        return false
    }

    private fun help(player: Player) {
        player.sendMessages(
            "$prefix §f§l거래 시스템 도움말",
            "",
            "$prefix /거래 신청 [닉네임]",
            "$prefix /거래 수락",
            "$prefix /거래 거절"
        )
    }

    override fun beginExchange(self: Player, target: Player) {
        instance.variableManager.addTrader(self.uniqueId, target.uniqueId)
        instance.variableManager.addTrader(target.uniqueId, self.uniqueId)

        object : BukkitRunnable() {
            var time = 30
            override fun run() {
                time--
                if (time == 0) {
                    cancel()
                    refuseMessage(self, target)
                    return
                }
                if (instance.variableManager.isAccepted(target.uniqueId)) {
                    cancel()
                    TradeManager(instance, self, target)
                } else {
                    cancel()
                    refuseMessage(self, target)
                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Exchange::class.java), 0, 20)
    }

    private fun refuseMessage(self: Player, target: Player) {
        instance.variableManager.apply {
            deleteExchange(self.uniqueId)
            deleteExchange(target.uniqueId)
        }
        self.sendMessage("$prefix 상대방이 거래를 거절하였습니다.")
        target.sendMessage("$prefix 거래를 거절하였습니다.")
    }

    override fun acceptExchange(self: Player) {
        if (!instance.variableManager.hasTrader(self.uniqueId)) {
            self.sendMessage("$prefix 거래 신청이 오지 않았습니다.")
            return
        }
        instance.variableManager.setAccept(self.uniqueId)
    }

    override fun refuseExchange(self: Player) {
        if (!instance.variableManager.hasTrader(self.uniqueId)) {
            self.sendMessage("$prefix 거래 신청이 오지 않았습니다.")
            return
        }
        instance.variableManager.setRefuse(self.uniqueId)
    }
}