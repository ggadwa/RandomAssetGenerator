package com.klinksoftware.rag.model.utility;

import com.klinksoftware.rag.model.utility.Limb;
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
        hipNode = scene.addChildNodeAndJoint(parentNode, ("hip_" + Integer.toString(limbIdx)), hipPnt);

        kneePnt = new RagPoint(0.0f, -(legLength * 0.5f), 0.0f);
        kneeNode = scene.addChildNodeAndJoint(hipNode, ("knee_" + Integer.toString(limbIdx)), kneePnt);

        anklePnt = new RagPoint(0.0f, -kneeNode.getAbsolutePoint().y, 0.0f);
        ankleNode = scene.addChildNodeAndJoint(kneeNode, ("ankle_" + Integer.toString(limbIdx)), anklePnt);

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        topLegRadius = legRadius * (1.0f + (AppWindow.random.nextFloat(0.3f)));
        limbs.add(new Limb(("thigh_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, hipNode, topLegRadius, kneeNode, legRadius));
        limbs.add(new Limb(("calf_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, kneeNode, legRadius, ankleNode, legRadius));

        // the foot nodes
        // feet are always parallel to ground, towards front
        heelPnt = new RagPoint(0.0f, 0.0f, -((legRadius * scaleFactor) + AppWindow.random.nextFloat(legRadius * 0.05f)));
        heelPnt.rotateY(footRot);
        heelNode = scene.addChildNodeAndJoint(ankleNode, ("heel_" + Integer.toString(limbIdx)), heelPnt);

        footPnt = new RagPoint(0.0f, 0.0f, footLength);
        footPnt.rotateY(footRot);
        footNode = scene.addChildNodeAndJoint(ankleNode, ("foot_" + Integer.toString(limbIdx)), footPnt);

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
            jointNode = scene.addChildNodeAndJoint(footNode, ("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), jointPnt);
            toeNode = scene.addChildNodeAndJoint(jointNode, ("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), toePnt);

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
        RagPoint jointPnt, fingerPnt, jointAdd, meshScale, rotAngle, globeRadius;

        // size and position around body
        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        // shoulder, elbow, wrist nodes
        if (modelType != MODEL_TYPE_ANIMAL) {
            shoulderPnt = new RagPoint(0.0f, -armRadius, (shoulderRadius - armRadius));
        } else {
            shoulderPnt = new RagPoint(0.0f, 0.0f, (shoulderRadius - armRadius));
        }
        shoulderPnt.rotateY(rotOffset);
        shoulderNode = scene.addChildNodeAndJoint(parentNode, ("shoulder_" + Integer.toString(limbIdx)), shoulderPnt);

        elbowPnt = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));    // extra from sinking shoulder into body
        elbowPnt.rotateY(rotOffset);
        elbowNode = scene.addChildNodeAndJoint(shoulderNode, ("elbow_" + Integer.toString(limbIdx)), elbowPnt);

        wristPnt = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));
        wristPnt.rotateY(rotOffset);
        wristNode = scene.addChildNodeAndJoint(elbowNode, ("wrist_" + Integer.toString(limbIdx)), wristPnt);

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        shoulderArmRadius = armRadius * (1.0f + AppWindow.random.nextFloat(0.1f));

        limbs.add(new Limb(("upper_arm_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderNode, shoulderArmRadius, elbowNode, armRadius));
        limbs.add(new Limb(("lower_arm_" + Integer.toString(limbIdx)), "arm", Limb.MESH_TYPE_CYLINDER, axis, meshScale, elbowNode, armRadius, wristNode, armRadius));

        // hand limb
        handPnt = new RagPoint(0.0f, 0.0f, handRadius);
        handPnt.rotateY(rotOffset);
        handNode = scene.addChildNodeAndJoint(wristNode, ("hand_" + Integer.toString(limbIdx)), handPnt);

        globeRadius = new RagPoint((axis == Limb.LIMB_AXIS_Z ? armRadius : handRadius), handRadius, (axis == Limb.LIMB_AXIS_X ? armRadius : (handRadius * scaleFactor)));

        rotAngle = null;
        if (modelType != MODEL_TYPE_ROBOT) {
            rotAngle = new RagPoint((AppWindow.random.nextFloat(20.0f) - 10.0f), 0.0f, 0.0f);
        }

        limbs.add(new Limb(("hand_" + Integer.toString(limbIdx)), "hand", Limb.MESH_TYPE_GLOBE, wristNode, globeRadius, rotAngle));

        // finger limbs
        if (fingerCount == 0) {
            return;
        }
        if (fingerCount == 1) {
            fingerCount = 2;  // always at least two fingers
        }

        fingerRadius = (((handRadius * 0.9f) * meshScale.y) / (float) fingerCount) * 0.5f;

        jointPnt = new RagPoint(0.0f, -((((float) fingerCount * (fingerRadius * 2.0f)) * 0.5f) - fingerRadius), 0.0f);

        fingerPnt = new RagPoint(0.0f, 0.0f, fingerLength);
        fingerPnt.rotateY(rotOffset);

        jointAdd = new RagPoint(0.0f, (fingerRadius * 2.0f), 0.0f);
        jointAdd.rotateY(rotOffset);

        for (n = 0; n != fingerCount; n++) {
            jointNode = scene.addChildNodeAndJoint(handNode, ("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), jointPnt);
            fingerNode = scene.addChildNodeAndJoint(jointNode, ("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), fingerPnt);

            limbs.add(new Limb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "hand", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, jointNode, fingerRadius, fingerNode, fingerRadius));

            jointPnt.addPoint(jointAdd);
        }
    }

        //
        // whip limbs
        //

    public void buildLimbWhip(int limbIdx, Node parentNode, float whipLength, float rotOffset, boolean hasShoulder) {
        int axis;
        Node shoulderNode, whip0Node, whip1Node, whip2Node, whip3Node;
        RagPoint pnt, shoulderPnt, meshScale;

        shoulderNode = null;

        if (hasShoulder) {
            if (modelType != MODEL_TYPE_ANIMAL) {
                shoulderPnt = new RagPoint(0.0f, -whipRadius, (shoulderRadius - whipRadius));
            } else {
                shoulderPnt = new RagPoint(0.0f, 0.0f, (shoulderRadius - whipRadius));
            }
            shoulderPnt.rotateY(rotOffset);
            shoulderNode = scene.addChildNodeAndJoint(parentNode, ("shoulder_" + Integer.toString(limbIdx)), shoulderPnt);
            parentNode = shoulderNode;
        }

        // size and position around body
        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.3f));
        pnt.rotateY(rotOffset);
        whip0Node = scene.addChildNodeAndJoint(parentNode, ("whip_" + Integer.toString(limbIdx) + "_0"), pnt);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.3f));
        pnt.rotateY(rotOffset);
        whip1Node = scene.addChildNodeAndJoint(whip0Node, ("whip_" + Integer.toString(limbIdx) + "_1"), pnt);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.2f));
        pnt.rotateY(rotOffset);
        whip2Node = scene.addChildNodeAndJoint(whip1Node, ("whip_" + Integer.toString(limbIdx) + "_2"), pnt);

        pnt = new RagPoint(0.0f, 0.0f, (whipLength * 0.2f));
        pnt.rotateY(rotOffset);
        whip3Node = scene.addChildNodeAndJoint(whip2Node, ("whip_" + Integer.toString(limbIdx) + "_3"), pnt);

        if (((rotOffset > 315) || (rotOffset < 45)) || ((rotOffset > 135) && (rotOffset < 225))) {
            axis = Limb.LIMB_AXIS_Z;
            meshScale = new RagPoint(scaleFactor, ((modelType != MODEL_TYPE_ANIMAL) ? scaleFactor : 1.0f), 1.0f);
        } else {
            axis = Limb.LIMB_AXIS_X;
            meshScale = new RagPoint(1.0f, ((modelType != MODEL_TYPE_ANIMAL) ? scaleFactor : 1.0f), scaleFactor);
        }

        if (hasShoulder) {
            limbs.add(new Limb(("whip_start_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderNode, whipRadius, whip1Node, whipRadius));
        }
        limbs.add(new Limb(("whip_start_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip0Node, whipRadius, whip1Node, (whipRadius * 0.8f)));
        limbs.add(new Limb(("whip_middle_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip1Node, (whipRadius * 0.8f), whip2Node, (whipRadius * 0.6f)));
        limbs.add(new Limb(("whip_end_" + Integer.toString(limbIdx)), "leg", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, whip2Node, (whipRadius * 0.6f), whip3Node, (whipRadius * 0.3f)));
    }

        //
        // head limb
        //

    public void buildLimbHeadAnimal(int limbIdx) {
        float neckRadius, headLength, jawLength, jawRadius;
        Node parentNode, headNode, jawNode;
        RagPoint pnt, meshScale, globeRadius, rotAngle;

        parentNode = scene.findNodeByName("torso_shoulder");
        neckRadius = headRadius * (0.3f + AppWindow.random.nextFloat(0.3f));

        // the head nodes
        pnt = new RagPoint(0.0f, ((headRadius * 0.5f) + bodyRadius), 0.0f);
        pnt.rotateZ(AppWindow.random.nextFloat(10.0f) - 5.0f);

        headNode = scene.addChildNodeAndJoint(parentNode, ("head_" + Integer.toString(limbIdx)), pnt);

        headLength = headRadius * (1.5f + (AppWindow.random.nextFloat(0.6f)));

        // the jaw nodes
        jawLength = headLength * (0.4f + AppWindow.random.nextFloat(0.2f));
        jawRadius = headRadius * (0.3f + AppWindow.random.nextFloat(0.2f));

        pnt = new RagPoint(0.0f, -jawRadius, jawLength);
        pnt.rotateX(AppWindow.random.nextFloat(25.0f));

        jawNode = scene.addChildNodeAndJoint(headNode, ("jaw_" + Integer.toString(limbIdx)), pnt);

        // neck and jaw
        meshScale = new RagPoint(scaleFactor, (scaleFactor * 0.8f), 1.0f);

        limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckRadius, headNode, neckRadius));
        limbs.add(new Limb(("jaw_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, headNode, jawRadius, jawNode, jawRadius));

        // head limb
        globeRadius = new RagPoint((headRadius * (0.3f + AppWindow.random.nextFloat(0.5f))), headRadius, headLength);
        globeRadius.scale(scaleFactor);

        rotAngle = null;
        if (modelType != MODEL_TYPE_ROBOT) {
            rotAngle = new RagPoint((AppWindow.random.nextFloat(20.0f) - 10.0f), 0.0f, 0.0f);
        }

        limbs.add(new Limb(("head_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_GLOBE, headNode, globeRadius, rotAngle));
    }

    public void buildLimbHeadNotAnimal(int limbIdx) {
        float neckRadius, headLength;
        Node parentNode, headNode;
        RagPoint pnt, meshScale, globeRadius, rotAngle;

        // blobs can sometimes have no heads
        if (modelType == MODEL_TYPE_BLOB) {
            if (AppWindow.random.nextFloat() < 0.5f) {
                return;
            }
        }

        parentNode = scene.findNodeByName("torso_top");
        neckRadius = headRadius * (0.2f + AppWindow.random.nextFloat(0.3f));
        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        // the head nodes
        pnt = new RagPoint(0.0f, (neckRadius + (headLength * 0.5f)), 0.0f);
        if (modelType != MODEL_TYPE_ROBOT) {
            pnt.rotateX(AppWindow.random.nextFloat(10.0f) - 5.0f);
            pnt.rotateZ(AppWindow.random.nextFloat(5.0f) - 2.5f);
        }

        headNode = scene.addChildNodeAndJoint(parentNode, ("head_" + Integer.toString(limbIdx)), pnt);

        // neck limb
        meshScale = new RagPoint((scaleFactor * 1.5f), 1.0f, scaleFactor);
        limbs.add(new Limb(("neck_" + Integer.toString(limbIdx)), "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, parentNode, neckRadius, headNode, neckRadius));

        // head limb
        globeRadius = new RagPoint(headRadius, headLength, (headRadius * 0.8f));
        globeRadius.scale(scaleFactor);

        rotAngle = null;
        if (modelType != MODEL_TYPE_ROBOT) {
            rotAngle = new RagPoint((AppWindow.random.nextFloat(10.0f) - 5.0f), 0.0f, 0.0f);
        }

        limbs.add(new Limb(("head_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_GLOBE, headNode, globeRadius, rotAngle));
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
                hipHigh = 1.0f + AppWindow.random.nextFloat(2.0f);
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
        hipNode = scene.addChildNodeAndJoint(scene.rootNode, "hip", hipPnt);

        waistPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.7f)), 0);
        if (hunchAng != 0.0f) {
            waistPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        waistNode = scene.addChildNodeAndJoint(hipNode, "waist", waistPnt);

        torsoBottomPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoBottomPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoBottomNode = scene.addChildNodeAndJoint(waistNode, "torso_bottom", torsoBottomPnt);

        torsoShoulderPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.6f)), 0);
        if (hunchAng != 0.0f) {
            torsoShoulderPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoShoulderNode = scene.addChildNodeAndJoint(torsoBottomNode, "torso_shoulder", torsoShoulderPnt);

        if (modelType == MODEL_TYPE_ANIMAL) {
            torsoTopPnt = new RagPoint(0, (0.5f + AppWindow.random.nextFloat(0.4f)), 0);
            topRadius = shoulderRadius * (0.3f + AppWindow.random.nextFloat(0.2f));
        } else {
            torsoTopPnt = new RagPoint(0, (0.3f + AppWindow.random.nextFloat(0.3f)), 0);
            topRadius = shoulderRadius * (0.5f + AppWindow.random.nextFloat(0.2f));
        }
        if (hunchAng != 0.0f) {
            torsoTopPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoTopNode = scene.addChildNodeAndJoint(torsoShoulderNode, "torso_top", torsoTopPnt);

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
            buttNode = scene.addChildNodeAndJoint(hipNode, "butt", buttPnt);
            limbs.add(new Limb("butt", "body", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, hipNode, hipRadius, buttNode, buttRadius));
        }

        // humaniods have groin
        if (modelType == MODEL_TYPE_HUMANOID) {
            groinPnt = new RagPoint(0, -(0.2f + AppWindow.random.nextFloat(0.3f)), 0);
            groinRadius = bodyRadius * (0.1f + (AppWindow.random.nextFloat(0.1f)));
            groinNode = scene.addChildNodeAndJoint(hipNode, "groin", groinPnt);
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
                buildLimbWhip((n + limbNameOffset), parentNode, armLength, ang, true);
            }
        }
    }

    private void buildArmsBilateralSet(Node parentNode, int limbNameOffset, float armLength, float handRadius, int fingerCount, float fingerLength) {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm(limbNameOffset, parentNode, armLength, handRadius, fingerCount, fingerLength, 90.0f);
            buildLimbArm((limbNameOffset + 1), parentNode, armLength, handRadius, fingerCount, fingerLength, 270.0f);
        }
        else {
            buildLimbWhip(limbNameOffset, parentNode, armLength, 90.0f, true);
            buildLimbWhip((limbNameOffset + 1), parentNode, armLength, 270.0f, true);
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
            node = scene.findNodeByName("torso_shoulder");

            if (!bilateral) {
                buildArmsRandomSet(node, armCount, 0, armLength, handRadius, fingerCount, fingerLength);
            }
            else {
                buildArmsBilateralSet(node, 0, armLength, handRadius, fingerCount, fingerLength);
            }
        }

        if (midArms) {
            node = scene.findNodeByName("torso_bottom");

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
        footLength = legRadius + (AppWindow.random.nextFloat(legRadius));
        footRadius = legRadius + AppWindow.random.nextFloat(legRadius * 0.2f);
        toeCount = (modelType == MODEL_TYPE_ROBOT) ? 0 : AppWindow.random.nextInt(5);
        toeLength = footLength * AppWindow.random.nextFloat(footLength * 0.5f);

            // hip legs

        node = scene.findNodeByName("hip");
        switch (modelType) {
            case MODEL_TYPE_ANIMAL:
                legOffset = hipRadius - (legRadius * 1.7f);
                ang = 250.0f + AppWindow.random.nextFloat(15.0f);
                break;
            case MODEL_TYPE_ROBOT:
                legOffset = hipRadius - (legRadius * 1.1f);
                ang = 90.0f;
            default:
                legOffset = hipRadius - (legRadius * 1.5f);
                ang = 255.0f + AppWindow.random.nextFloat(30.0f);
                break;
        }
        buildLimbLeg(0, node, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, -ang);
        buildLimbLeg(1, node, footLength, footRadius, footRot, toeCount, toeLength, legOffset, ang);

            // front legs

        if (modelType == MODEL_TYPE_ANIMAL) {
            node = scene.findNodeByName("torso_shoulder");
            switch (modelType) {
                case MODEL_TYPE_ANIMAL:
                    legOffset = shoulderRadius - (legRadius * 1.7f);
                    ang = 70.0f + AppWindow.random.nextFloat(15.0f);
                    break;
                case MODEL_TYPE_ROBOT:
                    legOffset = shoulderRadius - (legRadius * 1.1f);
                    ang = 90.0f;
                default:
                    legOffset = shoulderRadius - (legRadius * 1.5f);
                    ang = 75.0f + AppWindow.random.nextFloat(30.0f);
                    break;
            }
            buildLimbLeg(2, node, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, ang);
            buildLimbLeg(3, node, footLength, footRadius, footRot, toeCount, toeLength, legOffset, -ang);
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

        node = scene.findNodeByName("hip");
        buildLimbWhip(5, node, whipLength, 180.0f, false);
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
        if (bodyRadius < (legRadius * 4.0f)) {
            bodyRadius = legRadius * (4.0f + AppWindow.random.nextFloat(0.3f));
        }
        shoulderRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        headRadius = 0.3f + AppWindow.random.nextFloat(1.3f);
        headRadius = (headRadius > bodyRadius) ? bodyRadius : headRadius;
        headRadius = (headRadius < legRadius) ? legRadius : headRadius;

        // build the skeleton
        buildBody();
        buildLegs();
        buildArms();
        buildTail();
        buildHead();
    }

}
