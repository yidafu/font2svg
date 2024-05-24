package dev.yidafu.font2svg.dev.yidafu.font2svg.core

import org.lwjgl.util.freetype.FT_Vector

data class Vertex(val x: Long, val y: Long) {
    operator fun times(other: Matrix2d): Vertex {
        return Vertex(
            x = x * other.xx + y * other.xy,
            y = x * other.yx + y * other.yy
        )
    }

    override fun toString(): String {
        return "$x $y"
    }
    companion object {
        fun from(pointer: Long): Vertex {
            val vec = FT_Vector.create(pointer)
            return  Vertex(vec.x(), vec.y())
        }
    }
}
