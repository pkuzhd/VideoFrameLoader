attribute vec4 vPosition;

varying vec2 TexCoords;

void main() {
    gl_Position = vec4(vPosition.xy, 1.0, 1.0);
    TexCoords = vPosition.xy;
}