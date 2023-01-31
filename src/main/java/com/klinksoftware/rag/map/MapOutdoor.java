package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.FLOOR_HEIGHT;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapOutdoor extends MapBase {

    private void buildRequiredBitmaps() {
        String[] wallBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapBrickRowWood", "BitmapGeometric", "BitmapHexagon", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapMosaic", "BitmapOrganic", "BitmapPlaster", "BitmapRockCracked", "BitmapStonePattern", "BitmapStoneRound", "BitmapStoneRow", "BitmapStoneRowWood", "BitmapTemple", "BitmapTile", "BitmapWoodBoard"};
        String[] outsideFloorBitmaps = {"BitmapDirt", "BitmapGrass", "BitmapGravel", "BitmapStonePattern"};

        scene.bitmapGroup.add("wall_main", wallBitmaps);
        scene.bitmapGroup.add("floor", outsideFloorBitmaps);
    }

    @Override
    public RagPoint buildMeshes() {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        MapRoom room;
        ArrayList<MapRoom> rooms;
        RagPoint centerPnt;

        // create the rooms
        rooms = new ArrayList<>();

        // the main rooms and extensions
        mainFloorRoomCount = 20 + AppWindow.random.nextInt(20);
        mainFloorRoomExtensionCount = 1 + AppWindow.random.nextInt(mainFloorRoomCount / 10);

        // just one floor
        addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, 1.0f, false);

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps();

        // now create the meshes
        createRoomMeshes(rooms, false, false);

        // get center point
        room = rooms.get(0);
        centerPnt = new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE));

        // outdoor maps randomization
        scene.randomizeWallVertexesFromCenter(0.5f, (SEGMENT_SIZE / 3.0f), centerPnt);
        scene.randomizeFloorVertexes(0.5f, FLOOR_HEIGHT);

        // return the center point
        return (centerPnt);
    }

}
