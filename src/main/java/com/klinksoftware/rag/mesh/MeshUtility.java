package com.klinksoftware.rag.mesh;

import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshUtility
{
    public static final int STAIR_STEP_COUNT=10;

    public static final int STAIR_DIR_POS_Z=0;
    public static final int STAIR_DIR_NEG_Z=1;
    public static final int STAIR_DIR_POS_X=2;
    public static final int STAIR_DIR_NEG_X=3;

    public static final int UV_WHOLE=0;
    public static final int UV_BOX=1;
    public static final int UV_MAP=2;
    
        //
        // build UVs for vertex lists
        //
            
    public static float[] buildUVs(int[] vertexes,float[] normals,float uvScale)
    {
        int                 n,k,nVertex,offset,minIntX,minIntY;
        float               x,y,ang;
        float[]             uvs;
        RagPoint            v;
        RagVector           normal,mapUp;
        
        v=new RagPoint(0,0,0);
        normal=new RagVector(0.0f,0.0f,0.0f);

        nVertex=vertexes.length/3;
        
        uvs=new float[nVertex*2];

            // determine floor/wall like by
            // the dot product of the normal
            // and an up vector

        mapUp=new RagVector(0.0f,1.0f,0.0f);

            // run through the vertices
            // remember, both this and normals
            // are packed arrays

        for (n=0;n!=nVertex;n++) {

            offset=n*3;
            v.x=vertexes[offset];
            v.y=vertexes[offset+1];
            v.z=vertexes[offset+2];
            
            normal.x=normals[offset];
            normal.y=normals[offset+1];
            normal.z=normals[offset+2];

            ang=mapUp.dot(normal);

                // wall like
                // use longest of x/z coordinates + Y coordinates of vertex

            if (Math.abs(ang)<=0.4f) {
                if (Math.abs(normal.x)<Math.abs(normal.z)) {
                    x=v.x;
                }
                else {
                    x=v.z;
                }
                y=v.y;
            }

                // floor/ceiling like
                // use x/z coordinates of vertex

            else {
                x=v.x;
                y=v.z;
            }
            
            offset=n*2;
            uvs[offset]=x*uvScale;
            uvs[offset+1]=1.0f-(y*uvScale);
        }
        
            // reduce all the UVs to
            // their minimum integers
         
        minIntX=(int)(uvs[0]);
        minIntY=(int)(uvs[1]);
        
        for (n=1;n!=nVertex;n++) {
            offset=n*2;
            
            k=(int)(uvs[offset]);
            if (k<minIntX) minIntX=k;
            k=(int)(uvs[offset+1]);
            if (k<minIntY) minIntY=k;
        }
        
        for (n=0;n!=nVertex;n++) {
            offset=n*2;
            uvs[offset]-=(float)minIntX;
            uvs[offset+1]-=(float)minIntY;
        }
        
        return(uvs);
    }
    
        //
        // build normals
        //
        
    public static float[] buildNormals(int[] vertexes,int[] indexes,RagPoint meshCenter,boolean normalsIn)
    {

        int             n,nTrig,trigIdx,offset;
        float[]         normals;
        boolean         flip;
        RagPoint        trigCenter,v0,v1,v2;
        RagVector       faceVct,p10,p20,normal;

        normals=new float[vertexes.length];

        trigCenter=new RagPoint(0,0,0);
        faceVct=new RagVector(0.0f,0.0f,0.0f);

            // generate normals by the trigs
            // sometimes we will end up overwriting
            // but it depends on the mesh to have
            // constant shared vertices against
            // triangle normals

        v0=new RagPoint(0,0,0);
        v1=new RagPoint(0,0,0);
        v2=new RagPoint(0,0,0);
        p10=new RagVector(0.0f,0.0f,0.0f);
        p20=new RagVector(0.0f,0.0f,0.0f);
        normal=new RagVector(0.0f,0.0f,0.0f);

        nTrig=indexes.length/3;

        for (n=0;n!=nTrig;n++) {

                // get the vertex indexes and
                // the vertexes for the trig

            trigIdx=n*3;
            
            offset=indexes[trigIdx]*3;
            v0.x=vertexes[offset];
            v0.y=vertexes[offset+1];
            v0.z=vertexes[offset+2];
            
            offset=indexes[trigIdx+1]*3;
            v1.x=vertexes[offset];
            v1.y=vertexes[offset+1];
            v1.z=vertexes[offset+2];

            offset=indexes[trigIdx+2]*3;
            v2.x=vertexes[offset];
            v2.y=vertexes[offset+1];
            v2.z=vertexes[offset+2];

                // create vectors and calculate the normal
                // by the cross product

            p10.x=(float)(v1.x-v0.x);
            p10.y=(float)(v1.y-v0.y);
            p10.z=(float)(v1.z-v0.z);
            p20.x=(float)(v2.x-v0.x);
            p20.y=(float)(v2.y-v0.y);
            p20.z=(float)(v2.z-v0.z);
            
            normal.x=(p10.y*p20.z)-(p10.z*p20.y);
            normal.y=(p10.z*p20.x)-(p10.x*p20.z);
            normal.z=(p10.x*p20.y)-(p10.y*p20.x);

            normal.normalize();

                // determine if we need to flip
                // we can use the dot product to tell
                // us if the normal is pointing
                // more towards the center or more
                // away from it

            trigCenter.x=((v0.x+v1.x+v2.x)/3);
            trigCenter.y=((v0.y+v1.y+v2.y)/3);
            trigCenter.z=((v0.z+v1.z+v2.z)/3);
            
            faceVct.x=(float)(trigCenter.x-meshCenter.x);
            faceVct.y=(float)(trigCenter.y-meshCenter.y);
            faceVct.z=(float)(trigCenter.z-meshCenter.z);

            flip=(normal.dot(faceVct)>0.0f);
            if (!normalsIn) flip=!flip;

            if (flip) normal.scale(-1.0f);

                // and set the mesh normal
                // to all vertexes in this trig

            offset=indexes[trigIdx]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;

            offset=indexes[trigIdx+1]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;
            
            offset=indexes[trigIdx+2]*3;
            normals[offset]=normal.x;
            normals[offset+1]=normal.y;
            normals[offset+2]=normal.z;
        }
        
        return(normals);
    }
    
        //
        // build tangents
        //

    public static float[] buildTangents(int[] vertexes,float[] uvs,int[] indexes)
    {
        int             n,nTrig,trigIdx,offset;
        float           denom,u10,u20,v10,v20;
        float[]         tangents;
        RagPoint        v0,v1,v2;
        RagVector       uv0,uv1,uv2,p10,p20,
                        vLeft,vRight,vNum,
                        tangent;

            // generate tangents by the trigs
            // sometimes we will end up overwriting
            // but it depends on the mesh to have
            // constant shared vertices against
            // triangle tangents

            // note this recreates a bit of what
            // goes on to create the normal, because
            // we need that first to make the UVs

        v0=new RagPoint(0,0,0);
        v1=new RagPoint(0,0,0);
        v2=new RagPoint(0,0,0);
        uv0=new RagVector(0.0f,0.0f,0.0f);
        uv1=new RagVector(0.0f,0.0f,0.0f);
        uv2=new RagVector(0.0f,0.0f,0.0f);
        p10=new RagVector(0.0f,0.0f,0.0f);
        p20=new RagVector(0.0f,0.0f,0.0f);
        vLeft=new RagVector(0.0f,0.0f,0.0f);
        vRight=new RagVector(0.0f,0.0f,0.0f);
        vNum=new RagVector(0.0f,0.0f,0.0f);

        tangent=new RagVector(0.0f,0.0f,0.0f);

        nTrig=indexes.length/3;
        
        tangents=new float[vertexes.length];

        for (n=0;n!=nTrig;n++) {

                // get the vertex indexes and
                // the vertexes for the trig

            trigIdx=n*3;
            
            offset=indexes[trigIdx]*3;
            v0.x=vertexes[offset];
            v0.y=vertexes[offset+1];
            v0.z=vertexes[offset+2];
            
            offset=indexes[trigIdx]*2;
            uv0.x=uvs[offset];
            uv0.y=uvs[offset+1];
            
            offset=indexes[trigIdx+1]*3;
            v1.x=vertexes[offset];
            v1.y=vertexes[offset+1];
            v1.z=vertexes[offset+2];
            
            offset=indexes[trigIdx+1]*2;
            uv1.x=uvs[offset];
            uv1.y=uvs[offset+1];

            offset=indexes[trigIdx+2]*3;
            v2.x=vertexes[offset];
            v2.y=vertexes[offset+1];
            v2.z=vertexes[offset+2];
            
            offset=indexes[trigIdx+2]*2;
            uv2.x=uvs[offset];
            uv2.y=uvs[offset+1];

                // create vectors

            p10.x=(float)(v1.x-v0.x);
            p10.y=(float)(v1.y-v0.y);
            p10.z=(float)(v1.z-v0.z);
            p20.x=(float)(v2.x-v0.x);
            p20.y=(float)(v2.y-v0.y);
            p20.z=(float)(v2.z-v0.z);

                // get the UV scalars (u1-u0), (u2-u0), (v1-v0), (v2-v0)

            u10=uv1.x-uv0.x;        // x component
            u20=uv2.x-uv0.x;
            v10=uv1.y-uv0.y;        // y component
            v20=uv2.y-uv0.y;

                // calculate the tangent
                // (v20xp10)-(v10xp20) / (u10*v20)-(v10*u20)

            vLeft.x=p10.x*v20;
            vLeft.y=p10.y*v20;
            vLeft.z=p10.z*v20;
            
            vRight.x=p20.x*v10;
            vRight.y=p20.y*v10;
            vRight.z=p20.z*v10;
            
            vNum.x=vLeft.x-vRight.x;
            vNum.y=vLeft.y-vRight.y;
            vNum.z=vLeft.z-vRight.z;

            denom=(u10*v20)-(v10*u20);
            if (denom!=0.0f) denom=1.0f/denom;
            
            tangent.x=vNum.x*denom;
            tangent.y=vNum.y*denom;
            tangent.z=vNum.z*denom;
            
            tangent.normalize();

                // and set the mesh normal
                // to all vertexes in this trig
                
            offset=indexes[trigIdx]*3;
            tangents[offset]=tangent.x;
            tangents[offset+1]=tangent.y;
            tangents[offset+2]=tangent.z;

            offset=indexes[trigIdx+1]*3;
            tangents[offset]=tangent.x;
            tangents[offset+1]=tangent.y;
            tangents[offset+2]=tangent.z;
            
            offset=indexes[trigIdx+2]*3;
            tangents[offset]=tangent.x;
            tangents[offset+1]=tangent.y;
            tangents[offset+2]=tangent.z;
        }
        
        return(tangents);
    }

        //
        // mesh utilities
        //
        
    private static int addQuadToIndexes(ArrayList<Integer> indexArray,int trigIdx)
    {
        indexArray.addAll(Arrays.asList(trigIdx,(trigIdx+1),(trigIdx+2),trigIdx,(trigIdx+2),(trigIdx+3)));
        return(trigIdx+4);
    }

    public static int addBox(MeshList meshList,String name,String bitmapName,int negX,int posX,int negY,int posY,int negZ,int posZ,boolean isNegX,boolean isPosX,boolean isNegY,boolean isPosY,boolean isNegZ,boolean isPosZ,int segmentSize)
    {
        int                     trigIdx;
        ArrayList<Integer>      vertexArray,indexArray;
        int[]                   vertexes,indexes;
        float[]                 normals,tangents,uvs;
        RagPoint                centerPnt;
        
        trigIdx=0;
        
        vertexArray=new ArrayList<>();
        indexArray=new ArrayList<>();
        
        if (isNegX) {
            vertexArray.addAll(Arrays.asList(negX,negY,negZ,negX,negY,posZ,negX,posY,posZ,negX,posY,negZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        if (isPosX) {
            vertexArray.addAll(Arrays.asList(posX,negY,negZ,posX,negY,posZ,posX,posY,posZ,posX,posY,negZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        if (isNegY) {
            vertexArray.addAll(Arrays.asList(negX,negY,negZ,negX,negY,posZ,posX,negY,posZ,posX,negY,negZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        if (isPosY) {
            vertexArray.addAll(Arrays.asList(negX,posY,negZ,negX,posY,posZ,posX,posY,posZ,posX,posY,negZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        if (isNegZ) {
            vertexArray.addAll(Arrays.asList(negX,negY,negZ,posX,negY,negZ,posX,posY,negZ,negX,posY,negZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        if (isPosZ) {
            vertexArray.addAll(Arrays.asList(negX,negY,posZ,posX,negY,posZ,posX,posY,posZ,negX,posY,posZ));
            trigIdx=addQuadToIndexes(indexArray,trigIdx);
        }
        
            // convert to simple arrays
            
        vertexes=vertexArray.stream().mapToInt(Integer::intValue).toArray();
        indexes=indexArray.stream().mapToInt(Integer::intValue).toArray();
        
            // calculate the normal, tangent, uv
            
        centerPnt=new RagPoint(((negX+posX)/2),((negY+posY)/2),((negZ+posZ)/2));
     
        normals=MeshUtility.buildNormals(vertexes,indexes,centerPnt,false);
        uvs=MeshUtility.buildUVs(vertexes,normals,(1.0f/(float)segmentSize));
        tangents=MeshUtility.buildTangents(vertexes,uvs,indexes);

        return(meshList.add(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes)));
    }
    
        //
        // room pieces
        //
   /*     
    buildRoomFloorCeiling(room,centerPnt,name,bitmap,y,segmentSize)
    {
        let vertexArray=[];
        let normalArray;
        let uvArray;
        let tangentArray;
        let indexArray=[];
        
        vertexArray.push(room.offset.x,y,room.offset.z);
        vertexArray.push((room.offset.x+room.size.x),y,room.offset.z);
        vertexArray.push((room.offset.x+room.size.x),y,(room.offset.z+room.size.z));
        vertexArray.push(room.offset.x,y,(room.offset.z+room.size.z));

        this.addQuadToIndexes(indexArray,0);
        
        normalArray=this.buildNormals(vertexArray,indexArray,centerPnt,true);
        uvArray=this.buildUVs(vertexArray,normalArray,(1/segmentSize));
        tangentArray=this.buildTangents(vertexArray,uvArray,indexArray);
        
        this.core.game.map.meshList.add(new MeshClass(this.core,name,bitmap,-1,-1,new Float32Array(vertexArray),normalArray,tangentArray,uvArray,null,null,new Uint16Array(indexArray)));
    }
    
    buildRoomWalls(room,centerPnt,name,bitmap,segmentSize)
    {
        let n,k,k2,y;
        let nVertex,trigIdx;
        let vertexArray,indexArray,uvArray,normalArray,tangentArray;
        let piece=room.piece;
        
        nVertex=piece.vertexes.length;
        
        vertexArray=[];
        indexArray=[];

        trigIdx=0;
        y=room.offset.y;
        
        for (n=0;n!==room.storyCount;n++) {
            
            for (k=0;k!=nVertex;k++) {
                k2=k+1;
                if (k2===nVertex) k2=0;
                
                if (room.isWallHidden(n,k)) continue;
                
                vertexArray.push((Math.trunc(piece.vertexes[k][0]*segmentSize)+room.offset.x),(y+segmentSize),(Math.trunc(piece.vertexes[k][1]*segmentSize)+room.offset.z));
                vertexArray.push((Math.trunc(piece.vertexes[k2][0]*segmentSize)+room.offset.x),(y+segmentSize),(Math.trunc(piece.vertexes[k2][1]*segmentSize)+room.offset.z));
                vertexArray.push((Math.trunc(piece.vertexes[k2][0]*segmentSize)+room.offset.x),y,(Math.trunc(piece.vertexes[k2][1]*segmentSize)+room.offset.z));
                vertexArray.push((Math.trunc(piece.vertexes[k][0]*segmentSize)+room.offset.x),y,(Math.trunc(piece.vertexes[k][1]*segmentSize)+room.offset.z));

                trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
            }

            y+=segmentSize;
        }
        
        if (vertexArray.length===0) return;

        vertexArray=new Float32Array(vertexArray);
        indexArray=new Uint16Array(indexArray);
        normalArray=this.buildNormals(vertexArray,indexArray,centerPnt,true);
        uvArray=this.buildUVs(vertexArray,normalArray,(1/segmentSize));
        tangentArray=this.buildTangents(vertexArray,uvArray,indexArray);
        
        this.core.game.map.meshList.add(new MeshClass(this.core,name,bitmap,-1,-1,vertexArray,normalArray,tangentArray,uvArray,null,null,indexArray));
    }
    
        //
        // staircases
        //
        
    buildStairs(room,name,stepBitmap,segmentSize,x,y,z,dir,stepWidth,sides)
    {
        let n,trigIdx;
        let sx,sx2,sy,sz,sz2;
        let centerPnt;
        let vertexArray=[];
        let normalArray;
        let uvArray;
        let tangentArray;
        let indexArray=[];
        let stepSize=Math.trunc((segmentSize*10)*0.02);
        let stepHigh=Math.trunc(segmentSize/this.STAIR_STEP_COUNT);

            // initial locations

        switch (dir) {
            case this.STAIR_DIR_POS_Z:
            case this.STAIR_DIR_NEG_Z:
                sx=x;
                sx2=sx+(segmentSize*stepWidth);
                centerPnt=new PointClass(Math.trunc(x+(segmentSize*0.5)),room.offset.y,Math.trunc(z+segmentSize));
                break;
            case this.STAIR_DIR_POS_X:
            case this.STAIR_DIR_NEG_X:
                sz=z;
                sz2=sz+(segmentSize*stepWidth);
                centerPnt=new PointClass(Math.trunc(x+segmentSize),room.offset.y,Math.trunc(z+(segmentSize*0.5)));
                break;
        }
        
            // the steps
        
        trigIdx=0;
        sy=y+stepHigh;
        
        for (n=0;n!==this.STAIR_STEP_COUNT;n++) { 
            
                // step top
                
            switch (dir) {
                case this.STAIR_DIR_POS_Z:
                    sz=z+(n*stepSize);
                    sz2=sz+stepSize;
                    break;
                case this.STAIR_DIR_NEG_Z:
                    sz=(z+(segmentSize*2))-(n*stepSize);
                    sz2=sz-stepSize;
                    break;
                case this.STAIR_DIR_POS_X:
                    sx=x+(n*stepSize);
                    sx2=sx+stepSize;
                    break;
                case this.STAIR_DIR_NEG_X:
                    sx=(x+(segmentSize*2))-(n*stepSize);
                    sx2=sx-stepSize;
                    break;
            }
           
            vertexArray.push(sx,sy,sz);
            vertexArray.push(sx2,sy,sz);
            vertexArray.push(sx2,sy,sz2);
            vertexArray.push(sx,sy,sz2);
            
            trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
            
                // step front
                
            switch (dir) {
                case this.STAIR_DIR_POS_Z:
                case this.STAIR_DIR_NEG_Z:
                    vertexArray.push(sx,sy,sz);
                    vertexArray.push(sx2,sy,sz);
                    vertexArray.push(sx2,(sy-stepHigh),sz);
                    vertexArray.push(sx,(sy-stepHigh),sz);
                    break;
                case this.STAIR_DIR_POS_X:
                case this.STAIR_DIR_NEG_X:
                    vertexArray.push(sx,sy,sz);
                    vertexArray.push(sx,sy,sz2);
                    vertexArray.push(sx,(sy-stepHigh),sz2);
                    vertexArray.push(sx,(sy-stepHigh),sz);
                    break;
            }
            
            trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
            
                // step sides
                
            if (sides) {
                switch (dir) {
                    case this.STAIR_DIR_POS_Z:
                    case this.STAIR_DIR_NEG_Z:
                        vertexArray.push(sx,sy,sz);
                        vertexArray.push(sx,sy,sz2);
                        vertexArray.push(sx,y,sz2);
                        vertexArray.push(sx,y,sz);
                        vertexArray.push(sx2,sy,sz);
                        vertexArray.push(sx2,sy,sz2);
                        vertexArray.push(sx2,y,sz2);
                        vertexArray.push(sx2,y,sz);
                        break;
                    case this.STAIR_DIR_POS_X:
                    case this.STAIR_DIR_NEG_X:
                        vertexArray.push(sx,sy,sz);
                        vertexArray.push(sx2,sy,sz);
                        vertexArray.push(sx2,y,sz);
                        vertexArray.push(sx,y,sz);
                        vertexArray.push(sx,sy,sz2);
                        vertexArray.push(sx2,sy,sz2);
                        vertexArray.push(sx2,y,sz2);
                        vertexArray.push(sx,y,sz2);
                        break;
                }

                trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
                trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
            }
            
            sy+=stepHigh;
        }
        
            // step back
        
        if (sides) {
            sy=y+segmentSize;
            
            switch (dir) {
                case this.STAIR_DIR_POS_Z:
                    sx=x+(segmentSize*stepWidth);
                    sz=z+(segmentSize*2);
                    vertexArray.push(x,y,sz);
                    vertexArray.push(sx,y,sz);
                    vertexArray.push(sx,sy,sz);
                    vertexArray.push(x,sy,sz);
                    break;
                case this.STAIR_DIR_NEG_Z:
                    sx=x+(segmentSize*stepWidth);
                    vertexArray.push(x,y,z);
                    vertexArray.push(sx,y,z);
                    vertexArray.push(sx,sy,z);
                    vertexArray.push(x,sy,z);
                    break;
                case this.STAIR_DIR_POS_X:
                    sx=x+(segmentSize*2);
                    sz=z+(segmentSize*stepWidth);
                    vertexArray.push(sx,y,z);
                    vertexArray.push(sx,y,sz);
                    vertexArray.push(sx,sy,sz);
                    vertexArray.push(sx,sy,z);
                    break;
                case this.STAIR_DIR_NEG_X:
                    sz=z+(segmentSize*stepWidth);
                    vertexArray.push(x,y,z);
                    vertexArray.push(x,y,sz);
                    vertexArray.push(x,sy,sz);
                    vertexArray.push(x,sy,z);
                    break;
            }

            trigIdx=this.addQuadToIndexes(indexArray,trigIdx);
        }
        
            // create the mesh
            
        normalArray=this.buildNormals(vertexArray,indexArray,centerPnt,false);
        uvArray=this.buildUVs(vertexArray,normalArray,(1/segmentSize));
        tangentArray=this.buildTangents(vertexArray,uvArray,indexArray);
        
        this.core.game.map.meshList.add(new MeshClass(this.core,name,stepBitmap,-1,-1,new Float32Array(vertexArray),normalArray,tangentArray,uvArray,null,null,new Uint16Array(indexArray)));
    }
    
    buildRoomStairs(room,name,stepBitmap,segmentSize)
    {
        let dir,stepWidth;
        
            // determine room to room direction
            
        if (room.forwardPath) {
            dir=this.STAIR_DIR_POS_Z;
            stepWidth=room.piece.size.x;
        }
        else {
            dir=(room.pathXDeviation>0)?this.STAIR_DIR_POS_X:this.STAIR_DIR_NEG_X;
            stepWidth=room.piece.size.z;
        }
        
        this.buildStairs(room,name,stepBitmap,segmentSize,room.offset.x,room.offset.y,room.offset.z,dir,stepWidth,false);
    }
    
        //
        // cubes
        //

    createCubeRotated(room,name,bitmap,xBound,yBound,zBound,rotAngle,left,right,front,back,top,bottom,normalsIn,uvMode,segmentSize)
    {
        let idx,centerPnt,rotPnt;
        let n,mesh;
        let vertexArray=[];
        let uvArray=[];
        let normalArray,tangentArray;
        let indexArray=[];
        
        idx=0;
        
            // left

        if (left) {
            vertexArray.push(xBound.min,yBound.max,zBound.min);
            vertexArray.push(xBound.min,yBound.min,zBound.min);
            vertexArray.push(xBound.min,yBound.min,zBound.max);        
            vertexArray.push(xBound.min,yBound.max,zBound.max);     
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(0,0,0,1,1,1,1,0);
                    break;
                case this.UV_BOX:
                    uvArray.push(0,0,0,0.499,0.499,0.499,0.499,0);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

             // right

        if (right) {
            vertexArray.push(xBound.max,yBound.max,zBound.min);
            vertexArray.push(xBound.max,yBound.min,zBound.min);
            vertexArray.push(xBound.max,yBound.min,zBound.max);
            vertexArray.push(xBound.max,yBound.max,zBound.max);
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(0,1,0,0,1,0,1,1);
                    break;
                case this.UV_BOX:
                    uvArray.push(0,0.499,0,0,0.499,0,0.499,0.499);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

            // front

        if (front) {
            vertexArray.push(xBound.min,yBound.max,zBound.min);
            vertexArray.push(xBound.min,yBound.min,zBound.min);
            vertexArray.push(xBound.max,yBound.min,zBound.min);
            vertexArray.push(xBound.max,yBound.max,zBound.min);
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(1,0,1,1,0,1,0,0);
                    break;
                case this.UV_BOX:
                    uvArray.push(1,0,1,0.499,0.5,0.499,0.5,0);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

            // back

        if (back) {
            vertexArray.push(xBound.min,yBound.max,zBound.max);
            vertexArray.push(xBound.min,yBound.min,zBound.max);
            vertexArray.push(xBound.max,yBound.min,zBound.max);
            vertexArray.push(xBound.max,yBound.max,zBound.max);
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(0,0,0,1,1,1,1,0);
                    break;
                case this.UV_BOX:
                    uvArray.push(0.5,0,0.5,0.499,1,0.499,1,0);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

            // top

        if (top) {
            vertexArray.push(xBound.min,yBound.max,zBound.max);
            vertexArray.push(xBound.min,yBound.max,zBound.min);
            vertexArray.push(xBound.max,yBound.max,zBound.min);
            vertexArray.push(xBound.max,yBound.max,zBound.max);
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(0,0,0,1,1,1,1,0);
                    break;
                case this.UV_BOX:
                    uvArray.push(0,0.499,0,1,0.499,1,0.499,0.499);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

            // bottom

        if (bottom) {
            vertexArray.push(xBound.min,yBound.min,zBound.max);
            vertexArray.push(xBound.min,yBound.min,zBound.min);
            vertexArray.push(xBound.max,yBound.min,zBound.min);
            vertexArray.push(xBound.max,yBound.min,zBound.max);
            
            switch (uvMode) {
                case this.UV_WHOLE:
                    uvArray.push(0,0,0,1,1,1,1,0);
                    break;
                case this.UV_BOX:
                    uvArray.push(0,0.499,0,1,0.499,1,0.499,0.499);
                    break;
            }
            
            indexArray.push(idx,(idx+1),(idx+2),idx,(idx+2),(idx+3));
            idx+=4;
        }

            // rotate
        
        centerPnt=new PointClass(xBound.getMidPoint(),yBound.getMidPoint(),zBound.getMidPoint());

        if (rotAngle!==null) {
            rotPnt=new PointClass(0,0,0);
            
            for (n=0;n<vertexArray.length;n+=3) {
                rotPnt.setFromValues(vertexArray[n],vertexArray[n+1],vertexArray[n+2]);
                rotPnt.rotateAroundPoint(centerPnt,rotAngle);
                vertexArray[n]=rotPnt.x;
                vertexArray[n+1]=rotPnt.y;
                vertexArray[n+2]=rotPnt.z;
            }
        }

            // create the mesh

        normalArray=this.buildNormals(vertexArray,indexArray,centerPnt,normalsIn);
        if (uvMode===this.UV_MAP) uvArray=this.buildUVs(vertexArray,normalArray,(1/segmentSize));
        tangentArray=this.buildTangents(vertexArray,uvArray,indexArray);
        
        mesh=new MeshClass(this.core,name,bitmap,-1,-1,new Float32Array(vertexArray),new Float32Array(normalArray),tangentArray,new Float32Array(uvArray),null,null,new Uint16Array(indexArray));
        this.core.game.map.meshList.add(mesh);
    }
    
    createCube(room,name,bitmap,xBound,yBound,zBound,left,right,front,back,top,bottom,normalsIn,uvMode,segmentSize)
    {
        return(this.createCubeRotated(room,name,bitmap,xBound,yBound,zBound,null,left,right,front,back,top,bottom,normalsIn,uvMode,segmentSize));
    }
    
        //
        // cylinders
        //
    
    createCylinderSegmentList(segmentCount,segmentExtra,segmentRoundPercentage)
    {
        let n;
        let segCount=this.core.randomInt(segmentCount,segmentExtra);
        let segments=[];
        
        segments.push(1.0);      // top always biggest
        
        for (n=0;n!==segCount;n++) {
            if (this.core.randomPercentage(segmentRoundPercentage)) {
                segments.push(segments[segments.length-1]);
            }
            else {
                segments.push(this.core.randomFloat(0.8,0.2));
            }
        }
        
        segments.push(1.0);      // and bottom
        
        return(segments);
    }
    
    createCylinder(room,name,bitmap,centerPnt,yBound,segments,radius,top,bot)
    {
        let n,k,t,y,rd,tx,tz,tx2,tz2,bx,bz,bx2,bz2,mesh;
        let topRad,botRad;
        let u1,u2;
        let iIdx,vStartIdx;
        let yAdd,ySegBound,ang,ang2,angAdd;
        let sideCount=12;
        let segCount=segments.length-1;     // always one extra for top
        let normal=new PointClass(0,0,0);
        let vertexArray=[];
        let normalArray=[];
        let uvArray=[];
        let tangentArray;
        let indexArray=[];
        
        iIdx=0;
        
        angAdd=360.0/sideCount;
        yAdd=Math.trunc(yBound.getSize()/segCount);
            
        ySegBound=yBound.copy();
        ySegBound.min=ySegBound.max-yAdd;
        
        botRad=segments[0]*radius;
            
        for (k=0;k!==segCount;k++) {
            
                // new radius
                
            topRad=segments[k+1]*radius;

                // cyliner faces

            ang=0.0;

            for (n=0;n!==sideCount;n++) {
                ang2=ang+angAdd;
                
                    // the two Us
                    
                u1=(ang*segCount)/360.0;
                u2=(ang2*segCount)/360.0;

                    // force last segment to wrap
                    
                if (n===(sideCount-1)) ang2=0.0;

                rd=ang*(Math.PI/180.0);
                tx=centerPnt.x+((topRad*Math.sin(rd))+(topRad*Math.cos(rd)));
                tz=centerPnt.z+((topRad*Math.cos(rd))-(topRad*Math.sin(rd)));
                
                bx=centerPnt.x+((botRad*Math.sin(rd))+(botRad*Math.cos(rd)));
                bz=centerPnt.z+((botRad*Math.cos(rd))-(botRad*Math.sin(rd)));

                rd=ang2*(Math.PI/180.0);
                tx2=centerPnt.x+((topRad*Math.sin(rd))+(topRad*Math.cos(rd)));
                tz2=centerPnt.z+((topRad*Math.cos(rd))-(topRad*Math.sin(rd)));
                
                bx2=centerPnt.x+((botRad*Math.sin(rd))+(botRad*Math.cos(rd)));
                bz2=centerPnt.z+((botRad*Math.cos(rd))-(botRad*Math.sin(rd)));
                
                    // the points
                    
                vStartIdx=vertexArray.length;
                
                vertexArray.push(tx,ySegBound.min,tz);
                uvArray.push(u1,0.0);
                indexArray.push(iIdx++);
                
                vertexArray.push(tx2,ySegBound.min,tz2);
                uvArray.push(u2,0.0);
                indexArray.push(iIdx++);
                
                vertexArray.push(bx,ySegBound.max,bz);
                uvArray.push(u1,1.0);
                indexArray.push(iIdx++);
                
                vertexArray.push(tx2,ySegBound.min,tz2);
                uvArray.push(u2,0.0);
                indexArray.push(iIdx++);
                
                vertexArray.push(bx2,ySegBound.max,bz2);
                uvArray.push(u2,1.0);
                indexArray.push(iIdx++);
                
                vertexArray.push(bx,ySegBound.max,bz);
                uvArray.push(u1,1.0);
                indexArray.push(iIdx++);
                
                    // the normals
                    
                y=ySegBound.getMidPoint();
                
                for (t=0;t!==6;t++) {
                    normal.x=vertexArray[vStartIdx++]-centerPnt.x;
                    normal.y=(vertexArray[vStartIdx++]-y)*0.25;      // reduce the normal here so cylinders don't have heavy lighting
                    normal.z=vertexArray[vStartIdx++]-centerPnt.z;
                    normal.normalize();
                    normalArray.push(normal.x,normal.y,normal.z);
                }
                
                ang=ang2;
            }

            botRad=topRad;
            
            ySegBound.max=ySegBound.min;
            ySegBound.min-=yAdd;
        }
        
            // top and bottom triangles
            
        if (top) {
            vStartIdx=Math.trunc(vertexArray.length/3);
            
            ang=0.0;
            topRad=segments[0]*radius;

            for (n=0;n!==sideCount;n++) {
                rd=ang*(Math.PI/180.0);
                
                u1=(Math.sin(rd)*0.5)+0.5;
                u2=(Math.cos(rd)*0.5)+0.5;

                tx=centerPnt.x+((topRad*Math.sin(rd))+(topRad*Math.cos(rd)));
                tz=centerPnt.z+((topRad*Math.cos(rd))-(topRad*Math.sin(rd)));
                
                    // the points
                
                vertexArray.push(tx,yBound.max,tz);
                uvArray.push(u1,u2);
                normalArray.push(0.0,1.0,0.0);
                
                ang+=angAdd;
            }

            for (n=0;n!==(sideCount-2);n++) {
                indexArray.push(vStartIdx);
                indexArray.push(vStartIdx+(n+1));
                indexArray.push(vStartIdx+(n+2));
            }
        }
        
        if (bot) {
            vStartIdx=Math.trunc(vertexArray.length/3);
            
            ang=0.0;
            botRad=segments[segments.length-1]*radius;

            for (n=0;n!==sideCount;n++) {
                rd=ang*(Math.PI/180.0);
                
                u1=(Math.sin(rd)*0.5)+0.5;
                u2=(Math.cos(rd)*0.5)+0.5;

                bx=centerPnt.x+((botRad*Math.sin(rd))+(botRad*Math.cos(rd)));
                bz=centerPnt.z+((botRad*Math.cos(rd))-(botRad*Math.sin(rd)));
                
                    // the points
                
                vertexArray.push(bx,yBound.min,bz);
                uvArray.push(u1,u2);
                normalArray.push(0.0,-1.0,0.0);
                
                ang+=angAdd;
            }

            for (n=0;n!==(sideCount-2);n++) {
                indexArray.push(vStartIdx);
                indexArray.push(vStartIdx+(n+1));
                indexArray.push(vStartIdx+(n+2));
            }
        }
        
            // create the mesh

        tangentArray=this.buildTangents(vertexArray,uvArray,indexArray);
        
        mesh=new MeshClass(this.core,name,bitmap,-1,-1,new Float32Array(vertexArray),new Float32Array(normalArray),tangentArray,new Float32Array(uvArray),null,null,new Uint16Array(indexArray));
        this.core.game.map.meshList.add(mesh);
    }
    
    createMeshCylinderSimple(room,name,bitmap,centerPnt,yBound,radius,top,bot)
    {
        let segments=[1.0,1.0];
        
        this.createCylinder(room,name,bitmap,centerPnt,yBound,segments,radius,top,bot);
    }
 
}


    */
}
