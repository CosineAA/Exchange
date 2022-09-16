package com.cosine.exchange.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

fun Player.sendMessages(vararg message: String?) = message.filterNotNull().forEach(::sendMessage)
fun getPlayer(uuid: UUID) = Bukkit.getPlayer(uuid)