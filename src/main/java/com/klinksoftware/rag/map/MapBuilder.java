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
        // decorations
        //

    private void buildDecorations(ArrayList<MapRoom> rooms, HashMap<String, BitmapBase> bitmaps, MeshList meshList) {
        int n, x, z, roomCount;
        float by;
        MapRoom room;
        MapPillar mapPillar = null;
        MapStorage mapStorage = null;
        MapEquipment mapEquipment = null;

        roomCount = rooms.size();

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            if (!room.piece.decorate) {
                continue;
            }

            // room bottom Y
            switch (room.story) {
                case MapRoom.ROOM_STORY_UPPER:
                case MapRoom.ROOM_STORY_UPPER_EXTENSION:
                    by = MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2);
                    break;
                case MapRoom.ROOM_STORY_LOWER:
                case MapRoom.ROOM_STORY_LOWER_EXTENSION:
                    by = -(MapBuilder.SEGMENT_SIZE + (MapBuilder.FLOOR_HEIGHT * 2));
                    break;
                default:
                    by = 0;
                    break;
            }

            // the decorations
            for (z = 0; z != room.piece.sizeZ; z++) {
                for (x = 0; x != room.piece.sizeX; x++) {
                    if ((room.getFloorGrid(x, z) != 1) || (room.getBlockedGrid(x, z))) {
                        continue;
                    }
                    if (AppWindow.random.nextBoolean()) {
                        continue;
                    }

                    /*
                    if (mapPillar == null) {
                        mapPillar = new MapPillar(meshList, bitmaps);
                    }
                    mapPillar.build(room, n, x, by, z);
                     */
 /*
                    if (mapStorage == null) {
                        mapStorage = new MapStorage(meshList, bitmaps);
                    }
                    mapStorage.build(room, n, x, by, z);
                     */
 /*
                    if (mapEquipment == null) {
                        mapEquipment = new MapEquipment(meshList, bitmaps);
                    }
                    mapEquipment.build(room, n, x, by, z);
*/
                    room.setBlockedGrid(x, z);
                }
            }

        }

        /*
        int         decorationType;


            // build the decoration

            case 1:
                bitmapGenerator.generatePillar();
                (new MapPillar(meshList,room,("pillar_"+Integer.toString(roomIdx)))).build();
                break;
            case 2:
                bitmapGenerator.generateBox();
                bitmapGenerator.generateAccessory();
                (new MapStorage(meshList,room,("storage_"+Integer.toString(roomIdx)))).build();
                break;
            case 3:
                bitmapGenerator.generateComputer();
                bitmapGenerator.generatePanel();
                bitmapGenerator.generateMonitor();
                bitmapGenerator.generatePlatform();
                bitmapGenerator.generatePipe();
                bitmapGenerator.generateLiquid();
                bitmapGenerator.generateGlass();
                (new MapEquipment(meshList,room,("computer_"+Integer.toString(roomIdx)))).build();
                break;
            case 4:
                bitmapGenerator.generatePipe();
                (new MapPipe(meshList,room,("pipe_"+Integer.toString(roomIdx)))).build();
                break;
        }
         */
    }

        //
        // build main floor
        //

    private void addMainFloor(ArrayList<MapRoom> rooms, int roomCount, int roomExtensionCount, float mapCompactFactor, boolean complex) {
        int n, placeCount, moveCount, failCount, touchIdx, firstRoomIdx, endRoomIdx;
        float origX, origZ, xAdd, zAdd;
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

        for (n=0;n!=roomCount;n++) {
            room = new MapRoom(mapPieceList.getRandomPiece(mapCompactFactor, complex));
            room.story = MapRoom.ROOM_STORY_MAIN;

            failCount=25;

            while (failCount>0) {
                placeCount=10;

                while (placeCount>0) {
                    room.x = AppWindow.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE * 2) - ROOM_RANDOM_LOCATION_DISTANCE;
                    room.z = AppWindow.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE * 2) - ROOM_RANDOM_LOCATION_DISTANCE;
                    if (!room.collides(rooms)) break;

                    placeCount--;
                }

                if (placeCount==0) {        // could not place this anywhere, so fail this room
                    failCount--;
                    continue;
                }

                    // migrate it towards the last room

                xAdd=lastRoom.x-Math.signum(room.x);
                zAdd=lastRoom.z-Math.signum(room.z);

                moveCount=ROOM_RANDOM_LOCATION_DISTANCE;

                while (moveCount>0) {
                    origX=room.x;
                    origZ=room.z;

                        // we move each chunk independently, if we can't
                        // move either x or z, then fail this room

                        // if we can move, check for a touch than a shared
                        // wall, if we have one, then the room is good

                    room.x+=xAdd;
                    if (room.collides(rooms)) {
                        room.x-=xAdd;
                    }
                    else {
                        touchIdx=room.touches(rooms);
                        if (touchIdx!=-1) {
                            if (room.hasSharedWalls(rooms.get(touchIdx))) {
                                rooms.add(room);
                                lastRoom=room;
                                break;
                            }
                        }
                    }

                    room.z+=zAdd;
                    if (room.collides(rooms)) {
                        room.z-=zAdd;
                    }
                    else {
                        touchIdx=room.touches(rooms);
                        if (touchIdx!=-1) {
                            if (room.hasSharedWalls(rooms.get(touchIdx))) {
                                rooms.add(room);
                                lastRoom=room;
                                break;
                            }
                        }
                    }

                        // if we couldn't move at all, fail this room

                    if ((room.x==origX) && (room.z==origZ)) {
                        failCount--;
                        break;
                    }

                    moveCount--;
                }

                    // if we were able to get to a good place
                    // without triggering the move count, then
                    // its a good room

                if (moveCount!=0) break;
            }
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

    private void addUpperOrLowerFloor(ArrayList<MapRoom> rooms, boolean upper) {
        int roomStartIdx, roomEndIdx;
        int x, z, xDif, zDif, sizeX, sizeZ;
        MapRoom startRoom, endRoom, startFloorRoom, endFloorRoom, room, nextRoom;

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
        startRoom.changePiece(mapPieceList.createSpecificRectangularPiece(startRoom.piece.sizeX, startRoom.piece.sizeZ, false));
        if (endRoom != null) {
            endRoom.changePiece(mapPieceList.createSpecificRectangularPiece(endRoom.piece.sizeX, endRoom.piece.sizeZ, false));
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
            rooms.add(startFloorRoom);

            if (endRoom != null) {
                endRoom.hasLowerExtension = true;
                endFloorRoom = endRoom.duplicate(MapRoom.ROOM_STORY_LOWER_EXTENSION);
                endFloorRoom.extendedFromRoom = endRoom;
                rooms.add(endFloorRoom);
            }
        }

        if (endFloorRoom == null) {
            return;
        }

        // walk along with rectangles until we connect
        room = startFloorRoom;

        while (true) {
            if (room.touches(endFloorRoom)) {
                break;
            }

            // walk along until we connect
            xDif = Math.abs((room.x + (room.piece.sizeX / 2)) - (endFloorRoom.x + (endFloorRoom.piece.sizeX / 2)));
            zDif = Math.abs((room.z + (room.piece.sizeZ / 2)) - (endFloorRoom.z + (endFloorRoom.piece.sizeZ / 2)));

            if (xDif > zDif) {
                if (endFloorRoom.x < room.x) {
                    x = endFloorRoom.x + endFloorRoom.piece.sizeX;
                    sizeX = room.x - x;
                } else {
                    x = room.x + room.piece.sizeX;
                    sizeX = endFloorRoom.x - x;
                }
                z = room.z;
                sizeZ = room.piece.sizeZ;
            } else {
                if (endFloorRoom.z < room.z) {
                    z = endFloorRoom.z + endFloorRoom.piece.sizeZ;
                    sizeZ = room.z - z;
                } else {
                    z = room.z + room.piece.sizeZ;
                    sizeZ = endFloorRoom.z - z;
                }
                x = room.x;
                sizeX = room.piece.sizeX;
            }

            if ((sizeX <= 0) || (sizeZ <= 0)) {
                break;
            }

            nextRoom = new MapRoom(mapPieceList.createSpecificRectangularPiece(sizeX, sizeZ, true));
            nextRoom.x = x;
            nextRoom.z = z;
            nextRoom.story = upper ? MapRoom.ROOM_STORY_UPPER : MapRoom.ROOM_STORY_LOWER;

            // if we collide, just exit out, we can't connect
            if (nextRoom.collides(rooms)) {
                break;
            }

            rooms.add(nextRoom);

            room = nextRoom;
        }
    }

    //
    // required bitmaps
    //
    public void buildRequiredBitmaps() {
        String[] wallBitmaps = {"Brick", "Geometric", "Metal", "Mosaic", "Organic", "Plaster", "Stone", "Tile", "Wood"};
        String[] floorBitmaps = {"Brick", "Concrete", "Dirt", "Grass", "Metal", "Mosaic", "Tile", "Wood"};
        String[] ceilingBitmaps = {"Brick", "Concrete", "Metal", "Mosaic", "Plaster", "Tile", "Wood"};
        String[] platformBitmaps = {"Brick", "Concrete", "Metal", "Wood"};

        BitmapBase bitmap;

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + wallBitmaps[AppWindow.random.nextInt(wallBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("wall_main", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + wallBitmaps[AppWindow.random.nextInt(wallBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("wall_upper", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + wallBitmaps[AppWindow.random.nextInt(wallBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("wall_lower", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + floorBitmaps[AppWindow.random.nextInt(floorBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("floor", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + floorBitmaps[AppWindow.random.nextInt(floorBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("floor_lower", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + ceilingBitmaps[AppWindow.random.nextInt(ceilingBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("ceiling", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + ceilingBitmaps[AppWindow.random.nextInt(ceilingBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("ceiling_upper", bitmap);

            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + platformBitmaps[AppWindow.random.nextInt(platformBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("platform", bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        //
        // build a map
        //

    public void build(float mapSize, float mapCompactFactor, boolean complex, boolean upperFloor, boolean lowerFloor, boolean decorations) {
        int n, roomCount, roomExtensionCount;
        RagPoint centerPnt;
        MapRoom room;
        ArrayList<MapRoom> rooms;
        MapStair mapStair;
        MapPlatform mapPlatform;

        bitmaps = new HashMap<>();
        mapPieceList=new MapPieceList();

        rooms=new ArrayList<>();
        meshList=new MeshList();

        // the main floor
        roomCount = 1 + (int) (50.0f * mapSize);
        roomExtensionCount = AppWindow.random.nextInt(roomCount / 10);

        addMainFloor(rooms, roomCount, roomExtensionCount, mapCompactFactor, complex);

        // upper floor
        if (upperFloor) {
            addUpperOrLowerFloor(rooms, true);
        }
        if (lowerFloor) {
            addUpperOrLowerFloor(rooms, false);
        }

            // eliminate all combined walls

        removeSharedWalls(rooms);

            // maps always need walls, floors and ceilings

        buildRequiredBitmaps();

            // now create the meshes

        roomCount=rooms.size();

        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);

            centerPnt = new RagPoint((((room.x * SEGMENT_SIZE) + (room.piece.sizeX * SEGMENT_SIZE)) * 0.5f), ((SEGMENT_SIZE + this.FLOOR_HEIGHT) + (SEGMENT_SIZE * 0.5f)), (((room.z * SEGMENT_SIZE) + (room.piece.sizeZ * SEGMENT_SIZE)) * 0.5f));

                // meshes

            MeshMapUtility.buildRoomWalls(meshList, room, centerPnt, n);
            MeshMapUtility.buildRoomFloorCeiling(meshList, room, centerPnt, n, true);
            MeshMapUtility.buildRoomFloorCeiling(meshList, room, centerPnt, n, false);
        }

        // any steps
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

        // any platforms
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

        // decorations
        if (decorations) {
            buildDecorations(rooms, bitmaps, meshList);
        }

            // now build the fake skeleton

        skeleton=meshList.rebuildMapMeshesWithSkeleton();

        // setup the view center point
        room = rooms.get(0);
        viewCenterPoint = new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE));
    }

    public void writeToFile(String path) {
        try {
            (new Export()).export(meshList, skeleton, bitmaps, path, "map");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
