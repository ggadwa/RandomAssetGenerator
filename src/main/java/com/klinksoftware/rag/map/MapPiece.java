package com.klinksoftware.rag.map;

public class MapPiece
{
    public int sizeX,sizeZ;
    public int[] floorGrid;
    public float[][] vertexes;
    public float[][] floorQuads;
    public float[][] floorTrigs;
    public boolean decorateOK;
    public boolean structureOK;
    public String name;

    public MapPiece clone() {
        int n;
        MapPiece piece;

        piece=new MapPiece();
        piece.name=name;
        piece.sizeX=sizeX;
        piece.sizeZ=sizeZ;
        piece.decorateOK = decorateOK;
        piece.structureOK = structureOK;

        // we have to deep clone these
        piece.vertexes = vertexes.clone();
        for (n=0;n!=vertexes.length;n++) {
            piece.vertexes[n]=vertexes[n].clone();
        }

        /*
        piece.floorQuads = floorQuads.clone();
        for (n = 0; n != floorQuads.length; n++) {
            piece.floorQuads[n] = floorQuads[n].clone();
        }
        */
        piece.floorGrid = floorGrid.clone();

        return (piece);
    }
}


