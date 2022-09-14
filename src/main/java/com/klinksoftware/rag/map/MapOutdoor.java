package com.klinksoftware.rag.map;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import com.klinksoftware.rag.map.utility.MapInterface;
import com.klinksoftware.rag.map.utility.MapBase;
import static com.klinksoftware.rag.map.utility.MapBase.FLOOR_HEIGHT;
import static com.klinksoftware.rag.map.utility.MapBase.SEGMENT_SIZE;
import com.klinksoftware.rag.map.utility.MapRoom;
import com.klinksoftware.rag.map.utility.MeshMapUtility;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

@MapInterface
public class MapOutdoor extends MapBase {

    private void createRoomMeshes(ArrayList<MapRoom> rooms) {
        int n, roomCount;
        MapRoom room;
        RagPoint centerPnt;

        roomCount = rooms.size();

        for (n = 0; n != roomCount; n++) {
            room = rooms.get(n);
            centerPnt = new RagPoint((((room.x * SEGMENT_SIZE) + (room.piece.sizeX * SEGMENT_SIZE)) * 0.5f), ((SEGMENT_SIZE + FLOOR_HEIGHT) + (SEGMENT_SIZE * 0.5f)), (((room.z * SEGMENT_SIZE) + (room.piece.sizeZ * SEGMENT_SIZE)) * 0.5f));

            MeshMapUtility.buildRoomOutdoorWalls(room, centerPnt, n);
            MeshMapUtility.buildRoomFloorCeiling(room, centerPnt, n, true);
        }
    }

    private void buildRequiredBitmaps(int textureSize, boolean skyBox) {
        String[] wallBitmaps = {"BrickPattern", "BrickRow", "BrickRowWood", "Geometric", "Hexagon", "MetalPlank", "MetalPlate", "Mosaic", "Organic", "Plaster", "RockCracked", "StonePattern", "StoneRound", "StoneRow", "StoneRowWood", "Temple", "Tile", "WoodBoard"};
        String[] outsideFloorBitmaps = {"Dirt", "Grass", "StonePattern"};
        String[] skyBoxBitmaps = {"SkyBoxMountain"};

        BitmapBase.mapBitmapLoader(scene.bitmaps, "wall_main", wallBitmaps, textureSize);
        BitmapBase.mapBitmapLoader(scene.bitmaps, "floor", outsideFloorBitmaps, textureSize);

        if (skyBox) {
            BitmapBase.mapBitmapLoader(scene.bitmaps, "sky_box", skyBoxBitmaps, textureSize);
        }
    }

    @Override
    public RagPoint buildMeshes() {
        int mainFloorRoomCount, mainFloorRoomExtensionCount;
        MapRoom room;
        ArrayList<MapRoom> rooms;
        RagPoint centerPnt;

        // create the rooms
        rooms = new ArrayList<>();

        // the main rooms, stories, extensions
        mainFloorRoomCount = 1 + (int) (50.0f * mainFloorMapSize);
        mainFloorRoomExtensionCount = AppWindow.random.nextInt(mainFloorRoomCount / 10);

        // just one floor
        addMainFloor(rooms, mainFloorRoomCount, mainFloorRoomExtensionCount, 1.0f, false);

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

        // get center point
        room = rooms.get(0);
        centerPnt = new RagPoint(((float) (room.piece.sizeX / 2) * SEGMENT_SIZE), (SEGMENT_SIZE * 0.5f), ((float) (room.piece.sizeZ / 2) * SEGMENT_SIZE));

        // outdoor maps randomization
        scene.randomizeWallVertexesFromCenter(0.5f, (SEGMENT_SIZE / 3.0f), centerPnt);
        scene.randomizeFloorVertexes(0.5f, FLOOR_HEIGHT);

        // return the center point
        return (centerPnt);
    }

}
