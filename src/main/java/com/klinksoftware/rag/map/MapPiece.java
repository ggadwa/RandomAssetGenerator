package com.klinksoftware.rag.map;

public class MapPiece
{
    public int              storyMinimum;
    public int[]            margins;
    public float[][]        vertexes;
    public boolean          decorate;
    public String           name;
    public MapPieceSize     size;
    
    public MapPiece clone()
    {
        MapPiece        piece;
        
        piece=new MapPiece();
        piece.name=name;
        piece.storyMinimum=storyMinimum;
        piece.size=size.clone();
        piece.margins=margins.clone();
        piece.vertexes=vertexes.clone();
        piece.decorate=decorate;
        
        return(piece);
    }
}


