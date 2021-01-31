package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;

public class GeneratorMain
{
    public static void run()
    {
        System.out.println("start!");
        (new BitmapBase(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"base");
        (new BitmapBrick(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"brick");
        (new BitmapStone(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"stone");
        (new BitmapConcrete(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"concrete");
        (new BitmapTile(BitmapBase.COLOR_SCHEME_RANDOM)).generate(0,"tile");
        System.out.println("done!");
    }
}
