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
        int         n,k;
        float       f;
        MapPiece    piece;
        
            // no change
            
        if ((!rotate) && (!flipX) && (!flipZ)) return(origPiece);
        
            // duplicate
            
        piece=origPiece.clone();
        
            // and flip
        
        if (rotate) {
            k=piece.size.x;
            piece.size.x=piece.size.z;
            piece.size.z=k;
            
            k=piece.margins[0];
            piece.margins[0]=piece.margins[2];
            piece.margins[2]=k;
            
            k=piece.margins[1];
            piece.margins[1]=piece.margins[3];
            piece.margins[3]=k;
        }
            
        for (n=0;n!=piece.vertexes.length;n++) {
            if (rotate) {
                f=piece.vertexes[n][0];
                piece.vertexes[n][0]=piece.vertexes[n][1];
                piece.vertexes[n][1]=f;
            }
            if (flipX) piece.vertexes[n][0]=piece.size.x-piece.vertexes[n][0];
            if (flipZ) piece.vertexes[n][1]=piece.size.z-piece.vertexes[n][1];
        }
        
        if (flipX) {
            k=piece.margins[0];
            piece.margins[0]=piece.margins[2];
            piece.margins[2]=k;
        }
        
        if (flipZ) {
            k=piece.margins[1];
            piece.margins[1]=piece.margins[3];
            piece.margins[3]=k;
        }
        
        return(piece);
    }
    
    public MapPiece getDefaultPiece()
    {
        return(this.pieces.get(0));
    }

    public MapPiece getRandomPiece(boolean bigRoomsOnly)
    {
        int                 idx;
        MapPiece            piece;
        
        idx=GeneratorMain.random.nextInt(this.pieces.size());
        
        if (bigRoomsOnly) {
            while (true) {
                piece=this.pieces.get(idx);
                if ((piece.size.x==10) && (piece.size.z==10)) break;
                idx++;
                if (idx>=this.pieces.size()) idx=0;
            }
        }
        
        return(this.dupTransformPiece(this.pieces.get(idx),GeneratorMain.random.nextBoolean(),GeneratorMain.random.nextBoolean(),GeneratorMain.random.nextBoolean()));
    }

}
