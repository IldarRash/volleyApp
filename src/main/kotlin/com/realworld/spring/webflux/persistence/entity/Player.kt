package com.realworld.spring.webflux.persistence.entity

import com.realworld.spring.webflux.dto.view.FullPlayerView
import com.realworld.spring.webflux.dto.view.ShortPlayerView


enum class Position {
    OutsideHitter, OppositeHitter, MiddleBlocker, Setter, Libero, NO
}
enum class Type {
    Main, Side
}

data class PlayerPosition(
        val type: Type,
        val position: Position
)
enum class Property {
    Attack, Block, Serve, Set, Receive
}
data class PlayerProps(
        val score: Int = 0,
        val prop: Property

)

data class Player (
        val username: String,
        val score: Int,
        val positions: List<PlayerPosition> = listOf(),
        val playerProps: List<PlayerProps> = listOf(),
        val isToday: Boolean
) {
    fun toShortView() = ShortPlayerView(this.positions)

    fun toFullView() = FullPlayerView(this.positions, this.playerProps)
}