package ru.mmcs.openglexample.objects

import android.opengl.GLES30
import ru.mmcs.openglexample.GLRenderer
import java.io.InputStream

class Object3D(
    objFile: InputStream,
    val objectColor: FloatArray,
    vertexShaderText: String,
    fragmentShaderText: String,
) : ObjectGL(vertexShaderText, fragmentShaderText) {

    override val valuesPerVertex = 8

    init {
        parseObjFile(objFile)
        initBuffers()
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    }

    private fun parseObjFile(objFile: InputStream) {
        val packedVertices = mutableListOf<Float>()
        objFile.bufferedReader().use {
            val vertices = mutableListOf<Float>()
            val textureVertices = mutableListOf<Float>()
            val normales = mutableListOf<Float>()
            for (line in it.readLines()) {
                val details = line.split(" ")
                when (details.first()) {
                    "v" -> {
                        val coords = details.drop(1).map { s -> s.toFloat() }.toMutableList()
                        vertices.addAll(coords)
                    }

                    "vt" -> {
                        textureVertices.addAll(details.drop(1).map { s -> s.toFloat() })
                    }

                    "vn" -> {
                        normales.addAll(details.drop(1).map { s -> s.toFloat() })
                    }

                    "f" -> {
                        for (vertex in details.drop(1)) {
                            val triplet = vertex.split("/")
                            val vertexIndex = (triplet[0].toInt() - 1) * 3
                            packedVertices.addAll(
                                vertices.subList(
                                    vertexIndex,
                                    vertexIndex + 3
                                )
                            )
                            val normaleIndex = (triplet[2].toInt() - 1) * 3
                            packedVertices.addAll(
                                normales.subList(
                                    normaleIndex,
                                    normaleIndex + 3
                                )
                            )
                            val textureIndex = (triplet[1].toInt() - 1) * 2
                            packedVertices.addAll(
                                textureVertices.subList(
                                    textureIndex,
                                    textureIndex + 2
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
        this.verticesData = packedVertices.toFloatArray()
    }

    private var vTextureHandle : Int = 0
    private var vNormalHandle : Int = 0

    private var uColorHandle: Int = -1

    override fun bindBuffers() {
        super.bindBuffers()
        vNormalHandle = GLES30.glGetAttribLocation(glProgramId, "vertexNormal").also {
            GLES30.glEnableVertexAttribArray(it)
        }
        vTextureHandle = GLES30.glGetAttribLocation(glProgramId, "vertexTextureCoords").also {
            GLES30.glEnableVertexAttribArray(it)
        }

        GLES30.glVertexAttribPointer(
            vNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            valuesPerVertex * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )

        GLES30.glVertexAttribPointer(
            vTextureHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            valuesPerVertex * Float.SIZE_BYTES,
            6 * Float.SIZE_BYTES
        )

        GLES30.glBindVertexArray(0)
        GLES30.glDisableVertexAttribArray(vNormalHandle)
        GLES30.glDisableVertexAttribArray(vTextureHandle)
        GLRenderer.checkGLError("init normal/texture buffers")
    }

    override fun initUniformHandles() {
        uVPMatrixHandle = GLES30.glGetUniformLocation(glProgramId, "VPMatrix")
        uColorHandle = GLES30.glGetUniformLocation(glProgramId, "color")
        super.initUniformHandles()
    }

    override fun bindUniforms(vpMatrix: FloatArray) {
        GLES30.glUniform4fv(uColorHandle, 1, objectColor, 0)
        super.bindUniforms(vpMatrix)
    }
}