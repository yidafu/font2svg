package dev.yidafu.font2svg.core

import com.github.nwillc.ksvg.RenderMode
import com.github.nwillc.ksvg.elements.SVG
import dev.yidafu.font2svg.dev.yidafu.font2svg.core.SvgGlyph
import dev.yidafu.font2svg.dev.yidafu.font2svg.core.SvgLineCmd
import dev.yidafu.font2svg.dev.yidafu.font2svg.core.Vertex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.freetype.FT_Face
import org.lwjgl.util.freetype.FT_Outline_Funcs
import org.lwjgl.util.freetype.FreeType
import java.io.Closeable
import kotlin.math.abs
import kotlin.math.max

/**
 *
 * [PrimogemStudio/Advanced-Framework ~ FreeTypeFont.kt](https://github.com/PrimogemStudio/Advanced-Framework/blob/73e383733257a9494afac5a1190adec21131b80d/fontengine/src/main/java/com/primogemstudio/advancedfmk/fontengine/gen/FreeTypeFont.kt)
 *
 * @author yidafu
 */
class FontSvgGenerator(fontFilepath: String) : Closeable {
  private var face: FT_Face
  val stack = MemoryStack.stackPush()
  private val library: Long

  init {
    val pp = stack.mallocPointer(1)
    val err = FreeType.FT_Init_FreeType(pp)
    check(err == FreeType.FT_Err_Ok) {
      "Failed to initialize FreeType: " + FreeType.FT_Error_String(err)
    }

    library = pp[0]

    val facePointer = stack.mallocPointer(1)

    val err2 = FreeType.FT_New_Face(library, fontFilepath, 0, facePointer)
    check(err2 == FreeType.FT_Err_Ok) {
      "Failed to create new face: " + FreeType.FT_Error_String(err2)
    }
    face = FT_Face.create(facePointer[0])

    FreeType.FT_Set_Pixel_Sizes(
      face,
      0,
      16,
    )
  }

  fun generateSvg(char: Long): SvgGlyph {
    val glyphIndex = FreeType.FT_Get_Char_Index(face, char)
    val error3 =
      FreeType.FT_Load_Glyph(
        face,
        glyphIndex,
        FreeType.FT_LOAD_DEFAULT,
      )

    check(error3 == FreeType.FT_Err_Ok) {
      "Failed to load glyph: " + FreeType.FT_Error_String(error3)
    }

    val glyph = face.glyph()
    val metrics = glyph?.metrics()

    face.underline_position()
    face.underline_thickness()
    face.units_per_EM()
    val svgPaths = mutableListOf<SvgLineCmd>()
    val funcs =
      FT_Outline_Funcs.create().move_to { to, _ ->
        svgPaths.add(SvgLineCmd.MoveTo(Vertex.from(to)))
        0
      }.line_to { to, _ ->
        svgPaths.add(SvgLineCmd.LineTo(Vertex.from(to)))
        0
      }.conic_to { ct, to, _ ->
        svgPaths.add(
          SvgLineCmd.QuadraticBezierCurveTo(
            Vertex.from(ct),
            Vertex.from(to),
          ),
        )
        0
      }.cubic_to { ct1, ct2, to, _ ->
        svgPaths.add(
          SvgLineCmd.CubicBezierCurveTo(
            Vertex.from(ct1),
            Vertex.from(ct2),
            Vertex.from(to),
          ),
        )
        0
      }
    FreeType.FT_Outline_Decompose(glyph!!.outline(), funcs, 1)

    val vWidth1 = max(metrics?.width() ?: 0, metrics?.horiAdvance() ?: 0)
    val minY = -face.ascender()

    val svgGlyph =
      SvgGlyph(
        "0 $minY $vWidth1 ${face.height()}",
        svgPaths.joinToString(" ") { cmd -> cmd.toString() },
        face.ascender().toInt(),
        face.descender().toInt(),
        face.underline_position().toInt(),
        face.underline_thickness().toInt(),
        face.units_per_EM().toInt(),
        face.max_advance_height().toInt(),
        face.max_advance_width().toInt(),
      )

    return svgGlyph
  }

  fun getAllChars(): List<Long> {
    val map = mutableListOf<Long>()

    val index = stack.mallocInt(1)
    var chr = FreeType.FT_Get_First_Char(face, index)
    while (index.get(0) != 0) {
      map.add(chr)
      chr = FreeType.FT_Get_Next_Char(face, chr, index)
      if (map.contains(chr)) break
    }
    return map
  }

  fun generateAll(): Flow<Pair<Long, SvgGlyph>> {
    return flow {
      getAllChars().forEach {
        emit(Pair(it, generateSvg(it)))
      }
    }
  }

  override fun close() {
    FreeType.FT_Done_FreeType(library)
    stack.close()
  }

  companion object {
    fun glyphToSvgString(
      glyph: SvgGlyph,
      fontSize: Int,
      color: String,
      underline: Boolean = false,
    ): String {
      val ratio = (abs(glyph.descender) + abs(glyph.ascender)).toFloat() / 1000
      val svg =
        SVG.svg {
          height = "${ratio * fontSize}px"
          viewBox = glyph.viewBox
          attributes["data-underline-pos"] = glyph.underlinePos.toString()
          attributes["data-underline-thickness"] = glyph.underlinePos.toString()
          attributes["data-units-per-em"] = glyph.unitsPerEN.toString()
          if (glyph.path.isNotEmpty()) {
            path {
              d = glyph.path
              fill = color
            }
          } else {
            val list = glyph.viewBox.split(" ")
            rect {
              x = list[0]
              y = list[1]
              width = list[2]
              height = list[3]
              fill = "transparent"
            }
          }
          if (underline) {
            line {
              val yOffset = (glyph.ascender + glyph.underlinePos).toString()
              x1 = "0"
              y1 = yOffset
              x2 = (glyph.maxAdvanceWidth).toString()
              y2 = yOffset
              stroke = color
              strokeWidth = glyph.underlineThickness.toString()
            }
          }
        }
      val builder = StringBuilder()
      svg.render(builder, RenderMode.FILE)
      return builder.toString()
    }
  }
}
