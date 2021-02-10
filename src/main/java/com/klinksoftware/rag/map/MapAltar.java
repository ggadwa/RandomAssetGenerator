package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapAltar
{
    private float           segmentSize;
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapAltar(MeshList meshList,MapRoom room,String name,float segmentSize)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
        this.segmentSize=segmentSize;
    }
    
        //
        // single altar
        //

    public Mesh addAltar(int lx,int rx,int tz,int bz,float stepHigh)
    {
        int         n,x,z,dx,dz,levelCount;
        float       y,xMin,xMax,zMin,zMax;
        Mesh        mesh,mesh2;
        
        levelCount=5+GeneratorMain.random.nextInt(5);
        
        mesh=null;
        y=this.room.offset.y;
        
        for (n=0;n!=levelCount;n++) {
            xMin=room.offset.x+(lx*segmentSize);
            xMax=room.offset.x+(rx*segmentSize);
            zMin=room.offset.z+(tz*segmentSize);
            zMax=room.offset.z+(bz*segmentSize);

            mesh2=MeshUtility.createCube(room,(this.name+"_altar"),"platform",xMin,xMax,y,(y+stepHigh),zMin,zMax,true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize);
            
            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }
            
            if (n==0) {
                for (z=tz;z<bz;z++) {
                    for (x=lx;x<rx;x++) {
                        room.setGrid(0,x,z,1);
                    }
                }
            }
            
            y+=stepHigh;
            
            dx=Math.abs(lx-rx);
            dz=Math.abs(tz-bz);
            if ((dx<=1) || (dz<=1)) break;
            
            if (dx>dz) {
                if (GeneratorMain.random.nextBoolean()) {
                    lx++;
                }
                else {
                    rx--;
                }
            }
            else {
                if (GeneratorMain.random.nextBoolean()) {
                    tz++;
                }
                else {
                    bz--;
                }
            }
        }
        
        return(mesh);
    }
            
        //
        // alter
        //

    public void build()
    {
        int         mx,mz;
        float       stepHigh;
        Mesh        mesh;
        
        stepHigh=segmentSize*0.1f;
        
            // rooms with 10x10 can get half or quarter versions
            
        if ((room.piece.size.x>=10) && (room.piece.size.z>=10)) {
            
            mx=room.piece.size.x/2;
            mz=room.piece.size.z/2;
            
            switch (GeneratorMain.random.nextInt(3)) {
                case 0:
                    mesh=addAltar(2,(room.piece.size.x-2),2,(room.piece.size.z-2),stepHigh);
                    break;
                case 1:
                    mesh=addAltar(2,mx,2,(room.piece.size.z-2),stepHigh);
                    mesh.combine(addAltar(mx,(room.piece.size.x-2),2,(room.piece.size.z-2),stepHigh));
                    break;
                default:
                    mesh=addAltar(2,mx,2,mz,stepHigh);
                    mesh.combine(addAltar(2,mx,mz,(room.piece.size.z-2),stepHigh));
                    mesh.combine(addAltar(mx,(room.piece.size.x-2),2,mz,stepHigh));
                    mesh.combine(addAltar(mx,(room.piece.size.x-2),mz,(room.piece.size.z-2),stepHigh));
                    break;
            }
        }
        else {
            mesh=addAltar(2,(room.piece.size.x-2),2,(room.piece.size.z-2),stepHigh);
        }
        
        meshList.add(mesh);
    }

}
