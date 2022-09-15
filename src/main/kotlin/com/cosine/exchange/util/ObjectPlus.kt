package com.cosine.exchange.util

import org.bukkit.entity.Player

fun Player.sendMessages(vararg message: String?) = message.filterNotNull().forEach(::sendMessage)