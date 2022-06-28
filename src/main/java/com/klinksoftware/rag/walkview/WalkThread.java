package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.AppWindow;
import java.util.concurrent.TimeUnit;

public class WalkThread implements Runnable {

    private AppWindow appWindow;

    public WalkThread(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    public void run() {
        while (true) {
            // render
            appWindow.walkView.render();

            // have we triggered an exit?
            try {
                if (appWindow.glTerminate.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                    appWindow.walkView.doDisposeCanvas();
                    appWindow.glTerminated.release();
                    return;
                }
            } catch (InterruptedException e) {
            }
        }
    }

}
