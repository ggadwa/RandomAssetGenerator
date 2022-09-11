package com.klinksoftware.rag.models;

import com.klinksoftware.rag.models.Limb;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.scene.Node;
import com.klinksoftware.rag.scene.Scene;
import com.klinksoftware.rag.utility.RagPoint;
import java.util.ArrayList;

public class SkeletonBuilder
{
    public static final int MODEL_TYPE_HUMANOID = 0;
    public static final int MODEL_TYPE_ANIMAL = 1;
    public static final int MODEL_TYPE_BLOB = 2;
    public static final int MODEL_TYPE_ROBOT = 3;

    public Scene scene;
    public ArrayList<Limb> limbs;

    public SkeletonBuilder(Scene scene) {
        this.scene = scene;
        limbs = new ArrayList<>();
    }

        //
        // leg limb
        //

    public void buildLimbLeg(int limbIdx, Node parentNode, float legRadius, float footLength, float footRadius, float footRot, int toeCount, float toeLength, float legOffset, float rotOffset, float scaleFactor) {
        int n;
        float legLength, toeRadius;
        Node hipNode, kneeNode, ankleNode, heelNode, footBackNode, footFrontNode, jointNode, toeNode;
        RagPoint hipPnt, kneePnt, anklePnt, heelPnt, footBackPnt, footFrontPnt;
        RagPoint jointPnt, toePnt, jointAdd, meshScale;

        // leg nodes
        legLength = parentNode.pnt.y;

        hipPnt = new RagPoint(0.0f, 0.0f, ((parentNode.limbRadius - legOffset) - legRadius));
        hipPnt.rotateY(rotOffset);
        hipNode = new Node(("hip_" + Integer.toString(limbIdx)), (legRadius * (1.0f + (AppWindow.random.nextFloat(0.3f)))), hipPnt);
        parentNode.addChild(hipNode);

        kneePnt = new RagPoint(0.0f, -(legLength * 0.4f), 0.0f);
        kneePnt.rotateY(rotOffset);
        kneeNode = new Node(("knee_" + Integer.toString(limbIdx)), legRadius, kneePnt);
        hipNode.addChild(kneeNode);

        anklePnt = new RagPoint(0.0f, -(legLength * 0.45f), 0.0f);
        anklePnt.rotateY(rotOffset);
        ankleNode = new Node(("ankle_" + Integer.toString(limbIdx)), legRadius, anklePnt);
        kneeNode.addChild(ankleNode);

        heelPnt = new RagPoint(0.0f, -ankleNode.getAbsolutePoint().y, 0.0f); // make sure we are at 0.0f
        heelPnt.rotateY(rotOffset);
        heelNode = new Node(("heel_" + Integer.toString(limbIdx)), legRadius, heelPnt);
        ankleNode.addChild(heelNode);

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        limbs.add(new Limb(("leg_top_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, hipNode, kneeNode));
        limbs.add(new Limb(("leg_bottom_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, kneeNode, ankleNode));
        limbs.add(new Limb(("ankle_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, ankleNode, heelNode));

        // the foot nodes
        // feet are always parallel to ground, towards front
        footBackPnt = new RagPoint(0.0f, 0.0f, -((legRadius * scaleFactor) + AppWindow.random.nextFloat(legRadius * 0.05f)));
        footBackPnt.rotateY(footRot);
        footBackNode = new Node(("foot_back_" + Integer.toString(limbIdx)), footRadius, footBackPnt);
        heelNode.addChild(footBackNode);

        footFrontPnt = new RagPoint(0.0f, 0.0f, footLength);
        footFrontPnt.rotateY(footRot);
        footFrontNode = new Node(("foot_front_" + Integer.toString(limbIdx)), footRadius, footFrontPnt);
        heelNode.addChild(footFrontNode);

        meshScale = new RagPoint(1.0f, (0.5f + AppWindow.random.nextFloat(0.4f)), scaleFactor);
        limbs.add(new Limb(("foot_" + Integer.toString(limbIdx)), "foot", Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, footBackNode, footFrontNode));

        // toe limbs
        if (toeCount == 0) {
            return;
        }
        if (toeCount == 1) {
            toeCount = 2;  // always at least two toes
        }
        toeRadius = (((footRadius * (1.5f + AppWindow.random.nextFloat(0.5f))) * meshScale.x) / (float) toeCount) * 0.5f;

        jointPnt = new RagPoint(-((((float) toeCount * (toeRadius * 2.0f)) * 0.5f) - toeRadius), 0.0f, 0.0f);
        jointPnt.rotateY(footRot);

        toePnt = new RagPoint(0.0f, 0.0f, toeLength);
        toePnt.rotateY(footRot);

        jointAdd = new RagPoint((toeRadius * 2.0f), 0.0f, 0.0f);
        jointAdd.rotateY(footRot);

        for (n = 0; n != toeCount; n++) {
            jointNode = new Node(("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), toeRadius, jointPnt);
            footFrontNode.addChild(jointNode);

            toeNode = new Node(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), toeRadius, toePnt);
            jointNode.addChild(toeNode);

            limbs.add(new Limb(("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "foot", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, footFrontNode, jointNode));
            limbs.add(new Limb(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "foot", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, jointNode, toeNode));

            jointPnt.addPoint(jointAdd);
        }
    }

        //
        // arm limb
        //

    public void buildLimbArm(int limbIdx, Node parentNode, float armRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float rotOffset, float scaleFactor) {
        int n, axis;
        float y, fingerRadius;
        Node shoulderNode, elbowNode, wristNode, palmNode, handNode, jointNode, fingerNode;
        RagPoint shoulderPnt, elbowPnt, wristPnt, palmPnt, handPnt;
        RagPoint jointPnt, fingerPnt, jointAdd, meshScale;

        // size and position around body
        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        // shoulder, elbow, wrist nodes
        y = parentNode.pnt.y;

        shoulderPnt = new RagPoint(0.0f, 0.0f, (parentNode.limbRadius * scaleFactor));
        shoulderPnt.rotateY(rotOffset);
        shoulderNode = new Node(("shoulder_" + Integer.toString(limbIdx)), (armRadius * 1.5f), shoulderPnt);
        parentNode.addChild(shoulderNode);

        elbowPnt = new RagPoint(0.0f, 0.0f, ((armLength * 0.5f) + (parentNode.limbRadius * (1.0f - scaleFactor))));    // extra from sinking shoulder into body
        elbowPnt.rotateY(rotOffset);
        elbowNode = new Node(("elbow_" + Integer.toString(limbIdx)), armRadius, elbowPnt);
        shoulderNode.addChild(elbowNode);

        wristPnt = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));
        wristPnt.rotateY(rotOffset);
        wristNode = new Node(("wrist_" + Integer.toString(limbIdx)), armRadius, wristPnt);
        elbowNode.addChild(wristNode);

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        limbs.add(new Limb(("arm_top_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderNode, elbowNode));
        limbs.add(new Limb(("arm_bottom_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, elbowNode, wristNode));

        // hand limb
        palmPnt = new RagPoint(0.0f, 0.0f, (handRadius * 0.2f));
        palmPnt.rotateY(rotOffset);
        palmNode = new Node(("palm_" + Integer.toString(limbIdx)), handRadius, palmPnt);
        wristNode.addChild(palmNode);

        handPnt = new RagPoint(0.0f, 0.0f, handRadius);
        handPnt.rotateY(rotOffset);
        handNode = new Node(("hand_" + Integer.toString(limbIdx)), handRadius, handPnt);
        palmNode.addChild(handNode);

        limbs.add(new Limb(("hand_top_" + Integer.toString(limbIdx)), "hand", Limb.MESH_TYPE_CYLINDER, axis, meshScale, wristNode, palmNode));
        limbs.add(new Limb(("hand_bottom_" + Integer.toString(limbIdx)), "hand", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, palmNode, handNode));

        // finger limbs
        if (fingerCount == 0) {
            return;
        }
        if (fingerCount == 1) {
            fingerCount = 2;  // always at least two fingers
        }
        fingerRadius = (((handRadius * (1.5f + AppWindow.random.nextFloat(0.5f))) * meshScale.y) / (float) fingerCount) * 0.5f;

        jointPnt = new RagPoint(0.0f, -((((float) fingerCount * (fingerRadius * 2.0f)) * 0.5f) - fingerRadius), 0.0f);
        jointPnt.rotateY(rotOffset);

        fingerPnt = new RagPoint(0.0f, 0.0f, fingerLength);
        fingerPnt.rotateY(rotOffset);

        jointAdd = new RagPoint(0.0f, (fingerRadius * 2.0f), 0.0f);

        for (n = 0; n != fingerCount; n++) {
            jointNode = new Node(("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), fingerRadius, jointPnt);
            handNode.addChild(jointNode);

            fingerNode = new Node(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), fingerRadius, fingerPnt);
            jointNode.addChild(fingerNode);

            limbs.add(new Limb(("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "hand", Limb.MESH_TYPE_CYLINDER, axis, meshScale, handNode, jointNode));
            limbs.add(new Limb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "hand", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, jointNode, fingerNode));

            jointPnt.addPoint(jointAdd);
        }
    }

        //
        // whip limbs
        //

    public void buildLimbWhip(int limbIdx, Node parentNode, float whipRadius, float whipLength, float rotOffset, float scaleFactor) {
        int axis;
        Node whip0Node, whip1Node, whip2Node, whip3Node;
        RagPoint pnt, meshScale;

        // size and position around body
        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.5f));
        pnt.rotateY(rotOffset);
        whip0Node = new Node(("whip_" + Integer.toString(limbIdx) + "_0"), whipRadius, pnt);
        parentNode.addChild(whip0Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.25f));
        pnt.rotateY(rotOffset);
        whip1Node = new Node(("whip_" + Integer.toString(limbIdx) + "_1"), (whipRadius * 0.8f), pnt);
        whip0Node.addChild(whip1Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.15f));
        pnt.rotateY(rotOffset);
        whip2Node = new Node(("whip_" + Integer.toString(limbIdx) + "_2"), (whipRadius * 0.6f), pnt);
        whip1Node.addChild(whip2Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.1f));
        pnt.rotateY(rotOffset);
        whip3Node = new Node(("whip_" + Integer.toString(limbIdx) + "_3"), (whipRadius * 0.3f), pnt);
        whip2Node.addChild(whip3Node);

        if (((rotOffset > 315) || (rotOffset < 45)) || ((rotOffset > 135) && (rotOffset < 225))) {
            axis = Limb.LIMB_AXIS_Z;
            meshScale = new RagPoint(scaleFactor, 1.0f, 1.0f);
        } else {
            axis = Limb.LIMB_AXIS_X;
            meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        }

        limbs.add(new Limb(("whip_start_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip0Node, whip1Node));
        limbs.add(new Limb(("whip_middle_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip1Node, whip2Node));
        limbs.add(new Limb(("whip_end_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, whip2Node, whip3Node));
    }

        //
        // head limb
        //

    public void buildLimbHeadAnimal(int modelType, int limbIdx, Node parentNode, float neckRadius, float headRadius, float scaleFactor) {
        float neckLength, headLength, jawLength, jawRadius;
        Node neckNode, headBottomNode, headMiddleNode, headTopNode, jawNode;
        RagPoint pnt, meshScale;

        // neck node
        neckLength = headRadius * (0.3f + (AppWindow.random.nextFloat(0.5f)));
        pnt = new RagPoint(0.0f, neckLength, 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(-AppWindow.random.nextFloat(15.0f));
        }

        neckNode = new Node(("neck_" + Integer.toString(limbIdx)), neckRadius, pnt);
        parentNode.addChild(neckNode);

        // the head nodes
        pnt = new RagPoint(0.0f, (headRadius * 0.5f), 0.0f);
        pnt.rotateX(-AppWindow.random.nextFloat(25.0f));
        pnt.rotateZ(AppWindow.random.nextFloat(10.0f) - 5.0f);

        headMiddleNode = new Node(("head_middle_" + Integer.toString(limbIdx)), headRadius, pnt);
        neckNode.addChild(headMiddleNode);

        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        pnt = new RagPoint(0.0f, 0.0f, -(headLength * 0.25f));
        pnt.rotateX(-AppWindow.random.nextFloat(5.0f));
        pnt.rotateZ(AppWindow.random.nextFloat(5.0f) - 2.5f);

        headBottomNode = new Node(("head_bottom_" + Integer.toString(limbIdx)), headRadius, pnt);
        headMiddleNode.addChild(headBottomNode);

        pnt = new RagPoint(0.0f, 0.0f, (headLength * 0.25f));
        pnt.rotateX(-AppWindow.random.nextFloat(25.0f));

        headRadius = headRadius * (0.2f + AppWindow.random.nextFloat(0.5f));
        headTopNode = new Node(("head_top_" + Integer.toString(limbIdx)), headRadius, pnt);
        headMiddleNode.addChild(headTopNode);

        // the jaw nodes
        jawLength = (headLength * 0.75f) * (0.7f + AppWindow.random.nextFloat(0.2f));
        jawRadius = headRadius * (0.5f + AppWindow.random.nextFloat(0.2f));

        pnt = new RagPoint(0.0f, jawRadius, jawLength);
        pnt.rotateX(-AppWindow.random.nextFloat(25.0f));

        jawNode = new Node(("jaw_" + Integer.toString(limbIdx)), jawRadius, pnt);
        headMiddleNode.addChild(jawNode);

        // the limb over the neck and head
        meshScale = new RagPoint(scaleFactor, (scaleFactor * 1.5f), 1.0f);

        limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckNode));
        limbs.add(new Limb(("jaw_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, neckNode, jawNode));
        limbs.add(new Limb(("head_bottom_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, Limb.LIMB_AXIS_Z, meshScale, headBottomNode, headMiddleNode));
        limbs.add(new Limb(("head_top_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, headMiddleNode, headTopNode));
    }

    public void buildLimbHeadNotAnimal(int modelType, int limbIdx, Node parentNode, float neckRadius, float headRadius, float scaleFactor) {
        float neckLength, headLength;
        boolean hasNeck;
        Node neckNode, headBottomNode, headMiddleNode, headTopNode;
        RagPoint pnt, meshScale;

        // blobs can sometimes have no heads
        if (modelType == MODEL_TYPE_BLOB) {
            if (AppWindow.random.nextFloat() < 0.5f) {
                return;
            }
        }

        // randomly eliminate necks
        hasNeck = (AppWindow.random.nextFloat() < 0.8f);
        neckNode = null;

        // neck node
        if (hasNeck) {
            neckLength = headRadius * (0.2f + (AppWindow.random.nextFloat(0.3f)));
            pnt = new RagPoint(0.0f, neckLength, 0.0f);
            if (modelType != MODEL_TYPE_ROBOT) {
                pnt.rotateX(-AppWindow.random.nextFloat(15.0f));
            }

            neckNode = new Node(("neck_" + Integer.toString(limbIdx)), neckRadius, pnt);
            parentNode.addChild(neckNode);
        }

        // the head nodes
        pnt = new RagPoint(0.0f, neckRadius, 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(-AppWindow.random.nextFloat(5.0f));
            pnt.rotateZ(AppWindow.random.nextFloat(5.0f) - 2.5f);
        }

        headBottomNode = new Node(("head_bottom_" + Integer.toString(limbIdx)), headRadius, pnt);
        if (hasNeck) {
            neckNode.addChild(headBottomNode);
        } else {
            parentNode.addChild(headBottomNode);
        }

        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        pnt = new RagPoint(0.0f, (headLength * 0.75f), 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(-AppWindow.random.nextFloat(25.0f));
            pnt.rotateZ(AppWindow.random.nextFloat(10.0f) - 5.0f);
        }

        headMiddleNode = new Node(("head_middle_" + Integer.toString(limbIdx)), headRadius, pnt);
        headBottomNode.addChild(headMiddleNode);

        pnt = new RagPoint(0.0f, (headLength * 0.25f), 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(-AppWindow.random.nextFloat(25.0f));
        }

        headRadius = headRadius * (0.2f + AppWindow.random.nextFloat(0.5f));
        headTopNode = new Node(("head_top_" + Integer.toString(limbIdx)), headRadius, pnt);
        headMiddleNode.addChild(headTopNode);

        // the limb over the neck and head
        if (modelType == MODEL_TYPE_ANIMAL) {
            meshScale = new RagPoint(scaleFactor, (scaleFactor * 1.5f), 1.0f);
        } else {
            meshScale = new RagPoint((scaleFactor * 1.5f), 1.0f, scaleFactor);
        }

        if (hasNeck) {
            limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckNode));
        }
        limbs.add(new Limb(("head_bottom_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, Limb.LIMB_AXIS_Y, meshScale, headBottomNode, headMiddleNode));
        limbs.add(new Limb(("head_top_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Y, meshScale, headMiddleNode, headTopNode));
    }

    public void buildLimbHead(int modelType, int limbIdx, Node parentNode, float neckRadius, float headRadius, float scaleFactor) {
        if (modelType == MODEL_TYPE_ANIMAL) {
            buildLimbHeadAnimal(modelType, limbIdx, parentNode, neckRadius, headRadius, scaleFactor);
        } else {
            buildLimbHeadNotAnimal(modelType, limbIdx, parentNode, neckRadius, headRadius, scaleFactor);
        }
    }

        //
        // general body
        //

    public void buildBody(int modelType, float hunchAng, float legRadius, float scaleFactor) {
        int axis;
        float hipHigh, hipRadius, radius;
        float minRadius, extraRadius;
        Node hipNode, waistNode, torsoBottomNode, torsoShoulderNode, torsoTopNode, buttNode, groinNode;
        RagPoint hipPnt, waistPnt, torsoBottomPnt, torsoShoulderPnt, torsoTopPnt, buttPnt, groinPnt;
        RagPoint meshScale;

        minRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        if (minRadius < (legRadius * 2.0f)) {
            minRadius = legRadius * (2.0f + AppWindow.random.nextFloat(1.0f));
        }
        extraRadius = minRadius * 0.8f;

        switch (modelType) {
            case MODEL_TYPE_ANIMAL:
                hipHigh = 0.5f + AppWindow.random.nextFloat(3.0f);
                hipRadius = minRadius;
                meshScale = new RagPoint(1.0f, scaleFactor, 1.0f);
                break;
            case MODEL_TYPE_BLOB:
                hipHigh = 0.0f;
                hipRadius = minRadius + (AppWindow.random.nextFloat(extraRadius * 2.0f));
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
            default:
                hipHigh = 1.0f + AppWindow.random.nextFloat(3.5f);
                hipRadius = minRadius;
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
        }

        // the spine
        hipPnt = new RagPoint(0, hipHigh, 0);
        hipNode = new Node("Hip", hipRadius, hipPnt);
        scene.rootNode.addChild(hipNode);

        waistPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.7f)), 0);
        if (hunchAng != 0.0f) {
            waistPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        waistNode = new Node("Waist", minRadius, waistPnt);
        hipNode.addChild(waistNode);

        torsoBottomPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoBottomPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoBottomNode = new Node("Torso_Bottom", radius, torsoBottomPnt);
        waistNode.addChild(torsoBottomNode);

        torsoShoulderPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoShoulderPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoShoulderNode = new Node("Torso_Shoulder", radius, torsoShoulderPnt);
        torsoBottomNode.addChild(torsoShoulderNode);

        torsoTopPnt = new RagPoint(0, (0.1f + AppWindow.random.nextFloat(0.2f)), 0);
        if (hunchAng != 0.0f) {
            torsoTopPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        radius = minRadius * (0.5f + AppWindow.random.nextFloat(0.2f));
        torsoTopNode = new Node("Torso_Top", radius, torsoTopPnt);
        torsoShoulderNode.addChild(torsoTopNode);

        // animals have extra butt
        buttNode = null;
        if (modelType == MODEL_TYPE_ANIMAL) {
            buttPnt = new RagPoint(0, 0, -(legRadius + (0.3f + AppWindow.random.nextFloat(0.3f))));
            radius = minRadius * (0.2f + (AppWindow.random.nextFloat(0.3f)));
            buttNode = new Node("Butt", radius, buttPnt);
            hipNode.addChild(buttNode);
        }

        // humanoids have groins
        groinNode = null;
        if (modelType == MODEL_TYPE_HUMANOID) {
            groinPnt = new RagPoint(0, -(0.2f + AppWindow.random.nextFloat(0.3f)), 0);
            radius = minRadius * (0.1f + (AppWindow.random.nextFloat(0.1f)));
            groinNode = new Node("Groin", radius, groinPnt);
            hipNode.addChild(groinNode);
        }

        // body limbs
        axis = (modelType == MODEL_TYPE_ANIMAL) ? Limb.LIMB_AXIS_Z : Limb.LIMB_AXIS_Y;

        limbs.add(new Limb("hip", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, axis, meshScale, hipNode, waistNode));
        limbs.add(new Limb("waist", "body", Limb.MESH_TYPE_CYLINDER, axis, meshScale, waistNode, torsoBottomNode));
        limbs.add(new Limb("torso_shoulder", "body", Limb.MESH_TYPE_CYLINDER, axis, meshScale, torsoBottomNode, torsoShoulderNode));
        limbs.add(new Limb("torso_top", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, torsoShoulderNode, torsoTopNode));

        if (buttNode != null) {
            limbs.add(new Limb("butt", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, hipNode, buttNode));
        }
        if (groinNode != null) {
            limbs.add(new Limb("groin", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, hipNode, groinNode));
        }
    }

        //
        // arms
        //

    private void buildArmsRandomSet(Node parentNode, int armCount, int limbNameOffset, float armRadius, float whipRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float scaleFactor) {
        int n;
        float ang;

        for (n=0;n!=armCount;n++) {
            ang = (float) (AppWindow.random.nextInt(4) * 90) + (25.0f - (AppWindow.random.nextFloat(50.0f)));
            ang = AppWindow.random.nextFloat(360.0f);
            if (AppWindow.random.nextFloat()<0.8f) {
                buildLimbArm((n + limbNameOffset), parentNode, armRadius, armLength, handRadius, fingerCount, fingerLength, ang, scaleFactor);
            }
            else {
                buildLimbWhip((n + limbNameOffset), parentNode, whipRadius, armLength, ang, scaleFactor);
            }
        }
    }

    private void buildArmsBilateralSet(Node parentNode, int limbNameOffset, float armRadius, float whipRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float scaleFactor) {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm((limbNameOffset + 1), parentNode, armRadius, armLength, handRadius, fingerCount, fingerLength, 90.0f, scaleFactor);
            buildLimbArm((limbNameOffset + 2), parentNode, armRadius, armLength, handRadius, fingerCount, fingerLength, 270.0f, scaleFactor);
        }
        else {
            buildLimbWhip((limbNameOffset + 1), parentNode, whipRadius, armLength, 90.0f, scaleFactor);
            buildLimbWhip((limbNameOffset + 2), parentNode, whipRadius, armLength, 270.0f, scaleFactor);
        }
    }

    private void buildArms(int modelType, boolean bilateral, float armRadius, float whipRadius, float scaleFactor) {
        int armCount, fingerCount;
        float armLength, handRadius, fingerLength;
        boolean topArms, midArms;
        Node node;

        // some settings
        armLength = 1.0f + AppWindow.random.nextFloat(1.5f);
        armCount = 1 + AppWindow.random.nextInt(3);
        handRadius = armRadius * (1.2f + (AppWindow.random.nextFloat(0.5f)));
        fingerCount = (modelType == MODEL_TYPE_BLOB) ? 0 : AppWindow.random.nextInt(5);
        fingerLength = handRadius * (0.2f + AppWindow.random.nextFloat(2.5f));

            // determine number of arms

        topArms=false;
        midArms=false;

        if (modelType == MODEL_TYPE_ANIMAL) {
            topArms=(AppWindow.random.nextFloat()<0.3f);
        }
        else {
            topArms=(AppWindow.random.nextFloat()<0.9f);
            midArms=(AppWindow.random.nextFloat()<0.2f);
        }

            // the arm pairs

        if (topArms) {
            node = scene.findNodeByName("Torso_Shoulder");

            if (!bilateral) {
                buildArmsRandomSet(node, armCount, 0, armRadius, whipRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
            else {
                buildArmsBilateralSet(node, 0, armRadius, whipRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
        }

        if (midArms) {
            node = scene.findNodeByName("Torso_Bottom");

            if (!bilateral) {
                buildArmsRandomSet(node, armCount, armCount, armRadius, whipRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
            else {
                buildArmsBilateralSet(node, 2, armRadius, whipRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
        }
    }

        //
        // legs
        //

    public void buildLegs(int modelType, float legRadius, float scaleFactor) {
        int toeCount;
        float footRot, footLength, footRadius, toeLength, legOffset, ang;
        Node node;

        // blobs have no legs
        if (modelType == MODEL_TYPE_BLOB) {
            return;
        }

        // some settings
        footRot = AppWindow.random.nextFloat(15.0f);
        footLength = legRadius + (AppWindow.random.nextFloat(legRadius * 2.0f));
        footRadius = legRadius + (legRadius * AppWindow.random.nextFloat(0.1f));
        toeCount = (modelType == MODEL_TYPE_ROBOT) ? 0 : AppWindow.random.nextInt(5);
        toeLength = footRadius * (0.2f + AppWindow.random.nextFloat(2.5f));
        legOffset = ((modelType == MODEL_TYPE_ROBOT) ? 0.7f : 0.5f) * legRadius;

            // hip legs

        node = scene.findNodeByName("Hip");
        ang = (modelType == MODEL_TYPE_ROBOT) ? 90.0f : (75.0f + AppWindow.random.nextFloat(30.0f));
        buildLimbLeg(1, node, legRadius, footLength, footRadius, footRot, toeCount, toeLength, legOffset, ang, scaleFactor);
        ang = (modelType == MODEL_TYPE_ROBOT) ? 270.0f : (255.0f + AppWindow.random.nextFloat(30.0f));
        buildLimbLeg(2, node, legRadius, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, ang, scaleFactor);

            // front legs

        if (modelType == MODEL_TYPE_ANIMAL) {
            node = scene.findNodeByName("Torso_Shoulder");
            ang = (modelType == MODEL_TYPE_ROBOT) ? 90.0f : (75.0f + AppWindow.random.nextFloat(30.0f));
            buildLimbLeg(3, node, legRadius, footLength, footRadius, footRot, toeCount, toeLength, legOffset, ang, scaleFactor);
            ang = (modelType == MODEL_TYPE_ROBOT) ? 270.0f : (255.0f + AppWindow.random.nextFloat(30.0f));
            buildLimbLeg(4, node, legRadius, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, 270.0f, scaleFactor);
        }
    }

        //
        // tails
        //

    public void buildTail(int modelType, float whipRadius, float scaleFactor) {
        float whipLength;
        Node node;

        // robots have no tails
        if (modelType == MODEL_TYPE_ROBOT) {
            return;
        }

        if (AppWindow.random.nextFloat()<0.7f) return;

        whipLength = 0.7f + AppWindow.random.nextFloat(1.0f);

        node = scene.findNodeByName("Hip");
        buildLimbWhip(5, node, whipRadius, whipLength, 180.0f, scaleFactor);
    }

        //
        // heads
        //

    public void buildHead(int modelType, float legRadius, float scaleFactor) {
        float headRadius, neckRadius;
        Node node;

        node = scene.findNodeByName("Torso_Top");

        headRadius = 0.5f + AppWindow.random.nextFloat(0.8f);
        if ((headRadius >= node.limbRadius) && (modelType != MODEL_TYPE_ANIMAL)) {
            headRadius *= (0.8f - AppWindow.random.nextFloat(0.1f));
        }

        neckRadius = headRadius * (0.8f - AppWindow.random.nextFloat(0.5f));

        buildLimbHead(modelType, 0, node, neckRadius, headRadius, scaleFactor);
    }

        //
        // build skeleton nodes
        //

    public void build(int modelType, boolean bilateral) {
        float hunchAng, scaleFactor, armRadius, legRadius, whipRadius;
        boolean thin;

        limbs = new ArrayList<>();

        thin = (AppWindow.random.nextFloat() < 0.75f);

        // skeleton hunch angle
        switch (modelType) {
            case MODEL_TYPE_ANIMAL:
                hunchAng = (70.0f + (AppWindow.random.nextFloat(40.0f)));
                scaleFactor = thin ? (0.4f + (AppWindow.random.nextFloat(0.4f))) : (0.6f + (AppWindow.random.nextFloat(0.4f)));
                break;
            case MODEL_TYPE_BLOB:
                hunchAng = AppWindow.random.nextFloat(20.0f) - 10.0f;
                scaleFactor = thin ? (0.4f + (AppWindow.random.nextFloat(0.4f))) : (0.8f + (AppWindow.random.nextFloat(0.2f)));
                break;
            case MODEL_TYPE_ROBOT:
                hunchAng = 0.0f;
                scaleFactor = thin ? (0.4f + (AppWindow.random.nextFloat(0.4f))) : (0.8f + (AppWindow.random.nextFloat(0.2f)));
                break;
            default:    // humanoid
                hunchAng = AppWindow.random.nextFloat(20.0f) - 5.0f;
                scaleFactor = thin ? (0.4f + (AppWindow.random.nextFloat(0.4f))) : (0.6f + (AppWindow.random.nextFloat(0.4f)));
                break;
        }

        // limb sizes
        armRadius = 0.2f + (AppWindow.random.nextFloat(0.2f));
        legRadius = armRadius + (AppWindow.random.nextFloat(0.2f));
        whipRadius = 0.2f + (AppWindow.random.nextFloat(0.2f));

        // build the skeleton
        buildBody(modelType, hunchAng, legRadius, scaleFactor);
        buildLegs(modelType, legRadius, scaleFactor);
        buildArms(modelType, bilateral, armRadius, whipRadius, scaleFactor);
        buildTail(modelType, whipRadius, scaleFactor);
        buildHead(modelType, legRadius, scaleFactor);
    }

}
