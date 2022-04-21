#version 460

uniform lowp sampler2D baseTex;
uniform lowp sampler2D normalTex;
uniform lowp sampler2D metallicRoughnessTex;
//uniform lowp sampler2D emissiveTex;

uniform highp vec4 lightPositionIntensity;   // xyz = position, w = intensity

//uniform bool hasEmissive;
//uniform mediump vec3 emissiveFactor;

in highp vec3 eyeVector,eyePosition;
in highp vec2 fragUV;
in mediump vec3 tangentSpaceTangent,tangentSpaceBinormal,tangentSpaceNormal;

out lowp vec4 outputPixel;

void main(void)
{
    lowp float att;
    highp float intensity,dist;
    highp vec3 lightVector,lightVertexVector;
    lowp vec3 bumpMap,metallicRoughnessMap;
    lowp vec4 pixel,tex;

        // the texture fragment

    tex=texture(baseTex,fragUV);

        // the bump map
  
    bumpMap=normalize((texture(normalTex,fragUV).rgb*2.0)-1.0);
    highp vec3 bumpLightVertexVector;
    lowp float bump=0.0;

        // the metallic-roughness map

    metallicRoughnessMap=texture(metallicRoughnessTex,fragUV).rgb;
    lowp vec3 metallicHalfVector;
    lowp float metallic;

        // lights

    lowp vec3 lightCol=vec3(0,0,0);

        // this is a simple frag with one light

    intensity=lightPositionIntensity.w;

        // get vector for light

    lightVector=lightPositionIntensity.xyz-eyePosition;

    dist=length(lightVector);
    if (dist<intensity) {

            // the lighting attenuation

        att=1.0-(dist/intensity);
        att+=pow(att,5.0);
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
    lightCol=clamp(lightCol,0.2,1.0);

        // finally create the pixel

    pixel.rgb=(tex.rgb*lightCol);
    pixel.rgb+=(min(metallic,1.0)*lightCol);
    //if (hasEmissive) pixel.rgb+=(texture(emissiveTex,fragUV.xy).rgb*emissiveFactor);
    pixel.a=tex.a;

    outputPixel=pixel;
}

