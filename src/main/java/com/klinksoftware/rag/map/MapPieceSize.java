/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinksoftware.rag.map;

/**
 *
 * @author ggadwa
 */
public class MapPieceSize
{
    public int          x,z;
    
    public MapPieceSize clone()
    {
        MapPieceSize        mapPieceSize;
        
        mapPieceSize=new MapPieceSize();
        mapPieceSize.x=x;
        mapPieceSize.z=z;
        
        return(mapPieceSize);
    }
}
