package dev.yidafu.font2svg

import dev.yidafu.font2svg.core.FontSvgGenerator


fun main() {
    val generator = FontSvgGenerator("/Users/dovyih/Downloads/Roboto-Regular.ttf")
    generator.getAllChars().forEach {
        val svg = generator.generateSvg(it)
        println(svg)
    }
    generator.close()
}