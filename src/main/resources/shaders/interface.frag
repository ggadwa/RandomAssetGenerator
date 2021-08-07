#version 460

//uniform lowp sampler2D baseTex;
//uniform lowp vec4 color;

//in highp vec2 fragUV;

out lowp vec4 outputPixel;

void main(void)
{
    outputPixel=vec4(1,0,0,1); // texture(baseTex,fragUV)*color;
}
