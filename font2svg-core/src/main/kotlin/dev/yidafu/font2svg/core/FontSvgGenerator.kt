package dev.yidafu.font2svg.core

import com.github.nwillc.ksvg.RenderMode
import com.github.nwillc.ksvg.elements.SVG
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.freetype.FT_Face
import org.lwjgl.util.freetype.FT_Outline_Funcs
import org.lwjgl.util.freetype.FT_Vector
import org.lwjgl.util.freetype.FreeType
import java.io.Closeable

class FontSvgGenerator(fontFilepath: String): Closeable {

    private var face: FT_Face
    val stack = MemoryStack.stackPush()
    private val library: Long
    init {
            val pp = stack.mallocPointer(1)
            val err = FreeType.FT_Init_FreeType(pp)
            check(err == FreeType.FT_Err_Ok) { "Failed to initialize FreeType: " + FreeType.FT_Error_String(err) }

            library = pp[0]

            val facePointer = stack.mallocPointer(1)

            val err2 = FreeType.FT_New_Face(library, fontFilepath, 0, facePointer)

            face = FT_Face.create(facePointer[0])

            FreeType.FT_Set_Pixel_Sizes(
                face,
                0,
                16
            )
    }

    fun generateSvg(char: Long): String {
        val glyph_index = FreeType.FT_Get_Char_Index(face, char.toChar().toLong());
        val error3 = FreeType.FT_Load_Glyph(
            face,
            glyph_index,
            FreeType.FT_LOAD_DEFAULT
        )

        val glyph = face.glyph()
        val metrics = glyph?.metrics()

        val paths = mutableListOf<String>()

        val funcs = FT_Outline_Funcs.create().move_to { to, _ ->
            val p = addrToVec(to)
            paths.add("M ${p.first} ${p.second}")
            0
        }.line_to {to,_ ->
            val p = addrToVec(to)

            paths.add("L ${p.first} ${p.second}")
            0
        }.conic_to { ct, to, _ ->
            val p = addrToVec(to)
            val c1 = addrToVec(ct)
            paths.add("Q ${c1.first} ${c1.second}, ${p.first} ${p.second}")
            0
        }.cubic_to { ct1, ct2, to, _ ->

            val p = addrToVec(to)
            val c1 = addrToVec(ct1)
            val c2 = addrToVec(ct2)
            paths.add("C ${c1.first} ${c1.second}, ${c2.first} ${c2.second}, ${p.first} ${p.second}")
            0
        }
        FreeType.FT_Outline_Decompose(glyph?.outline()!!, funcs, 1)


        val svg = SVG.svg {
            width = (metrics?.width() ?: 800).toString()
            height = (metrics?.height() ?: 800).toString()
            viewBox = "0 ${-face.ascender()} ${metrics?.horiAdvance()} ${face.ascender() - face.descender()}"
            path {
                d = paths.joinToString(" ")
                fill = "red"
            }
        }
        val builder = StringBuilder()
        svg.render(builder, RenderMode.FILE)

        return builder.toString()
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

    private fun addrToVec(addr: Long): Pair<Int, Int> {
        val vec = FT_Vector.create(addr)
        return vec.x().toInt() to vec.y().toInt()
    }

    override fun close() {
        FreeType.FT_Done_FreeType(library)
        stack.close()
    }
}