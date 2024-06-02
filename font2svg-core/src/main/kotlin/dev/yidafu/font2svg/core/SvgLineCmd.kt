package dev.yidafu.font2svg.dev.yidafu.font2svg.core

/**
 * https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Paths
 */
sealed class SvgLineCmd {
  data class MoveTo(val target: Vertex) : SvgLineCmd() {
    override fun toString(): String {
      return "M ${target * Matrix2d.RotateMatrix}"
    }
  }

  data class LineTo(val target: Vertex) : SvgLineCmd() {
    override fun toString(): String {
      return "L ${target * Matrix2d.RotateMatrix}"
    }
  }

  data class QuadraticBezierCurveTo(val control1: Vertex, val control2: Vertex) : SvgLineCmd() {
    override fun toString(): String {
      return "Q ${control1 * Matrix2d.RotateMatrix}, ${control2 * Matrix2d.RotateMatrix}"
    }
  }

  data class CubicBezierCurveTo(val control1: Vertex, val control2: Vertex, val control3: Vertex) : SvgLineCmd() {
    override fun toString(): String {
      return "C ${control1 * Matrix2d.RotateMatrix}, ${control2 * Matrix2d.RotateMatrix}, ${control3 * Matrix2d.RotateMatrix}"
    }
  }
}
