package com.klinksoftware.rag.export;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;

public class Export
{
    public void export(String name)
    {
        ArrayList<Object>       arrList;
        LinkedHashMap<String,Object>  obj,obj2;
        ObjectMapper            objMapper;
        
        obj=new LinkedHashMap<>();
        
            // headers (assets, scene, scenes)
            
        obj2=new LinkedHashMap<>();
        obj2.put("generator","WSJS Random Asset Generator");
        obj2.put("version","0.1");
        obj.put("asset",obj2);
        
        obj.put("scene",0);
        
        obj2=new LinkedHashMap<>();
        obj2.put("name","Scene");
        arrList=new ArrayList<>();
        arrList.add(0);
        obj2.put("nodes",arrList);
        arrList=new ArrayList<>();
        arrList.add(obj2);
        obj.put("scenes",arrList);
        
            // the meshes (nodes, meshes)
            
        arrList=new ArrayList<>();
        obj.put("nodes",arrList);
            
        arrList=new ArrayList<>();
        obj.put("meshes",arrList);
            
            
            // the materials (materials, textures, images, samplers)
            
        arrList=new ArrayList<>();
        obj.put("materials",arrList);

        arrList=new ArrayList<>();
        obj.put("textures",arrList);

        arrList=new ArrayList<>();
        obj.put("images",arrList);

        arrList=new ArrayList<>();
        obj.put("samplers",arrList);
            
            // bin (accessors, bufferViews, buffers)
            
        arrList=new ArrayList<>();
        obj.put("accessors",arrList);

        arrList=new ArrayList<>();
        obj.put("bufferViews",arrList);

            
        obj2=new LinkedHashMap<>();
        obj2.put("byteLength",0);           // UPDATE THIS!
        obj2.put("uri",(name+".bin"));
        arrList=new ArrayList<>();
        arrList.add(obj2);
        obj.put("buffers",arrList);
        
            // finish and save
                    
        objMapper=new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {

            objMapper.writeValue(new File("output/"+name+".json"),obj);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


/*


{
    "asset" : {
        "generator" : "Khronos glTF Blender I/O v1.4.40",
        "version" : "2.0"
    },
    "scene" : 0,
    "scenes" : [
        {
            "name" : "Scene",
            "nodes" : [
                0,
                1,
                2
            ]
        }
    ],
    "nodes" : [
        {
            "mesh" : 0,
            "name" : "Cube"
        },
        {
            "name" : "Light",
            "rotation" : [
                0.16907575726509094,
                0.7558803558349609,
                -0.27217137813568115,
                0.570947527885437
            ],
            "translation" : [
                4.076245307922363,
                5.903861999511719,
                -1.0054539442062378
            ]
        },
        {
            "name" : "Camera",
            "rotation" : [
                0.483536034822464,
                0.33687159419059753,
                -0.20870360732078552,
                0.7804827094078064
            ],
            "translation" : [
                7.358891487121582,
                4.958309173583984,
                6.925790786743164
            ]
        }
    ],
    "materials" : [
        {
            "doubleSided" : true,
            "name" : "Material",
            "normalTexture" : {
                "index" : 0,
                "texCoord" : 0
            },
            "pbrMetallicRoughness" : {
                "baseColorTexture" : {
                    "index" : 1,
                    "texCoord" : 0
                },
                "metallicRoughnessTexture" : {
                    "index" : 2,
                    "texCoord" : 0
                }
            }
        }
    ],
    "meshes" : [
        {
            "name" : "Cube",
            "primitives" : [
                {
                    "attributes" : {
                        "POSITION" : 0,
                        "NORMAL" : 1,
                        "TEXCOORD_0" : 2
                    },
                    "indices" : 3,
                    "material" : 0
                }
            ]
        }
    ],
    "textures" : [
        {
            "sampler" : 0,
            "source" : 0
        },
        {
            "sampler" : 0,
            "source" : 1
        },
        {
            "sampler" : 0,
            "source" : 2
        }
    ],
    "images" : [
        {
            "mimeType" : "image/png",
            "name" : "stone_normal",
            "uri" : "textures/stone_normal.png"
        },
        {
            "mimeType" : "image/png",
            "name" : "stone_color",
            "uri" : "textures/stone_color.png"
        },
        {
            "mimeType" : "image/png",
            "name" : "stone_metallic_roughness",
            "uri" : "textures/stone_metallic_roughness.png"
        }
    ],
    "accessors" : [
        {
            "bufferView" : 0,
            "componentType" : 5126,
            "count" : 24,
            "max" : [
                1,
                1,
                1
            ],
            "min" : [
                -1,
                -1,
                -1
            ],
            "type" : "VEC3"
        },
        {
            "bufferView" : 1,
            "componentType" : 5126,
            "count" : 24,
            "type" : "VEC3"
        },
        {
            "bufferView" : 2,
            "componentType" : 5126,
            "count" : 24,
            "type" : "VEC2"
        },
        {
            "bufferView" : 3,
            "componentType" : 5123,
            "count" : 36,
            "type" : "SCALAR"
        }
    ],
    "bufferViews" : [
        {
            "buffer" : 0,
            "byteLength" : 288,
            "byteOffset" : 0
        },
        {
            "buffer" : 0,
            "byteLength" : 288,
            "byteOffset" : 288
        },
        {
            "buffer" : 0,
            "byteLength" : 192,
            "byteOffset" : 576
        },
        {
            "buffer" : 0,
            "byteLength" : 72,
            "byteOffset" : 768
        }
    ],
    "samplers" : [
        {
            "magFilter" : 9729,
            "minFilter" : 9986
        }
    ],
    "buffers" : [
        {
            "byteLength" : 840,
            "uri" : "zTest.bin"
        }
    ]
}


*/