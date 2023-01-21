package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapIndoorSimpleMultiFloor extends MapBase {

    private void buildRequiredBitmaps() {
        String[] wallBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapBrickRowWood", "BitmapGeometric", "BitmapHexagon", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapMosaic", "BitmapOrganic", "BitmapPlaster", "BitmapRockCracked", "BitmapStonePattern", "BitmapStoneRound", "BitmapStoneRow", "BitmapStoneRowWood", "BitmapTemple", "BitmapTile", "BitmapWoodBoard"};
        String[] insideFloorBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapHexagon", "BitmapMetalPlank", "BitmapMetalTread", "BitmapMosaic", "BitmapTile", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] ceilingBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapMosaic", "BitmapPlaster", "BitmapTile", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] platformBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] railingBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] stairBitmaps = {"BitmapBrickPattern", "BitmapBrickRow", "BitmapConcrete", "BitmapMetalPlank", "BitmapMetalPlate", "BitmapStonePattern", "BitmapWoodBoard"};
        String[] skyBoxBitmaps = {"BitmapSkyBoxMountain"};

        scene.bitmapGroup.add("wall_main", wallBitmaps);
        scene.bitmapGroup.add("wall_upper", wallBitmaps);
        scene.bitmapGroup.add("wall_lower", wallBitmaps);
        scene.bitmapGroup.add("floor", insideFloorBitmaps);
        scene.bitmapGroup.add("floor_lower", insideFloorBitmaps);
        scene.bitmapGroup.add("ceiling", ceilingBitmaps);
        scene.bitmapGroup.add("ceiling_upper", ceilingBitmaps);
        scene.bitmapGroup.add("platform", platformBitmaps);
        scene.bitmapGroup.add("railing", railingBitmaps);
        scene.bitmapGroup.add("stair", stairBitmaps);
        scene.bitmapGroup.add("sky_box", skyBoxBitmaps);
    }

    @Override
    public RagPoint buildMeshes() {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        int upperFloorRoomCount, lowerFloorRoomCount;
        MapRoom room;
        ArrayList<MapRoom> rooms;

        // create the rooms
        rooms = new ArrayList<>();

        // the main rooms and extensions
        mainFloorRoomCount = 20 + AppWindow.random.nextInt(20);
        mainFloorRoomExtensionCount = 1 + AppWindow.random.nextInt(mainFloorRoomCount / 10);

        // main floor
        addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, 1.0f, false);

        // upper and lower floors
        upperFloorRoomCount = 2 + AppWindow.random.nextInt(5);
        if (upperFloorRoomCount != 0) {
            addUpperOrLowerFloor(rooms, upperFloorRoomCount, true, 1.0f, false);
        }
        lowerFloorRoomCount = 2 + AppWindow.random.nextInt(5);
        if (lowerFloorRoomCount != 0) {
            addUpperOrLowerFloor(rooms, lowerFloorRoomCount, false, 1.0f, false);
        }

        // tall or sunken rooms
        setTallOrSunkenRooms(rooms, (0.3f + AppWindow.random.nextFloat(0.2f)), (0.3f + AppWindow.random.nextFloat(0.2f)));

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps();

        // now create the meshes
        createRoomMeshes(rooms, true, false);

        // skybox
        buildSkyBox();

        // steps, platforms, windows
        buildSteps(rooms);
        buildPlatforms(rooms);
        buildWindows(rooms);

        // return the center point
        room = rooms.get(0);
        return (new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE)));
    }

}
