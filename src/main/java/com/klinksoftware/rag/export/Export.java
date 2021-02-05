package com.klinksoftware.rag.export;

import com.klinksoftware.rag.mesh.*;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;

public class Export
{
    public void export(MeshList meshList,String basePath,String name)
    {
        int                             n,meshCount;
        String                          path;
        Mesh                            mesh;
        ArrayList<Object>               arrList,primitivesArr;
        LinkedHashMap<String,Object>    obj,obj2,primitiveObj,attributeObj;
        ObjectMapper                    objMapper;
        
        meshCount=meshList.count();
        
        obj=new LinkedHashMap<>();
        
            // headers (assets, scene, scenes)
            
        obj2=new LinkedHashMap<>();
        obj2.put("generator","WSJS Random Asset Generator");
        obj2.put("version","0.1");
        obj.put("asset",obj2);
        
        obj.put("scene",0);
        
        obj2=new LinkedHashMap<>();
        obj2.put("name","Scene");
        
        arrList=new ArrayList<>();      // one node for every mesh
        for (n=0;n!=meshCount;n++) {
            arrList.add(n);
        }
        obj2.put("nodes",arrList);
        
        arrList=new ArrayList<>();
        arrList.add(obj2);
        obj.put("scenes",arrList);
        
            // the mesh nodes
            // one for each mesh
            
        arrList=new ArrayList<>();
        
        for (n=0;n!=meshCount;n++) {
            mesh=meshList.get(n);

            obj2=new LinkedHashMap<>();
            obj2.put("mesh",0);
            obj2.put("name",mesh.name);
            
            arrList.add(obj2);
        }
        
        obj.put("nodes",arrList);
        
            // the meshes
            // one for each mesh, parallel with
            // the nodes
            
        arrList=new ArrayList<>();
        
        for (n=0;n!=meshCount;n++) {
            mesh=meshList.get(n);
            
            obj2=new LinkedHashMap<>();
            obj2.put("name",mesh.name);
            
            primitiveObj=new LinkedHashMap<>();
            
            attributeObj=new LinkedHashMap<>();
            attributeObj.put("POSITION",0);
            attributeObj.put("NORMAL",1);
            attributeObj.put("TANGENT",2);
            attributeObj.put("TEXCOORD_0",3);
            primitiveObj.put("attributes",attributeObj);
            
            primitiveObj.put("indices",3);
            primitiveObj.put("material",0);
            primitivesArr=new ArrayList<>();
            
            obj2.put("primitive",primitiveObj);
            
            arrList.add(obj2);
        }
        
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
               
        path=basePath+File.separator+name+".json";
        
        objMapper=new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {

            objMapper.writeValue(new File(path),obj);
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