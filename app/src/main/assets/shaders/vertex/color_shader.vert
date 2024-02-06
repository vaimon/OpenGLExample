#version 300 es

in vec3 vertexPosition;
in vec3 vertexNormal;
in vec2 vertexTextureCoords;

out vec3 normal;
out vec2 textureCoordinates;

uniform mat4 uVPMatrix;

void main() {
    gl_Position = uVPMatrix * vec4(vertexPosition, 1.0);
    normal = vertexNormal;
    textureCoordinates = vertexTextureCoords;
}