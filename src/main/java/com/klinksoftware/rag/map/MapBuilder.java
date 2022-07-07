package com.klinksoftware.rag.map;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MapBuilder
{
    public static final int ROOM_RANDOM_LOCATION_DISTANCE = 100;
    public static final int UPPER_LOWER_FLOOR_MAX_DISTANCE = 20;
    public static final float SEGMENT_SIZE=10.0f;
    public static final float FLOOR_HEIGHT=1.0f;

    public static final int FC_MARK_EMPTY=0;
    public static final int FC_MARK_FILL_INSIDE=1;
    public static final int FC_MARK_FILL_OUTSIDE=2;
    public static final int FC_MARK_FILL_PLATFORM=3;

    public MeshList meshList;
    public Skeleton skeleton;
    public HashMap<String, BitmapBase> bitmaps;
    public RagPoint viewCenterPoint;
    private MapPieceList mapPieceList;

    public MapBuilder() {
    }

        //
        // deleting shared walls, floors, and ceilings
        //

    private void removeSharedWalls(ArrayList<MapRoom> rooms) {
        int n, k, roomCount, vIdx, vIdx2;
        int nextIdx, nextIdx2, vertexCount, vertexCount2;
        float ax, az, ax2, az2, bx, bz, bx2, bz2;
        MapRoom room, room2;

            // run through ever room against every other room
            // and pull any walls that are equal as they will
            // be places the rooms connect

        roomCount=rooms.size();

        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            vertexCount=room.piece.vertexes.length;

            for (k=(n+1);k<roomCount;k++) {
                room2=rooms.get(k);
                vertexCount2=room2.piece.vertexes.length;

                vIdx=0;

                while (vIdx<vertexCount) {
                    nextIdx=vIdx+1;
                    if (nextIdx==vertexCount) nextIdx=0;

                    ax=room.x+room.piece.vertexes[vIdx][0];
                    az=room.z+room.piece.vertexes[vIdx][1];

                    ax2=room.x+room.piece.vertexes[nextIdx][0];
                    az2=room.z+room.piece.vertexes[nextIdx][1];

                    vIdx2=0;

                    while (vIdx2<vertexCount2) {
                        nextIdx2=vIdx2+1;
                        if (nextIdx2==vertexCount2) nextIdx2=0;

                        bx=room2.x+room2.piece.vertexes[vIdx2][0];
                        bz=room2.z+room2.piece.vertexes[vIdx2][1];

                        bx2=room2.x+room2.piece.vertexes[nextIdx2][0];
                        bz2=room2.z+room2.piece.vertexes[nextIdx2][1];

                        if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) {

                                // only blank out walls that are at the same story

                            if (room.storyEqual(room2)) {
                                room.hideWall(vIdx);
                                room2.hideWall(vIdx2);
                            }
                        }

                        vIdx2++;
                    }

                    vIdx++;
                }
            }
        }
    }

        //
        // build floors
    //
    private MapRoom createRoomAndGravityIn(ArrayList<MapRoom> rooms, MapRoom lastRoom, int story, float mapCompactFactor, boolean complex) {
        int failCount, placeCount, moveCount;
        int touchIdx;
        float xAdd, zAdd, origX, origZ;
        MapRoom room;

        // other rooms start outside around the first room
        // room and gravity brings them in until they connect
        room = new MapRoom(mapPieceList.getRandomPiece(mapCompactFactor, complex));
        room.story = story;

        failCount = 25;

        while (failCount > 0) {
            placeCount = 10;

            while (placeCount > 0) {
                room.x = AppWindow.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE * 2) - ROOM_RANDOM_LOCATION_DISTANCE;
                room.z = AppWindow.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE * 2) - ROOM_RANDOM_LOCATION_DISTANCE;
                if (!room.collides(rooms)) {
                    break;
                }

                placeCount--;
            }

            if (placeCount == 0) {        // could not place this anywhere, so fail this room
                failCount--;
                continue;
            }

            // migrate it towards the last room
            xAdd = lastRoom.x - Math.signum(room.x);
            zAdd = lastRoom.z - Math.signum(room.z);

            moveCount = ROOM_RANDOM_LOCATION_DISTANCE;

            while (moveCount > 0) {
                origX = room.x;
                origZ = room.z;

                // we move each chunk independently, if we can't
                // move either x or z, then fail this room
                // if we can move, check for a touch than a shared
                // wall, if we have one, then the room is good
                room.x += xAdd;
                if (room.collides(rooms)) {
                    room.x -= xAdd;
                } else {
                    touchIdx = room.touches(rooms);
                    if (touchIdx != -1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            rooms.add(room);
                            return (room);
                        }
                    }
                }

                room.z += zAdd;
                if (room.collides(rooms)) {
                    room.z -= zAdd;
                } else {
                    touchIdx = room.touches(rooms);
                    if (touchIdx != -1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            rooms.add(room);
                            return (room);
                        }
                    }
                }

                // if we couldn't move at all, fail this room
                if ((room.x == origX) && (room.z == origZ)) {
                    failCount--;
                    break;
                }

                moveCount--;
            }

            // if we were able to get to a good place
            // without triggering the move count, then
            // its a good room
            if (moveCount != 0) {
                break;
            }
        }

        // this room failed, don't change room
        return (lastRoom);
    }

    private void addMainFloor(ArrayList<MapRoom> rooms, int roomCount, int roomExtensionCount, float mapCompactFactor, boolean complex) {
        int n, failCount, firstRoomIdx, endRoomIdx;
        MapRoom room, lastRoom, connectRoom;

        // first room is alone so it
        // always can be laid down
        room = new MapRoom(mapPieceList.getRandomPiece(1.0f, complex));
        room.x = 0;
        room.z = 0;

        rooms.add(room);

        lastRoom=room;

        // other rooms start outside around the first room
        // room and gravity brings them in until they connect
        firstRoomIdx=rooms.size();

        for (n = 0; n != roomCount; n++) {
            lastRoom = createRoomAndGravityIn(rooms, lastRoom, MapRoom.ROOM_STORY_MAIN, mapCompactFactor, complex);
        }

        endRoomIdx=rooms.size();

            // extension rooms
            // these rooms try to attach to existing rooms

        for (n=0;n!=roomExtensionCount;n++) {
            room = new MapRoom(mapPieceList.getRandomPiece(1.0f, complex));
            room.story = MapRoom.ROOM_STORY_MAIN;

            failCount=25;

            while (failCount>0) {
                connectRoom=rooms.get(firstRoomIdx+AppWindow.random.nextInt(endRoomIdx-firstRoomIdx));

                room.x=connectRoom.x-room.piece.sizeX;
                room.z=connectRoom.z;     // on left
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x+connectRoom.piece.sizeX;
                room.z=connectRoom.z;     // on right
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x;
                room.z=connectRoom.z-room.piece.sizeZ;     // on top
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x;
                room.z=connectRoom.z+connectRoom.piece.sizeZ;     // on bottom
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }

                failCount--;
            }
        }
    }

    private void addUpperOrLowerFloor(ArrayList<MapRoom> rooms, int roomCount, boolean upper, float mapCompactFactor, boolean complex) {
        int n, roomStartIdx, roomEndIdx, roomCount2;
        MapRoom startRoom, endRoom, startFloorRoom, endFloorRoom;

        // get a start room for floor (only from main rooms)
        while (true) {
            roomStartIdx = AppWindow.random.nextInt(rooms.size());
            startRoom = rooms.get(roomStartIdx);
            if ((startRoom.story == MapRoom.ROOM_STORY_MAIN) && (!startRoom.hasLowerExtension) && (!startRoom.hasUpperExtension) && (startRoom.piece.sizeX >= 5) && (startRoom.piece.sizeZ >= 5)) {
                break;
            }
        }

        // find an end room that's close and only a main room
        // sometimes we don't have one, and just a single room
        endRoom = null;

        if (AppWindow.random.nextFloat() > 0.25f) {
            while (true) {
                roomEndIdx = AppWindow.random.nextInt(rooms.size());
                if (roomEndIdx == roomStartIdx) {
                    continue;
                }

                endRoom = rooms.get(roomEndIdx);
                if ((endRoom.story == MapRoom.ROOM_STORY_MAIN) && (!startRoom.hasLowerExtension) && (!startRoom.hasUpperExtension) && (endRoom.piece.sizeX >= 5) && (endRoom.piece.sizeZ >= 5)) {
                    if (startRoom.distance(endRoom) < UPPER_LOWER_FLOOR_MAX_DISTANCE) {
                        break;
                    }
                }
            }
        }

        // need to switch rooms with rectangular rooms
        // so they are eaiser to connect and add stairs
        startRoom.changePiece(mapPieceList.createSpecificRectangularPiece(startRoom.piece.sizeX, startRoom.piece.sizeZ, false, false));
        if (endRoom != null) {
            endRoom.changePiece(mapPieceList.createSpecificRectangularPiece(endRoom.piece.sizeX, endRoom.piece.sizeZ, false, false));
        }

        // add the new rooms
        endFloorRoom = null;

        if (upper) {
            startRoom.hasUpperExtension = true;
            startFloorRoom = startRoom.duplicate(MapRoom.ROOM_STORY_UPPER_EXTENSION);
            startFloorRoom.extendedFromRoom = startRoom;
            rooms.add(startFloorRoom);

            if (endRoom != null) {
                endRoom.hasUpperExtension = true;
                endFloorRoom = endRoom.duplicate(MapRoom.ROOM_STORY_UPPER_EXTENSION);
                endFloorRoom.extendedFromRoom = endRoom;
                rooms.add(endFloorRoom);
            }
        } else {
            startRoom.hasLowerExtension = true;
            startFloorRoom = startRoom.duplicate(MapRoom.ROOM_STORY_LOWER_EXTENSION);
            startFloorRoom.extendedFromRoom = startRoom;
            startFloorRoom.piece.decorateOK = true;
            rooms.add(startFloorRoom);

            if (endRoom != null) {
                endRoom.hasLowerExtension = true;
                endFloorRoom = endRoom.duplicate(MapRoom.ROOM_STORY_LOWER_EXTENSION);
                endFloorRoom.extendedFromRoom = endRoom;
                endFloorRoom.piece.decorateOK = true;
                rooms.add(endFloorRoom);
            }
        }

        // alternate between the two rooms (if both exist)
        // to build this floor
        roomCount2 = (endFloorRoom == null) ? roomCount : (roomCount / 2);

        for (n = 0; n < roomCount2; n++) {
            createRoomAndGravityIn(rooms, startFloorRoom, (upper ? MapRoom.ROOM_STORY_UPPER : MapRoom.ROOM_STORY_LOWER), mapCompactFactor, complex);
        }

        if (endFloorRoom != null) {
            roomCount2 = roomCount - roomCount2;
            for (n = 0; n < roomCount2; n++) {
                createRoomAndGravityIn(rooms, endFloorRoom, (upper ? MapRoom.ROOM_STORY_UPPER : MapRoom.ROOM_STORY_LOWER), mapCompactFactor, complex);
            }
        }
    }

    //
    // meshes
    //
    public void createRoomMeshes(int mapType, ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        RagPoint centerPnt;

        roomCount = rooms.size();

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            centerPnt = new RagPoint((((room.x * SEGMENT_SIZE) + (room.piece.sizeX * SEGMENT_SIZE)) * 0.5f), ((SEGMENT_SIZE + this.FLOOR_HEIGHT) + (SEGMENT_SIZE * 0.5f)), (((room.z * SEGMENT_SIZE) + (room.piece.sizeZ * SEGMENT_SIZE)) * 0.5f));

            if (mapType == SettingsMap.MAP_TYPE_INDOOR) {
                MeshMapUtility.buildRoomIndoorWalls(meshList, room, centerPnt, n);
                MeshMapUtility.buildRoomFloorCeiling(meshList, room, centerPnt, n, true);
                MeshMapUtility.buildRoomFloorCeiling(meshList, room, centerPnt, n, false);
            } else {
                MeshMapUtility.buildRoomOutdoorWalls(meshList, room, centerPnt, n);
                MeshMapUtility.buildRoomFloorCeiling(meshList, room, centerPnt, n, true);
            }
        }
    }

    public void buildSteps(int mapType, ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        MapStair mapStair;

        roomCount = rooms.size();
        mapStair = new MapStair(meshList, rooms);

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) {
                mapStair.build(room, n, true);
            }
            if (room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) {
                mapStair.build(room, n, false);
            }
        }
    }

    public void buildPlatforms(int mapType, ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        MapPlatform mapPlatform;

        roomCount = rooms.size();
        mapPlatform = new MapPlatform(meshList, rooms);

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (room.story == MapRoom.ROOM_STORY_UPPER_EXTENSION) {
                mapPlatform.build(room, n, true);
            }
            if (room.story == MapRoom.ROOM_STORY_LOWER_EXTENSION) {
                mapPlatform.build(room, n, false);
            }
        }
    }

    //
    // required bitmaps
    //
    public void buildRequiredBitmaps(int mapType) {
        String[] wallBitmaps = {"Brick", "Geometric", "MetalHeagon", "MetalPlank", "MetalPlate", "Mosaic", "Organic", "Plaster", "Stone", "Temple", "Tile", "WoodBoard"};
        String[] insideFloorBitmaps = {"Brick", "Concrete", "MetalHexagon", "MetalPlank", "MetalTread", "Mosaic", "Tile", "WoodBoard"};
        String[] outsideFloorBitmaps = {"Dirt", "Grass"};
        String[] ceilingBitmaps = {"Brick", "Concrete", "MetalPlank", "MetalPlate", "Mosaic", "Plaster", "Tile", "WoodBoard"};
        String[] platformBitmaps = {"Brick", "Concrete", "MetalPlank", "MetalPlate", "WoodBoard"};

        if (mapType == SettingsMap.MAP_TYPE_INDOOR) {
            BitmapBase.mapBitmapLoader(bitmaps, "wall_main", wallBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "wall_upper", wallBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "wall_lower", wallBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "floor", insideFloorBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "floor_lower", insideFloorBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "ceiling", ceilingBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "ceiling_upper", ceilingBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "platform", platformBitmaps);
        } else {
            BitmapBase.mapBitmapLoader(bitmaps, "wall_main", wallBitmaps);
            BitmapBase.mapBitmapLoader(bitmaps, "floor", outsideFloorBitmaps);
        }
    }

        //
        // build a map
        //

    public void build(int mapType, float mainFloorMapSize, float upperFloorMapSize, float lowerFloorMapSize, float mapCompactFactor, boolean complex) {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        int upperFloorRoomCount, lowerFloorRoomCount;
        MapRoom room;
        ArrayList<MapRoom> rooms;

        bitmaps = new HashMap<>();
        mapPieceList=new MapPieceList();

        rooms=new ArrayList<>();
        meshList=new MeshList();

        // the stories
        mainFloorRoomCount = 1 + (int) (50.0f * mainFloorMapSize);
        mainFloorRoomExtensionCount = AppWindow.random.nextInt(mainFloorRoomCount / 10);

        if (mapType == SettingsMap.MAP_TYPE_INDOOR) {
            addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, mapCompactFactor, complex);

            upperFloorRoomCount = 1 + (int) (10.0f * upperFloorMapSize);
            if (upperFloorRoomCount != 0) {
                addUpperOrLowerFloor(rooms, upperFloorRoomCount, true, mapCompactFactor, complex);
            }
            lowerFloorRoomCount = 1 + (int) (10.0f * lowerFloorMapSize);
            if (lowerFloorRoomCount != 0) {
                addUpperOrLowerFloor(rooms, lowerFloorRoomCount, false, mapCompactFactor, complex);
            }
        } else {
            addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, 1.0f, false);
        }

        // eliminate all combined walls
        removeSharedWalls(rooms);

        // required textures for map
        buildRequiredBitmaps(mapType);

        // now create the meshes
        createRoomMeshes(mapType, rooms);

        // setup the view center point
        room = rooms.get(0);
        viewCenterPoint = new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE));

        // outdoor maps randomization
        if (mapType == SettingsMap.MAP_TYPE_OUTDOOR) {
            meshList.randomizeWallVertexesFromCenter(0.5f, (SEGMENT_SIZE / 3.0f), viewCenterPoint);
            meshList.randomizeFloorVertexes(0.5f, FLOOR_HEIGHT);
        }

        // steps and platforms
        if (mapType == SettingsMap.MAP_TYPE_INDOOR) {
            buildSteps(mapType, rooms);
            buildPlatforms(mapType, rooms);
        }

        // now build a fake skeleton for the glTF
        skeleton=meshList.rebuildMapMeshesWithSkeleton();
    }

    public void writeToFile(String path) {
        try {
            (new Export()).export(meshList, skeleton, bitmaps, path, "map");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
