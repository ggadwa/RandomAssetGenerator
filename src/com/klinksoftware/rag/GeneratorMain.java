package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;

public class GeneratorMain
{
    public static void run()
    {
        (new BitmapBase(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"base");
        (new BitmapBrick(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"brick");
        System.out.println("done!");
    }
}
