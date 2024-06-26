package dev.yidafu.font2svg.web.beean

open class ErrorResponse(val code: Int, val message: String)

class FontAlreadyExists(filename: String) : ErrorResponse(1001, "font '$filename' already upload")

class OpenTempFileFailed(filename: String) : ErrorResponse(1002, "open temp file '$filename' failed")

class WriteFileFailed() : ErrorResponse(1003, "write file failed")

class FontFaceNotExist : ErrorResponse(1004, "font face not exist")

class ContentNotFound : ErrorResponse(404, "404 Not Found")

class InvalidUrl : ErrorResponse(403, "Invalid url")

class RemoteFontNotExistFile(code: Int) : ErrorResponse(404, "Fetch remote font failed. request status: $code")
