package dev.yidafu.font2svg.web.ext

fun Long.toCharacter(): String {
  val intUnicode = toInt()
  require(this <= Int.MAX_VALUE) { "Unicode value exceeds the maximum range of an Int" }
  return intUnicode.toChar().toString()
}
