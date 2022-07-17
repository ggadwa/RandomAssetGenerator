package com.klinksoftware.rag.collision;

import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshList;
import java.util.ArrayList;

public class Collision {

    private ArrayList<CollisionTrig> trigs;

    public Collision() {
        trigs = new ArrayList<>();
    }

    private void addMeshTrigsToCollision(Mesh mesh) {

    }

    public void buildFromMeshList(MeshList meshList) {
        int n, nMesh;

        trigs.clear();

        nMesh = meshList.count();
        for (n = 0; n != nMesh; n++) {
            addMeshTrigsToCollision(meshList.get(n));
        }
    }
}
