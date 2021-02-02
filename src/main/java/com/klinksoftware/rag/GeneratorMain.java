package com.klinksoftware.rag;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.export.*;

public class GeneratorMain
{
    public static void run()
    {
        System.out.println("start!");
        (new BitmapBrick(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapBrick.VARIATION_NONE,"brick");
        (new BitmapStone(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapStone.VARIATION_NONE,"stone");
        (new BitmapConcrete(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapConcrete.VARIATION_NONE,"concrete");
        (new BitmapTile(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapTile.VARIATION_NONE,"tile");
        (new BitmapMosaic(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapTile.VARIATION_NONE,"mosaic");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapWood.VARIATION_BOARDS,"wood");
        (new BitmapWood(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapWood.VARIATION_BOX,"box");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapMetal.VARIATION_PLATE,"metal_plate");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapMetal.VARIATION_BOX,"metal_box");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapMetal.VARIATION_PIPE,"metal_pipe");
        (new BitmapMetal(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapMetal.VARIATION_HEXAGON,"metal_hexagon");
        (new BitmapComputer(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapComputer.VARIATION_COMPUTER_BANK,"computer_bank");
        (new BitmapComputer(BitmapBase.COLOR_SCHEME_RANDOM)).generate(BitmapComputer.VARIATION_CONTROL_PANEL,"control_panel");
        
        (new Export()).export("test");
        System.out.println("done!");
    }
}
