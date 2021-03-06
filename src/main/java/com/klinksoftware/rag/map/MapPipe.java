package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapPipe
{
    private static final int PIPE_SIDE_COUNT=12;
    private static final int PIPE_CURVE_SEGMENT_COUNT=5;

    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapPipe(MeshList meshList,MapRoom room,String name)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
    }
    
    /*
        //
        // pieces of pipes
        //

    addPipeStraightChunk(pnt,len,radius,pipeAng)
    {
        let n,v,rd,tx,tz,tx2,tz2,bx,bz,bx2,bz2;
        let u1,u2,vfact;
        let ang,ang2,angAdd;
        let mesh,vertexList,indexes,iCount,vIdx;
        let nextPnt=new RagPoint(0,0,0);
        let addPnt=new RagPoint(0,0,0);
        
            // get turn pieces
        
        vertexList=MeshUtilityClass.createMapVertexList(PIPE_SIDE_COUNT*6);
        indexes=new Uint16Array(PIPE_SIDE_COUNT*6);

        iCount=PIPE_SIDE_COUNT*6;
        
        vIdx=0;
        
            // the end points
            
        nextPnt.setFromPoint(pnt);

        addPnt.setFromValues(0,len,0);
        addPnt.rotate(pipeAng);
        nextPnt.addPoint(addPnt);
        
            // the v factor
            
        vfact=len/radius;
        
            // cyliner faces

        ang=0.0;
        angAdd=360.0/PIPE_SIDE_COUNT;

        for (n=0;n!==PIPE_SIDE_COUNT;n++) {
            ang2=ang+angAdd;

                // the two Us

            u1=(ang*PIPE_SIDE_COUNT)/360.0;
            u2=(ang2*PIPE_SIDE_COUNT)/360.0;

                // force last segment to wrap

            if (n==(PIPE_SIDE_COUNT-1)) ang2=0.0;

            rd=ang*constants.DEGREE_TO_RAD;
            tx=nextPnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
            tz=nextPnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));

            bx=pnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
            bz=pnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));

            rd=ang2*constants.DEGREE_TO_RAD;
            tx2=nextPnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
            tz2=nextPnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));

            bx2=pnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
            bz2=pnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));

                // the points

            v=vertexList[vIdx++];
            v.position.setFromValues(tx,nextPnt.y,tz);
            v.position.rotateAroundPoint(nextPnt,pipeAng);
            v.normal.setFromSubPoint(v.position,nextPnt);
            v.normal.normalize();
            v.uv.setFromValues(u1,0.0);

            v=vertexList[vIdx++];
            v.position.setFromValues(tx2,nextPnt.y,tz2);
            v.position.rotateAroundPoint(nextPnt,pipeAng);
            v.normal.setFromSubPoint(v.position,nextPnt);
            v.normal.normalize();
            v.uv.setFromValues(u2,0.0);

            v=vertexList[vIdx++];
            v.position.setFromValues(bx,pnt.y,bz);
            v.position.rotateAroundPoint(pnt,pipeAng);
            v.normal.setFromSubPoint(v.position,pnt);
            v.normal.normalize();
            v.uv.setFromValues(u1,vfact);

            v=vertexList[vIdx++];
            v.position.setFromValues(tx2,nextPnt.y,tz2);
            v.position.rotateAroundPoint(nextPnt,pipeAng);
            v.normal.setFromSubPoint(v.position,nextPnt);
            v.normal.normalize();
            v.uv.setFromValues(u2,0.0);

            v=vertexList[vIdx++];
            v.position.setFromValues(bx2,pnt.y,bz2);
            v.position.rotateAroundPoint(pnt,pipeAng);
            v.normal.setFromSubPoint(v.position,pnt);
            v.normal.normalize();
            v.uv.setFromValues(u2,vfact);

            v=vertexList[vIdx++];
            v.position.setFromValues(bx,pnt.y,bz);
            v.position.rotateAroundPoint(pnt,pipeAng);
            v.normal.setFromSubPoint(v.position,pnt);
            v.normal.normalize();
            v.uv.setFromValues(u1,vfact);

            ang=ang2;
        }

        for (n=0;n!==iCount;n++) {
            indexes[n]=n;
        }
        
            // calcualte the tangents

        MeshUtilityClass.buildVertexListTangents(vertexList,indexes);

            // finally create the mesh
            // all cylinders are simple box collisions

        mesh=new MeshClass(this.view,this.pipeBitmap,vertexList,indexes,constants.MESH_FLAG_DECORATION);
        mesh.simpleCollisionGeometry=true;
        
        this.map.meshList.add(mesh);
    }

    addPipeCornerChunk(pnt,radius,xStart,zStart,xTurn,zTurn,yFlip)
    {
        let n,k,v,rd,tx,tz,tx2,tz2,bx,bz,bx2,bz2;
        let yAdd,xTurnAdd,zTurnAdd;
        let u1,u2;
        let ang,ang2,angAdd;
        let mesh,vertexList,indexes,iCount,vIdx,iIdx;
        let pipeAng=new RagPoint(xStart,0,zStart);
        let nextPipeAng=new RagPoint(0,0,0);
        let nextPnt=new RagPoint(0,0,0);
        let addPnt=new RagPoint(0,0,0);
        
            // get turn pieces
        
        vertexList=MeshUtilityClass.createMapVertexList(constants.PIPE_CURVE_SEGMENT_COUNT*(PIPE_SIDE_COUNT*6));
        indexes=new Uint16Array(constants.PIPE_CURVE_SEGMENT_COUNT*(PIPE_SIDE_COUNT*6));

        iCount=PIPE_SIDE_COUNT*6;
        
        vIdx=0;
        iIdx=0;
        
            // turn segments
            
        yAdd=Math.trunc((radius*2)/constants.PIPE_CURVE_SEGMENT_COUNT);
        if (yFlip) yAdd=-yAdd;
        
        xTurnAdd=xTurn/constants.PIPE_CURVE_SEGMENT_COUNT;
        zTurnAdd=zTurn/constants.PIPE_CURVE_SEGMENT_COUNT;
        
        angAdd=360.0/constants.PIPE_SIDE_COUNT;
        
        for (k=0;k!==constants.PIPE_CURVE_SEGMENT_COUNT;k++) {
            
            nextPnt.setFromPoint(pnt);
            
            addPnt.setFromValues(0,-yAdd,0);
            addPnt.rotate(pipeAng);
            nextPnt.addPoint(addPnt);
            
            nextPipeAng.setFromPoint(pipeAng);
            nextPipeAng.x+=xTurnAdd;
            nextPipeAng.z+=zTurnAdd;
            

                // cyliner faces

            ang=0.0;

            for (n=0;n!==constants.PIPE_SIDE_COUNT;n++) {
                ang2=ang+angAdd;
                
                    // the two Us
                    
                u1=(ang*constants.PIPE_SIDE_COUNT)/360.0;
                u2=(ang2*constants.PIPE_SIDE_COUNT)/360.0;

                    // force last segment to wrap
                    
                if (n==(constants.PIPE_SIDE_COUNT-1)) ang2=0.0;

                rd=ang*constants.DEGREE_TO_RAD;
                tx=nextPnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
                tz=nextPnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));
                
                bx=pnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
                bz=pnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));

                rd=ang2*constants.DEGREE_TO_RAD;
                tx2=nextPnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
                tz2=nextPnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));
                
                bx2=pnt.x+((radius*Math.sin(rd))+(radius*Math.cos(rd)));
                bz2=pnt.z+((radius*Math.cos(rd))-(radius*Math.sin(rd)));
                
                    // the points
                
                v=vertexList[vIdx++];
                v.position.setFromValues(tx,nextPnt.y,tz);
                v.position.rotateAroundPoint(nextPnt,nextPipeAng);
                v.normal.setFromSubPoint(v.position,nextPnt);
                v.normal.normalize();
                v.uv.setFromValues(u1,0.0);
                
                v=vertexList[vIdx++];
                v.position.setFromValues(tx2,nextPnt.y,tz2);
                v.position.rotateAroundPoint(nextPnt,nextPipeAng);
                v.normal.setFromSubPoint(v.position,nextPnt);
                v.normal.normalize();
                v.uv.setFromValues(u2,0.0);
                
                v=vertexList[vIdx++];
                v.position.setFromValues(bx,pnt.y,bz);
                v.position.rotateAroundPoint(pnt,pipeAng);
                v.normal.setFromSubPoint(v.position,pnt);
                v.normal.normalize();
                v.uv.setFromValues(u1,1.0);
                
                v=vertexList[vIdx++];
                v.position.setFromValues(tx2,nextPnt.y,tz2);
                v.position.rotateAroundPoint(nextPnt,nextPipeAng);
                v.normal.setFromSubPoint(v.position,nextPnt);
                v.normal.normalize();
                v.uv.setFromValues(u2,0.0);
                
                v=vertexList[vIdx++];
                v.position.setFromValues(bx2,pnt.y,bz2);
                v.position.rotateAroundPoint(pnt,pipeAng);
                v.normal.setFromSubPoint(v.position,pnt);
                v.normal.normalize();
                v.uv.setFromValues(u2,1.0);
                
                v=vertexList[vIdx++];
                v.position.setFromValues(bx,pnt.y,bz);
                v.position.rotateAroundPoint(pnt,pipeAng);
                v.normal.setFromSubPoint(v.position,pnt);
                v.normal.normalize();
                v.uv.setFromValues(u1,1.0);
                
                ang=ang2;
            }

            for (n=0;n!==iCount;n++) {
                indexes[iIdx+n]=iIdx+n;
            }
            
            iIdx+=iCount;
            
            pnt.setFromPoint(nextPnt);
            pipeAng.setFromPoint(nextPipeAng);
        }
        
            // calcualte the tangents

        MeshUtilityClass.buildVertexListTangents(vertexList,indexes);

            // finally create the mesh
            // all cylinders are simple box collisions

        mesh=new MeshClass(this.view,this.pipeBitmap,vertexList,indexes,constants.MESH_FLAG_DECORATION);
        mesh.simpleCollisionGeometry=true;
        
        this.map.meshList.add(mesh);
    }
    
        //
        // pipe types
        //
    
    addPipeSide(room,dir,pnt,radius,dirLen,yBound)
    {
        let len,pipeAng;
        
            // pipes always start up
            // length of up has to be a multiple of story size
            
        pipeAng=new RagPoint(0,0,180.0);     // force len to point up
        
        len=genRandom.randomInt(1,(room.storyCount-1));
        len=(len*(constants.ROOM_FLOOR_HEIGHT+floorDepth))-((radius*2)+floorDepth);
        
        this.addPipeStraightChunk(pnt,len,radius,pipeAng);
        
        pnt.y-=len;
        
            // the turn and exit
            
        switch (dir) {
            
            case constants.ROOM_SIDE_LEFT:  
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,0.0,-90.0,false);

                pipeAng.setFromValues(0.0,0.0,90.0);
                this.addPipeStraightChunk(pnt,dirLen,radius,pipeAng);
                
                pnt.x-=dirLen;
                this.addPipeCornerChunk(pnt,radius,0.0,-90.0,0.0,90.0,false);
                break;
                
            case constants.ROOM_SIDE_RIGHT:
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,0.0,90.0,false);

                pipeAng.setFromValues(0.0,0.0,-90.0);
                this.addPipeStraightChunk(pnt,dirLen,radius,pipeAng);
                
                pnt.x+=dirLen;
                this.addPipeCornerChunk(pnt,radius,0.0,90.0,0.0,-90.0,false);
                break;
                
            case constants.ROOM_SIDE_TOP:
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,90.0,0.0,false);

                pipeAng.setFromValues(-90.0,0.0,0.0);
                this.addPipeStraightChunk(pnt,dirLen,radius,pipeAng);
                
                pnt.z-=dirLen;
                this.addPipeCornerChunk(pnt,radius,90.0,0.0,-90.0,0.0,false);
                break;
                
            case constants.ROOM_SIDE_BOTTOM:
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,-90.0,0.0,false);

                pipeAng.setFromValues(90.0,0.0,0.0);
                this.addPipeStraightChunk(pnt,dirLen,radius,pipeAng);
                
                pnt.z+=dirLen;
                this.addPipeCornerChunk(pnt,radius,-90.0,0.0,90.0,0.0,false);
                break;
        }
        
            // final up section of pipe
        
        len=(pnt.y-yBound.min)+floorDepth;
        
        if (len>0) {
            pipeAng=new RagPoint(0,0,180.0);     // force len to point up
            this.addPipeStraightChunk(pnt,len,radius,pipeAng);
        }
    }
    
    addPipeUp(room,pnt,radius,yBound)
    {
        let pipeAng;
        
        pipeAng=new RagPoint(0,0,180.0);     // force len to point up
        this.addPipeStraightChunk(pnt,(pnt.y-yBound.min),radius,pipeAng);
    }
        
    addPipeDown(room,x,z,pnt,radius,yBound)
    {
        let pipeAng=new RagPoint(0,0,0);
        
        if (x==0) {
            
                // to left
                
            if (z==0) {
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,0.0,-90.0,false);
                
                pipeAng.setFromValues(0.0,0.0,90.0);
                this.addPipeStraightChunk(pnt,radius,radius,pipeAng);
                
                pnt.x-=radius;
                this.addPipeCornerChunk(pnt,radius,0.0,90.0,0.0,-90.0,true);
            }
            
                // to bottom
                
            else {
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,-90.0,0.0,false);
                
                pipeAng.setFromValues(90.0,0.0,0.0);
                this.addPipeStraightChunk(pnt,radius,radius,pipeAng);
                
                pnt.z+=radius;
                this.addPipeCornerChunk(pnt,radius,90.0,0.0,-90.0,0.0,true);
            }
        }
        else {
            
                // to top
                
            if (z==0) {
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,90.0,0.0,false);
                
                pipeAng.setFromValues(-90.0,0.0,0.0);
                this.addPipeStraightChunk(pnt,radius,radius,pipeAng);
                
                pnt.z-=radius;
                this.addPipeCornerChunk(pnt,radius,-90.0,0.0,90.0,0.0,true);
            }
            
                // to right
                
            else {
                this.addPipeCornerChunk(pnt,radius,0.0,0.0,0.0,90.0,false);
                
                pipeAng.setFromValues(0.0,0.0,-90.0);
                this.addPipeStraightChunk(pnt,radius,radius,pipeAng);
                
                pnt.x+=radius;
                this.addPipeCornerChunk(pnt,radius,0.0,-90.0,0.0,90.0,true);
            }
        }
        
            // finally down to ground
            
        pipeAng.setFromValues(0.0,0.0,0.0);
        this.addPipeStraightChunk(pnt,(yBound.max-pnt.y),radius,pipeAng);
    }
        
        //
        // pipes
        //
        
    addPipeSet(room,x,z)
    {
        let px,pz,sx,sz,yBound,platformBoundX,platformBoundY,platformBoundZ;
        let gridSize,radius;
        let pnt,dir,dirLen;
        
            // get # of pipes (on a grid so they can collide
            // properly) and their relative sizes
            
        gridSize=Math.trunc(MapIndoorBuilder.SEGMENT_SIZE/2);
        radius=Math.trunc(gridSize*0.3);
        
            // the pipe platform
        
        yBound=room.getGroundFloorSpawnToFirstPlatformOrTopBoundByCoordinate(x,z);
        
        x=room.xBound.min+(x*MapIndoorBuilder.SEGMENT_SIZE);
        z=room.zBound.min+(z*MapIndoorBuilder.SEGMENT_SIZE);
        
        platformBoundX=new BoundClass(x,(x+MapIndoorBuilder.SEGMENT_SIZE));
        platformBoundZ=new BoundClass(z,(z+MapIndoorBuilder.SEGMENT_SIZE));
        
        platformBoundY=new BoundClass((yBound.max-floorDepth),room.yBound.max);
        this.map.meshList.add(MeshUtility.createCube(room,"","platform",platformBoundX,platformBoundY,platformBoundZ,true,true,true,true,true,false,false,MeshUtility.UV_MAP,MapIndoorBuilder.SEGMENT_SIZE));
        
            // determine direction
        
        dir=room.getDirectionTowardsNearestWall(x,z);
        
        dirLen=dir.len-Math.trunc((MapIndoorBuilder.SEGMENT_SIZE*0.5)+(radius*2));
        if (dirLen<0) dirLen=100;
        
            // create the pipes
            
        sx=x+Math.trunc(gridSize*0.5);
        sz=z+Math.trunc(gridSize*0.5);
        
        pnt=new RagPoint(0,0,0);

        for (pz=0;pz!==2;pz++) {
            for (px=0;px!==2;px++) {
                pnt.x=sx+(px*gridSize);
                pnt.y=yBound.max-floorDepth;
                pnt.z=sz+(pz*gridSize);
                
                switch (genRandom.randomIndex(4)) {
                    case 0:
                        this.addPipeSide(room,dir.direction,pnt,radius,dirLen,yBound);
                        break;
                    case 1:
                        this.addPipeUp(room,pnt,radius,yBound);
                        break;
                    case 2:
                        this.addPipeDown(room,px,pz,pnt,radius,yBound);
                        break;
                }
            }
        }
    }
*/
    
    public void build()
    {
        int     x,z,lx,rx,tz,bz,skipX,skipZ,
                pieceCount;
        
            // bounds with margins
            
        lx=room.piece.margins[0];
        rx=room.piece.size.x-(room.piece.margins[2]);
        if (!room.requiredStairs.isEmpty()) {
            if (lx<3) lx=3;
            if (rx>(room.piece.size.x-3)) rx=room.piece.size.x-3;
        }
        if (rx<=lx) return;
        
        tz=this.room.piece.margins[1];
        bz=this.room.piece.size.z-(room.piece.margins[3]);
        if (!room.requiredStairs.isEmpty()) {
            if (tz<3) tz=3;
            if (bz>(room.piece.size.z-3)) bz=room.piece.size.z-3;
        }
        if (bz<=tz) return;
        
            // sizes
            
        
            // if enough room, make a path
            // through the equipment
        
        skipX=-1;
        if ((rx-lx)>2) skipX=(lx+1)+GeneratorMain.random.nextInt((rx-lx)-2);
        skipZ=-1;
        if ((bz-tz)>2) skipZ=(tz+1)+GeneratorMain.random.nextInt((bz-tz)-2);
        
            // the pieces
          
        pieceCount=0;
        
        for (z=tz;z<bz;z++) {
            if (z==skipZ) continue;
            
            for (x=lx;x<rx;x++) {
                if (x==skipX) continue;
                
                if (GeneratorMain.random.nextBoolean()) {
                    //addPipeSet(room,x,z);
                }
                
                pieceCount++;
                
                this.room.setGrid(0,x,z,1);
            }
        }
    }
}
