package dev.yidafu.font2svg.web.beean

data class Response<T>(
  val code: Int,
  val message: String,
  val data: T? = null,
) {

  companion object {
    fun <T> success(data: T): Response<T> {
      return Response(0, "ok", data)
    }

    fun <T> fail(err: ErrorResponse): Response<T> {
      return Response(err.code, err.message, null)
    }
  }
}
