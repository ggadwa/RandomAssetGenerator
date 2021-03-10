package com.klinksoftware.rag.map.indoor;

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
        int             n;
        MapPiece        piece;
        
        piece=new MapPiece();
        piece.name=name;
        piece.storyMinimum=storyMinimum;
        piece.size=size.clone();
        piece.margins=margins.clone();
        piece.decorate=decorate;
        
        piece.vertexes=vertexes.clone();        // need to make sure we are really duplicating this array
        for (n=0;n!=vertexes.length;n++) {
            piece.vertexes[n]=vertexes[n].clone();
            
        }
        
        return(piece);
    }
}


