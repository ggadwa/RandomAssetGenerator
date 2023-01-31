package com.klinksoftware.rag.map;

import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapUnderground extends MapBase {

    private void buildRequiredBitmaps() {
        String[] wallBitmaps = {"BitmapDirt", "BitmapRockCracked", "BitmapConcrete"};
        String[] floorBitmaps = {"BitmapDirt", "BitmapGrass", "BitmapGravel", "BitmapConcrete"};

        scene.bitmapGroup.add("wall_main", wallBitmaps);
        scene.bitmapGroup.add("floor", floorBitmaps);
    }

    @Override
    public RagPoint buildMeshes() {
        MapRoom room;
        ArrayList<MapRoom> rooms;
        RagPoint centerPnt;

        // create the rooms
        rooms = new ArrayList<>();
        generateMazeRooms(rooms, true);

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps();

        // now create the meshes
        createRoomMeshes(rooms, true, true);

        // get center point
        room = rooms.get(0);
        centerPnt = new RagPoint(((room.x * SEGMENT_SIZE) + (SEGMENT_SIZE * 0.5f)), (SEGMENT_SIZE * 0.5f), ((room.z * SEGMENT_SIZE) + (SEGMENT_SIZE * 0.5f)));

        // map randomization
        scene.randomizeWallVertexesFromCenter(0.5f, (SEGMENT_SIZE / 3.0f), centerPnt);
        scene.randomizeFloorVertexes(0.5f, (FLOOR_HEIGHT * 0.5f));
        scene.randomizeCeilingVertexes(0.5f, (FLOOR_HEIGHT * 0.5f));

        // return the center point
        return (centerPnt);
    }

}
