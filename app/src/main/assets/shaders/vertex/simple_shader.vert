#version 300 es
in vec3 vertexPosition;

uniform mat4 uVPMatrix;

void main() {
    gl_Position = uVPMatrix * vec4(vertexPosition, 1.0);
}