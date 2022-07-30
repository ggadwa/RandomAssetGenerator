package com.klinksoftware.rag.map;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;
import com.klinksoftware.rag.AppWindow;

public class MapPieceList {

    private List<MapPiece> pieces;

    public MapPieceList() {
        String jsonStr;
        File jsonFile;

            // get the pieces from json in resources
            // at data/pieces.json

        try {
            jsonFile=new File(getClass().getClassLoader().getResource("data/pieces.json").getFile());
            jsonStr=new String(Files.readAllBytes(jsonFile.toPath()));

            pieces=(new ObjectMapper()).readValue(jsonStr,new TypeReference<List<MapPiece>>(){});
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private MapPiece dupTransformPiece(MapPiece origPiece, boolean rotate, boolean flipX, boolean flipZ) {
        int n, k, x, z;
        int[] tempGrid;
        float f;
        MapPiece piece;

            // no change

        if ((!rotate) && (!flipX) && (!flipZ)) return(origPiece);

            // duplicate

        piece=origPiece.clone();

            // flip and rotate

        for (n = 0; n != piece.wallLines.length; n++) {
            if (rotate) {
                f = piece.wallLines[n][0];
                piece.wallLines[n][0] = piece.wallLines[n][1];
                piece.wallLines[n][1] = f;
            }
            if (flipX) {
                piece.wallLines[n][0] = piece.sizeX - piece.wallLines[n][0];
            }
            if (flipZ) {
                piece.wallLines[n][1] = piece.sizeZ - piece.wallLines[n][1];
            }
        }

        for (n = 0; n != piece.floorQuads.length; n++) {
            if (rotate) {
                f = piece.floorQuads[n][0];
                piece.floorQuads[n][0] = piece.floorQuads[n][1];
                piece.floorQuads[n][1] = f;
            }
            if (flipX) {
                piece.floorQuads[n][0] = piece.sizeX - piece.floorQuads[n][0];
            }
            if (flipZ) {
                piece.floorQuads[n][1] = piece.sizeZ - piece.floorQuads[n][1];
            }
        }

        for (n = 0; n != piece.floorTrigs.length; n++) {
            if (rotate) {
                f = piece.floorTrigs[n][0];
                piece.floorTrigs[n][0] = piece.floorTrigs[n][1];
                piece.floorTrigs[n][1] = f;
            }
            if (flipX) {
                piece.floorTrigs[n][0] = piece.sizeX - piece.floorTrigs[n][0];
            }
            if (flipZ) {
                piece.floorTrigs[n][1] = piece.sizeZ - piece.floorTrigs[n][1];
            }
        }

        return(piece);
    }

    public MapPiece getRandomPiece(float mapCompactFactor, boolean complex) {
        int idx;

        // compact factor determines how many hallways there ends up
        // being (less hallways = more compact)
        if (AppWindow.random.nextFloat() > mapCompactFactor) {
            if (AppWindow.random.nextBoolean()) {
                return (createSpecificRectangularPiece((1 + AppWindow.random.nextInt(1)), (4 + AppWindow.random.nextInt(6))));
            } else {
                return (createSpecificRectangularPiece((4 + AppWindow.random.nextInt(6)), (1 + AppWindow.random.nextInt(1))));
            }
        }

        // complex rooms pick from any of the random shapes
        if (complex) {
            idx = AppWindow.random.nextInt(this.pieces.size());
            //idx=25;   // testing new pieces
            return (this.dupTransformPiece(this.pieces.get(idx), AppWindow.random.nextBoolean(), AppWindow.random.nextBoolean(), AppWindow.random.nextBoolean()));
        }

        // non-complex are always rectangles
        return (createSpecificRectangularPiece((4 + AppWindow.random.nextInt(6)), (4 + AppWindow.random.nextInt(6))));
    }

    public MapPiece createSpecificRectangularPiece(int sizeX, int sizeZ) {
        int x, z, idx;
        MapPiece piece;

        piece = new MapPiece();

        piece.name = "rect";
        piece.sizeX = sizeX;
        piece.sizeZ = sizeZ;

        piece.wallLines = new float[((piece.sizeX * 2) + (piece.sizeZ * 2)) + 1][2];

        idx=0;
        for (x=0;x!=(piece.sizeX+1);x++) {
            piece.wallLines[idx][0] = x;
            piece.wallLines[idx++][1] = 0;
        }
        for (z=1;z!=(piece.sizeZ+1);z++) {
            piece.wallLines[idx][0] = piece.sizeX;
            piece.wallLines[idx++][1] = z;
        }
        for (x=(piece.sizeX-1);x>=0;x--) {
            piece.wallLines[idx][0] = x;
            piece.wallLines[idx++][1] = piece.sizeZ;
        }
        for (z=(piece.sizeZ-1);z>=1;z--) {
            piece.wallLines[idx][0] = 0;
            piece.wallLines[idx++][1] = z;
        }

        piece.floorQuads = new float[(piece.sizeX * piece.sizeZ) * 4][2];

        idx = 0;

        for (x = 0; x != piece.sizeX; x++) {
            for (z = 0; z != piece.sizeZ; z++) {
                piece.floorQuads[idx][0] = x;
                piece.floorQuads[idx++][1] = z;
                piece.floorQuads[idx][0] = x + 1;
                piece.floorQuads[idx++][1] = z;
                piece.floorQuads[idx][0] = x + 1;
                piece.floorQuads[idx++][1] = z + 1;
                piece.floorQuads[idx][0] = x;
                piece.floorQuads[idx++][1] = z + 1;
            }
        }

        piece.floorTrigs = new float[0][2];

        return(piece);
    }
}
