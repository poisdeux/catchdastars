#ifdef GL_ES 
#define LOW lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOW
#define HIGH
#endif

uniform sampler2D u_texture;

varying MED vec4 v_color;
varying MED vec2 v_texCoord;

const MED float smoothing = 1.0/16.0;

void main() {
    MED float distance = texture2D(u_texture, v_texCoord).a;
    MED float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    gl_FragColor = vec4(v_color.rgb, alpha);
}
