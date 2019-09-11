#version 410

uniform vec2      resolution;
uniform float     time;

uniform sampler2D tex0;

const float       centralDispersionRate = 10.;
const float       shapeFactor           = .06;
const float       fadeOutRate           = .93;

out vec3 color;

// --- Spectral Zucconi --------------------------------------------
// By Alan Zucconi
// Based on GPU Gems: https://developer.nvidia.com/sites/all/modules/custom/gpugems/books/GPUGems/gpugems_ch08.html
// But with values optimised to match as close as possible the visible spectrum
// Fits this: https://commons.wikimedia.org/wiki/File:Linear_visible_spectrum.svg
// With weighter MSE (RGB weights: 0.3, 0.59, 0.11)
// https://www.alanzucconi.com/

float saturate(float x) {
  return min(1.0, max(0.0,x));
}

vec3 saturate (vec3 x) {
  return min(vec3(1.,1.,1.), max(vec3(0.,0.,0.),x));
}

vec3 bump3y (vec3 x, vec3 yoffset) {
  vec3 y = vec3(1.,1.,1.) - x * x;
  y = saturate(y-yoffset);
  return y;
}

const vec3 c1 = vec3(3.54585104, 2.93225262, 2.41593945);
const vec3 x1 = vec3(0.69549072, 0.49228336, 0.27699880);
const vec3 y1 = vec3(0.02312639, 0.15225084, 0.52607955);

const vec3 c2 = vec3(3.90307140, 3.21182957, 3.96587128);
const vec3 x2 = vec3(0.11748627, 0.86755042, 0.66077860);
const vec3 y2 = vec3(0.84897130, 0.88445281, 0.73949448);

// Based on GPU Gems
// Optimised by Alan Zucconi
vec3 spectral_zucconi6(float x) {
  return
  bump3y(c1 * (x - x1), y1) +
  bump3y(c2 * (x - x2), y2) ;
}

void main() {
  vec2 uv = gl_FragCoord.xy / resolution;
  vec2 cartCoord = (2 * gl_FragCoord.xy - resolution) / min(resolution.x, resolution.y);
  float dist = length(cartCoord);
  vec2 prevCoord = (gl_FragCoord.xy - cartCoord.xy * centralDispersionRate) / resolution;
  color = texture(tex0, prevCoord).rgb;
  color *= fadeOutRate;
  color += spectral_zucconi6(dist + sin(time) * .3) * shapeFactor;
  color = clamp(color, 0, 1);
}
