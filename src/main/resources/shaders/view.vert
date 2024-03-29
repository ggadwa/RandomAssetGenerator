#version 330

in highp vec3 vertexPosition;
in highp vec3 vertexNormal;
in highp vec3 vertexTangent;
in highp vec2 vertexUV;
in highp vec4 vertexJoint;
in highp vec4 vertexWeight;

uniform highp mat4 perspectiveMatrix;
uniform highp mat4 viewMatrix,modelMatrix;
uniform highp mat4 jointMatrix[128];

uniform int displayType;
uniform bool skinned;

out highp vec3 eyeVector,eyePosition;
out highp vec2 fragUV;
out mediump vec3 tangentSpaceTangent,tangentSpaceBinormal,tangentSpaceNormal;

void main(void)
{
    highp vec4 pos;
    highp mat3 normalMatrix;

    // calculate the position
    if ((skinned) && (displayType!=5)) {
        highp mat4 skinMatrix=(vertexWeight.x*jointMatrix[int(vertexJoint.x)])+(vertexWeight.y*jointMatrix[int(vertexJoint.y)])+(vertexWeight.z*jointMatrix[int(vertexJoint.z)])+(vertexWeight.w*jointMatrix[int(vertexJoint.w)]);
        pos=viewMatrix*modelMatrix*skinMatrix*vec4(vertexPosition,1.0);
        normalMatrix=transpose(inverse(mat3(viewMatrix)*mat3(modelMatrix)*mat3(skinMatrix)));
    }
    else {
        pos=viewMatrix*modelMatrix*vec4(vertexPosition,1.0);
        normalMatrix=transpose(inverse(mat3(viewMatrix)*mat3(modelMatrix)));
    }

    // the frag position
    gl_Position=perspectiveMatrix*pos;

    // if skeleton draw, we can skip the rest
    if (displayType==5) return;

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

