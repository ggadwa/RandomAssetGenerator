package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.utility.*;

import java.util.*;

public class Bone
{
    public int                          meshIdx;
    public final float radius;
    public final String                 name;
    public final RagPoint               pnt;
    public final ArrayList<Integer>     children;

    public Bone(String name, int meshIdx, float radius, RagPoint pnt)    {
        this.name=name;
        this.meshIdx=meshIdx;
        this.radius = radius;
        this.pnt=new RagPoint(pnt.x,pnt.y,pnt.z);

        children=new ArrayList<>();
    }

    public void addChild(int index)
    {
        children.add(index);
    }
}
