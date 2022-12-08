precision mediump float;

varying vec2 TexCoords;

uniform sampler2D screenTexture;
uniform sampler2D yTexture;
uniform sampler2D uTexture;
uniform sampler2D vTexture;
uniform sampler2D depthTexture;
uniform sampler2D maskTexture;

void main() {
    float red;
    vec2 coords = vec2((TexCoords.x+1.0)/2.0, 1.0- (TexCoords.y+1.0)/2.0);
    if (coords.y <= 0.2) {
        red = texture2D(yTexture, vec2(coords.x, coords.y*5.0)).x;
    } else if (coords.y <= 0.4) {
        red = texture2D(uTexture, vec2(coords.x, coords.y*5.0-1.0)).x;
    } else if (coords.y <= 0.6) {
        red = texture2D(vTexture, vec2(coords.x, coords.y*5.0-2.0)).x;
    } else if (coords.y <= 0.8) {
        red = texture2D(depthTexture, vec2(coords.x, coords.y*5.0-3.0)).x;
    } else {
        red = texture2D(maskTexture, vec2(coords.x, coords.y*5.0-4.0)).x;
    }
    gl_FragColor = vec4(red, 0.0, 0.0, 1.0);
}