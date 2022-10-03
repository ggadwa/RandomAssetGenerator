package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.BitmapTest;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapGroup {

    private int textureSize;
    private HashMap<String, String> bitmapList;
    public ConcurrentHashMap<String, BitmapBase> loadedBitmaps;

    public BitmapGroup(int textureSize) {
        this.textureSize = textureSize;
        bitmapList = new HashMap<>();
        loadedBitmaps = new ConcurrentHashMap<>();
    }

    public void add(String name, String[] classNames) {
        if (!bitmapList.containsKey(name)) {
            bitmapList.put(name, ("com.klinksoftware.rag.bitmap.Bitmap" + classNames[AppWindow.random.nextInt(classNames.length)].replace(" ", "")));
        }
    }

    public void add(String name, String bitmapName) {
        if (!bitmapList.containsKey(name)) {
            bitmapList.put(name, ("com.klinksoftware.rag.bitmap.Bitmap" + bitmapName.replace(" ", "")));
        }
    }

    public void addPreloadedBitmap(String name, BitmapBase bitmap) {
        loadedBitmaps.put(name, bitmap);
    }

    public void generateAll() {
        Thread thread;
        ArrayList<Thread> threads;

        threads = new ArrayList<>();

        for (String name : bitmapList.keySet()) {
            thread = new Thread(new BitmapGeneratorThread(name));
            thread.start();
            threads.add(thread);
        }

        for (Thread thread2 : threads) {
            try {
                thread2.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getBitmapNames() {
        return (loadedBitmaps.keySet());
    }

    public BitmapBase getBitmap(String name) {
        return (loadedBitmaps.get(name));
    }

    private class BitmapGeneratorThread implements Runnable {

        private String name, bitmapName;
        //private ConcurrentHashMap<String,BitmapBase> loadedBitmaps;

        public BitmapGeneratorThread(String name) {
            this.name = name;
            //this.bitmapName=bitmapName;
            //this.loadedBitmaps=loadedBitmaps;
        }

        @Override
        public void run() {
            BitmapBase bitmap;

            try {
                bitmap = (BitmapBase) Class.forName(bitmapList.get(name)).getConstructor(int.class).newInstance(textureSize);
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = new BitmapTest(textureSize);
            }

            bitmap.generate();
            loadedBitmaps.put(name, bitmap);
        }

    }

}
