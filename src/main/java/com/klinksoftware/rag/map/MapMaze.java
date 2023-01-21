package com.klinksoftware.rag.map;

import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapMaze extends MapBase {

    private void buildRequiredBitmaps() {
        String[] wallBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapBrickRowWood", "BitmapGeometric", "BitmapHexagon", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapMosaic", "BitmapOrganic", "BitmapPlaster", "BitmapRockCracked", "BitmapStonePattern", "BitmapStoneRound", "BitmapStoneRow", "BitmapStoneRowWood", "BitmapTemple", "BitmapTile", "BitmapWoodBoard"};
        String[] floorBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapHexagon", "BitmapMetalPlank", "BitmapMetalTread", "BitmapMosaic", "BitmapTile", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] ceilingBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapMosaic", "BitmapPlaster", "BitmapTile", "BitmapStonePattern", "BitmapWoodBoard"};

        scene.bitmapGroup.add("wall_main", wallBitmaps);
        scene.bitmapGroup.add("floor", floorBitmaps);
        scene.bitmapGroup.add("ceiling", ceilingBitmaps);
    }

    @Override
    public RagPoint buildMeshes() {
        MapRoom room;
        ArrayList<MapRoom> rooms;
        RagPoint centerPnt;

        // create the rooms
        rooms = new ArrayList<>();
        generateMazeRooms(rooms, false);

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps();

        // now create the meshes
        createRoomMeshes(rooms, true, false);

        // get center point
        room = rooms.get(0);
        centerPnt = new RagPoint(((room.x * SEGMENT_SIZE) + (SEGMENT_SIZE * 0.5f)), (SEGMENT_SIZE * 0.5f), ((room.z * SEGMENT_SIZE) + (SEGMENT_SIZE * 0.5f)));

        // return the center point
        return (centerPnt);
    }

}
