package ru.mmcs.openglexample.objects

class Triangle2D(
    vertexShader: String,
    fragmentShader: String,
) : ObjectGL(vertexShader, fragmentShader) {
    init {
        verticesData = floatArrayOf(
            0.0f, 0.622008459f, 0.0f,       // top
            -0.5f, -0.311004243f, 0.0f,     // bottom left
            0.5f, -0.311004243f, 0.0f       // bottom right
        )
        initBuffers()
    }
}