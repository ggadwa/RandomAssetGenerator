package com.klinksoftware.rag.mesh;

import static com.klinksoftware.rag.mesh.MeshMapUtility.floatArrayListToFloat;
import static com.klinksoftware.rag.mesh.MeshMapUtility.intArrayListToInt;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MeshModelUtility
{
    private static final int EXTRA_SMOOTH_BONES=3;
    
        //
        // find bounds for a collection of bones
        // and find the width and heigth of a globe
        // that will circle them and be within the min
        // gravity distance
        //
        
    public static void findBoundsForBoneList(ArrayList<Bone> boneList,RagBound xBound,RagBound yBound,RagBound zBound)
    {
        int                 n;
        RagPoint            pnt;
        
        pnt=boneList.get(0).pnt;
        xBound.min=xBound.max=pnt.x;
        yBound.min=yBound.max=pnt.y;
        zBound.min=zBound.max=pnt.z;
        
        for (n=1;n<boneList.size();n++) {
            pnt=boneList.get(n).pnt;
            xBound.adjust(pnt.x);
            yBound.adjust(pnt.y);
            zBound.adjust(pnt.z);
        }
    }

    public static float findMaxGravityForBoneList(ArrayList<Bone> boneList)
    {
        int         n;
        float       maxGravityDist;
        Bone        bone;
        
        maxGravityDist=0;
        
        for (n=0;n!=boneList.size();n++) {
            bone=boneList.get(n);
            if (bone.gravityLockDistance>maxGravityDist) maxGravityDist=bone.gravityLockDistance;
        }
            
        return(maxGravityDist);
    }
    
        //
        // build a large global around
        // center point
        //
  
    public static Mesh buildGlobeAroundSkeletonX(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
    {
        int                 x,yz,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,px,
                            vAng,xAng,yzAng,yzAngAdd,xAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,uvs;
        
            // allocate proper buffers
            
        vertexArray=new ArrayList<>();
        uvArray=new ArrayList<>();
        indexArray=new ArrayList<>();
        
            // create the globe without a top
            // or bottom and build that with trigs later

        yzAngAdd=360.0f/(float)aroundSurfaceCount;
        xAngAdd=180.0f/(float)(acrossSurfaceCount-1);
        xAng=xAngAdd;
        
        for (x=1;x!=(acrossSurfaceCount-1);x++) {
            
                // get x position and radius
                // from angle
            
            rd=xAng*((float)Math.PI/180.0f);
            radius=aroundRadius*(float)Math.sin(rd);
            px=centerPnt.x-(acrossRadius*(float)Math.cos(rd));
            
            vAng=xAng/180.0f;
            
                // the band of vertexes
            
            yzAng=0.0f;
            
            for (yz=0;yz<=aroundSurfaceCount;yz++) {
                rd=(yz!=aroundSurfaceCount)?(yzAng*((float)Math.PI/180.0f)):0.0f;
                
                vertexArray.add(px);
                vertexArray.add(centerPnt.y+((radius*(float)Math.sin(rd))+(radius*(float)Math.cos(rd))));
                vertexArray.add(centerPnt.z+((radius*(float)Math.cos(rd))-(radius*(float)Math.sin(rd))));

                uvArray.add((yz!=aroundSurfaceCount)?(yzAng/360.0f):0.9999f);
                uvArray.add(vAng);

                yzAng+=yzAngAdd;
            }
            
            xAng+=xAngAdd;
        }
        /*
            // end points
       
        minIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues((centerPnt.x-acrossRadius),centerPnt.y,centerPnt.z);
        v.uv.setFromValues(0.5,0.0);

        maxIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues((centerPnt.x+acrossRadius),centerPnt.y,centerPnt.z);
        v.uv.setFromValues(0.5,1.0);
        
            // build the triangles on
            // all the strips except the
            // end points
            
        iIdx=0;
        
        for (x=0;x!==(acrossSurfaceCount-3);x++) {
            
            for (yz=0;yz!==aroundSurfaceCount;yz++) {
                
                vIdx=(x*(aroundSurfaceCount+1))+yz;
                v2Idx=((x+1)*(aroundSurfaceCount+1))+yz;
                
                vNextIdx=(x*(aroundSurfaceCount+1))+(yz+1);
                v2NextIdx=((x+1)*(aroundSurfaceCount+1))+(yz+1);
                 
                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vIdx;
                indexes[iIdx++]=vNextIdx;

                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vNextIdx;
                indexes[iIdx++]=v2NextIdx;
            }
        }
        
            // min end point
        
        for (yz=0;yz!==aroundSurfaceCount;yz++) {
            indexes[iIdx++]=yz;
            indexes[iIdx++]=minIdx;
            indexes[iIdx++]=yz+1;
        }
        
            // max end point
            
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (yz=0;yz!==aroundSurfaceCount;yz++) {
            indexes[iIdx++]=maxOff+yz;
            indexes[iIdx++]=maxIdx;
            indexes[iIdx++]=maxOff+(yz+1);
        }
        */
            // create the mesh
            
        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        
        return(new Mesh(name,bitmapName,vertexes,normals,uvs,indexes));
    }
    
    public static Mesh buildGlobeAroundSkeletonY(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
    {
        int                 xz,y,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,py,
                            vAng,xzAng,yAng,xzAngAdd,yAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,uvs;
        
            // allocate proper buffers
            
        vertexArray=new ArrayList<>();
        uvArray=new ArrayList<>();
        indexArray=new ArrayList<>();

            // create the globe without a top
            // or bottom and build that with trigs later

        xzAngAdd=360.0f/(float)aroundSurfaceCount;
        yAngAdd=180.0f/(float)(acrossSurfaceCount-1);
        yAng=yAngAdd;
        
        for (y=1;y!=(acrossSurfaceCount-1);y++) {
            
                // get y position and radius
                // from angle
            
            rd=yAng*((float)Math.PI/180.0f);
            radius=aroundRadius*(float)Math.sin(rd);
            py=centerPnt.y-(acrossRadius*(float)Math.cos(rd));
            
            vAng=yAng/180.0f;
            
                // the band of vertexes
            
            xzAng=0.0f;
            
            for (xz=0;xz<=aroundSurfaceCount;xz++) {
                rd=(xz!=aroundSurfaceCount)?(xzAng*((float)Math.PI/180.0f)):0.0f;
                
                vertexArray.add(centerPnt.x+((radius*(float)Math.sin(rd))+(radius*(float)Math.cos(rd))));
                vertexArray.add(py);
                vertexArray.add(centerPnt.z+((radius*(float)Math.cos(rd))-(radius*(float)Math.sin(rd))));

                uvArray.add((xz!=aroundSurfaceCount)?(xzAng/360.0f):0.9999f);
                uvArray.add(vAng);
                
                xzAng+=xzAngAdd;
            }
            
            yAng+=yAngAdd;
        }
        /*
            // end points
        
        minIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues(centerPnt.x,(centerPnt.y-acrossRadius),centerPnt.z);
        v.uv.setFromValues(0.5,0.0);
    
        maxIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues(centerPnt.x,(centerPnt.y+acrossRadius),centerPnt.z);
        v.uv.setFromValues(0.5,1.0);
    
            // build the triangles on
            // all the strips except the
            // end points
            
        iIdx=0;
        
        for (y=0;y!==(acrossSurfaceCount-3);y++) {
            
            for (xz=0;xz!==aroundSurfaceCount;xz++) {
                
                vIdx=(y*(aroundSurfaceCount+1))+xz;
                v2Idx=((y+1)*(aroundSurfaceCount+1))+xz;
                
                vNextIdx=(y*(aroundSurfaceCount+1))+(xz+1);
                v2NextIdx=((y+1)*(aroundSurfaceCount+1))+(xz+1);
                 
                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vIdx;
                indexes[iIdx++]=vNextIdx;

                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vNextIdx;
                indexes[iIdx++]=v2NextIdx;
            }
        }
        
            // min end point
        
        for (xz=0;xz!==aroundSurfaceCount;xz++) {
            indexes[iIdx++]=xz;
            indexes[iIdx++]=minIdx;
            indexes[iIdx++]=xz+1;
        }
        
            // max end point
        
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (xz=0;xz!==aroundSurfaceCount;xz++) {
            indexes[iIdx++]=maxOff+xz;
            indexes[iIdx++]=maxIdx;
            indexes[iIdx++]=maxOff+(xz+1);
        }
        */
            // create the mesh
            
        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        
        return(new Mesh(name,bitmapName,vertexes,normals,uvs,indexes));
    }
    
    public static Mesh buildGlobeAroundSkeletonZ(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
    {
        int                 xy,z,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,pz,
                            vAng,xyAng,zAng,xyAngAdd,zAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,uvs;
        
            // allocate proper buffers
            
        vertexArray=new ArrayList<>();
        uvArray=new ArrayList<>();
        indexArray=new ArrayList<>();
        
            // create the globe without a top
            // or bottom and build that with trigs later
  
        xyAngAdd=360.0f/(float)aroundSurfaceCount;
        zAngAdd=180.0f/(float)(acrossSurfaceCount-1);
        zAng=zAngAdd;
        
        for (z=1;z!=(acrossSurfaceCount-1);z++) {
            
                // get y position and radius
                // from angle
            
            rd=zAng*((float)Math.PI/180.0f);
            radius=aroundRadius*(float)Math.sin(rd);
            pz=centerPnt.z-(acrossRadius*(float)Math.cos(rd));
            
            vAng=zAng/180.0f;
            
                // the band of vertexes
            
            xyAng=0.0f;
            
            for (xy=0;xy<=aroundSurfaceCount;xy++) {
                rd=(xy!=aroundSurfaceCount)?(xyAng*((float)Math.PI/180.0f)):0.0f;
                
                vertexArray.add(centerPnt.x+((radius*(float)Math.sin(rd))+(radius*(float)Math.cos(rd))));
                vertexArray.add(centerPnt.y+((radius*(float)Math.cos(rd))-(radius*(float)Math.sin(rd))));
                vertexArray.add(pz);

                uvArray.add((xy!=aroundSurfaceCount)?(xyAng/360.0f):0.9999f);
                uvArray.add(vAng);

                xyAng+=xyAngAdd;
            }
            
            zAng+=zAngAdd;
        }
        /*
            // end points
        
        minIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues(centerPnt.x,centerPnt.y,(centerPnt.z-acrossRadius));
        v.uv.setFromValues(0.5,0.0);
    
        maxIdx=vIdx;

        v=vertexList[vIdx++];
        v.position.setFromValues(centerPnt.x,centerPnt.y,(centerPnt.z+acrossRadius));
        v.uv.setFromValues(0.5,1.0);
        
            // build the triangles on
            // all the strips except the
            // end points
            
        iIdx=0;
        
        for (z=0;z!==(acrossSurfaceCount-3);z++) {
            
            for (xy=0;xy!==aroundSurfaceCount;xy++) {
                
                vIdx=(z*(aroundSurfaceCount+1))+xy;
                v2Idx=((z+1)*(aroundSurfaceCount+1))+xy;
                
                vNextIdx=(z*(aroundSurfaceCount+1))+(xy+1);
                v2NextIdx=((z+1)*(aroundSurfaceCount+1))+(xy+1);
                 
                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vIdx;
                indexes[iIdx++]=vNextIdx;

                indexes[iIdx++]=v2Idx;
                indexes[iIdx++]=vNextIdx;
                indexes[iIdx++]=v2NextIdx;
            }
        }
        
            // min end point
        
        for (xy=0;xy!==aroundSurfaceCount;xy++) {
            indexes[iIdx++]=xy;
            indexes[iIdx++]=minIdx;
            indexes[iIdx++]=xy+1;
        }
        
            // max end point
        
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (xy=0;xy!==aroundSurfaceCount;xy++) {
            indexes[iIdx++]=maxOff+xy;
            indexes[iIdx++]=maxIdx;
            indexes[iIdx++]=maxOff+(xy+1);
        }
        */
            // create the mesh
            
        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        
        return(new Mesh(name,bitmapName,vertexes,normals,uvs,indexes));
    }

        //
        // shrink wrap the globe around a
        // collection of points
        //
/*        
    shrinkWrapGlobe(vertexList,boneList,centerPnt)
    {
        let n,k;
        let v,bone,dist,shrinkDist,gravityMaxDistance;
        let nVertex=vertexList.length;
        let nBone=boneList.length;
        let moving=[];
        let anyMove;
        let moveVector=new PointClass(0,0,0);
        let gravityVector=new PointClass(0,0,0);
        let moveCount=0;
        let boneHit;
        
            // move distance for shrinking bones
            
        shrinkDist=10.0;
        gravityMaxDistance=5000;
        
            // keep a parallel list of
            // what bones are moving (bones
            // stop when they get within the
            // gravity min distance of all gravity bones)
        
        for (n=0;n!==nVertex;n++) {
            moving.push(true);
        }
        
            // loop the moves
            
        while (moveCount<1000) {
            
            moveCount++;
            anyMove=false;
        
                // run through the vertices

            for (n=0;n!==nVertex;n++) {

                    // is this one moving?

                if (!moving[n]) continue;

                    // get the vertex

                v=vertexList[n];
                               
                    // get the gravity to each bone

                boneHit=false;
                moveVector.setFromValues(0,0,0);
                
                for (k=0;k!==nBone;k++) {
                    bone=boneList[k];
                    dist=bone.position.distance(v.position);
                    
                        // if too close, then all movement stops
                        
                    if (dist<bone.gravityLockDistance) {
                        moving[n]=false;
                        break;
                    }
                    
                        // outside of max gravity well
                        
                    if (dist>gravityMaxDistance) continue;
                    
                        // otherwise add in gravity
                        
                    gravityVector.setFromSubPoint(bone.position,v.position);
                    gravityVector.normalize();
                    gravityVector.scale((1.0-(dist/gravityMaxDistance))*shrinkDist);
                    
                    moveVector.addPoint(gravityVector);
                    
                    boneHit=true;
                }
                
                    // are we done moving?
                    
                if (!moving[n]) continue;
                
                    // if we didn't hit any bones, then we
                    // always move towards center, otherwise do
                    // the gravity move
                    
                if (!boneHit) {
                    moveVector.setFromSubPoint(centerPnt,v.position);
                    moveVector.normalize();
                    moveVector.scale(shrinkDist);
                }

                v.position.addPoint(moveVector);
                
                    // we did a move so we go
                    // around again
                    
                anyMove=true;
            }
            
                // no moves?  Then done
                
            if (!anyMove) break;
        }
    }
    
        //
        // attach vertices to nearest bone
        //
        
    attachVertexToBones(vertexList,boneList,centerPnt)
    {
        let n,k,v;
        let bone,boneIdx,d,dist;
        let nVertex=vertexList.length;
        let nBone=boneList.length;
        
        for (n=0;n!==nVertex;n++) {
            v=vertexList[n];
            
                // attach a bone
                
            boneIdx=-1;
            
            for (k=0;k!==nBone;k++) {
                bone=boneList[k];
                if (bone.idx===-1) continue;        // this is a temp bone, skip it

                d=bone.position.distance(v.position);
                if (boneIdx===-1) {
                    boneIdx=boneList[k].idx;
                    dist=d;
                }
                else {
                    if (d<dist) {
                        boneIdx=boneList[k].idx;
                        dist=d;
                    }
                }
            }
            
            v.boneIdx=boneIdx;
        }
    }
    
        //
        // scale all the vertexes along
        // the line to the bone, use this to
        // squish a model in a certain direction
        //
        
    scaleVertexToBones(vertexList,scaleMin,scaleMax)
    {
        let n,v;
        let nVertex=vertexList.length;
        let bones=this.model.skeleton.bones;
        
        for (n=0;n!==nVertex;n++) {
            v=vertexList[n];
            if (v.boneIdx!==-1) v.position.scaleFromMinMaxPoint(bones[v.boneIdx].position,scaleMin,scaleMax);
        }
    }
    */
        //
        // random vertex moves along
        // the line to the attached bone
        //
        
    public static void randomScaleVertexToBones(Mesh mesh)
    {
    /*
        let n,k,v,v2,f;
        let bone,pos;
        let nVertex=vertexList.length;
        let bones=this.model.skeleton.bones;
        
        let prevMove=new Uint8Array(nVertex);
        
        pos=new PointClass(0,0,0);

        for (n=0;n!==nVertex;n++) {
            v=vertexList[n];
            if (v.boneIdx===-1) continue;
            
                // some vertexes are in the same place,
                // we don't want to move them separately or
                // the mesh breaks up
                
            if (prevMove[n]!==0) continue;
            
                // get original position
                
            pos.setFromPoint(v.position);
            
                // move the vertex
                // and any similar vertex
            
            bone=bones[v.boneIdx];
            f=0.9+(genRandom.random()*0.2);
            
            for (k=0;k!==nVertex;k++) {
                if (prevMove[k]!==0) continue;
                v2=vertexList[k];
                if ((k===n) || (v2.position.truncEquals(pos))) {
                    v2.position.subPoint(bone.position);
                    v2.position.scale(f);
                    v2.position.addPoint(bone.position);
                    prevMove[k]=1;
                }
            }
        }
    */
    }

        //
        // build mesh around limb
        //
        
    public static Mesh buildMeshAroundBoneLimb(Skeleton skeleton,Limb limb,String bitmapName)
    {
        int                     n,k,boneCount;
        float                   f,gravityLockDistance,maxGravity,
                                acrossRadius,aroundRadius;
        RagPoint                pnt,centerPnt;
        RagBound                xBound,yBound,zBound;
        Bone                    bone,parentBone,extraBone;
        ArrayList<Bone>         boneList;
        Mesh                    mesh;
        
            // create list of bones
            
        boneList=new ArrayList<>();
        boneCount=limb.boneIndexes.length;
        
        for (n=0;n!=boneCount;n++) {
            boneList.add(skeleton.bones.get(limb.boneIndexes[n]));
        }
        
            // if any bone in the list is a parent of
            // another bone in the list, then add some
            // temp bones to smooth out the shrink wrapping
   
        for (n=0;n!=boneCount;n++) {
            bone=boneList.get(n);
            
                // parented in this list?
                
            parentBone=null;
            
            for (k=0;k!=boneCount;k++) {
                if (n==k) continue;
                if (boneList.get(k).children.contains(bone)) {
                    parentBone=boneList.get(k);
                    break;
                }
            }
            
            if (parentBone==null) continue;
            
                // create temp bones
                // based on the distance, we insert extra
                // bones inbetween to smooth out shrink wrap
             
            for (k=1;k!=EXTRA_SMOOTH_BONES;k++) {
                f=(float)k/(float)EXTRA_SMOOTH_BONES;
                
                gravityLockDistance=bone.gravityLockDistance+((parentBone.gravityLockDistance-bone.gravityLockDistance)*f);
                pnt=new RagPoint((bone.pnt.x+((parentBone.pnt.x-bone.pnt.x)*f)),(bone.pnt.y+((parentBone.pnt.y-bone.pnt.y)*f)),(bone.pnt.z+((parentBone.pnt.z-bone.pnt.z)*f)));
                extraBone=new Bone(("extra_"+Integer.toString(k)),-1,gravityLockDistance,pnt);
                
                boneList.add(extraBone);
            }
        }
   
            // find the bounds for this list of bones
            
        xBound=new RagBound(0.0f,0.0f);
        yBound=new RagBound(0.0f,0.0f);
        zBound=new RagBound(0.0f,0.0f);
        
        findBoundsForBoneList(boneList,xBound,yBound,zBound);
        centerPnt=new RagPoint(xBound.getMidPoint(),yBound.getMidPoint(),zBound.getMidPoint());
        
            // build the globe around the bones
            
        mesh=null;
        maxGravity=findMaxGravityForBoneList(boneList);
        
        switch (limb.axis) {
            case Limb.LIMB_AXIS_X:
                acrossRadius=(xBound.getSize()*0.5f)+maxGravity;
                aroundRadius=(yBound.getSize()>zBound.getSize())?((yBound.getSize()*0.5f)+maxGravity):((zBound.getSize()*0.5f)+maxGravity);
                mesh=buildGlobeAroundSkeletonX(limb.name,bitmapName,limb.acrossSurfaceCount,limb.aroundSurfaceCount,centerPnt,acrossRadius,aroundRadius);
                break;
            case Limb.LIMB_AXIS_Y:
                acrossRadius=(yBound.getSize()*0.5f)+maxGravity;
                aroundRadius=(xBound.getSize()>zBound.getSize())?((xBound.getSize()*0.5f)+maxGravity):((zBound.getSize()*0.5f)+maxGravity);
                mesh=buildGlobeAroundSkeletonY(limb.name,bitmapName,limb.acrossSurfaceCount,limb.aroundSurfaceCount,centerPnt,acrossRadius,aroundRadius);
                break;
            case Limb.LIMB_AXIS_Z:
                acrossRadius=(zBound.getSize()*0.5f)+maxGravity;
                aroundRadius=(xBound.getSize()>yBound.getSize())?((xBound.getSize()*0.5f)+maxGravity):((yBound.getSize()*0.5f)+maxGravity);
                mesh=buildGlobeAroundSkeletonZ(limb.name,bitmapName,limb.acrossSurfaceCount,limb.aroundSurfaceCount,centerPnt,acrossRadius,aroundRadius);
                break;
        }
        
            // reset the UVs to work within the
            // texture chunks

        switch (limb.limbType) {
            case Limb.LIMB_TYPE_BODY:
                mesh.transformUVs(0.0f,0.5f,0.5f,0.5f);
                break;
            case Limb.LIMB_TYPE_HEAD:
            case Limb.LIMB_TYPE_JAW:
                mesh.transformUVs(0.5f,0.0f,0.5f,0.5f);
                break;
            default:
                mesh.transformUVs(0.0f,0.0f,0.5f,0.5f);
                break;
        }

            // shrink wrap the globe and rebuild
            // any normals, etc
            
        //this.shrinkWrapGlobe(vertexList,boneList,centerPnt);
        //this.attachVertexToBones(vertexList,boneList,centerPnt);
        //this.scaleVertexToBones(vertexList,limb.scaleMin,limb.scaleMax);

        return(mesh);
    }
    /*
        //
        // builds the normals based on bones
        //
        
    buildNormalsToBones(vertexList)
    {
        let n,k,v,bone,bonePos,dist,curDist;
        let xBound,yBound,zBound,centerPnt;
        let nVertex=vertexList.length;
        let bones=this.model.skeleton.bones;
        let nBone=bones.length;
        
            // get the center in case there's a no-attachment
            // bone (this shouldn't happen right now)
            
        xBound=new BoundClass(0,0);
        yBound=new BoundClass(0,0);
        zBound=new BoundClass(0,0);
        
        this.findBoundsForBoneList(bones,xBound,yBound,zBound);
        centerPnt=new PointClass(xBound.getMidPoint(),yBound.getMidPoint(),zBound.getMidPoint());
        
            // find the closest bone in the bone list,
            // even though we have attachments, we need to
            // use the enlarged bone list because it
            // gives up better normals
        
        for (n=0;n!==nVertex;n++) {
            v=vertexList[n];
            
            curDist=-1;
            bonePos=null;
            
            for (k=0;k!==nBone;k++) {
                bone=bones[k];
                dist=bone.position.distance(v.position);
                
                if ((dist<curDist) || (curDist===-1)) {
                    curDist=dist;
                    bonePos=bone.position;
                }
            }
            
                // rebuild the normals
                
            if (bonePos===null) {
                v.normal.setFromSubPoint(v.position,centerPnt);
            }
            else {
                v.normal.setFromSubPoint(v.position,bonePos);
            }
            
            v.normal.normalize();
        }
    }    
    
    */
}
