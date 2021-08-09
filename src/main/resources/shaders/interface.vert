#version 460

in highp vec3 vertexPosition;
in highp vec2 vertexUV;

uniform highp mat4 orthoMatrix;
uniform highp mat4 perspectiveMatrix;
uniform highp mat4 viewMatrix;

out highp vec2 fragUV;

void main(void)
{
    //gl_Position=orthoMatrix*vec4(vertexPosition,1.0);
    gl_Position=perspectiveMatrix*viewMatrix*vec4(vertexPosition,1.0);
    fragUV=vertexUV;
}

