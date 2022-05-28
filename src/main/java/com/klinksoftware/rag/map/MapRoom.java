package com.klinksoftware.rag.map;

import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;

public class MapRoom
{

    public static final int ROOM_STORY_MAIN = 0;
    public static final int ROOM_STORY_UPPER = 1;
    public static final int ROOM_STORY_LOWER = 2;
    public static final int ROOM_STORY_UPPER_EXTENSION = 3;
    public static final int ROOM_STORY_LOWER_EXTENSION = 4;

    public int x, z, story;
    public int stairDir, stairX, stairZ;
    public boolean hasUpperExtension, hasLowerExtension;
    public byte[] wallHideArray;
    public int[] floorGrid, ceilingGrid;
    public boolean[] platformGrid, blockedGrid;
    public MapRoom extendedFromRoom;
    public MapPiece piece;

    public MapRoom(MapPiece piece)
    {
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

            // need a copy of floor grid

        floorGrid=piece.floorGrid.clone();
        ceilingGrid=piece.floorGrid.clone();

            // wall hiding

        wallHideArray=new byte[piece.vertexes.length];

        // grids for blocking off platforms and decorations
        platformGrid = new boolean[piece.sizeX * piece.sizeZ];
        blockedGrid = new boolean[piece.sizeX * piece.sizeZ];
    }

    public MapRoom duplicate(int story) {
        MapRoom room;

        room = new MapRoom(piece);
        room.x = x;
        room.z = z;

        room.story = story;
        room.stairDir = 0;
        this.stairX = 0;
        this.stairZ = 0;

        room.hasUpperExtension = false;
        room.hasLowerExtension = false;
        room.extendedFromRoom = null;

        // need a copy of floor grid
        room.floorGrid = piece.floorGrid.clone();
        room.ceilingGrid = piece.floorGrid.clone();

        room.wallHideArray = new byte[piece.vertexes.length];
        room.platformGrid = new boolean[piece.sizeX * piece.sizeZ];
        room.blockedGrid = new boolean[piece.sizeX * piece.sizeZ];

        return (room);
    }

    public void changePiece(MapPiece piece) {
        this.piece = piece;

        wallHideArray = new byte[piece.vertexes.length];
        platformGrid = new boolean[piece.sizeX * piece.sizeZ];
        blockedGrid = new boolean[piece.sizeX * piece.sizeZ];
    }

    //
    // compare stories
    //
    public boolean storyEqual(MapRoom room) {
        if ((story == ROOM_STORY_MAIN) && (room.story == ROOM_STORY_MAIN)) {
            return (true);
        }
        if ((story == ROOM_STORY_UPPER) && ((room.story == ROOM_STORY_UPPER) || (room.story == ROOM_STORY_UPPER_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_UPPER_EXTENSION) && ((room.story == ROOM_STORY_UPPER) || (room.story == ROOM_STORY_UPPER_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_LOWER) && ((room.story == ROOM_STORY_LOWER) || (room.story == ROOM_STORY_LOWER_EXTENSION))) {
            return (true);
        }
        if ((story == ROOM_STORY_LOWER_EXTENSION) && ((room.story == ROOM_STORY_LOWER) || (room.story == ROOM_STORY_LOWER_EXTENSION))) {
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

        vertexCount=piece.vertexes.length;
        vertexCount2=checkRoom.piece.vertexes.length;

        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax=x+piece.vertexes[vIdx][0];
            az=z+piece.vertexes[vIdx][1];

            ax2=x+piece.vertexes[nextIdx][0];
            az2=z+piece.vertexes[nextIdx][1];

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=checkRoom.x+checkRoom.piece.vertexes[vIdx2][0];
                bz=checkRoom.z+checkRoom.piece.vertexes[vIdx2][1];

                bx2=checkRoom.x+checkRoom.piece.vertexes[nextIdx2][0];
                bz2=checkRoom.z+checkRoom.piece.vertexes[nextIdx2][1];

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

        if (!storyEqual(checkRoom)) {
            return (null);
        }

        touchPoints=new ArrayList<>();

            // find all the touching wall segements

        vertexCount=piece.vertexes.length;
        vertexCount2=checkRoom.piece.vertexes.length;

        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax=x+piece.vertexes[vIdx][0];
            az=z+piece.vertexes[vIdx][1];

            ax2=x+piece.vertexes[nextIdx][0];
            az2=z+piece.vertexes[nextIdx][1];

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=checkRoom.x+checkRoom.piece.vertexes[vIdx2][0];
                bz=checkRoom.z+checkRoom.piece.vertexes[vIdx2][1];

                bx2=checkRoom.x+checkRoom.piece.vertexes[nextIdx2][0];
                bz2=checkRoom.z+checkRoom.piece.vertexes[nextIdx2][1];

                if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) {
                    if (xRun) {
                        touchPoints.add(Math.min(piece.vertexes[vIdx][0],piece.vertexes[nextIdx][0]));   // always use the min, as stairs draw from there
                    }
                    else {
                         touchPoints.add(Math.min(piece.vertexes[vIdx][1],piece.vertexes[nextIdx][1]));
                    }
                }

                vIdx2++;
            }

            vIdx++;
        }

            // now convert into x or z runs

        if (touchPoints.isEmpty()) return(null);

        touchMin=touchMax=touchPoints.get(0);

        for (n=1;n<touchPoints.size();n++) {
            f=touchPoints.get(n);
            if (f<touchMin) touchMin=f;
            if (f>touchMax) touchMax=f;
        }

        return(new RagBound(touchMin,touchMax));
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

    public boolean checkPlatformGridAcrossX(int x) {
        int z;

        for (z = 0; z != piece.sizeZ; z++) {
            if (platformGrid[(z * piece.sizeX) + x]) {
                return (true);
            }
        }

        return (false);
    }

    public void setPlatformGridAcrossX(int x, boolean value) {
        int z;

        for (z = 0; z != piece.sizeZ; z++) {
            platformGrid[(z * piece.sizeX) + x] = value;
        }
    }

    public boolean checkPlatformGridAcrossZ(int z) {
        int x;

        for (x = 0; x != piece.sizeX; x++) {
            if (platformGrid[(z * piece.sizeX) + x]) {
                return (true);
            }
        }

        return (false);
    }

    public void setPlatformGridAcrossZ(int z, boolean value) {
        int x;

        for (x = 0; x != piece.sizeX; x++) {
            platformGrid[(z * piece.sizeX) + x] = value;
        }
    }

    public void setBlockedGrid(int x, int z) {
        blockedGrid[(z * piece.sizeX) + x] = true;
    }

    public boolean getBlockedGrid(int x, int z) {
        return (blockedGrid[(z * piece.sizeX) + x]);
    }

    public int getFloorGrid(int x, int z) {
        return (floorGrid[(z * piece.sizeX) + x]);
    }

}
