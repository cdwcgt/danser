package me.wieku.framework.graphics.buffers

internal data class VBOHolder(
    val vbo: VertexBufferObject,
    val maxVertices: Int,
    val divisor: Int,
    val vertexSize: Int,
    val attributes: Array<VertexAttribute>
)