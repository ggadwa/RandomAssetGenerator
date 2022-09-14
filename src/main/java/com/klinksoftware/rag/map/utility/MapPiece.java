package com.klinksoftware.rag.map.utility;

public class MapPiece
{
    public int sizeX, sizeZ;
    public float[][] wallLines;
    public float[][] floorQuads;
    public float[][] floorTrigs;
    public String name;

    public MapPiece clone() {
        int n;
        MapPiece piece;

        piece=new MapPiece();
        piece.name=name;
        piece.sizeX=sizeX;
        piece.sizeZ=sizeZ;

        // we have to deep clone these
        piece.wallLines = wallLines.clone();
        for (n = 0; n != wallLines.length; n++) {
            piece.wallLines[n] = wallLines[n].clone();
        }

        piece.floorQuads = floorQuads.clone();
        for (n = 0; n != floorQuads.length; n++) {
            piece.floorQuads[n] = floorQuads[n].clone();
        }

        piece.floorTrigs = floorTrigs.clone();
        for (n = 0; n != floorTrigs.length; n++) {
            piece.floorTrigs[n] = floorTrigs[n].clone();
        }

        return (piece);
    }
}


