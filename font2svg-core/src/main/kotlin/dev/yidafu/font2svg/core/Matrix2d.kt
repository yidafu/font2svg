package dev.yidafu.font2svg.dev.yidafu.font2svg.core

data class Matrix2d(val xx: Long, val xy: Long, val yx: Long, val yy: Long) {
  companion object {
    val RotateMatrix = Matrix2d(1, 0, 0, -1)
  }
}
