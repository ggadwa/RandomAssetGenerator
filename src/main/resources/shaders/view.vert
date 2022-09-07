#version 330

in highp vec3 vertexPosition;
in highp vec3 vertexNormal;
in highp vec3 vertexTangent;
in highp vec2 vertexUV;

uniform highp mat4 perspectiveMatrix;
uniform highp mat4 viewMatrix;

out highp vec3 eyeVector,eyePosition;
out highp vec2 fragUV;
out mediump vec3 tangentSpaceTangent,tangentSpaceBinormal,tangentSpaceNormal;

void main(void)
{
    highp vec4 pos;
    highp mat3 normalMatrix;

    // calculate the position, we have a couple ways, for maps we
    // create absolutely positioned meshes, for models, we have relatively
    // positioned with bones so we can animate them in the gltf way 
    pos=viewMatrix*vec4(vertexPosition,1.0);
    normalMatrix=transpose(inverse(mat3(viewMatrix)));

    
    // the frag position
    gl_Position=perspectiveMatrix*pos;

    // get the tangent space
    // this gets passed to the fragment so we can calculate lights
    tangentSpaceTangent=normalize(normalMatrix*vertexTangent);
    tangentSpaceBinormal=normalize(normalMatrix*cross(vertexNormal,vertexTangent));
    tangentSpaceNormal=normalize(normalMatrix*vertexNormal);

    // translate the eye vector
    eyePosition=vec3(pos);

    eyeVector.x=dot(-eyePosition,tangentSpaceTangent);
    eyeVector.y=dot(-eyePosition,tangentSpaceBinormal);
    eyeVector.z=dot(-eyePosition,tangentSpaceNormal);

    // the varying uv
    fragUV=vertexUV;
}

