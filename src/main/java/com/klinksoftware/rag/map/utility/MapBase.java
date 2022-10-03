package com.klinksoftware.rag.map.utility;

import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.map.utility.MapPieceList;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.Export;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MapBase {
    public static final int ROOM_RANDOM_LOCATION_DISTANCE = 100;
    public static final int UPPER_LOWER_FLOOR_MAX_DISTANCE = 20;
    public static final float SEGMENT_SIZE = 10.0f;
    public static final float FLOOR_HEIGHT = 1.0f;
    public static final float SUNKEN_HEIGHT = 3.0f;

    public static final int FC_MARK_EMPTY = 0;
    public static final int FC_MARK_FILL_INSIDE = 1;
    public static final int FC_MARK_FILL_OUTSIDE = 2;
    public static final int FC_MARK_FILL_PLATFORM = 3;

    public static final int FAIL_COUNT = 50;

    public Scene scene;
    public RagPoint viewCenterPoint;
    public MapPieceList mapPieceList;

    public int textureSize;
    public float mainFloorMapSize, upperFloorMapSize, lowerFloorMapSize, mapCompactFactor, tallRoom, sunkenRoom;
    public boolean complex, skyBox;

    public MapBase() {
    }

        //
        // deleting shared walls, floors, and ceilings
        //

    protected void removeSharedWalls(ArrayList<MapRoom> rooms) {
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
            vertexCount = room.piece.wallLines.length;

            for (k=(n+1);k<roomCount;k++) {
                room2=rooms.get(k);
                vertexCount2 = room2.piece.wallLines.length;

                vIdx=0;

                while (vIdx<vertexCount) {
                    nextIdx=vIdx+1;
                    if (nextIdx==vertexCount) nextIdx=0;

                    ax = room.x + room.piece.wallLines[vIdx][0];
                    az = room.z + room.piece.wallLines[vIdx][1];

                    ax2 = room.x + room.piece.wallLines[nextIdx][0];
                    az2 = room.z + room.piece.wallLines[nextIdx][1];

                    vIdx2=0;

                    while (vIdx2<vertexCount2) {
                        nextIdx2=vIdx2+1;
                        if (nextIdx2==vertexCount2) nextIdx2=0;

                        bx = room2.x + room2.piece.wallLines[vIdx2][0];
                        bz = room2.z + room2.piece.wallLines[vIdx2][1];

                        bx2 = room2.x + room2.piece.wallLines[nextIdx2][0];
                        bz2 = room2.z + room2.piece.wallLines[nextIdx2][1];

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
    protected MapRoom createRoomAndGravityIn(ArrayList<MapRoom> rooms, MapRoom lastRoom, int story, float mapCompactFactor, boolean complex) {
        int failCount, placeCount, moveCount;
        int touchIdx;
        float xAdd, zAdd, origX, origZ;
        String name;
        MapRoom room;

        // other rooms start outside around the first room
        // room and gravity brings them in until they connect
        room = new MapRoom(mapPieceList.getRandomPiece(mapCompactFactor, complex));
        room.story = story;

        name = "room_" + Integer.toString(rooms.size());

        failCount = FAIL_COUNT;

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
                            room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
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
                            room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
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

            failCount--;
        }

        // this room failed, don't change room
        return (lastRoom);
    }

    protected void addMainFloor(ArrayList<MapRoom> rooms, int roomCount, int roomExtensionCount, float mapCompactFactor, boolean complex) {
        int n, failCount, firstRoomIdx, endRoomIdx;
        String name;
        MapRoom room, lastRoom, connectRoom;

        // first room is alone so it
        // always can be laid down
        room = new MapRoom(mapPieceList.getRandomPiece(1.0f, complex));
        room.x = 0;
        room.z = 0;

        name = "room_" + Integer.toString(rooms.size());

        room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
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

            name = "room_" + Integer.toString(rooms.size());

            failCount = FAIL_COUNT;

            while (failCount>0) {
                connectRoom=rooms.get(firstRoomIdx+AppWindow.random.nextInt(endRoomIdx-firstRoomIdx));

                room.x=connectRoom.x-room.piece.sizeX;
                room.z=connectRoom.z;     // on left
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x+connectRoom.piece.sizeX;
                room.z=connectRoom.z;     // on right
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x;
                room.z=connectRoom.z-room.piece.sizeZ;     // on top
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x;
                room.z=connectRoom.z+connectRoom.piece.sizeZ;     // on bottom
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        room.node = scene.addChildNode(scene.rootNode, name, room.getNodePoint());
                        rooms.add(room);
                        break;
                    }
                }

                failCount--;
            }
        }
    }

    protected void addUpperOrLowerFloor(ArrayList<MapRoom> rooms, int roomCount, boolean upper, float mapCompactFactor, boolean complex) {
        int n, roomStartIdx, roomEndIdx, roomCount2, failCount;
        MapRoom startRoom, endRoom, startFloorRoom, endFloorRoom;

        // get a start room for floor (only from main rooms)
        roomStartIdx = -1;
        failCount = FAIL_COUNT;

        while (failCount > 0) {
            roomStartIdx = AppWindow.random.nextInt(rooms.size());
            startRoom = rooms.get(roomStartIdx);
            if ((startRoom.story == MapRoom.ROOM_STORY_MAIN) && (!startRoom.hasLowerExtension) && (!startRoom.hasUpperExtension) && (startRoom.piece.sizeX >= 5) && (startRoom.piece.sizeZ >= 5)) {
                break;
            }

            failCount--;
        }

        if (roomStartIdx == -1) {
            return;
        }
        startRoom = rooms.get(roomStartIdx);

        // find an end room that's close and only a main room
        // sometimes we don't have one, and just a single room
        endRoom = null;

        if (AppWindow.random.nextFloat() > 0.25f) {
            roomEndIdx = -1;
            failCount = FAIL_COUNT;

            while (failCount > 0) {
                roomEndIdx = AppWindow.random.nextInt(rooms.size());
                if (roomEndIdx == roomStartIdx) {
                    continue;
                }

                endRoom = rooms.get(roomEndIdx);
                if ((endRoom.story == MapRoom.ROOM_STORY_MAIN) && (!endRoom.hasLowerExtension) && (!endRoom.hasUpperExtension) && (endRoom.piece.sizeX >= 5) && (endRoom.piece.sizeZ >= 5)) {
                    if (startRoom.distance(endRoom) < UPPER_LOWER_FLOOR_MAX_DISTANCE) {
                        break;
                    }
                }

                failCount--;
            }

            if (roomEndIdx == -1) {
                return;
            }
        }

        // need to switch rooms with rectangular rooms
        // so they are eaiser to connect and add stairs
        startRoom.changePiece(mapPieceList.createSpecificRectangularPiece(startRoom.piece.sizeX, startRoom.piece.sizeZ));
        if (endRoom != null) {
            endRoom.changePiece(mapPieceList.createSpecificRectangularPiece(endRoom.piece.sizeX, endRoom.piece.sizeZ));
        }

        // add the new rooms
        // don't add a node here, use the same node as the duplicated room
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
            rooms.add(startFloorRoom);

            if (endRoom != null) {
                endRoom.hasLowerExtension = true;
                endFloorRoom = endRoom.duplicate(MapRoom.ROOM_STORY_LOWER_EXTENSION);
                endFloorRoom.extendedFromRoom = endRoom;
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
    // set some random rooms to tall or sunken
    //
    protected void setTallOrSunkenRooms(ArrayList<MapRoom> rooms, float tallRoom, float sunkenRoom) {
        int n, k, nRoom;
        boolean badRoom;
        MapRoom room, addRoom, checkRoom;

        nRoom = rooms.size();

        for (n = 0; n != nRoom; n++) {
            room = rooms.get(n);

            // only check for main story rooms
            if (room.story != MapRoom.ROOM_STORY_MAIN) {
                continue;
            }

            // skip anything already having an extension
            if ((room.hasUpperExtension) || (room.hasLowerExtension)) {
                continue;
            }

            // tall rooms
            // don't add a node here, use same room node
            if (room.roomAbove(rooms) == -1) {
                if (AppWindow.random.nextFloat() < tallRoom) {
                    room.hasUpperExtension = true;
                    addRoom = room.duplicate(MapRoom.ROOM_STORY_TALL_EXTENSION);
                    addRoom.extendedFromRoom = room;
                    rooms.add(addRoom);
                }
            }

            // sunken rooms
            if (room.roomBelow(rooms) == -1) {

                // sunken rooms have stairs, so they need to be bigger
                if ((room.piece.sizeX < 5) || (room.piece.sizeZ < 5)) {
                    continue;
                }

                // extra check to skip touching any room with a lower
                // extension so we don't have to deal with shared walls
                badRoom = false;

                for (k = 0; k != nRoom; k++) {
                    if (k != n) {
                        checkRoom = rooms.get(k);
                        if (room.touches(checkRoom)) {
                            if (checkRoom.hasLowerExtension) {
                                badRoom = true;
                                break;
                            }
                        }
                    }
                }

                if (badRoom) {
                    continue;
                }

                // sink the room
                // don't add a node here, use same room node
                if (AppWindow.random.nextFloat() < sunkenRoom) {
                    room.hasLowerExtension = true;
                    addRoom = room.duplicate(MapRoom.ROOM_STORY_SUNKEN_EXTENSION);
                    addRoom.extendedFromRoom = room;
                    rooms.add(addRoom);
                }
            }
        }
    }

    // sky boxes
    protected void buildSkyBox() {
        float min, max;
        RagPoint minPnt, maxPnt;

        // get dimensions of sky box
        minPnt = new RagPoint(0.0f, 0.0f, 0.0f);
        maxPnt = new RagPoint(0.0f, 0.0f, 0.0f);

        scene.getAbsoluteMixMaxVertexForAbsoluteVertexes(minPnt, maxPnt);

        minPnt.scale(1.1f);
        maxPnt.scale(1.1f);

        min = Math.min(Math.min(minPnt.x, minPnt.y), minPnt.z);
        max = Math.max(Math.max(maxPnt.x, maxPnt.y), maxPnt.z);

        // make skybox
        scene.rootNode.addMesh(MeshUtility.createCube("sky_box", min, max, min, max, min, max, true, true, true, true, true, true, true, MeshUtility.UV_SKY_BOX));

        // so view drawer knows not to erase
        scene.skyBox = true;
    }

    //
        // build a map
        //
    public RagPoint buildMeshes() {
        return (new RagPoint(0.0f, 0.0f, 0.0f));
    }

    public void build(int textureSize, float mainFloorMapSize, float upperFloorMapSize, float lowerFloorMapSize, float mapCompactFactor, float tallRoom, float sunkenRoom, boolean complex, boolean skyBox) {
        this.textureSize = textureSize;
        this.mainFloorMapSize = mainFloorMapSize;
        this.upperFloorMapSize = upperFloorMapSize;
        this.lowerFloorMapSize = lowerFloorMapSize;
        this.mapCompactFactor = mapCompactFactor;
        this.tallRoom = tallRoom;
        this.sunkenRoom = sunkenRoom;
        this.complex = complex;
        this.skyBox = skyBox;

        // blank scene
        scene = new Scene(textureSize);

        // map construction classes
        mapPieceList = new MapPieceList();

        // build the specific map and return the absolute center point
        viewCenterPoint = buildMeshes();

        // maps are build with absolute vertexes, we
        // need to adjust these to be relative to nodes
        scene.shiftAbsoluteMeshesToNodeRelativeMeshes();

        // need to build unique indexes for all the meshes,
        // which is how they refer to each other in
        // the gltf
        scene.createMeshIndexes();

        // generate the bitmaps
        scene.bitmapGroup.generateAll();
    }

    public void writeToFile(String path) {
        try {
            (new Export()).export(scene, path, "map");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
