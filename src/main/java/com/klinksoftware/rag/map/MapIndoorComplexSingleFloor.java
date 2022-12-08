package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapIndoorComplexSingleFloor extends MapBase {

    private void buildRequiredBitmaps() {
        String[] wallBitmaps = {"BrickPattern", "BrickRow", "BrickRowWood", "Geometric", "Hexagon", "MetalPlank", "MetalPlate", "Mosaic", "Organic", "Plaster", "RockCracked", "StonePattern", "StoneRound", "StoneRow", "StoneRowWood", "Temple", "Tile", "WoodBoard"};
        String[] insideFloorBitmaps = {"BrickPattern", "BrickRow", "Concrete", "Hexagon", "MetalPlank", "MetalTread", "Mosaic", "Tile", "StonePattern", "WoodBoard"};
        String[] ceilingBitmaps = {"BrickPattern", "BrickRow", "Concrete", "MetalPlank", "MetalPlate", "Mosaic", "Plaster", "Tile", "StonePattern", "WoodBoard"};
        String[] skyBoxBitmaps = {"SkyBoxMountain"};

        scene.bitmapGroup.add("wall_main", wallBitmaps);
        scene.bitmapGroup.add("floor", insideFloorBitmaps);
        scene.bitmapGroup.add("ceiling", ceilingBitmaps);
        scene.bitmapGroup.add("sky_box", skyBoxBitmaps);
    }

    @Override
    public RagPoint buildMeshes() {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        float mapCompactFactor;
        MapRoom room;
        ArrayList<MapRoom> rooms;

        // create the rooms
        rooms = new ArrayList<>();

        // the main rooms and extensions
        mainFloorRoomCount = 20 + AppWindow.random.nextInt(20);
        mainFloorRoomExtensionCount = 1 + AppWindow.random.nextInt(mainFloorRoomCount / 10);
        mapCompactFactor = 0.5f + AppWindow.random.nextFloat(0.2f);

        // main floor
        addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, mapCompactFactor, true);

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps();

        // now create the meshes
        createRoomMeshes(rooms, true);

        // skybox
        buildSkyBox();

        // return the center point
        room = rooms.get(0);
        return (new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE)));
    }

}
