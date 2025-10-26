package com.baccaro.ai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform