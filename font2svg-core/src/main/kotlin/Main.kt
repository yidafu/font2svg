package dev.yidafu.font2svg

import dev.yidafu.font2svg.core.FontSvgGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.buffer

fun main() {
    val generator = FontSvgGenerator("/Users/dovyih/Downloads/JinBuTi.ttf")
    listOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'a', 'b', 'c', 'd', 'e', 'f', 'g',
        '1', '2', '3', '4', '5', '6', '7', '8', '9',
        ' ',
        ).map { it.toLong() }.forEach {
        val svg = generator.generateSvg(it)
        println(FontSvgGenerator.glyphToSvgString(svg, 16, "#444"))
    }
//    println("main")
//    coroutineScope {
//        println("launch")
//        generator.generateAll().collect {
//            delay(10)
//            println(it.second)
//        }
//    }

//    generator.close()
}