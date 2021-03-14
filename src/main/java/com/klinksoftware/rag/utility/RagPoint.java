package com.klinksoftware.rag.utility;

public class RagPoint
{
    public float        x,y,z;
    
    public RagPoint(float x,float y,float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void setFromValues(float x,float y,float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void setFromSubPoint(RagPoint p1,RagPoint p2)
    {
        this.x=p1.x-p2.x;
        this.y=p1.y-p2.y;
        this.z=p1.z-p2.z;
    }
    
    public void addPoint(RagPoint p)
    {
        this.x+=p.x;
        this.y+=p.y;
        this.z+=p.z;
    }
    
    public void scale(float f)
    {
        x*=f;
        y*=f;
        z*=f;
    }
    
    public float distance(RagPoint pnt)
    {
        float       px,py,pz;
        
        px=x-pnt.x;
        py=y-pnt.y;
        pz=z-pnt.z;
        return((float)Math.sqrt((px*px)+(py*py)+(pz*pz)));
    }
    
    public void normalize()
    {
        float       f;
        
        f=(float)Math.sqrt((x*x)+(y*y)+(z*z));
        if (f!=0.0f) f=1.0f/f;
        
        x*=f;
        y*=f;
        z*=f;
    }
    
    public void normalize2D()
    {
        float       f;
        
        f=(float)Math.sqrt((x*x)+(y*y));
        if (f!=0.0f) f=1.0f/f;
        
        x*=f;
        y*=f;
    }
    
    public float dot(RagPoint vct)
    {
        return((x*vct.x)+(y*vct.y)+(z*vct.z));
    }
    
    public void rotateAroundPoint(RagPoint centerPnt,RagPoint ang)
    {
        float       rd,sn,cs,rx,ry,rz;
        
        if (centerPnt!=null) {
            x-=centerPnt.x;
            y-=centerPnt.y;
            z-=centerPnt.z;
        }
        
            // rotate X
        
        if (ang.x!=0.0f) {
            rd=ang.x*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            ry=(y*cs)-(z*sn);
            rz=(y*sn)+(z*cs);

            y=ry;
            z=rz;
        }
        
            // rotate Y
        
        if (ang.y!=0.0f) {
            rd=ang.y*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            rx=(z*sn)+(x*cs);
            rz=(z*cs)-(x*sn);

            x=rx;
            z=rz;
        }
        
            // rotate Z
        
        if (ang.z!=0.0f) {
            rd=ang.z*((float)Math.PI/180.0f);
            sn=(float)Math.sin(rd);
            cs=(float)Math.cos(rd);

            rx=(x*cs)-(y*sn);
            ry=(x*sn)+(y*cs);

            x=rx;
            y=ry;
        }
        
        if (centerPnt!=null) {
            x+=centerPnt.x;
            y+=centerPnt.y;
            z+=centerPnt.z;
        }
    }
    
    public void rotateX(float fx)
    {
        float       rd,sn,cs,ry,rz;
        
        rd=fx*((float)Math.PI/180.0f);
        sn=(float)Math.sin(rd);
        cs=(float)Math.cos(rd);

        ry=(y*cs)-(z*sn);
        rz=(y*sn)+(z*cs);

        y=ry;
        z=rz;
    }
}
