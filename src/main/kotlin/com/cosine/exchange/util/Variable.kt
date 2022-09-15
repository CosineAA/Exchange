package com.cosine.exchange.util

import java.util.*
import kotlin.collections.HashMap

object Variable {

    var traders: HashMap<UUID, UUID> = HashMap()
    var acceptOrRefuse: HashMap<UUID, Int> = HashMap()
}