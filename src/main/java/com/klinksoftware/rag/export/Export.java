package com.klinksoftware.rag.export;

import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.skeleton.*;

import java.util.*;
import java.io.*;
import java.nio.*;
import com.fasterxml.jackson.databind.*;

public class Export
{
    private static final float[] EMISSIVE_FACTOR={1.0f,1.0f,1.0f};
    
        //
        // adding materials, textures, and images
        //
    
    private int addTextureAndImage(String name,ArrayList<Object> texturesArr,ArrayList<Object> imagesArr)
    {
        int                             imageIdx;
        LinkedHashMap<String,Object>    textureObj,imageObj;
        
        imageIdx=imagesArr.size();              // the next image
        
        textureObj=new LinkedHashMap<>();
        textureObj.put("sampler",0);
        textureObj.put("source",imageIdx);
        texturesArr.add(textureObj);

        imageObj=new LinkedHashMap<>();
        imageObj.put("mimeType","image/png");
        imageObj.put("name",name);
        imageObj.put("uri",("textures/"+name+".png"));
        imagesArr.add(imageObj);
        
            // and return the newly created texture
            
        return(texturesArr.size()-1);
    }
    
    private int addMaterial(String name,ArrayList<Object> materialsArr,ArrayList<Object> texturesArr,ArrayList<Object> imagesArr)
    {
        int                             n,materialIdx,textureIdx;
        String                          checkName;
        LinkedHashMap<String,Object>    materialObj,normalObj,pbrObj,
                                        colorObj,metallicRoughnessObj,
                                        emissiveObj;
        
            // did we already use this material?
        
        for (n=0;n!=materialsArr.size();n++) {
            materialObj=(LinkedHashMap<String,Object>)materialsArr.get(n);
            if (((String)materialObj.get("name")).equals(name)) return(n);
        }
        
            // add a new material
            
        materialObj=new LinkedHashMap<>();
        materialObj.put("doubleSided",true);
        materialObj.put("name",name);
        
            // the normal
            
        textureIdx=addTextureAndImage((name+"_normal"),texturesArr,imagesArr);

        normalObj=new LinkedHashMap<>();
        normalObj.put("index",textureIdx);
        normalObj.put("texCoord",0);
        materialObj.put("normalTexture",normalObj);

            // the color and metallicRoughness
            
        pbrObj=new LinkedHashMap<>();
        
        textureIdx=addTextureAndImage((name+"_color"),texturesArr,imagesArr);
        
        colorObj=new LinkedHashMap<>();
        colorObj.put("index",textureIdx);
        colorObj.put("texCoord",0);
        pbrObj.put("baseColorTexture",colorObj);
        
        textureIdx=addTextureAndImage((name+"_metallic_roughness"),texturesArr,imagesArr);
        
        metallicRoughnessObj=new LinkedHashMap<>();
        metallicRoughnessObj.put("index",textureIdx);
        metallicRoughnessObj.put("texCoord",0);
        pbrObj.put("metallicRoughnessTexture",metallicRoughnessObj);
        materialObj.put("pbrMetallicRoughness",pbrObj);

            // any emissives

        if (BitmapGenerator.hasEmissive(name)) {
            textureIdx=addTextureAndImage((name+"_emissive"),texturesArr,imagesArr);
            
            emissiveObj=new LinkedHashMap<>();
            emissiveObj.put("index",textureIdx);
            emissiveObj.put("texCoord",0);
            materialObj.put("emissiveTexture",emissiveObj);
            materialObj.put("emissiveFactor",EMISSIVE_FACTOR);
        }

            // add material to array and return
            // index of material
            
        materialsArr.add(materialObj);
            
        return(materialsArr.size()-1);
    }
    
        //
        // bin utilities
        //
    
    private int addBinData(int componentType,int count,String dataType,byte[] bytes,ArrayList<Object> accessorsArr,ArrayList<Object> bufferViewsArr,RagPoint min,RagPoint max,ByteArrayOutputStream bin) throws Exception
    {
        int                                 bufferIdx;
        LinkedHashMap<String,Object>        accessorObj,bufferViewObj;
        
        bufferIdx=bufferViewsArr.size();      // next buffer index
        
            // the accessor
            
        accessorObj=new LinkedHashMap<>();
        accessorObj.put("bufferView",bufferIdx);
        accessorObj.put("componentType",componentType);
        accessorObj.put("count",count);
        accessorObj.put("type",dataType);
        if (min!=null) accessorObj.put("min",new float[]{min.x,min.y,min.z});
        if (max!=null) accessorObj.put("max",new float[]{max.x,max.y,max.z});
        accessorsArr.add(accessorObj);
        
            // the buffer view
            
        bufferViewObj=new LinkedHashMap<>();
        bufferViewObj.put("buffer",0);          // always only one
        bufferViewObj.put("byteLength",bytes.length);
        bufferViewObj.put("byteOffset",bin.size());
        bufferViewsArr.add(bufferViewObj);
        
            // and write to the bin
            
        bin.write(bytes);
        
            // return the accessor
            
        return(accessorsArr.size()-1);
    }
    
    private byte[] floatArrayToFloatBytes(float[] arr)
    {
        int             n;
        ByteBuffer      byteBuf;
        
        byteBuf=ByteBuffer.allocate(4*arr.length).order(ByteOrder.LITTLE_ENDIAN);
        
        for (n=0;n!=arr.length;n++) {
            byteBuf.putFloat(arr[n]);
        }
        
        return(byteBuf.array());
    }
    
    private byte[] intArrayToShortBytes(int[] arr)
    {
        int             n;
        ByteBuffer      byteBuf;
        
        byteBuf=ByteBuffer.allocate(2*arr.length).order(ByteOrder.LITTLE_ENDIAN);
        
        for (n=0;n!=arr.length;n++) {
            byteBuf.putShort((short)arr[n]);
        }
        
        return(byteBuf.array());
    }
    
        //
        // adding a mesh
        //
    
    private void addMesh(Mesh mesh,int materialIdx,ArrayList<Object> meshesArr,ArrayList<Object> accessorsArr,ArrayList<Object> bufferViewsArr,ByteArrayOutputStream bin) throws Exception
    {
        int                                 accessorIdx;
        ArrayList<Object>                   primitiveArr;
        LinkedHashMap<String,Object>        meshObj,primitiveObj,attributeObj;
        RagPoint                            min,max;
        
            // figure out the min and max of vertex data
        
        min=new RagPoint(0.0f,0.0f,0.0f);
        max=new RagPoint(0.0f,0.0f,0.0f);
        mesh.getMinMaxVertex(min,max);
        
            // do the mesh object

        meshObj=new LinkedHashMap<>();
        meshObj.put("name",mesh.name);

            // and it's primitive

        primitiveObj=new LinkedHashMap<>();

        attributeObj=new LinkedHashMap<>();
        
        accessorIdx=addBinData(5126,(mesh.vertexes.length/3),"VEC3",floatArrayToFloatBytes(mesh.vertexes),accessorsArr,bufferViewsArr,min,max,bin);
        attributeObj.put("POSITION",accessorIdx);
        
        accessorIdx=addBinData(5126,(mesh.normals.length/3),"VEC3",floatArrayToFloatBytes(mesh.normals),accessorsArr,bufferViewsArr,null,null,bin);
        attributeObj.put("NORMAL",accessorIdx);
        
        accessorIdx=addBinData(5126,(mesh.uvs.length/2),"VEC2",floatArrayToFloatBytes(mesh.uvs),accessorsArr,bufferViewsArr,null,null,bin);
        attributeObj.put("TEXCOORD_0",accessorIdx);
        
        primitiveObj.put("attributes",attributeObj);

        accessorIdx=addBinData(5123,mesh.indexes.length,"SCALAR",intArrayToShortBytes(mesh.indexes),accessorsArr,bufferViewsArr,null,null,bin);
        primitiveObj.put("indices",accessorIdx);
        
        primitiveObj.put("material",materialIdx);
        primitiveArr=new ArrayList<>();
        primitiveArr.add(primitiveObj);

        meshObj.put("primitives",primitiveArr);

        meshesArr.add(meshObj);
    }
            
        //
        // main export
        //
    
    public void export(Skeleton skeleton,MeshList meshList,String name) throws Exception
    {
        int                             n,meshCount,materialIdx;
        byte[]                          binBytes;
        float[]                         translation,rotation,scale;
        ByteArrayOutputStream           bin;
        String                          path;
        Mesh                            mesh;
        Bone                            bone;
        ArrayList<Object>               arrList,nodesArr,meshesArr,
                                        accessorsArr,bufferViewsArr,bufferArr,
                                        samplerArr,materialsArr,texturesArr,imagesArr;
        LinkedHashMap<String,Object>    json,obj2,nodeObj,bufferObj,samplerObj;
        ObjectMapper                    objMapper;
        
            // the bin data
            
        bin=new ByteArrayOutputStream(32*1024);
        
            // the json
            
        meshCount=meshList.count();
        
        json=new LinkedHashMap<>();
        
            // asset header
            
        obj2=new LinkedHashMap<>();
        obj2.put("generator","WSJS Random Asset Generator");
        obj2.put("version","2.0");
        json.put("asset",obj2);
        
            // the single scene
            // points to the root node
            
        json.put("scene",0);
        
        obj2=new LinkedHashMap<>();
        obj2.put("name","Scene");
        
        nodesArr=new ArrayList<>();      // one node for every mesh
        nodesArr.add(0);
        obj2.put("nodes",nodesArr);
        
        arrList=new ArrayList<>();
        arrList.add(obj2);
        json.put("scenes",arrList);
        
            // the nodes, pointed from scene
            // one for each bone

        rotation=new float[]{0.0f,0.0f,0.0f,1.0f};      // always the same, we can share
        scale=new float[]{1.0f,1.0f,1.0f};
        
        nodesArr=new ArrayList<>();
        
        for (n=0;n!=skeleton.bones.size();n++) {
            bone=skeleton.bones.get(n);

            nodeObj=new LinkedHashMap<>();
            
            nodeObj.put("name",bone.name);
            translation=new float[3];
            translation[0]=bone.pnt.x;
            translation[1]=bone.pnt.y;
            translation[2]=bone.pnt.z;
            nodeObj.put("translation",translation);
            nodeObj.put("rotation",rotation);
            nodeObj.put("scale",scale);
            if (bone.meshIdx!=-1) nodeObj.put("mesh",bone.meshIdx);
            
            if (!bone.children.isEmpty()) nodeObj.put("children",bone.children);
            
            nodesArr.add(nodeObj);
        }
        
        json.put("nodes",nodesArr);
        
            // the meshes, pointed from nodes
            // one for each mesh, parallel with the nodes
            
            // we build the accessor and bufferViews at the
            // same time, along with the materials, textures,
            // and images
            
        meshesArr=new ArrayList<>();
        accessorsArr=new ArrayList<>();
        bufferViewsArr=new ArrayList<>();
        materialsArr=new ArrayList<>();
        texturesArr=new ArrayList<>();
        imagesArr=new ArrayList<>();
        
        for (n=0;n!=meshCount;n++) {
            mesh=meshList.get(n);
            
            materialIdx=addMaterial(mesh.bitmapName,materialsArr,texturesArr,imagesArr);
            addMesh(mesh,materialIdx,meshesArr,accessorsArr,bufferViewsArr,bin);
        }
        
        json.put("meshes",meshesArr);
            
            // the materials, pointed to from mesh primitives
            // this is generated while building the meshes
            
        json.put("materials",materialsArr);
        
            // textures, pointed to from materials
            // this is generated while building the meshes

        json.put("textures",texturesArr);
        
            // images, pointed to from textures
            // this is generated while building the meshes

        json.put("images",imagesArr);

            // samplers, pointed to from textures
            // just one, with mag/min filter
            
        samplerArr=new ArrayList<>();
        samplerObj=new LinkedHashMap<>();
        samplerObj.put("magFilter",9729);
        samplerObj.put("minFilter",9986);
        samplerArr.add(samplerObj);
        json.put("samplers",samplerArr);
            
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
               
        path="output"+File.separator+name+File.separator+name+".gltf";
        
        objMapper=new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {

            objMapper.writeValue(new File(path),json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
            // and write the bin
            
        path="output"+File.separator+name+File.separator+name+".bin";
        
        try(FileOutputStream binFile=new FileOutputStream(path)) {
            binFile.write(binBytes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
