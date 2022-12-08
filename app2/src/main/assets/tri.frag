precision mediump float;

uniform vec4 vColor;

varying vec4 tex;

void main() {
    gl_FragColor = vec4(tex.xy/2.0+0.5, 1.0, 1.0);
}