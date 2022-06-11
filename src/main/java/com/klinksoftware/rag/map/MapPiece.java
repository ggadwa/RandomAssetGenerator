package com.klinksoftware.rag.map;

public class MapPiece
{
    public int sizeX,sizeZ;
    public int[] floorGrid;
    public float[][] vertexes;
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
        this.structureOK = structureOK;

        piece.vertexes=vertexes.clone();        // need to make sure we are really duplicating this array
        for (n=0;n!=vertexes.length;n++) {
            piece.vertexes[n]=vertexes[n].clone();
        }

        piece.floorGrid=floorGrid.clone();

        return(piece);
    }
}


