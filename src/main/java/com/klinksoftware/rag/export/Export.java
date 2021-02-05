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
        byte[]                          binBytes;
        ByteArrayOutputStream           bin;
        String                          path;
        Mesh                            mesh;
        ArrayList<Object>               arrList,nodeArr,meshesArr,primitivesArr,
                                        accessorsArr,bufferViewsArr,bufferArr;
        LinkedHashMap<String,Object>    json,obj2,nodeObj,meshObj,primitiveObj,attributeObj,
                                        bufferObj;
        ObjectMapper                    objMapper;
        
            // the bin data
            
        bin=new ByteArrayOutputStream(32*1024);
        
            // the json
            
        meshCount=meshList.count();
        
        json=new LinkedHashMap<>();
        
            // asset header
            
        obj2=new LinkedHashMap<>();
        obj2.put("generator","WSJS Random Asset Generator");
        obj2.put("version","0.1");
        json.put("asset",obj2);
        
            // the single scene
            // points to all the nodes
            
        json.put("scene",0);
        
        obj2=new LinkedHashMap<>();
        obj2.put("name","Scene");
        
        nodeArr=new ArrayList<>();      // one node for every mesh
        for (n=0;n!=meshCount;n++) {
            nodeArr.add(n);
        }
        obj2.put("nodes",nodeArr);
        
        arrList=new ArrayList<>();
        arrList.add(obj2);
        json.put("scenes",arrList);
        
            // the nodes, pointed from scene
            // one for each mesh
            
        nodeArr=new ArrayList<>();
        
        for (n=0;n!=meshCount;n++) {
            mesh=meshList.get(n);

            nodeObj=new LinkedHashMap<>();
            nodeObj.put("mesh",n);
            nodeObj.put("name",mesh.name);
            
            nodeArr.add(nodeObj);
        }
        
        json.put("nodes",nodeArr);
        
            // the meshes, pointed from nodes
            // one for each mesh, parallel with the nodes
            
            // we build the accessor and bufferViews at the
            // same time
            
        meshesArr=new ArrayList<>();
        accessorsArr=new ArrayList<>();
        bufferViewsArr=new ArrayList<>();
        
        for (n=0;n!=meshCount;n++) {
            mesh=meshList.get(n);
            
            meshObj=new LinkedHashMap<>();
            meshObj.put("name",mesh.name);
            
            primitiveObj=new LinkedHashMap<>();
            
            attributeObj=new LinkedHashMap<>();
            attributeObj.put("POSITION",0);
            attributeObj.put("NORMAL",1);
            attributeObj.put("TANGENT",2);
            attributeObj.put("TEXCOORD_0",3);
            primitiveObj.put("attributes",attributeObj);
            
            byte[] temp=new byte[]{0x0,0x1,0x2};
            try { bin.write(temp); } catch(Exception e) {}
            
            primitiveObj.put("indices",3);
            primitiveObj.put("material",0);
            primitivesArr=new ArrayList<>();
            primitivesArr.add(primitiveObj);
            
            meshObj.put("primitives",primitivesArr);
            
            meshesArr.add(meshObj);
        }
        
        json.put("meshes",meshesArr);
            
            // the materials, pointed to from mesh primitives
            
        arrList=new ArrayList<>();
        json.put("materials",arrList);
        
            // textures, pointed to from materials

        arrList=new ArrayList<>();
        json.put("textures",arrList);
        
            // images, pointed to from textures

        arrList=new ArrayList<>();
        json.put("images",arrList);

            // samplers, pointed to from textures
            // just one, with mag/min filter
            
        arrList=new ArrayList<>();
        obj2=new LinkedHashMap<>();
        obj2.put("magFilter",9729);
        obj2.put("minFilter",9986);
        arrList.add(obj2);
        json.put("samplers",arrList);
            
            // accessors, pointed to from mesh primitives
            // these are built when we build the meshes
            
        json.put("accessors",accessorsArr);

            // buffer view, pointed to from accessors
            // these are built when we build the meshes
            
        json.put("bufferViews",bufferViewsArr);

            // the buffer, just one for all information
            
        binBytes=bin.toByteArray();
            
        bufferObj=new LinkedHashMap<>();
        bufferObj.put("byteLength",binBytes.length);
        bufferObj.put("uri",(name+".bin"));
        bufferArr=new ArrayList<>();
        bufferArr.add(bufferObj);
        json.put("buffers",bufferArr);
        
            // save the json
               
        path=basePath+File.separator+name+".json";
        
        objMapper=new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {

            objMapper.writeValue(new File(path),json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
            // now write the bin
            
        path=basePath+File.separator+name+".bin";
        
        try(FileOutputStream binFile=new FileOutputStream(path)) {
            binFile.write(binBytes);
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