package com.klinksoftware.rag;

import java.util.*;
import java.awt.*;
import java.nio.ByteBuffer;

public class TestCanvas extends Canvas
{
    private static final int MAP_WID=200;
    private static final int MAP_HIGH=200;
    private static final int MAP_FLOOR_COUNT=10;
    private static final int MAP_MAX_BRANCH_COUNT=10;
    private static final int MAP_MAX_LINE_COUNT=20;
    private static final int MAP_MIN_RECT_EDGE_FILL=4;
    
    private Image drawBuffer;
    private Random random;
    private byte[] mapBytes,mapCopyBytes;
    
    public TestCanvas()
    {
        drawBuffer=null;
    }
    /*
    private void drawChart(Graphics2D g2D,Color color,String title,int wid,int top,int bot,float maxValue,float[] data)
    {
        int         x,y,fontWid;
        float       fHigh;
        String      str;
        
            // the chart
            
        g2D.setColor(color);
        
        fHigh=(float)(bot-top);
        
        for (x=0;x!=valueLen;x++) {
            if (data[x]==0) {
                y=bot-1;
            }
            else {
                y=bot-(int)(fHigh*(data[x]/maxValue));
                if (y==bot) y=bot-1;
            }
            
            g2D.drawLine(x,y,x,bot);
        }
        
            // the label
        
        g2D.setColor(Color.BLACK);
            
        str=String.format(title,maxValue);
        
        fontWid=g2D.getFontMetrics().stringWidth(str);
        g2D.drawString(str,((wid-fontWid)/2),(bot-10));
    }
    */
    @Override
    public void paint(Graphics g)
    {
        int             wid,high,mid;
        float           maxValue;
        Graphics2D      g2D;
        Runtime         rt;
        
            // draw to back image
            
        wid=this.getWidth();
        high=this.getHeight();
        mid=high/2;
            
        if (drawBuffer==null) drawBuffer=createImage(wid,high);
            
        g2D=(Graphics2D)drawBuffer.getGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        
            // clear
            
        g2D.setColor(Color.GREEN);
        g2D.fillRect(0,0,wid,high);
        
            // flip the buffer
            
        g.drawImage(drawBuffer,0,0,this);
    }
    
    
    private void recurseSnakeDraw(int floorIdx,int x,int y,int dx,int dy,Rect rect,int branchCount,int pathCount)
    {
        int n;
        int offset,floorOffset;
        
        floorOffset=floorIdx*(MAP_WID*MAP_HIGH);
        
            // fill in a line of cubes for the count
            
        for (n=0;n!=pathCount;n++) {
            if (n!=0) {         // this is so x,y end at the end of the line, not after it
                x+=dx;
                y+=dy;
            }
            
            offset=floorOffset+((y*MAP_WID)+x);
            
            if ((x<0) || (y<0) || (x>=MAP_WID) || (y>=MAP_HIGH) || (mapBytes[offset]!=0)) return;

            mapBytes[offset]=1;
            
            if (x<rect.lx) rect.lx=x;
            if (x>rect.rx) rect.rx=x;
            if (y<rect.ty) rect.ty=y;
            if (y>rect.by) rect.by=y;
        }
        
            // branch
            
        branchCount--;
        if (branchCount<=0) return;
            
        recurseSnakeDraw(floorIdx,x,(y-1),0,-1,rect,branchCount,(1+random.nextInt(MAP_MAX_LINE_COUNT)));
        recurseSnakeDraw(floorIdx,x,(y+1),0,1,rect,branchCount,(1+random.nextInt(MAP_MAX_LINE_COUNT)));
        recurseSnakeDraw(floorIdx,(x-1),y,-1,0,rect,branchCount,(1+random.nextInt(MAP_MAX_LINE_COUNT)));
        recurseSnakeDraw(floorIdx,(x+1),y,1,0,rect,branchCount,(1+random.nextInt(MAP_MAX_LINE_COUNT)));
    }
    
    private boolean floodFill(int floorIdx,int x,int y,int recurseCount)
    {
        int n;
        int offset,floorOffset;
        
        if (recurseCount>10) return(false);
        
        floorOffset=floorIdx*(MAP_WID*MAP_HIGH);
        if (mapBytes[floorOffset+((y*MAP_WID)+x)]!=0) return(true);
        
        mapBytes[floorOffset+((y*MAP_WID)+x)]=2;
        
        if (y>0) {
            if (mapBytes[floorOffset+(((y-1)*MAP_WID)+x)]==0) {
                if (!floodFill(floorIdx,x,(y-1),(recurseCount+1))) return(false);
            }
        }
        if (y<(MAP_HIGH-1)) {
            if (mapBytes[floorOffset+(((y+1)*MAP_WID)+x)]==0) {
                if (!floodFill(floorIdx,x,(y+1),(recurseCount+1))) return(false);
            }
        }
        if (x>0) {
            if (mapBytes[floorOffset+((y*MAP_WID)+(x-1))]==0) {
                if (!floodFill(floorIdx,(x-1),y,(recurseCount+1))) return(false);
            }
        }
        if (x<(MAP_WID-1)) {
            if (mapBytes[floorOffset+((y*MAP_WID)+(x+1))]==0) {
                if (!floodFill(floorIdx,(x+1),y,(recurseCount+1))) return(false);
            }
        }
        
        return(true);
    }
    
    private void buildSingleFloor(int floorIdx)
    {
        int x,y,mx,my;
        Rect rect;
        
            // start in the middle and keep a list
            // of the final bounds
            
        mx=MAP_WID/2;
        my=MAP_HIGH/2;
        
        rect=new Rect();
        rect.lx=rect.rx=mx;
        rect.ty=rect.by=my;
        
            // recurse snake draw the initial map
            
        recurseSnakeDraw(floorIdx,(MAP_WID/2),(MAP_HIGH/2),0,0,rect,MAP_MAX_BRANCH_COUNT,0);
        
        for (y=rect.ty;y<rect.by;y++) {
            for (x=rect.lx;x<rect.rx;x++) {
                floodFill(floorIdx,x,y,0);
            }
        }
//        
    }
    
    public void run()
    {
        int x,y,wid,high;
        int offset;
        Graphics2D g2D;
        
        wid=this.getWidth();
        high=this.getHeight();
        
        g2D=(Graphics2D)drawBuffer.getGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        g2D.setColor(Color.GREEN);
        g2D.fillRect(0,0,wid,high);
        
            // the map is represented by a bunch of cubes
            
        mapBytes=new byte[MAP_FLOOR_COUNT*(MAP_WID*MAP_HIGH)];
        random=new Random(Calendar.getInstance().getTimeInMillis());
        
        buildSingleFloor(0);
        
        offset=0;

        
        g2D.setColor(Color.BLUE);
        for (y=0;y!=MAP_HIGH;y++) {
            for (x=0;x!=MAP_WID;x++) {
                if (mapBytes[offset+((y*MAP_WID)+x)]!=0) {
                    g2D.setColor((mapBytes[offset+((y*MAP_WID)+x)]==1)?Color.BLUE:Color.YELLOW);
                    g2D.fillRect((x*2),(y*2),2,2);
                }
            }
        }
        
        
            
        
        
        
            // flip the buffer
            
        this.getGraphics().drawImage(drawBuffer,0,0,this);
    }
    
}

class Rect
{
    public int lx,rx,ty,by;
}