package com.klinksoftware.rag.scene;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.bitmap.BitmapTest;
import com.klinksoftware.rag.bitmap.utility.BitmapBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapGroup {

    private HashMap<String, String> bitmapList;
    public ConcurrentHashMap<String, BitmapBase> loadedBitmaps;

    public BitmapGroup() {
        bitmapList = new HashMap<>();
        loadedBitmaps = new ConcurrentHashMap<>();
    }

    public void add(String name, String[] classNames) {
        if (!bitmapList.containsKey(name)) {
            bitmapList.put(name, ("com.klinksoftware.rag.bitmap." + classNames[AppWindow.random.nextInt(classNames.length)].replace(" ", "")));
        }
    }

    public void add(String name, String bitmapName) {
        if (!bitmapList.containsKey(name)) {
            bitmapList.put(name, ("com.klinksoftware.rag.bitmap." + bitmapName.replace(" ", "")));
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

        private String name;

        public BitmapGeneratorThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            BitmapBase bitmap;

            try {
                bitmap = (BitmapBase) Class.forName(bitmapList.get(name)).getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = new BitmapTest();
            }

            bitmap.generate();
            loadedBitmaps.put(name, bitmap);
        }

    }

}
