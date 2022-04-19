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

    public MapPiece getRandomPiece()    {
        int                 idx;

        idx=AppWindow.random.nextInt(this.pieces.size());
        //idx=25;   // testing new pieces
        return(this.dupTransformPiece(this.pieces.get(idx),AppWindow.random.nextBoolean(),AppWindow.random.nextBoolean(),AppWindow.random.nextBoolean()));
    }

    public MapPiece getRandomPiece2() {
        int n,x,z,idx;
        MapPiece piece=new MapPiece();

        /*
            public int sizeX,sizeZ;
    public int[] floorGrid;
    public float[][] vertexes;
    public boolean decorate;
    public String name;
*/
        piece.name="rect";
        piece.sizeX=1+AppWindow.random.nextInt(10);
        piece.sizeZ=1+AppWindow.random.nextInt(10);

        piece.decorate=false;
        piece.floorGrid=new int[piece.sizeX*piece.sizeZ];
        for (n=0;n!=(piece.sizeX*piece.sizeZ);n++) {
            piece.floorGrid[n]=1;
        }

        piece.vertexes=new float[((piece.sizeX*2)+(piece.sizeZ*2))+1][2];

        idx=0;
        for (x=0;x!=(piece.sizeX+1);x++) {
            piece.vertexes[idx][0]=x;
            piece.vertexes[idx++][1]=0;
        }
        for (z=1;z!=(piece.sizeZ+1);z++) {
            piece.vertexes[idx][0]=piece.sizeX;
            piece.vertexes[idx++][1]=z;
        }
        for (x=(piece.sizeX-1);x>=0;x--) {
            piece.vertexes[idx][0]=x;
            piece.vertexes[idx++][1]=piece.sizeZ;
        }
        for (z=(piece.sizeZ-1);z>=1;z--) {
            piece.vertexes[idx][0]=0;
            piece.vertexes[idx++][1]=z;
        }

System.out.println(idx);


        return(piece);
    }
}
