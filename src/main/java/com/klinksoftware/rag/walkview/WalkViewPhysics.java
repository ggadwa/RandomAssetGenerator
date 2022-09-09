package com.klinksoftware.rag.walkview;

import com.klinksoftware.rag.collision.Collision;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagMatrix4f;
import com.klinksoftware.rag.utility.RagPoint;

public class WalkViewPhysics {

    private static final float MOVE_SPEED = 0.2f;
    private static final float FAST_MOVE_SPEED = 0.6f;
    private static final float CENTER_ROTATE_MOVE_SPEED = 0.07f;
    private static final float MAX_GRAVITY = 0.15f;
    private static final float INITIAL_GRAVITY = 0.05f;
    private static final float GRAVITY_ADD = 0.001f;

    public boolean flyMode, speedFast;
    public float gravity, cameraFeetOffsetY;
    public RagPoint moveVector;
    private RagPoint rotMovePoint, walkPoint;
    private RagMatrix4f rotMatrix, rotMatrix2;
    private Collision collision;
    private WalkView view;

    public WalkViewPhysics(WalkView view) {
        this.view = view;

        flyMode = false;
        speedFast = false;
        gravity = INITIAL_GRAVITY;
        cameraFeetOffsetY = 0.0f;

        moveVector = new RagPoint(0.0f, 0.0f, 0.0f);
        rotMovePoint = new RagPoint(0.0f, 0.0f, 0.0f);
        walkPoint = new RagPoint(0.0f, 0.0f, 0.0f);

        rotMatrix = new RagMatrix4f();
        rotMatrix2 = new RagMatrix4f();

        collision = null;
    }

    public void setupCollision(Scene scene) {
        collision = new Collision();
        collision.buildFromScene(scene);
    }

    public void run(boolean cameraCenterRotate) {
        float origY;

        // xz movement
        if (moveVector.hasXZValues()) {
            rotMovePoint.setFromScaleNoY(moveVector, (speedFast ? FAST_MOVE_SPEED : MOVE_SPEED));
            rotMatrix.setRotationFromYAngle(view.cameraAngle.y);
            rotMatrix2.setRotationFromXAngle(view.cameraAngle.x);
            rotMatrix.multiply(rotMatrix2);
            rotMovePoint.matrixMultiply(rotMatrix);

            if (!flyMode) {
                collision.slideWithWall(view.cameraPoint, rotMovePoint);
            } else {
                view.cameraPoint.addPoint(rotMovePoint);
            }
        }

        // y movement
        if (moveVector.y != 0.0f) {
            view.cameraPoint.y += (moveVector.y * (speedFast ? FAST_MOVE_SPEED : MOVE_SPEED));
        }

        // center rotate just zooms
        if (cameraCenterRotate) {
            view.cameraRotateDistance -= (moveVector.z * CENTER_ROTATE_MOVE_SPEED);
            return;
        }

        // any gravity
        // we run it at the feet offset so the camera is at eye level
        if ((!flyMode) && (moveVector.y <= 0.0f)) {
            origY = view.cameraPoint.y;

            // move down with gravity
            walkPoint.setFromValues(view.cameraPoint.x, (view.cameraPoint.y - cameraFeetOffsetY), view.cameraPoint.z);
            walkPoint.y -= gravity;

            // collide with floor
            collision.collideWithFloor(walkPoint);
            view.cameraPoint.y = walkPoint.y + cameraFeetOffsetY;

            // turn off gravity if no movement
            if (origY == view.cameraPoint.y) {
                gravity = INITIAL_GRAVITY;
            } else {
                gravity = Math.max((gravity + GRAVITY_ADD), MAX_GRAVITY);
            }
        } else {
            gravity = INITIAL_GRAVITY;
        }

    }

}
