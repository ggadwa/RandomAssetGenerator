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
        // build a large global around
        // center point
        //

    private static Mesh buildGlobeAroundSkeletonX(String name, String bitmapName, int acrossSurfaceCount, int aroundSurfaceCount, RagPoint centerPnt, float acrossRadius, float aroundRadius) {
        int                 x,yz,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,px,
                            vAng,xAng,yzAng,yzAngAdd,xAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,tangents,uvs;

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
            for (yz = 0; yz != aroundSurfaceCount; yz++) {
                indexArray.add(yz);
                indexArray.add(minIdx);
                indexArray.add(yz + 1);
        }

            // max end point
            maxOff = (aroundSurfaceCount + 1) * (acrossSurfaceCount - 3);

            for (yz = 0; yz != aroundSurfaceCount; yz++) {
                indexArray.add(maxOff + (yz + 1));
                indexArray.add(maxIdx);
                indexArray.add(maxOff + yz);
        }

            // create the mesh

        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        return(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes));
    }

    private static Mesh buildGlobeAroundSkeletonY(String name, String bitmapName, int acrossSurfaceCount, int aroundSurfaceCount, RagPoint centerPnt, float acrossRadius, float aroundRadius) {
        int                 xz,y,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,py,
                            vAng,xzAng,yAng,xzAngAdd,yAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,tangents,uvs;

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
            for (xz = 0; xz != aroundSurfaceCount; xz++) {
                indexArray.add(xz);
                indexArray.add(minIdx);
                indexArray.add(xz + 1);
            }


            // max end point
            maxOff = (aroundSurfaceCount + 1) * (acrossSurfaceCount - 3);

            for (xz = 0; xz != aroundSurfaceCount; xz++) {
                indexArray.add(maxOff + (xz + 1));
                indexArray.add(maxIdx);
                indexArray.add(maxOff + xz);
        }

            // create the mesh

        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        return(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes));
    }

    private static Mesh buildGlobeAroundSkeletonZ(String name, String bitmapName, int acrossSurfaceCount, int aroundSurfaceCount, RagPoint centerPnt, float acrossRadius, float aroundRadius) {
        int                 xy,z,
                            vIdx,v2Idx,vNextIdx,v2NextIdx,
                            minIdx,maxIdx,maxOff;
        float               rd,radius,pz,
                            vAng,xyAng,zAng,xyAngAdd,zAngAdd;
        ArrayList<Float>    vertexArray,uvArray;
        ArrayList<Integer>  indexArray;
        int[]               indexes;
        float[]             vertexes,normals,tangents,uvs;

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
            for (xy = 0; xy != aroundSurfaceCount; xy++) {
                indexArray.add(xy);
                indexArray.add(minIdx);
                indexArray.add(xy + 1);
            }


            // max end point
            maxOff = (aroundSurfaceCount + 1) * (acrossSurfaceCount - 3);

            for (xy = 0; xy != aroundSurfaceCount; xy++) {
                indexArray.add(maxOff + (xy + 1));
                indexArray.add(maxIdx);
                indexArray.add(maxOff + xy);
            }


            // create the mesh

        vertexes=floatArrayListToFloat(vertexArray);
        uvs=floatArrayListToFloat(uvArray);
        indexes=intArrayListToInt(indexArray);
        normals=MeshMapUtility.buildNormals(vertexes,indexes,centerPnt,false);
        tangents=MeshMapUtility.buildTangents(vertexes,uvs,indexes);

        return(new Mesh(name,bitmapName,vertexes,normals,tangents,uvs,indexes));
    }

    public static Mesh buildCylinderAroundLimbY(String name, String bitmapName, RagPoint centerPnt, float ty, float topRadius, float by, float botRadius, int acrossSurfaceCount, int aroundSurfaceCount) {
        int n, k, t, iIdx, vStartIdx;
        float ang, ang2, angAdd, segTy, segBy, yAdd;
        float u1, u2, v1, v2, rd;
        float tx, tz, tx2, tz2, bx, bz, bx2, bz2;
        float rAdd, botRad, topRad;
        ArrayList<Float> vertexArray, normalArray, uvArray;
        ArrayList<Integer> indexArray;
        int[] indexes;
        float[] vertexes, normals, tangents, uvs;
        RagPoint normal;

        // allocate arrays
        vertexArray = new ArrayList<>();
        normalArray = new ArrayList<>();
        uvArray = new ArrayList<>();
        indexArray = new ArrayList<>();

        normal = new RagPoint(0.0f, 0.0f, 0.0f);

        // make the cylinder
        iIdx = 0;

        angAdd = 360.0f / (float) aroundSurfaceCount;
        yAdd = (ty - by) / (float) acrossSurfaceCount;
        rAdd = (topRadius - botRadius) / (float) acrossSurfaceCount;

        segBy = by;
        segTy = by + yAdd;

        botRad = botRadius;
        topRad = botRad + rAdd;

        for (k = 0; k != acrossSurfaceCount; k++) {

            v1 = (float) k / (float) acrossSurfaceCount;
            v2 = (k == (acrossSurfaceCount - 1)) ? 1.0f : ((float) (k + 1) / (float) acrossSurfaceCount);

            // cyliner faces
            ang = 0.0f;

            for (n = 0; n != aroundSurfaceCount; n++) {
                ang2 = ang + angAdd;

                // the two Us
                u1 = ang / 360.0f;
                u2 = ang2 / 360.0f;

                // force last segment to wrap
                if (n == (aroundSurfaceCount - 1)) {
                    ang2 = 0.0f;
                }

                rd = ang * ((float) Math.PI / 180.0f);
                tx = centerPnt.x + ((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd)));
                tz = centerPnt.z + ((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd)));

                bx = centerPnt.x + ((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd)));
                bz = centerPnt.z + ((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd)));

                rd = ang2 * ((float) Math.PI / 180.0f);
                tx2 = centerPnt.x + ((topRad * (float) Math.sin(rd)) + (topRad * (float) Math.cos(rd)));
                tz2 = centerPnt.z + ((topRad * (float) Math.cos(rd)) - (topRad * (float) Math.sin(rd)));

                bx2 = centerPnt.x + ((botRad * (float) Math.sin(rd)) + (botRad * (float) Math.cos(rd)));
                bz2 = centerPnt.z + ((botRad * (float) Math.cos(rd)) - (botRad * (float) Math.sin(rd)));

                // the points
                vStartIdx = vertexArray.size();

                vertexArray.addAll(Arrays.asList(tx, segTy, tz));
                uvArray.addAll(Arrays.asList(u1, 0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2, segTy, tz2));
                uvArray.addAll(Arrays.asList(u2, 0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx, segBy, bz));
                uvArray.addAll(Arrays.asList(u1, 1.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(tx2, segTy, tz2));
                uvArray.addAll(Arrays.asList(u2, 0.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx2, segBy, bz2));
                uvArray.addAll(Arrays.asList(u2, 1.0f));
                indexArray.add(iIdx++);

                vertexArray.addAll(Arrays.asList(bx, segBy, bz));
                uvArray.addAll(Arrays.asList(u1, 1.0f));
                indexArray.add(iIdx++);

                // the normals
                for (t = 0; t != 6; t++) {
                    normal.x = vertexArray.get(vStartIdx) - centerPnt.x;
                    normal.y = 0.0f;
                    normal.z = vertexArray.get(vStartIdx + 2) - centerPnt.z;
                    normal.normalize();
                    normalArray.addAll(Arrays.asList(normal.x, normal.y, normal.z));

                    vStartIdx += 3;
                }

                ang = ang2;
            }

            botRad = topRad;
            topRad = botRad + rAdd;

            segBy = segTy;
            segTy = segBy + yAdd;
        }

        // create the mesh
        vertexes = floatArrayListToFloat(vertexArray);
        normals = floatArrayListToFloat(normalArray);
        uvs = floatArrayListToFloat(uvArray);
        indexes = intArrayListToInt(indexArray);
        tangents = MeshMapUtility.buildTangents(vertexes, uvs, indexes);

        return (new Mesh(name, bitmapName, vertexes, normals, tangents, uvs, indexes));
    }

        //
        // shrink wrap the globe around a
        // collection of points
        //

    private static void shrinkWrapGlobe(Mesh mesh, Bone bone1, Bone bone2, int axis, RagPoint centerPnt, float gravityMaxDistance)    {
        int n, vIdx, nVertex, moveCount;
        float               dist,shrinkDist;
        boolean             anyMove,boneHit;
        boolean[]           moving;
        RagPoint pnt, moveVector, gravityVector;

            // move distance for shrinking bones

        shrinkDist=gravityMaxDistance*0.001f;

            // keep a parallel list of
            // what bones are moving (bones
            // stop when they get within the
            // gravity min distance of all gravity bones)

        nVertex = mesh.vertexes.length / 3;

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

                // if too close, then all movement stops
                dist = bone1.pnt.distance(pnt);
                if (dist > bone1.gravityLockDistance) {

                    // outside of max gravity well

                    if (dist < gravityMaxDistance) {

                        // otherwise add in gravity

                        gravityVector.setFromSubPoint(bone1.pnt, pnt);
                        gravityVector.normalize();
                        gravityVector.scale((1.0f - (dist / gravityMaxDistance)) * shrinkDist);

                        moveVector.addPoint(gravityVector);

                        boneHit = true;
                    }
                }

                dist = bone2.pnt.distance(pnt);
                if (dist > bone2.gravityLockDistance) {

                    // outside of max gravity well
                    if (dist < gravityMaxDistance) {

                        // otherwise add in gravity
                        gravityVector.setFromSubPoint(bone2.pnt, pnt);
                        gravityVector.normalize();
                        gravityVector.scale((1.0f - (dist / gravityMaxDistance)) * shrinkDist);

                        moveVector.addPoint(gravityVector);

                        boneHit = true;
                    }
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

                if ((axis==Limb.LIMB_AXIS_Y) || (axis==Limb.LIMB_AXIS_Z)) mesh.vertexes[vIdx]+=moveVector.x;
                if ((axis==Limb.LIMB_AXIS_X) || (axis==Limb.LIMB_AXIS_Z)) mesh.vertexes[vIdx+1]+=moveVector.y;
                if ((axis==Limb.LIMB_AXIS_X) || (axis==Limb.LIMB_AXIS_Y)) mesh.vertexes[vIdx+2]+=moveVector.z;

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
        // no vertexes can extend past floor, everything
        // there is flat
        //

    private static void clipFloorVertexes(Mesh mesh)
    {
        int         n,vIdx,nVertex;

        nVertex=mesh.vertexes.length/3;

        for (n=0;n!=nVertex;n++) {
            vIdx=n*3;
            if (mesh.vertexes[vIdx+1]<0) mesh.vertexes[vIdx+1]=0.0f;
        }
    }

        //
        // rebuild the normal by finding the nearest bone
        // and tracing the normal from there
        //

    private static void rebuildNormals(Mesh mesh, Bone bone1, Bone bone2)    {
        int n, vIdx, nVertex;
        float d, d2;
        RagPoint    pnt;

        nVertex=mesh.vertexes.length/3;

        pnt=new RagPoint(0.0f,0.0f,0.0f);

        for (n=0;n!=nVertex;n++) {
            vIdx=n*3;

            pnt.x=mesh.vertexes[vIdx];
            pnt.y=mesh.vertexes[vIdx+1];
            pnt.z=mesh.vertexes[vIdx+2];

                // find closest bone
            d = bone1.pnt.distance(pnt);
            d2 = bone2.pnt.distance(pnt);

                // get normal

            pnt.subPoint((d < d2) ? bone1.pnt : bone2.pnt);
            pnt.normalize();

            mesh.normals[vIdx]=pnt.x;
            mesh.normals[vIdx+1]=pnt.y;
            mesh.normals[vIdx+2]=pnt.z;
        }
    }

        //
        // build mesh around limb
        //

    public static Mesh buildMeshAroundBoneLimb(Skeleton skeleton,Limb limb)
    {
        int n, k, boneCount;
        float f, gravityLockDistance, avgGravity, acrossRadius, aroundRadius;
        RagPoint centerPnt;
        RagBound xBound, yBound, zBound;
        Bone bone1, bone2;
        Mesh mesh;

        bone1 = skeleton.bones.get(limb.bone1Idx);
        bone2 = skeleton.bones.get(limb.bone2Idx);

        // find the bounds for these bones

        xBound = new RagBound(bone1.pnt.x, bone2.pnt.x);
        yBound = new RagBound(bone1.pnt.y, bone2.pnt.y);
        zBound = new RagBound(bone1.pnt.z, bone2.pnt.z);

        centerPnt=new RagPoint(xBound.getMidPoint(),yBound.getMidPoint(),zBound.getMidPoint());

            // build the globe around the bones

        mesh=null;
        aroundRadius=0.0f;
        avgGravity = (bone1.gravityLockDistance + bone2.gravityLockDistance) * 0.5f;

        switch (limb.axis) {
            case Limb.LIMB_AXIS_X:
                acrossRadius = (xBound.getSize() * 0.5f) + avgGravity;
                aroundRadius = (yBound.getSize() > zBound.getSize()) ? ((yBound.getSize() * 0.5f) + avgGravity) : ((zBound.getSize() * 0.5f) + avgGravity);
                mesh = buildGlobeAroundSkeletonX(limb.name, limb.bitmapName, limb.acrossSurfaceCount, limb.aroundSurfaceCount, centerPnt, acrossRadius, aroundRadius);
                break;
            case Limb.LIMB_AXIS_Y:
                acrossRadius = (yBound.getSize() * 0.5f) + avgGravity;
                aroundRadius = (xBound.getSize() > zBound.getSize()) ? ((xBound.getSize() * 0.5f) + avgGravity) : ((zBound.getSize() * 0.5f) + avgGravity);
                mesh = buildCylinderAroundLimbY(limb.name, limb.bitmapName, centerPnt, bone1.pnt.y, bone1.gravityLockDistance, bone2.pnt.y, bone2.gravityLockDistance, limb.acrossSurfaceCount, limb.aroundSurfaceCount);

                acrossRadius = (yBound.getSize() * 0.5f) + avgGravity;
                aroundRadius = (xBound.getSize() > zBound.getSize()) ? ((xBound.getSize() * 0.5f) + avgGravity) : ((zBound.getSize() * 0.5f) + avgGravity);
                // mesh = buildGlobeAroundSkeletonY(limb.name, limb.bitmapName, limb.acrossSurfaceCount, limb.aroundSurfaceCount, centerPnt, acrossRadius, aroundRadius);
                break;
            case Limb.LIMB_AXIS_Z:
                acrossRadius = (zBound.getSize() * 0.5f) + avgGravity;
                aroundRadius = (xBound.getSize() > yBound.getSize()) ? ((xBound.getSize() * 0.5f) + avgGravity) : ((yBound.getSize() * 0.5f) + avgGravity);
                mesh = buildGlobeAroundSkeletonZ(limb.name, limb.bitmapName, limb.acrossSurfaceCount, limb.aroundSurfaceCount, centerPnt, acrossRadius, aroundRadius);
                break;
        }

            // shrink wrap the globe and rebuild
            // any normals, etc

        //shrinkWrapGlobe(mesh, skeleton.bones.get(limb.bone1Idx), skeleton.bones.get(limb.bone2Idx), limb.axis, centerPnt, aroundRadius);
        //scaleVertexToBones(mesh,centerPnt,limb.scale);
        clipFloorVertexes(mesh);
        rebuildNormals(mesh, bone1, bone2);

        return(mesh);
    }

}
