#version 300 es
precision mediump float;

in vec3 normal;
in vec2 textureCoordinates;

uniform vec4 color;

out vec4 fragColor;

const float colorHide =  0.0000001f;

void main() {
    fragColor = color * 0.7f + vec4(normal, 1.0f) * 0.3f  + vec4(textureCoordinates, 0.0f, 1.0f) * colorHide;
}