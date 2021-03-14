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
        
    private static void findBoundsForBoneList(ArrayList<Bone> boneList,RagBound xBound,RagBound yBound,RagBound zBound)
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

    private static float findMaxGravityForBoneList(ArrayList<Bone> boneList)
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
  
    private static Mesh buildGlobeAroundSkeletonX(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
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

            // end points
       
        minIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x-acrossRadius);
        vertexArray.add(centerPnt.y);
        vertexArray.add(centerPnt.z);
        
        uvArray.add(0.5f);
        uvArray.add(0.0f);

        maxIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x+acrossRadius);
        vertexArray.add(centerPnt.y);
        vertexArray.add(centerPnt.z);
        
        uvArray.add(0.5f);
        uvArray.add(1.0f);
        
            // build the triangles on
            // all the strips except the
            // end points

        for (x=0;x!=(acrossSurfaceCount-3);x++) {
            
            for (yz=0;yz!=aroundSurfaceCount;yz++) {
                
                vIdx=(x*(aroundSurfaceCount+1))+yz;
                v2Idx=((x+1)*(aroundSurfaceCount+1))+yz;
                
                vNextIdx=(x*(aroundSurfaceCount+1))+(yz+1);
                v2NextIdx=((x+1)*(aroundSurfaceCount+1))+(yz+1);
                 
                indexArray.add(v2Idx);
                indexArray.add(vIdx);
                indexArray.add(vNextIdx);

                indexArray.add(v2Idx);
                indexArray.add(vNextIdx);
                indexArray.add(v2NextIdx);
            }
        }
        
            // min end point
        
        for (yz=0;yz!=aroundSurfaceCount;yz++) {
            indexArray.add(yz);
            indexArray.add(minIdx);
            indexArray.add(yz+1);
        }
        
            // max end point
            
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (yz=0;yz!=aroundSurfaceCount;yz++) {
            indexArray.add(maxOff+yz);
            indexArray.add(maxIdx);
            indexArray.add(maxOff+(yz+1));
        }

            // create the mesh
            
        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        
        return(new Mesh(name,bitmapName,vertexes,normals,uvs,indexes));
    }
    
    private static Mesh buildGlobeAroundSkeletonY(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
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

            // end points
        
        minIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x);
        vertexArray.add(centerPnt.y-acrossRadius);
        vertexArray.add(centerPnt.z);
        
        uvArray.add(0.5f);
        uvArray.add(0.0f);
    
        maxIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x);
        vertexArray.add(centerPnt.y+acrossRadius);
        vertexArray.add(centerPnt.z);
        
        uvArray.add(0.5f);
        uvArray.add(1.0f);
    
            // build the triangles on
            // all the strips except the
            // end points
  
        for (y=0;y!=(acrossSurfaceCount-3);y++) {
            
            for (xz=0;xz!=aroundSurfaceCount;xz++) {
                
                vIdx=(y*(aroundSurfaceCount+1))+xz;
                v2Idx=((y+1)*(aroundSurfaceCount+1))+xz;
                
                vNextIdx=(y*(aroundSurfaceCount+1))+(xz+1);
                v2NextIdx=((y+1)*(aroundSurfaceCount+1))+(xz+1);
                 
                indexArray.add(v2Idx);
                indexArray.add(vIdx);
                indexArray.add(vNextIdx);

                indexArray.add(v2Idx);
                indexArray.add(vNextIdx);
                indexArray.add(v2NextIdx);
            }
        }
        
            // min end point
        
        for (xz=0;xz!=aroundSurfaceCount;xz++) {
            indexArray.add(xz);
            indexArray.add(minIdx);
            indexArray.add(xz+1);
        }
        
            // max end point
        
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (xz=0;xz!=aroundSurfaceCount;xz++) {
            indexArray.add(maxOff+xz);
            indexArray.add(maxIdx);
            indexArray.add(maxOff+(xz+1));
        }

            // create the mesh
            
        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        
        return(new Mesh(name,bitmapName,vertexes,normals,uvs,indexes));
    }
    
    private static Mesh buildGlobeAroundSkeletonZ(String name,String bitmapName,int acrossSurfaceCount,int aroundSurfaceCount,RagPoint centerPnt,float acrossRadius,float aroundRadius)
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

            // end points
        
        minIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x);
        vertexArray.add(centerPnt.y);
        vertexArray.add(centerPnt.z-acrossRadius);
        
        uvArray.add(0.5f);
        uvArray.add(0.0f);
    
        maxIdx=vertexArray.size()/3;

        vertexArray.add(centerPnt.x);
        vertexArray.add(centerPnt.y);
        vertexArray.add(centerPnt.z+acrossRadius);
        
        uvArray.add(0.5f);
        uvArray.add(1.0f);
        
            // build the triangles on
            // all the strips except the
            // end points
 
        for (z=0;z!=(acrossSurfaceCount-3);z++) {
            
            for (xy=0;xy!=aroundSurfaceCount;xy++) {
                
                vIdx=(z*(aroundSurfaceCount+1))+xy;
                v2Idx=((z+1)*(aroundSurfaceCount+1))+xy;
                
                vNextIdx=(z*(aroundSurfaceCount+1))+(xy+1);
                v2NextIdx=((z+1)*(aroundSurfaceCount+1))+(xy+1);
                 
                indexArray.add(v2Idx);
                indexArray.add(vIdx);
                indexArray.add(vNextIdx);

                indexArray.add(v2Idx);
                indexArray.add(vNextIdx);
                indexArray.add(v2NextIdx);
            }
        }
        
            // min end point
        
        for (xy=0;xy!=aroundSurfaceCount;xy++) {
            indexArray.add(xy);
            indexArray.add(minIdx);
            indexArray.add(xy+1);
        }
        
            // max end point
        
        maxOff=(aroundSurfaceCount+1)*(acrossSurfaceCount-3);

        for (xy=0;xy!=aroundSurfaceCount;xy++) {
            indexArray.add(maxOff+xy);
            indexArray.add(maxIdx);
            indexArray.add(maxOff+(xy+1));
        }

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
    
    private static void shrinkWrapGlobe(Mesh mesh,ArrayList<Bone> boneList,RagPoint centerPnt,float gravityMaxDistance)
    {
        int                 n,k,vIdx,nVertex,nBone,moveCount;
        float               dist,shrinkDist;
        boolean             anyMove,boneHit;
        boolean[]           moving;
        Bone                bone;
        RagPoint            pnt,moveVector,gravityVector;
        
            // move distance for shrinking bones
            
        shrinkDist=gravityMaxDistance*0.001f;
        
            // keep a parallel list of
            // what bones are moving (bones
            // stop when they get within the
            // gravity min distance of all gravity bones)
        
        nVertex=mesh.vertexes.length/3;
        nBone=boneList.size();
        
        moving=new boolean[nVertex];
        
        Arrays.fill(moving,true);
        
            // loop the moves
            
        moveCount=0;
        pnt=new RagPoint(0.0f,0.0f,0.0f);
        moveVector=new RagPoint(0.0f,0.0f,0.0f);
        gravityVector=new RagPoint(0.0f,0.0f,0.0f);
        
        while (moveCount<1000) {
            
            moveCount++;
            anyMove=false;
        
                // run through the vertices

            for (n=0;n!=nVertex;n++) {

                    // is this one moving?

                if (!moving[n]) continue;

                    // get the vertex

                vIdx=n*3;
                pnt.x=mesh.vertexes[vIdx];
                pnt.y=mesh.vertexes[vIdx+1];
                pnt.z=mesh.vertexes[vIdx+2];
                
                    // get the gravity to each bone

                boneHit=false;
                moveVector.setFromValues(0.0f,0.0f,0.0f);
                
                for (k=0;k!=nBone;k++) {
                    bone=boneList.get(k);
                    dist=bone.pnt.distance(pnt);
                    
                        // if too close, then all movement stops
                        
                    if (dist<bone.gravityLockDistance) {
                        moving[n]=false;
                        break;
                    }
                    
                        // outside of max gravity well
                        
                    if (dist>gravityMaxDistance) continue;
                    
                        // otherwise add in gravity
                        
                    gravityVector.setFromSubPoint(bone.pnt,pnt);
                    gravityVector.normalize();
                    gravityVector.scale((1.0f-(dist/gravityMaxDistance))*shrinkDist);
                    
                    moveVector.addPoint(gravityVector);
                    
                    boneHit=true;
                }
                
                    // are we done moving?
                    
                if (!moving[n]) continue;
                
                    // if we didn't hit any bones, then we
                    // always move towards center, otherwise do
                    // the gravity move
                    
                if (!boneHit) {
                    moveVector.setFromSubPoint(centerPnt,pnt);
                    moveVector.normalize();
                    moveVector.scale(shrinkDist);
                }

                mesh.vertexes[vIdx]+=moveVector.x;
                mesh.vertexes[vIdx+1]+=moveVector.y;
                mesh.vertexes[vIdx+2]+=moveVector.z;
                
                    // we did a move so we go
                    // around again
                    
                anyMove=true;
            }
            
                // no moves?  Then done
                
            if (!anyMove) break;
        }
    }
        
        //
        // scale all the vertexes along
        // the line of the bones, to
        // squish the model in a certain direction
        //
        
    private static void scaleVertexToBones(Mesh mesh,RagPoint centerPnt,RagPoint scale)
    {
        int         n,vIdx,nVertex;
        
        nVertex=mesh.vertexes.length/3;
        
        for (n=0;n!=nVertex;n++) {
            vIdx=n*3;
            
            mesh.vertexes[vIdx]=((mesh.vertexes[vIdx]-centerPnt.x)*scale.x)+centerPnt.x;
            mesh.vertexes[vIdx+1]=((mesh.vertexes[vIdx+1]-centerPnt.y)*scale.y)+centerPnt.y;
            mesh.vertexes[vIdx+2]=((mesh.vertexes[vIdx+2]-centerPnt.z)*scale.z)+centerPnt.z;
        }
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
        aroundRadius=0.0f;
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
            
        shrinkWrapGlobe(mesh,boneList,centerPnt,aroundRadius);
        scaleVertexToBones(mesh,centerPnt,limb.scale);

        return(mesh);
    }

}
