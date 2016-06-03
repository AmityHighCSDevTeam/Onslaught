 
varying vec2 v_texCoord0;

uniform vec2 u_trans;
uniform sampler2D texture;
uniform vec2 u_texLoc;
uniform vec2 u_texSze;
uniform float u_scale;

void main(){
	vec2 screenCoord = v_texCoord0;
	vec2 worldCoord = (screenCoord-vec2(0.5, 0.5))*u_trans + u_texLoc;
    gl_FragColor = texture2D(texture, mod(worldCoord/u_scale, 1.0));
}