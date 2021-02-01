package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;

public class GeneratorMain
{
    public static void run()
    {
        System.out.println("start!");
        (new BitmapBrick(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapBrick.VARIATION_NONE,"brick");
        (new BitmapStone(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapStone.VARIATION_NONE,"stone");
        (new BitmapConcrete(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapConcrete.VARIATION_NONE,"concrete");
        (new BitmapTile(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapTile.VARIATION_NONE,"tile");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapWood.VARIATION_NONE,"wood");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapWood.VARIATION_BOX,"box");
        System.out.println("done!");
    }
}
