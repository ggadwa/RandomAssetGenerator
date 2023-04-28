#version 330

uniform lowp sampler2D baseTex;
uniform lowp sampler2D normalTex;
uniform lowp sampler2D metallicRoughnessTex;
uniform lowp sampler2D emissiveTex;

uniform int displayType;
uniform vec3 baseColor;

uniform highp vec3 lightPosition;
uniform mediump float lightIntensity;
uniform mediump float lightExponent;
uniform mediump float lightAmbient;

uniform bool highlighted, hasEmissive;
uniform mediump float emissiveFactor;

in highp vec3 eyeVector,eyePosition;
in highp vec2 fragUV;
in mediump vec3 tangentSpaceTangent,tangentSpaceBinormal,tangentSpaceNormal;

out lowp vec4 outputPixel;

void main(void)
{
    lowp float att,bump,metallic;
    highp float dist;
    highp vec3 lightVector,lightVertexVector,bumpLightVertexVector;
    lowp vec3 bumpMap,metallicRoughnessMap,metallicHalfVector,lightCol,metalCol;
    lowp vec4 pixel,tex;

    // color-only processing
    if ((displayType==1) || (highlighted)) {
        outputPixel.rgb=texture(baseTex,fragUV).rgb;
        outputPixel.a=1.0;
        return;
    }

    // normal-only processing
    if (displayType==2) {
        outputPixel.rgb=texture(normalTex,fragUV).rgb;
        outputPixel.a=1.0;
        return;
    }

    // metallic-roughness-only processing
    if (displayType==3) {
        outputPixel.rgb=texture(metallicRoughnessTex,fragUV).rgb;
        outputPixel.a=1.0;
        return;
    }

    // emissive-only processing
    if (displayType==4) {
        if (hasEmissive) {
            outputPixel.rgb=texture(emissiveTex,fragUV).rgb;
        }
        else {
            outputPixel.rgb=vec3(0,0,0);
        }
        outputPixel.a=1.0;
        return;
    }

    // skeleton-only (just a color) processing
    if (displayType==5) {
        outputPixel.rgb=baseColor.rgb;
        outputPixel.a=1.0;
        return;
    }

    // regular rendering
    tex=texture(baseTex,fragUV);

    // the bump map
    bumpMap=normalize((texture(normalTex,fragUV).rgb*2.0)-1.0);
    bump=0.0;

    // the metallic-roughness map
    metallicRoughnessMap=texture(metallicRoughnessTex,fragUV).rgb;

    // lights
    lightCol=vec3(lightAmbient,lightAmbient,lightAmbient);

    // this is a simple frag with one light
    // get vector for light
    lightVector=lightPosition-eyePosition;

    dist=length(lightVector);
    if (dist<lightIntensity) {

        // the lighting attenuation
        att=1.0-(dist/lightIntensity);
        att+=pow(att,lightExponent);
        lightCol+=(vec3(1,1,1)*att);

        // lights in tangent space
        lightVertexVector.x=dot(lightVector,tangentSpaceTangent);
        lightVertexVector.y=dot(lightVector,tangentSpaceBinormal);
        lightVertexVector.z=dot(lightVector,tangentSpaceNormal);

        // per-light bump
        bumpLightVertexVector=normalize(lightVertexVector);
        bump+=(dot(bumpLightVertexVector,bumpMap)*att);

        // per-light metallic
        metallicHalfVector=normalize(normalize(eyeVector)+bumpLightVertexVector);
        metallic+=((metallicRoughnessMap.b*pow(max(dot(bumpMap,metallicHalfVector),0.0),5.0))*att);
    }

    // calculate the final lighting
    lightCol*=bump;
    lightCol=clamp(lightCol,lightAmbient,1.0);

    metalCol=min(metallic,1.0)*lightCol;

    // finally create the pixel
    pixel.rgb=(tex.rgb*lightCol);
    //pixel.r+=clamp(metalCol.r,0.0,(1-pixel.r));
    //pixel.g+=clamp(metalCol.g,0.0,(1-pixel.g));
    //pixel.b+=clamp(metalCol.b,0.0,(1-pixel.b));


    pixel.rgb+=metalCol;
    //pixel.rgb=clamp(pixel.rgb,0.0,1.0);
    if (hasEmissive) pixel.rgb+=(texture(emissiveTex,fragUV.xy).rgb*emissiveFactor);
    pixel.a=tex.a;

    outputPixel=pixel;
}

