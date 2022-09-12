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

    private int modelType;
    private boolean bilateral;
    private float bodyRadius, shoulderRadius, hipRadius, torsoRadius, headRadius;
    private float armRadius, legRadius, whipRadius;
    private float hunchAng, scaleFactor;

    public SkeletonBuilder(Scene scene, int modelType, boolean bilateral) {
        this.scene = scene;
        this.modelType = modelType;
        this.bilateral = bilateral;

        limbs = new ArrayList<>();
    }

    //
        // leg limb
        //

    public void buildLimbLeg(int limbIdx, Node parentNode, float footLength, float footRadius, float footRot, int toeCount, float toeLength, float legOffset, float rotOffset) {
        int n;
        float topLegRadius, legLength, toeRadius;
        Node hipNode, kneeNode, ankleNode, heelNode, footNode, jointNode, toeNode;
        RagPoint hipPnt, kneePnt, anklePnt, heelPnt, footPnt;
        RagPoint jointPnt, toePnt, jointAdd, meshScale;

        // leg nodes
        legLength = parentNode.pnt.y;

        hipPnt = new RagPoint(0.0f, 0.0f, -legOffset);
        hipPnt.rotateY(rotOffset);
        hipNode = new Node(("hip_" + Integer.toString(limbIdx)), hipPnt);
        parentNode.addChild(hipNode);

        kneePnt = new RagPoint(0.0f, -(legLength * 0.5f), 0.0f);
        kneeNode = new Node(("knee_" + Integer.toString(limbIdx)), kneePnt);
        hipNode.addChild(kneeNode);

        anklePnt = new RagPoint(0.0f, -kneeNode.getAbsolutePoint().y, 0.0f);
        ankleNode = new Node(("ankle_" + Integer.toString(limbIdx)), anklePnt);
        kneeNode.addChild(ankleNode);

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        topLegRadius = legRadius * (1.0f + (AppWindow.random.nextFloat(0.3f)));
        limbs.add(new Limb(("thigh_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, hipNode, topLegRadius, kneeNode, legRadius));
        limbs.add(new Limb(("calf_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, kneeNode, legRadius, ankleNode, legRadius));

        // the foot nodes
        // feet are always parallel to ground, towards front
        heelPnt = new RagPoint(0.0f, 0.0f, -((legRadius * scaleFactor) + AppWindow.random.nextFloat(legRadius * 0.05f)));
        heelPnt.rotateY(footRot);
        heelNode = new Node(("heel_" + Integer.toString(limbIdx)), heelPnt);
        ankleNode.addChild(heelNode);

        footPnt = new RagPoint(0.0f, 0.0f, footLength);
        footPnt.rotateY(footRot);
        footNode = new Node(("foot_" + Integer.toString(limbIdx)), footPnt);
        ankleNode.addChild(footNode);

        meshScale = new RagPoint(1.0f, (0.5f + AppWindow.random.nextFloat(0.4f)), scaleFactor);

        limbs.add(new Limb(("foot_" + Integer.toString(limbIdx)), "foot", Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, heelNode, footRadius, footNode, footRadius));

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
            jointNode = new Node(("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), jointPnt);
            footNode.addChild(jointNode);

            toeNode = new Node(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), toePnt);
            jointNode.addChild(toeNode);

            limbs.add(new Limb(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "foot", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, jointNode, toeRadius, toeNode, toeRadius));
            jointPnt.addPoint(jointAdd);
        }
    }

        //
        // arm limb
        //

    public void buildLimbArm(int limbIdx, Node parentNode, float armLength, float handRadius, int fingerCount, float fingerLength, float rotOffset) {
        int n, axis;
        float shoulderArmRadius, fingerRadius;
        Node shoulderNode, elbowNode, wristNode, handNode, jointNode, fingerNode;
        RagPoint shoulderPnt, elbowPnt, wristPnt, handPnt;
        RagPoint jointPnt, fingerPnt, jointAdd, meshScale;

        // size and position around body
        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        // shoulder, elbow, wrist nodes

        shoulderPnt = new RagPoint(0.0f, -armRadius, (shoulderRadius - armRadius));
        shoulderPnt.rotateY(rotOffset);
        shoulderNode = new Node(("shoulder_" + Integer.toString(limbIdx)), shoulderPnt);
        parentNode.addChild(shoulderNode);

        elbowPnt = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));    // extra from sinking shoulder into body
        elbowPnt.rotateY(rotOffset);
        elbowNode = new Node(("elbow_" + Integer.toString(limbIdx)), elbowPnt);
        shoulderNode.addChild(elbowNode);

        wristPnt = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));
        wristPnt.rotateY(rotOffset);
        wristNode = new Node(("wrist_" + Integer.toString(limbIdx)), wristPnt);
        elbowNode.addChild(wristNode);

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        shoulderArmRadius = armRadius * (1.0f + AppWindow.random.nextFloat(0.1f));

        limbs.add(new Limb(("upper_arm_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderNode, shoulderArmRadius, elbowNode, armRadius));
        limbs.add(new Limb(("lower_arm_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, elbowNode, armRadius, wristNode, armRadius));

        // hand limb
        handPnt = new RagPoint(0.0f, 0.0f, handRadius);
        handPnt.rotateY(rotOffset);
        handNode = new Node(("hand_" + Integer.toString(limbIdx)), handPnt);
        wristNode.addChild(handNode);

        limbs.add(new Limb(("hand_" + Integer.toString(limbIdx)), "hand", Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, axis, meshScale, wristNode, handRadius, handNode, handRadius));

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
        jointAdd.rotateY(rotOffset);

        for (n = 0; n != fingerCount; n++) {
            jointNode = new Node(("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), jointPnt);
            handNode.addChild(jointNode);

            fingerNode = new Node(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), fingerPnt);
            jointNode.addChild(fingerNode);

            limbs.add(new Limb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "hand", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, jointNode, fingerRadius, fingerNode, fingerRadius));

            jointPnt.addPoint(jointAdd);
        }
    }

        //
        // whip limbs
        //

    public void buildLimbWhip(int limbIdx, Node parentNode, float whipLength, float whipOffset, float rotOffset) {
        int axis;
        Node whip0Node, whip1Node, whip2Node, whip3Node;
        RagPoint pnt, meshScale;

        // size and position around body
        pnt = new RagPoint(0.0f, -whipRadius, (whipOffset - whipRadius));
        pnt.rotateY(rotOffset);
        whip0Node = new Node(("whip_" + Integer.toString(limbIdx) + "_0"), pnt);
        parentNode.addChild(whip0Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.6f));
        pnt.rotateY(rotOffset);
        whip1Node = new Node(("whip_" + Integer.toString(limbIdx) + "_1"), pnt);
        whip0Node.addChild(whip1Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.3f));
        pnt.rotateY(rotOffset);
        whip2Node = new Node(("whip_" + Integer.toString(limbIdx) + "_2"), pnt);
        whip1Node.addChild(whip2Node);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.1f));
        pnt.rotateY(rotOffset);
        whip3Node = new Node(("whip_" + Integer.toString(limbIdx) + "_3"), pnt);
        whip2Node.addChild(whip3Node);

        if (((rotOffset > 315) || (rotOffset < 45)) || ((rotOffset > 135) && (rotOffset < 225))) {
            axis = Limb.LIMB_AXIS_Z;
            meshScale = new RagPoint(scaleFactor, 1.0f, 1.0f);
        } else {
            axis = Limb.LIMB_AXIS_X;
            meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        }

        limbs.add(new Limb(("whip_start_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip0Node, whipRadius, whip1Node, (whipRadius * 0.8f)));
        limbs.add(new Limb(("whip_middle_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip1Node, (whipRadius * 0.8f), whip2Node, (whipRadius * 0.6f)));
        limbs.add(new Limb(("whip_end_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, whip2Node, (whipRadius * 0.6f), whip3Node, (whipRadius * 0.3f)));
    }

        //
        // head limb
        //

    public void buildLimbHeadAnimal(int limbIdx) {
        float neckRadius, headLength, jawLength, jawRadius, rotX;
        Node parentNode, headBottomNode, headMiddleNode, headTopNode, jawNode;
        RagPoint pnt, meshScale;

        parentNode = scene.findNodeByName("Torso_Shoulder");
        neckRadius = headRadius * (0.3f + AppWindow.random.nextFloat(0.3f));

        // the head nodes
        pnt = new RagPoint(0.0f, ((headRadius * 0.5f) + bodyRadius), 0.0f);
        pnt.rotateX(AppWindow.random.nextFloat(50.0f) - 25.0f);
        pnt.rotateZ(AppWindow.random.nextFloat(10.0f) - 5.0f);

        headMiddleNode = new Node(("head_middle_" + Integer.toString(limbIdx)), pnt);
        parentNode.addChild(headMiddleNode);

        rotX = AppWindow.random.nextFloat(20.0f) - 10.0f;
        headLength = headRadius * (1.5f + (AppWindow.random.nextFloat(0.6f)));

        pnt = new RagPoint(0.0f, 0.0f, -(headLength * 0.25f));
        pnt.rotateX(rotX);
        pnt.rotateZ(AppWindow.random.nextFloat(5.0f) - 2.5f);

        headBottomNode = new Node(("head_bottom_" + Integer.toString(limbIdx)), pnt);
        headMiddleNode.addChild(headBottomNode);

        pnt = new RagPoint(0.0f, 0.0f, (headLength * 0.75f));
        pnt.rotateX(-rotX);

        headRadius = headRadius * (0.2f + AppWindow.random.nextFloat(0.5f));
        headTopNode = new Node(("head_top_" + Integer.toString(limbIdx)), pnt);
        headMiddleNode.addChild(headTopNode);

        // the jaw nodes
        jawLength = (headLength * 0.75f) * (0.7f + AppWindow.random.nextFloat(0.2f));
        jawRadius = headRadius * (0.5f + AppWindow.random.nextFloat(0.2f));

        pnt = new RagPoint(0.0f, -jawRadius, jawLength);
        pnt.rotateX(AppWindow.random.nextFloat(25.0f));

        jawNode = new Node(("jaw_" + Integer.toString(limbIdx)), pnt);
        headMiddleNode.addChild(jawNode);

        // the limb over the neck and head
        meshScale = new RagPoint(scaleFactor, (scaleFactor * 1.5f), 1.0f);

        limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckRadius, headMiddleNode, neckRadius));
        limbs.add(new Limb(("jaw_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, headMiddleNode, jawRadius, jawNode, jawRadius));
        limbs.add(new Limb(("head_bottom_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, Limb.LIMB_AXIS_Z, meshScale, headBottomNode, headRadius, headMiddleNode, headRadius));
        limbs.add(new Limb(("head_top_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, headMiddleNode, headRadius, headTopNode, headRadius));
    }

    public void buildLimbHeadNotAnimal(int limbIdx) {
        float neckRadius, headLength, headTopRadius;
        Node parentNode, headBottomNode, headTopNode;
        RagPoint pnt, meshScale;

        // blobs can sometimes have no heads
        if (modelType == MODEL_TYPE_BLOB) {
            if (AppWindow.random.nextFloat() < 0.5f) {
                return;
            }
        }

        parentNode = scene.findNodeByName("Torso_Top");
        neckRadius = headRadius * (0.2f + AppWindow.random.nextFloat(0.3f));

        // the head nodes
        pnt = new RagPoint(0.0f, neckRadius, 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(AppWindow.random.nextFloat(10.0f) - 5.0f);
            pnt.rotateZ(AppWindow.random.nextFloat(5.0f) - 2.5f);
        }

        headBottomNode = new Node(("head_bottom_" + Integer.toString(limbIdx)), pnt);
        parentNode.addChild(headBottomNode);

        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        pnt = new RagPoint(0.0f, headLength, 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(AppWindow.random.nextFloat(50.0f) - 25.0f);
            pnt.rotateZ(AppWindow.random.nextFloat(10.0f) - 5.0f);
        }

        headTopNode = new Node(("head_top_" + Integer.toString(limbIdx)), pnt);
        headBottomNode.addChild(headTopNode);

        // the limb over the neck and head
        if (modelType == MODEL_TYPE_ANIMAL) {
            meshScale = new RagPoint(scaleFactor, (scaleFactor * 1.5f), 1.0f);
        } else {
            meshScale = new RagPoint((scaleFactor * 1.5f), 1.0f, scaleFactor);
        }

        headTopRadius = headRadius * (0.9f + AppWindow.random.nextFloat(0.1f));

        limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckRadius, headBottomNode, neckRadius));
        limbs.add(new Limb(("head_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, Limb.LIMB_AXIS_Y, meshScale, headBottomNode, headRadius, headTopNode, headTopRadius));
    }

    //
        // general body
        //

    public void buildBody() {
        int axis;
        float hipHigh, topRadius, buttRadius, groinRadius;
        Node hipNode, waistNode, torsoBottomNode, torsoShoulderNode, torsoTopNode, buttNode, groinNode;
        RagPoint hipPnt, waistPnt, torsoBottomPnt, torsoShoulderPnt, torsoTopPnt, buttPnt, groinPnt;
        RagPoint meshScale;

        switch (modelType) {
            case MODEL_TYPE_ANIMAL:
                hipHigh = 0.5f + AppWindow.random.nextFloat(3.0f);
                hipRadius = bodyRadius;
                meshScale = new RagPoint(1.0f, scaleFactor, 1.0f);
                break;
            case MODEL_TYPE_BLOB:
                hipHigh = 0.0f;
                hipRadius = bodyRadius + (AppWindow.random.nextFloat(bodyRadius * 0.5f));
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
            default:
                hipHigh = 1.0f + AppWindow.random.nextFloat(3.5f);
                hipRadius = bodyRadius;
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
        }

        // the spine
        hipPnt = new RagPoint(0, hipHigh, 0);
        hipNode = new Node("Hip", hipPnt);
        scene.rootNode.addChild(hipNode);

        waistPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.7f)), 0);
        if (hunchAng != 0.0f) {
            waistPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        waistNode = new Node("Waist", waistPnt);
        hipNode.addChild(waistNode);

        torsoBottomPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoBottomPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoBottomNode = new Node("Torso_Bottom", torsoBottomPnt);
        waistNode.addChild(torsoBottomNode);

        torsoShoulderPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoShoulderPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoShoulderNode = new Node("Torso_Shoulder", torsoShoulderPnt);
        torsoBottomNode.addChild(torsoShoulderNode);

        torsoTopPnt = new RagPoint(0, (0.3f + AppWindow.random.nextFloat(0.3f)), 0);
        if (hunchAng != 0.0f) {
            torsoTopPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        topRadius = bodyRadius * (0.5f + AppWindow.random.nextFloat(0.2f));
        torsoTopNode = new Node("Torso_Top", torsoTopPnt);
        torsoShoulderNode.addChild(torsoTopNode);

        // body limbs
        axis = (modelType == MODEL_TYPE_ANIMAL) ? Limb.LIMB_AXIS_Z : Limb.LIMB_AXIS_Y;

        limbs.add(new Limb("hip", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, axis, meshScale, hipNode, hipRadius, waistNode, bodyRadius));
        limbs.add(new Limb("waist", "body", Limb.MESH_TYPE_CYLINDER, axis, meshScale, waistNode, bodyRadius, torsoBottomNode, shoulderRadius));
        limbs.add(new Limb("torso_shoulder", "body", Limb.MESH_TYPE_CYLINDER, axis, meshScale, torsoBottomNode, shoulderRadius, torsoShoulderNode, shoulderRadius));
        limbs.add(new Limb("torso_top", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, torsoShoulderNode, shoulderRadius, torsoTopNode, topRadius));

        // animals have extra butt
        if (modelType == MODEL_TYPE_ANIMAL) {
            buttPnt = new RagPoint(0, 0, -(legRadius + (0.3f + AppWindow.random.nextFloat(0.3f))));
            buttRadius = bodyRadius * (0.2f + (AppWindow.random.nextFloat(0.3f)));
            buttNode = new Node("Butt", buttPnt);
            hipNode.addChild(buttNode);
            limbs.add(new Limb("butt", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, hipNode, hipRadius, buttNode, buttRadius));
        }

        // humaniods have groin
        if (modelType == MODEL_TYPE_HUMANOID) {
            groinPnt = new RagPoint(0, -(0.2f + AppWindow.random.nextFloat(0.3f)), 0);
            groinRadius = bodyRadius * (0.1f + (AppWindow.random.nextFloat(0.1f)));
            groinNode = new Node("Groin", groinPnt);
            hipNode.addChild(groinNode);
            limbs.add(new Limb("groin", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, hipNode, hipRadius, groinNode, groinRadius));
        }
    }

        //
        // arms
        //

    private void buildArmsRandomSet(Node parentNode, int armCount, int limbNameOffset, float armLength, float handRadius, int fingerCount, float fingerLength) {
        int n;
        float ang;

        for (n = 0; n != armCount; n++) {
            ang = AppWindow.random.nextFloat(360.0f);
            if (AppWindow.random.nextFloat()<0.8f) {
                buildLimbArm((n + limbNameOffset), parentNode, armLength, handRadius, fingerCount, fingerLength, ang);
            }
            else {
                buildLimbWhip((n + limbNameOffset), parentNode, armLength, shoulderRadius, ang);
            }
        }
    }

    private void buildArmsBilateralSet(Node parentNode, int limbNameOffset, float armLength, float handRadius, int fingerCount, float fingerLength) {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm((limbNameOffset + 1), parentNode, armLength, handRadius, fingerCount, fingerLength, 90.0f);
            buildLimbArm((limbNameOffset + 2), parentNode, armLength, handRadius, fingerCount, fingerLength, 270.0f);
        }
        else {
            buildLimbWhip((limbNameOffset + 1), parentNode, armLength, shoulderRadius, 90.0f);
            buildLimbWhip((limbNameOffset + 2), parentNode, armLength, shoulderRadius, 270.0f);
        }
    }

    private void buildArms() {
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
                buildArmsRandomSet(node, armCount, 0, armLength, handRadius, fingerCount, fingerLength);
            }
            else {
                buildArmsBilateralSet(node, 0, armLength, handRadius, fingerCount, fingerLength);
            }
        }

        if (midArms) {
            node = scene.findNodeByName("Torso_Bottom");

            if (!bilateral) {
                buildArmsRandomSet(node, armCount, armCount, armLength, handRadius, fingerCount, fingerLength);
            }
            else {
                buildArmsBilateralSet(node, 2, armLength, handRadius, fingerCount, fingerLength);
            }
        }
    }

        //
        // legs
        //

    public void buildLegs() {
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

            // hip legs

        node = scene.findNodeByName("Hip");
        legOffset = (modelType != MODEL_TYPE_ROBOT) ? (hipRadius - (legRadius * 1.1f)) : (hipRadius - (legRadius * 1.5f));
        ang = (modelType == MODEL_TYPE_ROBOT) ? 90.0f : (75.0f + AppWindow.random.nextFloat(30.0f));
        buildLimbLeg(1, node, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, ang);
        ang = (modelType == MODEL_TYPE_ROBOT) ? 270.0f : (255.0f + AppWindow.random.nextFloat(30.0f));
        buildLimbLeg(2, node, footLength, footRadius, footRot, toeCount, toeLength, legOffset, ang);

            // front legs

        if (modelType == MODEL_TYPE_ANIMAL) {
            node = scene.findNodeByName("Torso_Shoulder");
            legOffset = (modelType != MODEL_TYPE_ROBOT) ? (shoulderRadius - (legRadius * 1.1f)) : (shoulderRadius - (legRadius * 1.5f));
            ang = (modelType == MODEL_TYPE_ROBOT) ? 90.0f : (75.0f + AppWindow.random.nextFloat(30.0f));
            buildLimbLeg(3, node, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, ang);
            ang = (modelType == MODEL_TYPE_ROBOT) ? 270.0f : (255.0f + AppWindow.random.nextFloat(30.0f));
            buildLimbLeg(4, node, footLength, footRadius, footRot, toeCount, toeLength, legOffset, ang);
        }
    }

        //
        // tails
        //

    public void buildTail() {
        float whipLength;
        Node node;

        // robots have no tails, or random no tail
        if ((modelType == MODEL_TYPE_ROBOT) || (AppWindow.random.nextFloat() < 0.7f)) {
            return;
        }

        whipLength = 0.7f + AppWindow.random.nextFloat(1.0f);

        node = scene.findNodeByName("Hip");
        buildLimbWhip(5, node, whipLength, hipRadius, 180.0f);
    }

        //
        // heads
        //

    public void buildHead() {
        if (modelType == MODEL_TYPE_ANIMAL) {
            buildLimbHeadAnimal(0);
        } else {
            buildLimbHeadNotAnimal(0);
        }
    }

        //
        // build skeleton nodes
        //

    public void build() {
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
        bodyRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        if (bodyRadius < (legRadius * 2.0f)) {
            bodyRadius = legRadius * (2.0f + AppWindow.random.nextFloat(0.3f));
        }
        shoulderRadius = bodyRadius * (1.1f + AppWindow.random.nextFloat(0.2f));
        headRadius = 0.3f + AppWindow.random.nextFloat(1.3f);
        if (headRadius > bodyRadius) {
            headRadius = bodyRadius;
        }

        // build the skeleton
        buildBody();
        buildLegs();
        buildArms();
        buildTail();
        buildHead();
    }

}
