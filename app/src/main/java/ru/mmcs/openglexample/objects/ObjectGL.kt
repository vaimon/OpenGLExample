package ru.mmcs.openglexample.objects

import android.opengl.GLES30
import ru.mmcs.openglexample.GLRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Any OpenGL object (both 2D & 3D)
abstract class ObjectGL(
    vertexShaderText: String,
    fragmentShaderText: String,
) {
    // Object vertices. May be packed (vertices + normals + texture coordinates)
    protected lateinit var verticesData: FloatArray

    protected var glProgramId: Int = -1

    // How many values describe one vertex
    protected open val valuesPerVertex = 3

    init {
        compileShaders(vertexShaderText, fragmentShaderText)
    }

    private fun compileShaders(vertexShader: String, fragmentShader: String) {
        val vertexShaderId = GLRenderer.compileShader(GLES30.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = GLRenderer.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader)

        glProgramId = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShaderId)
            GLES30.glAttachShader(it, fragmentShaderId)
            GLES30.glLinkProgram(it)
        }

        GLRenderer.checkGLError("shader compilation")
    }

    // Must contain a handle for each uniform/in/out in shader files
    protected var vPositionHandle : Int = -1
    protected var uVPMatrixHandle : Int = -1

    // Get all uniform locations
    protected open fun initUniformHandles(){
        uVPMatrixHandle = GLES30.glGetUniformLocation(glProgramId, "uVPMatrix")
        GLRenderer.checkGLError("Uniform init")
    }

    protected val VAO: IntArray = intArrayOf(0)
    protected val VBO: IntArray = intArrayOf(0)

    // Create buffers for coordinates/textures
    protected fun initBuffers(){
        GLES30.glGenVertexArrays(1, VAO, 0)
        GLES30.glGenBuffers(1, VBO, 0)

        GLES30.glBindVertexArray(VAO[0])

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            verticesData.size * Float.SIZE_BYTES,
            ByteBuffer.allocateDirect(verticesData.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(verticesData)
                    position(0)
                }
            },
            GLES30.GL_STATIC_DRAW
        )

        bindBuffers()

        GLES30.glBindVertexArray(0)
        GLES30.glDisableVertexAttribArray(vPositionHandle)
        GLRenderer.checkGLError("init buffers")
    }

    protected open fun bindBuffers(){
        vPositionHandle = GLES30.glGetAttribLocation(glProgramId, "vertexPosition").also {
            GLES30.glEnableVertexAttribArray(it)
        }

        GLES30.glVertexAttribPointer(
            vPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            valuesPerVertex * Float.SIZE_BYTES,
            0
        )
    }

    // Pass actual values to uniforms, bind textures, etc.
    open fun bindUniforms(vpMatrix: FloatArray){
        GLES30.glUniformMatrix4fv(uVPMatrixHandle, 1, false, vpMatrix, 0)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(glProgramId)

        initUniformHandles()
        bindUniforms(mvpMatrix)

        // Bind vertex array and draw the object
        GLES30.glBindVertexArray(VAO[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, verticesData.size)
        GLES30.glBindVertexArray(0)

        GLES30.glUseProgram(0)
        GLRenderer.checkGLError("draw")
    }
}