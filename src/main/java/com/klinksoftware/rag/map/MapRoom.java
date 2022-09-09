package com.klinksoftware.rag.map;

import com.klinksoftware.rag.scene.Node;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;

public class MapRoom
{

    public static final int ROOM_STORY_MAIN = 0;
    public static final int ROOM_STORY_UPPER = 1;
    public static final int ROOM_STORY_LOWER = 2;
    public static final int ROOM_STORY_UPPER_EXTENSION = 3;
    public static final int ROOM_STORY_LOWER_EXTENSION = 4;
    public static final int ROOM_STORY_TALL_EXTENSION = 5;
    public static final int ROOM_STORY_SUNKEN_EXTENSION = 6;

    public int x, z, story;
    public int stairDir, stairX, stairZ;
    public boolean hasUpperExtension, hasLowerExtension;
    public byte[] wallHideArray;
    public boolean[] platformGrid;
    public MapRoom extendedFromRoom;
    public Node node;
    public MapPiece piece;

    public MapRoom(MapPiece piece) {
        this.piece=piece;

        this.x=0;
        this.z=0;

        this.story = ROOM_STORY_MAIN;
        this.stairDir = 0;
        this.stairX = 0;
        this.stairZ = 0;

        this.hasUpperExtension = false;
        this.hasLowerExtension = false;
        this.extendedFromRoom = null;

        // wall hiding
        wallHideArray = new byte[piece.wallLines.length];

        // grids for blocking off platforms and decorations
        platformGrid = new boolean[piece.sizeX * piece.sizeZ];

        // rooms remember a node to attach meshes to
        // this gets set later after creation
        node = null;
    }

    public MapRoom duplicate(int story) {
        MapRoom room;

        room = new MapRoom(piece.clone());
        room.x = x;
        room.z = z;

        room.node = node;

        room.story = story;
        room.stairDir = 0;
        this.stairX = 0;
        this.stairZ = 0;

        room.hasUpperExtension = false;
        room.hasLowerExtension = false;
        room.extendedFromRoom = null;

        room.wallHideArray = new byte[piece.wallLines.length];
        room.platformGrid = new boolean[piece.sizeX * piece.sizeZ];

        return (room);
    }

    public void changePiece(MapPiece piece) {
        this.piece = piece;

        wallHideArray = new byte[piece.wallLines.length];
        platformGrid = new boolean[piece.sizeX * piece.sizeZ];
    }

    //
    // compare stories
    //
    public boolean storyEqual(MapRoom room) {
        if ((story == ROOM_STORY_MAIN) && (room.story == ROOM_STORY_MAIN)) {
            return (true);
        }
        if ((story == ROOM_STORY_UPPER) && ((room.story == ROOM_STORY_UPPER) || (room.story == ROOM_STORY_UPPER_EXTENSION) || (room.story == ROOM_STORY_TALL_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_UPPER_EXTENSION) && ((room.story == ROOM_STORY_UPPER) || (room.story == ROOM_STORY_UPPER_EXTENSION) || (room.story == ROOM_STORY_TALL_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_TALL_EXTENSION) && ((room.story == ROOM_STORY_UPPER) || (room.story == ROOM_STORY_UPPER_EXTENSION) || (room.story == ROOM_STORY_TALL_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_LOWER) && ((room.story == ROOM_STORY_LOWER) || (room.story == ROOM_STORY_LOWER_EXTENSION) || (room.story == ROOM_STORY_SUNKEN_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_LOWER_EXTENSION) && ((room.story == ROOM_STORY_LOWER) || (room.story == ROOM_STORY_LOWER_EXTENSION) || (room.story == ROOM_STORY_SUNKEN_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_SUNKEN_EXTENSION) && ((room.story == ROOM_STORY_LOWER) || (room.story == ROOM_STORY_LOWER_EXTENSION) || (room.story == ROOM_STORY_SUNKEN_EXTENSION))) {
            return (true);
        }

        return (false);
    }

        //
        // collisions and touches with room boxes
        //

    public boolean collides(ArrayList<MapRoom> rooms)
    {
        int         n;
        MapRoom     checkRoom;

        for (n=0;n!=rooms.size();n++) {
            checkRoom=rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if (x>=(checkRoom.x+checkRoom.piece.sizeX)) continue;
            if ((x+piece.sizeX)<=checkRoom.x) continue;
            if (z>=(checkRoom.z+checkRoom.piece.sizeZ)) continue;
            if ((z+piece.sizeZ)<=checkRoom.z) continue;

            return(true);
        }

        return(false);
    }

    public int touches(ArrayList<MapRoom> rooms)
    {
        int         n;
        MapRoom     checkRoom;

        for (n=0;n!=rooms.size();n++) {
            checkRoom=rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if ((x==(checkRoom.x+checkRoom.piece.sizeX)) || ((x+piece.sizeX)==checkRoom.x)) {
                if (z>=(checkRoom.z+checkRoom.piece.sizeZ)) continue;
                if ((z+piece.sizeZ)<=checkRoom.z) continue;
                return(n);
            }

            if ((z==(checkRoom.z+checkRoom.piece.sizeZ)) || ((z+piece.sizeZ)==checkRoom.z)) {
                if (x>=(checkRoom.x+checkRoom.piece.sizeX)) continue;
                if ((x+piece.sizeX)<=checkRoom.x) continue;
                return(n);
            }
        }

        return(-1);
    }

    public int roomAbove(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (checkRoom.story != ROOM_STORY_UPPER) {
                continue;
            }

            if (x >= (checkRoom.x + checkRoom.piece.sizeX)) {
                continue;
            }
            if ((x + piece.sizeX) <= checkRoom.x) {
                continue;
            }
            if (z >= (checkRoom.z + checkRoom.piece.sizeZ)) {
                continue;
            }
            if ((z + piece.sizeZ) <= checkRoom.z) {
                continue;
            }

            return (n);
        }

        return (-1);
    }

    public int roomBelow(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (checkRoom.story != ROOM_STORY_LOWER) {
                continue;
            }

            if (x >= (checkRoom.x + checkRoom.piece.sizeX)) {
                continue;
            }
            if ((x + piece.sizeX) <= checkRoom.x) {
                continue;
            }
            if (z >= (checkRoom.z + checkRoom.piece.sizeZ)) {
                continue;
            }
            if ((z + piece.sizeZ) <= checkRoom.z) {
                continue;
            }

            return (n);
        }

        return (-1);
    }

    public boolean roomAtPosition(ArrayList<MapRoom> rooms, int cx, int cz) {
        for (MapRoom checkRoom : rooms) {
            if ((cx >= checkRoom.x) && (cx < (checkRoom.x + checkRoom.piece.sizeX)) && (cz >= checkRoom.z) && (cz < (checkRoom.z + checkRoom.piece.sizeZ))) {
                return (true);
            }
        }

        return (false);
    }

    public boolean touchesNegativeX(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if (x == (checkRoom.x + checkRoom.piece.sizeX)) {
                if (z >= (checkRoom.z + checkRoom.piece.sizeZ)) {
                    continue;
                }
                if ((z + piece.sizeZ) <= checkRoom.z) {
                    continue;
                }
                return (true);
            }
        }

        return (false);
    }

    public boolean touchesPositiveX(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if ((x + piece.sizeX) == checkRoom.x) {
                if (z >= (checkRoom.z + checkRoom.piece.sizeZ)) {
                    continue;
                }
                if ((z + piece.sizeZ) <= checkRoom.z) {
                    continue;
                }
                return (true);
            }
        }

        return (false);
    }

    public boolean touchesNegativeX(MapRoom checkRoom) {
        if (x != (checkRoom.x + checkRoom.piece.sizeX)) {
            return (false);
        }
        return ((z < (checkRoom.z + checkRoom.piece.sizeZ)) && ((z + piece.sizeZ) > checkRoom.z));
    }

    public boolean touchesPositiveX(MapRoom checkRoom) {
        if ((x + piece.sizeX) != checkRoom.x) {
            return (false);
        }
        return ((z < (checkRoom.z + checkRoom.piece.sizeZ)) && ((z + piece.sizeZ) > checkRoom.z));
    }

    public boolean touchesNegativeZ(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if (z == (checkRoom.z + checkRoom.piece.sizeZ)) {
                if (x >= (checkRoom.x + checkRoom.piece.sizeX)) {
                    continue;
                }
                if ((x + piece.sizeX) <= checkRoom.x) {
                    continue;
                }
                return (true);
            }
        }

        return (false);
    }

    public boolean touchesPositiveZ(ArrayList<MapRoom> rooms) {
        int n;
        MapRoom checkRoom;

        for (n = 0; n != rooms.size(); n++) {
            checkRoom = rooms.get(n);
            if (!storyEqual(checkRoom)) {
                continue;
            }

            if ((z + piece.sizeZ) == checkRoom.z) {
                if (x >= (checkRoom.x + checkRoom.piece.sizeX)) {
                    continue;
                }
                if ((x + piece.sizeX) <= checkRoom.x) {
                    continue;
                }
                return (true);
            }
        }

        return (false);
    }

    public boolean touchesNegativeZ(MapRoom checkRoom) {
        if (z != (checkRoom.z + checkRoom.piece.sizeZ)) {
            return (false);
        }
        return ((x < (checkRoom.x + checkRoom.piece.sizeX)) && ((x + piece.sizeX) > checkRoom.x));

    }

    public boolean touchesPositiveZ(MapRoom checkRoom) {
        if ((z + piece.sizeZ) != checkRoom.z) {
            return (false);
        }
        return ((x < (checkRoom.x + checkRoom.piece.sizeX)) && ((x + piece.sizeX) > checkRoom.x));

    }

    public boolean touches(MapRoom room) {
        if (!storyEqual(room)) {
            return (false);
        }

        if ((x == (room.x + room.piece.sizeX)) || ((x + piece.sizeX) == room.x)) {
            if ((z < (room.z + room.piece.sizeZ)) && ((z + piece.sizeZ) > room.z)) {
                return (true);
            }
        }

        if ((z == (room.z + room.piece.sizeZ)) || ((z + piece.sizeZ) == room.z)) {
            if ((x < (room.x + room.piece.sizeX)) && ((x + piece.sizeX) > room.x)) {
                return (true);
            }
        }

        return (false);
    }

    public int distance(MapRoom room) {
        int dx, dz;

        dx = Math.abs((room.x + (room.piece.sizeX / 2)) - (x + (piece.sizeX / 2)));
        dz = Math.abs((room.z + (room.piece.sizeZ / 2)) - (z + (piece.sizeZ / 2)));
        return ((int) Math.sqrt((dx * dx) + (dz * dz)));
    }

    public RagPoint getNodePoint() {
        float cx, cy, cz;

        cx = ((x * MapBuilder.SEGMENT_SIZE) + ((x + piece.sizeX) * MapBuilder.SEGMENT_SIZE)) * 0.5f;
        cy = 0.0f; // always at neutral 0 of map
        cz = ((z * MapBuilder.SEGMENT_SIZE) + ((z + piece.sizeZ) * MapBuilder.SEGMENT_SIZE)) * 0.5f;

        return (new RagPoint(cx, cy, cz));
    }

        //
        // shared/touching walls
        //

    public boolean hasSharedWalls(MapRoom checkRoom)
    {
        int         vIdx,vIdx2,nextIdx,nextIdx2,
                    vertexCount,vertexCount2;
        float       ax,az,ax2,az2,bx,bz,bx2,bz2;

        if (!storyEqual(checkRoom)) {
            return (false);
        }

        // check to see if two rooms share a wall segment
        vertexCount = piece.wallLines.length;
        vertexCount2 = checkRoom.piece.wallLines.length;

        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax = x + piece.wallLines[vIdx][0];
            az = z + piece.wallLines[vIdx][1];

            ax2 = x + piece.wallLines[nextIdx][0];
            az2 = z + piece.wallLines[nextIdx][1];

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx = checkRoom.x + checkRoom.piece.wallLines[vIdx2][0];
                bz = checkRoom.z + checkRoom.piece.wallLines[vIdx2][1];

                bx2 = checkRoom.x + checkRoom.piece.wallLines[nextIdx2][0];
                bz2 = checkRoom.z + checkRoom.piece.wallLines[nextIdx2][1];

                if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) return(true);

                vIdx2++;
            }

            vIdx++;
        }

        return(false);
    }

    public RagBound getTouchWallRange(MapRoom checkRoom,boolean xRun)
    {
        int                 n,vIdx,vIdx2,nextIdx,nextIdx2,
                            vertexCount,vertexCount2;
        float               f,ax,az,ax2,az2,bx,bz,bx2,bz2,
                            touchMin,touchMax;
        ArrayList<Float>    touchPoints;

        // we don't care about story equal here as this can be used
        // to connect rooms on different stories
        touchPoints = new ArrayList<>();

        // find all the touching wall segements
        vertexCount = piece.wallLines.length;
        vertexCount2 = checkRoom.piece.wallLines.length;

        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax = x + piece.wallLines[vIdx][0];
            az = z + piece.wallLines[vIdx][1];

            ax2 = x + piece.wallLines[nextIdx][0];
            az2 = z + piece.wallLines[nextIdx][1];

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx = checkRoom.x + checkRoom.piece.wallLines[vIdx2][0];
                bz = checkRoom.z + checkRoom.piece.wallLines[vIdx2][1];

                bx2 = checkRoom.x + checkRoom.piece.wallLines[nextIdx2][0];
                bz2 = checkRoom.z + checkRoom.piece.wallLines[nextIdx2][1];

                if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) {
                    if (xRun) {
                        touchPoints.add(Math.min(piece.wallLines[vIdx][0], piece.wallLines[nextIdx][0]));   // always use the min, as stairs draw from there
                    }
                    else {
                        touchPoints.add(Math.min(piece.wallLines[vIdx][1], piece.wallLines[nextIdx][1]));
                    }
                }

                vIdx2++;
            }

            vIdx++;
        }
        /*
        if (xRun) {
            return (new RagBound(piece.sizeX / 2, piece.sizeX / 2));
        } else {
            return (new RagBound(piece.sizeZ / 2, piece.sizeZ / 2));
        }
        */

        // now convert into x or z runs
        if (touchPoints.isEmpty()) return(null);

        touchMin=touchMax=touchPoints.get(0);

        for (n=1;n<touchPoints.size();n++) {
            f=touchPoints.get(n);
            if (f<touchMin) touchMin=f;
            if (f>touchMax) touchMax=f;
        }

        return (new RagBound(touchMin, touchMax));
    }

        //
        // hiding walls
        // the vertex offset is the first vertex of the
        // the wall (ascending) to hide
        //

    public void hideWall(int vertexOffset)
    {
        wallHideArray[vertexOffset]=0x1;
    }

    public boolean isWallHidden(int vertexOffset)
    {
        return(wallHideArray[vertexOffset]==0x1);
    }

        //
        // platform and blocking grid
        //

    public void setPlatformGrid(int x, int z)    {
        platformGrid[(z * piece.sizeX) + x] = true;
    }

    public boolean getPlatformGrid(int x, int z)    {
        return (platformGrid[(z * piece.sizeX) + x]);
    }

    public void setPlatformGridAcrossX(int x, boolean value) {
        int z;

        for (z = 0; z != piece.sizeZ; z++) {
            platformGrid[(z * piece.sizeX) + x] = value;
        }
    }

    public void setPlatformGridAcrossZ(int z, boolean value) {
        int x;

        for (x = 0; x != piece.sizeX; x++) {
            platformGrid[(z * piece.sizeX) + x] = value;
        }
    }

}
