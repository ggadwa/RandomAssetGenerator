package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

public class MapPieceList
{
    private List<MapPiece>          pieces;

    public MapPieceList()
    {
        String          jsonStr;
        File            jsonFile;
        
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
    
    private MapPiece dupTransformPiece(MapPiece origPiece,boolean rotate,boolean flipX,boolean flipZ)
    {
        int         n,k,x,z;
        int[]       tempGrid;
        float       f;
        MapPiece    piece;
        
            // no change
            
        if ((!rotate) && (!flipX) && (!flipZ)) return(origPiece);
        
            // duplicate
            
        piece=origPiece.clone();
        
            // flip and rotate
        
        if (rotate) {
            k=piece.sizeX;
            piece.sizeX=piece.sizeZ;
            piece.sizeZ=k;
            
            for (z=0;z!=piece.sizeZ;z++) {
                for (x=0;x!=piece.sizeX;x++) {
                    piece.floorGrid[(z*piece.sizeX)+x]=origPiece.floorGrid[(x*origPiece.sizeX)+z];
                }
            }
        }
            
        for (n=0;n!=piece.vertexes.length;n++) {
            if (rotate) {
                f=piece.vertexes[n][0];
                piece.vertexes[n][0]=piece.vertexes[n][1];
                piece.vertexes[n][1]=f;
            }
            if (flipX) piece.vertexes[n][0]=piece.sizeX-piece.vertexes[n][0];
            if (flipZ) piece.vertexes[n][1]=piece.sizeZ-piece.vertexes[n][1];
        }
        
        if (flipX) {
            tempGrid=new int[piece.sizeX];
            
            for (z=0;z!=piece.sizeZ;z++) {
                for (x=0;x!=piece.sizeX;x++) {
                    tempGrid[x]=piece.floorGrid[(z*piece.sizeX)+x];
                }
                for (x=0;x!=piece.sizeX;x++) {
                    piece.floorGrid[(z*piece.sizeX)+x]=tempGrid[(piece.sizeX-x)-1];
                }
            }
        }
        
        if (flipZ) {
            tempGrid=new int[piece.sizeZ];
            
            for (x=0;x!=piece.sizeX;x++) {
                for (z=0;z!=piece.sizeZ;z++) {
                    tempGrid[z]=piece.floorGrid[(z*piece.sizeX)+x];
                }
                for (z=0;z!=piece.sizeZ;z++) {
                    piece.floorGrid[(z*piece.sizeX)+x]=tempGrid[(piece.sizeZ-z)-1];
                }
            }
        }
        
        return(piece);
    }
    
    public MapPiece getRandomPiece()
    {
        int                 idx;
        
        idx=AppWindow.random.nextInt(this.pieces.size());
        //idx=15;   // testing new pieces
        return(this.dupTransformPiece(this.pieces.get(idx),AppWindow.random.nextBoolean(),AppWindow.random.nextBoolean(),AppWindow.random.nextBoolean()));
    }

}
