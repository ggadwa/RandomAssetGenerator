package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.FLOOR_HEIGHT;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapPlatform;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.map.utility.MapStair;
import com.klinksoftware.rag.map.utility.MeshMapUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapIndoor extends MapBase {

    private void createRoomMeshes(ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        RagPoint centerPnt;

        roomCount = rooms.size();

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            centerPnt = new RagPoint((((room.x * SEGMENT_SIZE) + (room.piece.sizeX * SEGMENT_SIZE)) * 0.5f), ((SEGMENT_SIZE + FLOOR_HEIGHT) + (SEGMENT_SIZE * 0.5f)), (((room.z * SEGMENT_SIZE) + (room.piece.sizeZ * SEGMENT_SIZE)) * 0.5f));

            MeshMapUtility.buildRoomIndoorWalls(room, centerPnt, n);
            MeshMapUtility.buildRoomFloorCeiling(room, centerPnt, n, true);
            MeshMapUtility.buildRoomFloorCeiling(room, centerPnt, n, false);
        }
    }

    private void buildSteps(ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapStair mapStair;
        MapRoom room;

        roomCount = rooms.size();
        mapStair = new MapStair(rooms);

        // stairs when changing to upper or lower
        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) {
                mapStair.buildPlatformStair(room, n, true);
                continue;
            }
            if (room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) {
                mapStair.buildPlatformStair(room, n, false);
            }
        }

        // stairs for sunken rooms
        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (room.story == MapRoom.ROOM_STORY_SUNKEN_EXTENSION) {
                mapStair.buildSunkenStairs(room, n);
            }
        }
    }

    private void buildPlatforms(ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        MapPlatform mapPlatform;

        roomCount = rooms.size();
        mapPlatform = new MapPlatform(rooms);

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) {
                mapPlatform.build(room, n, true);
                continue;
            }
            if (room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) {
                mapPlatform.build(room, n, false);
            }
        }
    }

    private void buildRequiredBitmaps(int textureSize, boolean skyBox) {
        String[] wallBitmaps = {"BrickPattern", "BrickRow", "BrickRowWood", "Geometric", "Hexagon", "MetalPlank", "MetalPlate", "Mosaic", "Organic", "Plaster", "RockCracked", "StonePattern", "StoneRound", "StoneRow", "StoneRowWood", "Temple", "Tile", "WoodBoard"};
        String[] insideFloorBitmaps = {"BrickPattern", "BrickRow", "Concrete", "Hexagon", "MetalPlank", "MetalTread", "Mosaic", "Tile", "StonePattern", "WoodBoard"};
        String[] outsideFloorBitmaps = {"Dirt", "Grass", "StonePattern"};
        String[] ceilingBitmaps = {"BrickPattern", "BrickRow", "Concrete", "MetalPlank", "MetalPlate", "Mosaic", "Plaster", "Tile", "StonePattern", "WoodBoard"};
        String[] platformBitmaps = {"BrickPattern", "BrickRow", "Concrete", "MetalPlank", "MetalPlate", "StonePattern", "WoodBoard"};
        String[] railingBitmaps = {"BrickPattern", "BrickRow", "Concrete", "MetalPlank", "MetalPlate", "StonePattern", "WoodBoard"};
        String[] stairBitmaps = {"BrickPattern", "BrickRow", "Concrete", "MetalPlank", "MetalPlate", "StonePattern", "WoodBoard"};
        String[] skyBoxBitmaps = {"SkyBoxMountain"};

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

        if (skyBox) {
            scene.bitmapGroup.add("sky_box", skyBoxBitmaps);
        }
    }

    @Override
    public RagPoint buildMeshes() {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        int upperFloorRoomCount, lowerFloorRoomCount;
        MapRoom room;
        ArrayList<MapRoom> rooms;

        // create the rooms
        rooms = new ArrayList<>();

        // the main rooms, stories, extensions
        mainFloorRoomCount = 1 + (int) (50.0f * mainFloorMapSize);
        mainFloorRoomExtensionCount = AppWindow.random.nextInt(mainFloorRoomCount / 10);

        // main floor
        addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, mapCompactFactor, complex);

        // upper and lower floors
        upperFloorRoomCount = (int) (10.0f * upperFloorMapSize);
        if (upperFloorRoomCount != 0) {
            addUpperOrLowerFloor(rooms, upperFloorRoomCount, true, mapCompactFactor, complex);
        }
        lowerFloorRoomCount = (int) (10.0f * lowerFloorMapSize);
        if (lowerFloorRoomCount != 0) {
            addUpperOrLowerFloor(rooms, lowerFloorRoomCount, false, mapCompactFactor, complex);
        }

        // tall or sunken rooms
        setTallOrSunkenRooms(rooms, tallRoom, sunkenRoom);

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps(textureSize, skyBox);

        // now create the meshes
        createRoomMeshes(rooms);

        // any skybox
        if (skyBox) {
            buildSkyBox();
        }

        // steps and platforms
        buildSteps(rooms);
        buildPlatforms(rooms);

        // return the center point
        room = rooms.get(0);
        return (new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE)));
    }

}
