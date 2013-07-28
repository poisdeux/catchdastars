uniform sampler2D u_texture;

varying mediump vec4 v_color;
varying mediump vec2 v_texCoord;

const mediump float smoothing = 1.0/16.0;

void main() {
    mediump float distance = texture2D(u_texture, v_texCoord).a;
    mediump float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    gl_FragColor = vec4(v_color.rgb, alpha);
}
