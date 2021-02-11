package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapEquipment
{
    private float           segmentSize;
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapEquipment(MeshList meshList,MapRoom room,String name,float segmentSize)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
        this.segmentSize=segmentSize;
    }
    
        //
        // platform
        //
        
    private void addPedestal(MapRoom room,int gx,int gz,float floorDepth)
    {
        float       x,z;
        
        x=room.offset.x+((float)gx*segmentSize);
        z=room.offset.z+((float)gz*segmentSize);
            
        meshList.add(MeshUtility.createCube(room,(name+"_pedestal"),"accessory",x,(x+segmentSize),room.offset.y,(room.offset.y+floorDepth),z,(z+segmentSize),true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize));
    }
        
        //
        // computer banks
        //
        
    private void addBank(MapRoom room,int gx,int gz,float wid,float high,float floorDepth,int pieceCount)
    {
        float       x,y,z,widOffset;
        
            // bank platform
            
        addPedestal(room,gx,gz,floorDepth);
        
            // computer bank
        
        widOffset=(segmentSize-wid)*0.5f;
        
        x=(room.offset.x+((float)gx*segmentSize))+widOffset;
        y=room.offset.y+floorDepth;
        z=(room.offset.z+((float)gz*segmentSize))+widOffset;

        meshList.add(MeshUtility.createCube(room,(name+"_computer_"+pieceCount),"computer",x,(x+wid),y,(y+high),z,(z+wid),true,true,true,true,true,false,false,MeshUtility.UV_BOX,segmentSize));
    }
   
        //
        // terminals
        //
        
    public void addTerminal(MapRoom room,int gx,int gz,float wid,float high,int pieceCount)
    {
        float       x,y,z,widOffset,
                    deskHalfWid,deskShortHalfWid,
                    standWid,standHalfWid,standHigh;
        RagPoint    rotAngle;
        Mesh        mesh;
        
            // the desk and stand
            
        widOffset=(segmentSize-wid)*0.5f;
        
        x=(room.offset.x+((float)gx*segmentSize))+(segmentSize*0.5f);
        y=room.offset.y;
        z=(room.offset.z+((float)gz*segmentSize))+(segmentSize*0.5f);
        
        deskHalfWid=wid*0.5f;
        deskShortHalfWid=deskHalfWid*0.9f;
        
        standWid=wid*0.05f;
        standHalfWid=standWid*0.5f;
        standHigh=high*0.1f;
        
        rotAngle=new RagPoint(0.0f,(GeneratorMain.random.nextBoolean()?0.0f:90.0f),0.0f);
        mesh=MeshUtility.createCube(room,(name+"_monitor_box_"+pieceCount),"accessory",(x-deskHalfWid),(x+deskHalfWid),y,(y+high),(z-deskShortHalfWid),(z+deskShortHalfWid),true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize);
        
        y+=high;

        rotAngle.setFromValues(0.0f,(GeneratorMain.random.nextFloat()*360.0f),0.0f);
        mesh.combine(MeshUtility.createCubeRotated(room,(name+"_monitor_stand_"+pieceCount),"accessory",(x-standHalfWid),(x+standHalfWid),y,(y+standHigh),(z-standHalfWid),(z+standHalfWid),rotAngle,true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize));
        
        meshList.add(mesh);
        
            // the monitor
            
        x=(room.offset.x+((float)gx*segmentSize))+widOffset;
        y+=standHigh;
        
        meshList.add(MeshUtility.createCubeRotated(room,(name+"_monitor_"+pieceCount),"monitor",x,(x+wid),y,(y+high),(z-standHalfWid),(z+standHalfWid),rotAngle,true,true,true,true,true,true,false,MeshUtility.UV_BOX,segmentSize));
    }
    
        //
        // junctions
        //
       
    public void addJunction(MapRoom room,int gx,int gz,float juncWid,float pipeHigh,float pipeRadius,int pieceCount)
    {
        boolean         upperPipe,lowerPipe;
        float           x,y,z,juncHalfWid;
        RagPoint        rotAngle,centerPnt;
        Mesh            mesh,mesh2;
        
        x=(room.offset.x+((float)gx*segmentSize))+(segmentSize*0.5f);
        y=room.offset.y;
        z=(room.offset.z+((float)gz*segmentSize))+(segmentSize*0.5f);
        
            // the junction
            
        juncHalfWid=juncWid*0.5f;
        
        rotAngle=new RagPoint(0.0f,(GeneratorMain.random.nextBoolean()?0.0f:90.0f),0.0f);
        meshList.add(MeshUtility.createCubeRotated(room,(name+"_panel_"+pieceCount),"panel",(x-juncHalfWid),(x+juncHalfWid),(y+pipeHigh),((y+pipeHigh)+juncWid),(z-pipeRadius),(z+pipeRadius),rotAngle,true,true,true,true,true,true,false,MeshUtility.UV_BOX,segmentSize));
        
            // the pipes
            
        upperPipe=GeneratorMain.random.nextBoolean();
        lowerPipe=((GeneratorMain.random.nextBoolean())||(!upperPipe));
        
        mesh=null;
        centerPnt=new RagPoint(x,y,z);
        
        if (upperPipe) {
            mesh=MeshUtility.createMeshCylinderSimple(room,(name+"_panel_pipe_"+pieceCount),"pipe",centerPnt,((y+pipeHigh)+juncWid),(room.offset.y+((float)room.storyCount*segmentSize)),pipeRadius,false,false);
        }
        if (lowerPipe) {
            mesh2=MeshUtility.createMeshCylinderSimple(room,(name+"_panel_pipe_"+pieceCount),"pipe",centerPnt,y,(y+pipeHigh),pipeRadius,false,false);
            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }
        }
        
        meshList.add(mesh);
    }
    
        //
        // lab tubes
        //

    private void addTubeInternal(RagPoint centerPnt,String tubeName,float tubeRadius,float tubeHigh,float tubeCapRadius,float tubeTopCapHigh,float tubeBotCapHigh)
    {
        float       yBotCapBy,yBotCapTy,yTopCapBy,yTopCapTy,y;
        Mesh        mesh,mesh2;
        
            // the top and bottom caps
            
        yBotCapBy=centerPnt.y;
        yBotCapTy=yBotCapBy+tubeBotCapHigh;
        
        yTopCapBy=yBotCapTy+tubeHigh;
        yTopCapTy=yTopCapBy+tubeTopCapHigh;
        
        mesh=null;
        
        if (tubeBotCapHigh!=0.0f) {
            mesh=MeshUtility.createMeshCylinderSimple(room,(name+"_top"),"accessory",centerPnt,yBotCapTy,yBotCapBy,tubeCapRadius,true,false);
        }
        
        if (tubeTopCapHigh!=0.0f) {
            mesh2=MeshUtility.createMeshCylinderSimple(room,(name+"_top"),"accessory",centerPnt,yTopCapTy,yTopCapBy,tubeCapRadius,true,true);
            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }
        }
        
        meshList.add(mesh);

            // the tube
        
        meshList.add(MeshUtility.createMeshCylinderSimple(room,(name+"_glass"),"glass",centerPnt,yTopCapBy,yBotCapTy,tubeRadius,false,false));
        
            // the liquid in the tube
        
        y=yBotCapTy+(GeneratorMain.random.nextFloat()*(yTopCapBy-yBotCapTy));    
        meshList.add(MeshUtility.createMeshCylinderSimple(room,(name+"_liquid"),"liquid",centerPnt,y,yBotCapTy,(tubeRadius*0.98f),true,false));
    }
    
    public void addTube(MapRoom room,int gx,int gz,float tubeRadius,float tubeHigh,float tubeCapRadius,float tubeTopCapHigh,float tubeBotCapHigh,int pieceCount)
    {
        float           x,z,radius;
        RagPoint        centerPnt;
        
        x=(room.offset.x+((float)gx*segmentSize))+(segmentSize*0.5f);
        z=(room.offset.z+((float)gz*segmentSize))+(segmentSize*0.5f);

        centerPnt=new RagPoint(x,room.offset.y,z);
        
        addTubeInternal(centerPnt,(name+"_tube_"+pieceCount),tubeRadius,tubeHigh,tubeCapRadius,tubeTopCapHigh,tubeBotCapHigh);
    }
   
        //
        // equipment build
        //
    
    public void build()
    {
        int     x,z,lx,rx,tz,bz,skipX,skipZ,
                pieceType,pieceCount;
        float   bankWid,bankHigh,
                terminalWid,terminalHigh,
                juncWid,pipeHigh,pipeRadius,
                roomHigh,tubeRadius,tubeHigh,
                tubeCapRadius,tubeTopCapHigh,tubeBotCapHigh,
                floorDepth;
        Mesh    mesh;
        
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
            
        roomHigh=(float)room.storyCount*segmentSize;
        
        bankWid=(segmentSize*0.5f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.3f));
        bankHigh=bankWid*(1.0f+(GeneratorMain.random.nextFloat()*(segmentSize*0.15f)));
        
        terminalWid=(segmentSize*0.5f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.3f));
        terminalHigh=(segmentSize*0.2f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.3f));
        
        juncWid=(segmentSize*0.4f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.2f));
        pipeHigh=(segmentSize*0.2f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.2f));
        pipeRadius=(segmentSize*0.05f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.1f));
        
        tubeRadius=(segmentSize*0.2f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.15f));
        tubeHigh=(roomHigh*0.2f)+(GeneratorMain.random.nextFloat()*(roomHigh*0.3f));
        tubeCapRadius=tubeRadius*(1.0f+(GeneratorMain.random.nextFloat()*0.15f));
        tubeTopCapHigh=(roomHigh*0.05f)+(GeneratorMain.random.nextFloat()*(roomHigh*0.1f));
        tubeBotCapHigh=(roomHigh*0.05f)+(GeneratorMain.random.nextFloat()*(roomHigh*0.1f));

        floorDepth=segmentSize*0.1f;
        
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
                
                    // only terminals on edges
                    
                pieceType=GeneratorMain.random.nextInt(4);
                if ((pieceType==1) && ((z!=tz) && (z!=(bz-1)) && (x!=lx) && (x!=(rx-1)))) pieceType=0;
                
                switch (pieceType) {
                    case 0:
                        addBank(room,x,z,bankWid,bankHigh,floorDepth,pieceCount);
                        break;
                    case 1:
                        this.addTerminal(room,x,z,terminalWid,terminalHigh,pieceCount);
                        break;
                    case 2:
                        this.addJunction(room,x,z,juncWid,pipeHigh,pipeRadius,pieceCount);
                        break;
                    case 3:
                        addTube(room,x,z,tubeRadius,tubeHigh,tubeCapRadius,tubeTopCapHigh,tubeBotCapHigh,pieceCount);
                        break;
                }
                
                pieceCount++;
                
                this.room.setGrid(0,x,z,1);
            }
        }
    }

}
