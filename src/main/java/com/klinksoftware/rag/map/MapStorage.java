package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapStorage {

    private float boxSize;
    private MeshList meshList;

    public MapStorage(MeshList meshList, HashMap<String, BitmapBase> bitmaps)    {
        this.meshList=meshList;

        buildBitmap(bitmaps);
        boxSize = (MapBuilder.SEGMENT_SIZE * 0.2f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.2f));
    }

    public void buildBitmap(HashMap<String, BitmapBase> bitmaps) {
        String[] pillarBitmaps = {"MetalBox", "WoodBox"};

        BitmapBase bitmap;

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + pillarBitmaps[AppWindow.random.nextInt(pillarBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("box", bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        //
        // boxes
        //

    private void addBoxes(MapRoom room, int roomNumber, int x, float by, int z) {
        int stackLevel, stackCount;
        float dx, dy, dz, boxHalfSize;
        String name;
        RagPoint rotAngle;
        Mesh mesh, mesh2;

            // box size

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dy = 0;
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        boxHalfSize=boxSize*0.5f;

        rotAngle=new RagPoint(0.0f,0.0f,0.0f);

            // stacks of boxes

        stackCount=1+AppWindow.random.nextInt(3);

            // the stacks

        mesh = null;
        name = "storage_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {
            rotAngle.setFromValues(0.0f,(-10.0f+(AppWindow.random.nextFloat()*20.0f)),0.0f);
            mesh2 = MeshMapUtility.createCubeRotated(room, name, "box", (dx - boxHalfSize), (dx + boxHalfSize), (by + dy), ((by + dy) + boxSize), (dz - boxHalfSize), (dz + boxHalfSize), rotAngle, true, true, true, true, true, (stackLevel != 0), false, MeshMapUtility.UV_WHOLE);

            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }

                // go up one level

            dy += boxSize;
            if ((dy + boxSize) > MapBuilder.SEGMENT_SIZE) {
                break;
            }
        }

        meshList.add(mesh);
    }

        //
        // shelves
        //

    private void addShelf(int gx,int gz,int xSize,int zSize,float shelfHigh,float shelfLegWid,float xShelfMargin,float zShelfMargin,float floorDepth,int storageCount)
    {
        /*
        int             x2,z2,stackLevel,stackCount,boxCount;
        float           x,y,z,bx,bz,
                        tableXMin,tableXMax,tableZMin,tableZMax,
                        boxSize;
        RagPoint        rotAngle;
        Mesh            shelfMesh,boxMesh,mesh2;

        x=room.offset.x+((float)gx*MapBuilder.SEGMENT_SIZE);
        z=room.offset.z+((float)gz*MapBuilder.SEGMENT_SIZE);

            // height and width

        stackCount=1+AppWindow.random.nextInt(3);

            // some preset bounds

        rotAngle=new RagPoint(0.0f,0.0f,0.0f);

        tableXMin=x+xShelfMargin;
        tableXMax=(x+((float)xSize*MapBuilder.SEGMENT_SIZE))-xShelfMargin;
        tableZMin=z+zShelfMargin;
        tableZMax=(z+((float)zSize*MapBuilder.SEGMENT_SIZE))-zShelfMargin;

        y=room.offset.y;

        rotAngle=new RagPoint(0.0f,0.0f,0.0f);

            // the stacked shelves

        shelfMesh=null;
        boxMesh=null;

        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {

                // the table

            mesh2=MeshMapUtility.createCube(room,(name+"_shelf_"+Integer.toString(storageCount)),"accessory",tableXMin,tableXMax,(y+shelfHigh),((y+shelfHigh)+floorDepth),tableZMin,tableZMax,true,true,true,true,true,true,false,MeshMapUtility.UV_MAP);
            if (shelfMesh==null) {
                shelfMesh=mesh2;
            }
            else {
                shelfMesh.combine(mesh2);
            }

                // legs

            mesh2=MeshMapUtility.createCube(room,"","accessory",tableXMin,(tableXMin+shelfLegWid),y,(y+shelfHigh),tableZMin,(tableZMin+shelfLegWid),true,true,true,true,false,false,false,MeshMapUtility.UV_MAP);
            shelfMesh.combine(mesh2);

            mesh2=MeshMapUtility.createCube(room,"","accessory",tableXMin,(tableXMin+shelfLegWid),y,(y+shelfHigh),(tableZMax-shelfLegWid),tableZMax,true,true,true,true,false,false,false,MeshMapUtility.UV_MAP);
            shelfMesh.combine(mesh2);

            mesh2=MeshMapUtility.createCube(room,"","accessory",(tableXMax-shelfLegWid),tableXMax,y,(y+shelfHigh),tableZMin,(tableZMin+shelfLegWid),true,true,true,true,false,false,false,MeshMapUtility.UV_MAP);
            shelfMesh.combine(mesh2);

            mesh2=MeshMapUtility.createCube(room,"","accessory",(tableXMax-shelfLegWid),tableXMax,y,(y+shelfHigh),(tableZMax-shelfLegWid),tableZMax,true,true,true,true,false,false,false,MeshMapUtility.UV_MAP);
            shelfMesh.combine(mesh2);

                // items on shelf

            boxCount=0;

            for (z2=gz;z2<(gz+zSize);z2++) {
                for (x2=gx;x2<(gx+xSize);x2++) {
                    if (AppWindow.random.nextBoolean()) continue;

                    boxSize=(shelfHigh*0.5f)+(AppWindow.random.nextFloat()*(shelfHigh*0.25f));
                    bx=(room.offset.x+((float)x2*MapBuilder.SEGMENT_SIZE))+(MapBuilder.SEGMENT_SIZE*0.5f);
                    bz=(room.offset.z+((float)z2*MapBuilder.SEGMENT_SIZE))+(MapBuilder.SEGMENT_SIZE*0.5f);

                    rotAngle.setFromValues(0.0f,(-10.0f+(AppWindow.random.nextFloat()*20.0f)),0.0f);
                    mesh2=MeshMapUtility.createCubeRotated(room,(name+"_shelf_box_"+Integer.toString(storageCount)+"_"+Integer.toString(boxCount)),"box",(bx-boxSize),(bx+boxSize),((y+shelfHigh)+floorDepth),(((y+shelfHigh)+floorDepth)+boxSize),(bz-boxSize),(bz+boxSize),rotAngle,true,true,true,true,true,true,false,MeshMapUtility.UV_WHOLE);
                    if (boxMesh==null) {
                        boxMesh=mesh2;
                    }
                    else {
                        boxMesh.combine(mesh2);
                    }
                }

                boxCount++;
            }

                // go up one level

            y+=(shelfHigh+floorDepth);
            if (y>(room.offset.y+(MapBuilder.SEGMENT_SIZE*room.storyCount))) break;
        }

        if (shelfMesh!=null) meshList.add(shelfMesh);
        if (boxMesh!=null) meshList.add(boxMesh);
*/
    }

        //
        // storage
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {
        addBoxes(room, roomNumber, x, by, z);

        /*
        int     x,z,x2,z2,lx,rx,tz,bz,xSize,zSize,
                storageCount;
        float   boxSize,shelfHigh,shelfLegWid,xShelfMargin,zShelfMargin,
                floorDepth;

            // create the pieces

        storageCount=0;

        boxSize=(MapBuilder.SEGMENT_SIZE*0.4f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.4f));

        shelfHigh=(MapBuilder.SEGMENT_SIZE*0.35f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.35f));
        shelfLegWid=(MapBuilder.SEGMENT_SIZE*0.03f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.05f));
        xShelfMargin=(MapBuilder.SEGMENT_SIZE*0.025f)+AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.05f);
        zShelfMargin=(MapBuilder.SEGMENT_SIZE*0.025f)+AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.05f);

                    // add item to segment





        if (AppWindow.random.nextBoolean()) {
            // stack of boxes
            addBoxes(x,z,boxSize,storageCount);
        } else {
            // shelf with possible boxes

                        xSize=zSize=1;

                        if (AppWindow.random.nextBoolean()) {           // shelfs have random sizes
                            xSize=2;
                            if ((x+xSize)>=rx) {
                                xSize=1;
                            }
                            else {
                                if (room.getGrid(0,(x+1),z)!=0) xSize=1;
                            }
                        }
                        else {
                            zSize=2;
                            if ((z+zSize)>=bz) {
                                zSize=1;
                            }
                            else {
                                if (room.getGrid(0,x,(z+1))!=0) zSize=1;
                            }
                        }

                        addShelf(x,z,xSize,zSize,shelfHigh,shelfLegWid,xShelfMargin,zShelfMargin,floorDepth,storageCount);
                        storageCount++;

                        for (z2=z;z2<(z+zSize);z2++) {
                            for (x2=x;x2<(x+xSize);x2++) {
                                room.setGrid(0,x2,z2,1);
                            }
                        }

                        break;
                }
            }
        }
*/
    }

}
