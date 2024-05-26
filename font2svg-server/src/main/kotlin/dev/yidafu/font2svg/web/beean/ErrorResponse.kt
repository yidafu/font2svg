package dev.yidafu.font2svg.web.beean

open class ErrorResponse(val code: Int, val message: String) {

}

class FontAlreadyExists(filename: String) : ErrorResponse(1001, "font '$filename' already upload")

class OpenTempFileFailed(filename: String) : ErrorResponse(1002, "open temp file '$filename' failed")

class WriteFileFailed() : ErrorResponse(1003, "write file failed")
