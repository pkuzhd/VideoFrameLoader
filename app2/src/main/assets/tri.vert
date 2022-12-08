attribute vec4 vPosition;

varying vec4 tex;

void main() {
    gl_Position = vPosition;
    tex = vPosition;
}