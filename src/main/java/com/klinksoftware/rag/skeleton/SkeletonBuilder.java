package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //

    public void buildLimbLeg(Skeleton skeleton, int limbIdx, int parentBoneIdx, float legRadius, float footLength, float footRadius, float footRot, int toeCount, float toeLength, float legOffset, float rotOffset, float scaleFactor) {
        int n, hipBoneIdx, kneeBoneIdx, ankleBoneIdx, footBoneIdx;
        int heelBoneIdx, jointBoneIdx, toeBoneIdx;
        float z, legLength, toeRadius;
        Bone parentBone;
        RagPoint hipVct, hipPnt, kneeVct, kneePnt, ankleVct, anklePnt, heelVct, heelPnt, footVct, footPnt;
        RagPoint jointVct, jointPnt, toeVct, toePnt, toeAdd, meshScale;

        parentBone=skeleton.bones.get(parentBoneIdx);

        // leg bones
        z = parentBone.pnt.z;
        legLength = parentBone.pnt.y;

        hipVct = new RagPoint(0.0f, 0.0f, (parentBone.radius - legOffset));
        hipVct.rotateY(rotOffset);
        hipPnt = new RagPoint((parentBone.pnt.x + hipVct.x), (parentBone.pnt.y + hipVct.y), z);
        hipBoneIdx = skeleton.addChildBone(parentBoneIdx, ("hip_" + Integer.toString(limbIdx)), -1, (legRadius * (1.0f + (AppWindow.random.nextFloat(0.3f)))), hipPnt);

        kneeVct = new RagPoint(0.0f, -(legLength * 0.4f), 0.0f);
        kneeVct.rotateY(rotOffset);
        kneePnt = new RagPoint((hipPnt.x + kneeVct.x), (hipPnt.y + kneeVct.y), z);
        kneeBoneIdx = skeleton.addChildBone(hipBoneIdx, ("knee_" + Integer.toString(limbIdx)), -1, legRadius, kneePnt);

        ankleVct = new RagPoint(0.0f, -(legLength * 0.4f), 0.0f);
        ankleVct.rotateY(rotOffset);
        anklePnt = new RagPoint((kneePnt.x + ankleVct.x), (kneePnt.y + ankleVct.y), z);
        ankleBoneIdx = skeleton.addChildBone(kneeBoneIdx, ("ankle_" + Integer.toString(limbIdx)), -1, legRadius, anklePnt);

        heelVct = new RagPoint(0.0f, 0.0f, 0.0f);
        heelVct.rotateY(rotOffset);
        heelPnt = new RagPoint(anklePnt.x, 0.0f, z);
        heelBoneIdx = skeleton.addChildBone(ankleBoneIdx, ("heel_" + Integer.toString(limbIdx)), -1, legRadius, heelPnt);

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        skeleton.addLimb(("leg_top_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.0f, 0.5f, 0.2f, hipBoneIdx, kneeBoneIdx);
        skeleton.addLimb(("leg_bottom_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.2f, 0.5f, 0.2f, kneeBoneIdx, ankleBoneIdx);
        skeleton.addLimb(("ankle_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.4f, 0.5f, 0.1f, ankleBoneIdx, heelBoneIdx);

            // the foot bones
            // feet are always parallel to ground, towards front
        footVct = new RagPoint(0.0f, 0.0f, footLength);
        footVct.rotateY(footRot);
        footPnt = new RagPoint((heelPnt.x + footVct.x), 0.0f, (heelPnt.z + footVct.z));

        meshScale = new RagPoint(1.0f, (0.5f + AppWindow.random.nextFloat(0.4f)), scaleFactor);
        footBoneIdx = skeleton.addChildBone(heelBoneIdx, ("foot_" + Integer.toString(limbIdx)), -1, footRadius, footPnt);
        skeleton.addLimb(("foot_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, 0.0f, 0.5f, 0.5f, 0.4f, heelBoneIdx, footBoneIdx);

        // toe limbs
        if (toeCount == 0) {
            return;
        }
        if (toeCount == 1) {
            toeCount = 2;  // always at least two toes
        }
        toeRadius = (((footRadius * (1.5f + AppWindow.random.nextFloat(0.5f))) * meshScale.x) / (float) toeCount) * 0.5f;

        jointVct = new RagPoint(-((((float) toeCount * (toeRadius * 2.0f)) * 0.5f) - toeRadius), 0.0f, 0.0f);
        jointVct.rotateY(footRot);
        jointPnt = new RagPoint((footPnt.x + jointVct.x), 0.0f, (footPnt.z + jointVct.z));

        toeVct = new RagPoint(0.0f, 0.0f, toeLength);
        toeVct.rotateY(footRot);
        toePnt = new RagPoint((jointPnt.x + toeVct.x), 0.0f, (jointPnt.z + toeVct.z));

        toeAdd = new RagPoint((toeRadius * 2.0f), 0.0f, 0.0f);
        toeAdd.rotateY(footRot);

        for (n=0;n!=toeCount;n++) {
            jointBoneIdx = skeleton.addChildBone(heelBoneIdx, ("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, toeRadius, jointPnt);
            toeBoneIdx = skeleton.addChildBone(jointBoneIdx, ("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, toeRadius, toePnt);
            skeleton.addLimb(("toe_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, 0.0f, 0.5f, 0.5f, 0.25f, footBoneIdx, jointBoneIdx);
            skeleton.addLimb(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, 0.0f, 0.25f, 0.5f, 0.25f, jointBoneIdx, toeBoneIdx);

            jointPnt.addPoint(toeAdd);
            toePnt.addPoint(toeAdd);
        }
    }

        //
        // arm limb
        //

    public void buildLimbArm(Skeleton skeleton, int limbIdx, int parentBoneIdx, float armRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float rotOffset, float scaleFactor) {
        int n, axis;
        int shoulderBoneIdx, elbowBoneIdx, wristBoneIdx;
        int handBoneIdx, jointBoneIdx, fingerBoneIdx;
        float y, fingerRadius;
        Bone parentBone;
        RagPoint shoulderVct, shoulderPnt, elbowVct, elbowPnt, wristVct, wristPnt, handVct, handPnt;
        RagPoint jointVct, jointPnt, fingerVct, fingerPnt, fingerAdd, meshScale;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        // shoulder, elbow, wrist bones
        y = parentBone.pnt.y;

        shoulderVct = new RagPoint(0.0f, 0.0f, parentBone.radius);
        shoulderVct.rotateY(rotOffset);
        shoulderPnt = new RagPoint((parentBone.pnt.x + shoulderVct.x), y, (parentBone.pnt.z + shoulderVct.z));
        shoulderBoneIdx = skeleton.addChildBone(parentBoneIdx, ("shoulder_" + Integer.toString(limbIdx)), -1, (armRadius * 1.5f), shoulderPnt);

        elbowVct = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));
        elbowVct.rotateY(rotOffset);
        elbowPnt = new RagPoint((shoulderPnt.x + shoulderVct.x), y, (shoulderPnt.z + shoulderVct.z));
        elbowBoneIdx = skeleton.addChildBone(shoulderBoneIdx, ("elbow_" + Integer.toString(limbIdx)), -1, armRadius, elbowPnt);

        wristVct = new RagPoint(0.0f, 0.0f, (armLength * 0.5f));
        wristVct.rotateY(rotOffset);
        wristPnt = new RagPoint((elbowPnt.x + wristVct.x), y, (elbowPnt.z + wristVct.z));
        wristBoneIdx = skeleton.addChildBone(elbowBoneIdx, ("wrist_" + Integer.toString(limbIdx)), -1, armRadius, wristPnt);

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        skeleton.addLimb(("arm_top_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.5f, 0.0f, 0.2f, 0.5f, shoulderBoneIdx, elbowBoneIdx);
        skeleton.addLimb(("arm_bottom_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.7f, 0.0f, 0.2f, 0.5f, elbowBoneIdx, wristBoneIdx);

            // hand limb

        handVct = new RagPoint(0.0f, 0.0f, (handRadius * 2.0f));
        handVct.rotateY(rotOffset);
        handPnt = new RagPoint((wristPnt.x + handVct.x), y, (wristPnt.z + handVct.z));
        handBoneIdx = skeleton.addChildBone(wristBoneIdx, ("hand_" + Integer.toString(limbIdx)), -1, handRadius, handPnt);
        skeleton.addLimb(("hand_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, axis, meshScale, 0.9f, 0.0f, 0.1f, 0.5f, wristBoneIdx, handBoneIdx);

            // finger limbs

        if (fingerCount == 0) {
            return;
        }
        if (fingerCount == 1) {
            fingerCount = 2;  // always at least two fingers
        }
        fingerRadius = (((handRadius * (1.5f + AppWindow.random.nextFloat(0.5f))) * meshScale.y) / (float) fingerCount) * 0.5f;

        jointVct = new RagPoint(0.0f, -((((float) fingerCount * (fingerRadius * 2.0f)) * 0.5f) - fingerRadius), 0.0f);
        jointVct.rotateY(rotOffset);
        jointPnt = new RagPoint((handPnt.x + jointVct.x), (handPnt.y + jointVct.y), (handPnt.z + jointVct.z));

        fingerVct = new RagPoint(0.0f, 0.0f, fingerLength);
        fingerVct.rotateY(rotOffset);
        fingerPnt = new RagPoint((jointPnt.x + fingerVct.x), jointPnt.y, (jointPnt.z + fingerVct.z));

        fingerAdd = new RagPoint(0.0f, (fingerRadius * 2.0f), 0.0f);

        for (n=0;n!=fingerCount;n++) {
            jointBoneIdx = skeleton.addChildBone(handBoneIdx, ("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, fingerRadius, jointPnt);
            fingerBoneIdx = skeleton.addChildBone(jointBoneIdx, ("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, fingerRadius, fingerPnt);

            skeleton.addLimb(("finger_joint_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.0f, 0.5f, 0.25f, 0.5f, handBoneIdx, jointBoneIdx);
            skeleton.addLimb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, 0.25f, 0.5f, 0.25f, 0.5f, jointBoneIdx, fingerBoneIdx);

            jointPnt.addPoint(fingerAdd);
            fingerPnt.addPoint(fingerAdd);
        }
    }

        //
        // whip limbs
        //

    public void buildLimbWhip(Skeleton skeleton, int limbIdx, int parentBoneIdx, float whipRadius, float whipLength, float rotOffset, float scaleFactor)    {
        int axis, whip0BoneIdx, whip1BoneIdx, whip2BoneIdx, whip3BoneIdx;
        Bone parentBone;
        RagPoint pnt, vct, pushVct, meshScale;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        pushVct = new RagPoint(0.0f, 0.0f, (whipRadius * 0.5f));
        pushVct.rotateY(rotOffset);

        pnt=parentBone.pnt.copy();
        pnt.addPoint(pushVct);

            // whips face out

        vct=new RagPoint(0.0f,0.0f,whipLength);
        vct.rotateY(rotOffset);

            // whip limb

        whip0BoneIdx=skeleton.addChildBone(parentBoneIdx,("whip_"+Integer.toString(limbIdx)+"_0"),-1,whipRadius,pnt);
        pnt.addPoint(vct);
        whip1BoneIdx=skeleton.addChildBone(whip0BoneIdx,("whip_"+Integer.toString(limbIdx)+"_1"),-1,(whipRadius*0.8f),pnt);
        pnt.addPoint(vct);
        whip2BoneIdx=skeleton.addChildBone(whip1BoneIdx,("whip_"+Integer.toString(limbIdx)+"_2"),-1,(whipRadius*0.6f),pnt);
        pnt.addPoint(vct);
        whip3BoneIdx=skeleton.addChildBone(whip2BoneIdx,("whip_"+Integer.toString(limbIdx)+"_3"),-1,(whipRadius*0.3f),pnt);

        if (((rotOffset > 315) || (rotOffset < 45)) || ((rotOffset > 135) && (rotOffset < 225))) {
            axis = Limb.LIMB_AXIS_Z;
            meshScale = new RagPoint(scaleFactor, 1.0f, 1.0f);
        } else {
            axis = Limb.LIMB_AXIS_X;
            meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        }

        skeleton.addLimb(("whip_start_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.0f, 0.5f, 0.16f, 0.5f, whip0BoneIdx, whip1BoneIdx);
        skeleton.addLimb(("whip_middle_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.16f, 0.5f, 0.16f, 0.5f, whip1BoneIdx, whip2BoneIdx);
        skeleton.addLimb(("whip_end_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, 0.32f, 0.5f, 0.18f, 0.5f, whip2BoneIdx, whip3BoneIdx);
    }

        //
        // head limb
        //

    public void buildLimbHead(Skeleton skeleton, int modelType, int limbIdx, int parentBoneIdx, float neckRadius, float headRadius, float scaleFactor) {
        int neckBotBoneIdx, neckTopBoneIdx, headBottomBoneIdx, headTopBoneIdx;
        float neckLength, headLength;
        boolean hasNeck;
        Bone parentBone;
        RagPoint pnt, vct, meshScale;

        // blobs can sometimes have no heads
        if (modelType == SettingsModel.MODEL_TYPE_BLOB) {
            if (AppWindow.random.nextFloat() < 0.5f) {
                return;
            }
        }

        parentBone = skeleton.bones.get(parentBoneIdx);

        // randomly eliminate necks
        hasNeck = (AppWindow.random.nextFloat() < 0.8f);
        neckBotBoneIdx = -1;
        neckTopBoneIdx = -1;

        // neck bone
        pnt = parentBone.pnt.copy();

        if (hasNeck) {
            neckLength = headRadius * (0.5f + (AppWindow.random.nextFloat(0.5f)));

            neckBotBoneIdx = skeleton.addChildBone(parentBoneIdx, ("neck_bottom_" + Integer.toString(limbIdx)), -1, neckRadius, pnt);

            pnt = parentBone.pnt.copy();
            vct = new RagPoint(0.0f, neckLength, 0.0f);
            vct.rotateX(-AppWindow.random.nextFloat(15.0f));
            pnt.addPoint(vct);
            neckTopBoneIdx = skeleton.addChildBone(parentBoneIdx, ("neck_top_" + Integer.toString(limbIdx)), -1, neckRadius, pnt);

            pnt = pnt.copy();
            pnt.y += (neckLength * 0.5f);
        }

            // the head bones
        headBottomBoneIdx = skeleton.addChildBone((hasNeck) ? neckTopBoneIdx : parentBoneIdx, ("head_bottom_" + Integer.toString(limbIdx)), -1, headRadius, pnt);

        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        pnt=pnt.copy();
        vct=new RagPoint(0.0f,headLength,0.0f);
        vct.rotateX(-AppWindow.random.nextFloat(25.0f));
        pnt.addPoint(vct);

        headTopBoneIdx=skeleton.addChildBone(headBottomBoneIdx,("head_top_"+Integer.toString(limbIdx)),-1,headRadius,pnt);

            // the limb over the neck and head
        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            meshScale = new RagPoint(scaleFactor, (scaleFactor * 1.5f), 1.0f);
        } else {
            meshScale = new RagPoint((scaleFactor * 1.5f), 1.0f, scaleFactor);
        }

        if (hasNeck) {
            skeleton.addLimb(("neck_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.5f, 0.5f, 0.1f, neckBotBoneIdx, neckTopBoneIdx);
            skeleton.addLimb(("jaw_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.6f, 0.5f, 0.1f, neckTopBoneIdx, headBottomBoneIdx);
        }
        skeleton.addLimb(("head_" + Integer.toString(limbIdx)), Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Y, meshScale, 0.5f, 0.7f, 0.5f, 0.3f, headBottomBoneIdx, headTopBoneIdx);
    }

        //
        // general body
        //

    public void buildBody(Skeleton skeleton, int modelType, float hunchAng, float scaleFactor) {
        int hipBoneIdx, waistBoneIdx, torsoBottomBoneIdx, torsoShoulderBoneIdx, torsoTopBoneIdx, buttBoneIdx;
        int axis;
        float hipHigh, hipRadius, hipAdd, torsoAdd, radius;
        float minRadius, extraRadius;
        RagPoint hipPnt, waistPnt, torsoBottomPnt, torsoShoulderPnt, torsoTopPnt, buttPnt;
        RagPoint meshScale;

        minRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        extraRadius = minRadius * 0.2f;

        switch (modelType) {
            case SettingsModel.MODEL_TYPE_ANIMAL:
                hipHigh = 1.5f + AppWindow.random.nextFloat(2.0f);
                hipRadius = minRadius;
                meshScale = new RagPoint(1.0f, scaleFactor, 1.0f);
                break;
            case SettingsModel.MODEL_TYPE_BLOB:
                hipHigh = 0.0f;
                hipRadius = minRadius + (AppWindow.random.nextFloat(extraRadius * 2.0f));
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
            default:
                hipHigh = 2.5f + AppWindow.random.nextFloat(1.5f);
                hipRadius = minRadius + (AppWindow.random.nextFloat(extraRadius));
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
        }

        hipAdd = 0.5f + AppWindow.random.nextFloat(0.5f);
        torsoAdd = 0.5f + AppWindow.random.nextFloat(0.4f);

            // the spine

        hipPnt = new RagPoint(0, hipHigh, 0);
        hipBoneIdx = skeleton.addChildBone(0, "Hip", -1, hipRadius, hipPnt);            // 0 is always the root bone

        waistPnt = new RagPoint(0, hipAdd, 0);
        if (hunchAng != 0.0f) {
            waistPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        waistPnt.y+=hipPnt.y;
        waistPnt.z+=hipPnt.z;
        radius = minRadius;
        waistBoneIdx = skeleton.addChildBone(hipBoneIdx, "Waist", -1, radius, waistPnt);

        torsoBottomPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoBottomPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoBottomPnt.y += waistPnt.y;
        torsoBottomPnt.z += waistPnt.z;
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoBottomBoneIdx = skeleton.addChildBone(waistBoneIdx, "Torso_Bottom", -1, radius, torsoBottomPnt);

        torsoShoulderPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoShoulderPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoShoulderPnt.y += torsoBottomPnt.y;
        torsoShoulderPnt.z += torsoBottomPnt.z;
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoShoulderBoneIdx = skeleton.addChildBone(torsoBottomBoneIdx, "Torso_Shoulder", -1, radius, torsoShoulderPnt);

        torsoTopPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoTopPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat(0.05f))));
        }
        torsoTopPnt.y += torsoShoulderPnt.y;
        torsoTopPnt.z += torsoShoulderPnt.z;
        radius = minRadius;
        torsoTopBoneIdx = skeleton.addChildBone(torsoShoulderBoneIdx, "Torso_Top", -1, radius, torsoTopPnt);

        // animals have extra butt
        buttBoneIdx = -1;
        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            buttPnt = new RagPoint(hipPnt.x, hipPnt.y, hipPnt.z - (hipAdd * (1.0f + AppWindow.random.nextFloat(0.2f))));
            radius = minRadius * (AppWindow.random.nextFloat(0.2f));
            buttBoneIdx = skeleton.addChildBone(hipBoneIdx, "Butt", -1, radius, buttPnt);
        }

        // body limbs
        axis = (modelType == SettingsModel.MODEL_TYPE_ANIMAL) ? Limb.LIMB_AXIS_Z : Limb.LIMB_AXIS_Y;

        skeleton.addLimb("hip", Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, axis, meshScale, 0.0f, 0.3f, 0.5f, 0.2f, hipBoneIdx, waistBoneIdx);
        skeleton.addLimb("waist", Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.0f, 0.2f, 0.5f, 0.3f, waistBoneIdx, torsoBottomBoneIdx);
        skeleton.addLimb("torso_shoulder", Limb.MESH_TYPE_CYLINDER, axis, meshScale, 0.0f, 0.1f, 0.5f, 0.1f, torsoBottomBoneIdx, torsoShoulderBoneIdx);
        skeleton.addLimb("torso_top", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, 0.0f, 0.0f, 0.5f, 0.1f, torsoShoulderBoneIdx, torsoTopBoneIdx);

        if (buttBoneIdx != -1) {
            skeleton.addLimb("butt", Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, 0.0f, 0.0f, 0.5f, 0.2f, hipBoneIdx, buttBoneIdx);
        }
    }

        //
        // arms
        //

    private void buildArmsRandomSet(Skeleton skeleton, int boneIdx, int armCount, int limbNameOffset, float limbRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float scaleFactor) {
        int n;
        float ang;

        for (n=0;n!=armCount;n++) {
            ang = (float) (AppWindow.random.nextInt(4) * 90) + (25.0f - (AppWindow.random.nextFloat(50.0f)));
            ang = AppWindow.random.nextFloat(360.0f);
            if (AppWindow.random.nextFloat()<0.8f) {
                buildLimbArm(skeleton, (n + limbNameOffset), boneIdx, limbRadius, armLength, handRadius, fingerCount, fingerLength, ang, scaleFactor);
            }
            else {
                buildLimbWhip(skeleton, (n + limbNameOffset), boneIdx, limbRadius, armLength, ang, scaleFactor);
            }
        }
    }

    private void buildArmsBilateralSet(Skeleton skeleton, int boneIdx, int limbNameOffset, float limbRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float scaleFactor) {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm(skeleton, (limbNameOffset + 1), boneIdx, limbRadius, armLength, handRadius, fingerCount, fingerLength, 90.0f, scaleFactor);
            buildLimbArm(skeleton, (limbNameOffset + 2), boneIdx, limbRadius, armLength, handRadius, fingerCount, fingerLength, 270.0f, scaleFactor);
        }
        else {
            buildLimbWhip(skeleton, (limbNameOffset + 1), boneIdx, limbRadius, armLength, 90.0f, scaleFactor);
            buildLimbWhip(skeleton, (limbNameOffset + 2), boneIdx, limbRadius, armLength, 270.0f, scaleFactor);
        }
    }

    private void buildArms(Skeleton skeleton, int modelType, boolean bilateral, float limbRadius, float scaleFactor) {
        int boneIdx, armCount, fingerCount;
        float armLength, handRadius, fingerLength;
        boolean topArms, midArms;

        // some settings
        armLength = 1.0f + AppWindow.random.nextFloat(1.5f);
        armCount = 1 + AppWindow.random.nextInt(3);
        handRadius = limbRadius * (1.0f + (AppWindow.random.nextFloat(0.5f)));
        fingerCount = (modelType == SettingsModel.MODEL_TYPE_BLOB) ? 0 : AppWindow.random.nextInt(5);
        fingerLength = handRadius * (0.2f + AppWindow.random.nextFloat(2.5f));

            // determine number of arms

        topArms=false;
        midArms=false;

        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            topArms=(AppWindow.random.nextFloat()<0.3f);
        }
        else {
            topArms=(AppWindow.random.nextFloat()<0.9f);
            midArms=(AppWindow.random.nextFloat()<0.2f);
        }

            // the arm pairs

        if (topArms) {
            boneIdx = skeleton.findBoneIndex("Torso_Shoulder");

            if (!bilateral) {
                buildArmsRandomSet(skeleton, boneIdx, armCount, 0, limbRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
            else {
                buildArmsBilateralSet(skeleton, boneIdx, 0, limbRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
        }

        if (midArms) {
            boneIdx = skeleton.findBoneIndex("Torso_Bottom");

            if (!bilateral) {
                buildArmsRandomSet(skeleton, boneIdx, armCount, armCount, limbRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
            else {
                buildArmsBilateralSet(skeleton, boneIdx, 2, limbRadius, armLength, handRadius, fingerCount, fingerLength, scaleFactor);
            }
        }
    }

        //
        // legs
        //

    public void buildLegs(Skeleton skeleton, int modelType, float limbRadius, float scaleFactor) {
        int boneIdx, toeCount;
        float footRot, footLength, footRadius, toeLength, legOffset;

        // blobs have no legs
        if (modelType == SettingsModel.MODEL_TYPE_BLOB) {
            return;
        }

        // some settings
        footRot = AppWindow.random.nextFloat(15.0f);
        footLength = limbRadius + (AppWindow.random.nextFloat(limbRadius * 2.0f));
        footRadius = (limbRadius * (0.7f + AppWindow.random.nextFloat(0.4f)));
        toeCount = (modelType == SettingsModel.MODEL_TYPE_ROBOT) ? 0 : AppWindow.random.nextInt(5);
        toeLength = footRadius * (0.2f + AppWindow.random.nextFloat(2.5f));
        legOffset = ((modelType == SettingsModel.MODEL_TYPE_ROBOT) ? 0.7f : 0.5f) * limbRadius;

            // hip legs

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbLeg(skeleton, 1, boneIdx, limbRadius, footLength, footRadius, footRot, toeCount, toeLength, legOffset, 90.0f, scaleFactor);
        buildLimbLeg(skeleton, 2, boneIdx, limbRadius, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, 270.0f, scaleFactor);

            // front legs

        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            boneIdx = skeleton.findBoneIndex("Torso_Shoulder");
            buildLimbLeg(skeleton, 3, boneIdx, limbRadius, footLength, footRadius, footRot, toeCount, toeLength, legOffset, 90.0f, scaleFactor);
            buildLimbLeg(skeleton, 4, boneIdx, limbRadius, footLength, footRadius, -footRot, toeCount, toeLength, legOffset, 270.0f, scaleFactor);
        }
    }

        //
        // tails
        //

    public void buildTail(Skeleton skeleton, int modelType, float limbRadius, float scaleFactor) {
        int boneIdx;
        float whipLength;

        // robots have no tails
        if (modelType == SettingsModel.MODEL_TYPE_ROBOT) {
            return;
        }

        if (AppWindow.random.nextFloat()<0.7f) return;

        whipLength = 0.7f + AppWindow.random.nextFloat(1.0f);

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbWhip(skeleton, 5, boneIdx, limbRadius, whipLength, 180.0f, scaleFactor);
    }

        //
        // heads
        //

    public void buildHead(Skeleton skeleton, int modelType, float limbRadius, float scaleFactor) {
        int boneIdx;
        float headRadius;

        headRadius = 0.4f + (AppWindow.random.nextFloat(0.6f));

        boneIdx=skeleton.findBoneIndex("Torso_Top");
        buildLimbHead(skeleton, modelType, 0, boneIdx, limbRadius, headRadius, scaleFactor);
    }

        //
        // build skeleton bones
        //

    public Skeleton build(int modelType, boolean thin, boolean bilateral) {
        float hunchAng, scaleFactor, limbRadius;
        Skeleton skeleton;

        skeleton = new Skeleton();

        // skeleton hunch angle
        switch (modelType) {
            case SettingsModel.MODEL_TYPE_ANIMAL:
                hunchAng = (60.0f + (AppWindow.random.nextFloat(60.0f)));
                break;
            case SettingsModel.MODEL_TYPE_ROBOT:
                hunchAng = 0.0f;
                break;
            default:
                hunchAng = AppWindow.random.nextFloat(20.0f);
                break;
        }

        // limb sizes
        limbRadius = 0.2f + (AppWindow.random.nextFloat(0.2f));

        // skeleton scale factor
        scaleFactor = thin ? (0.3f + (AppWindow.random.nextFloat(0.5f))) : (0.6f + (AppWindow.random.nextFloat(0.3f)));

        // build the skeleton
        buildBody(skeleton, modelType, hunchAng, scaleFactor);
        buildLegs(skeleton, modelType, limbRadius, scaleFactor);
        buildArms(skeleton, modelType, bilateral, limbRadius, scaleFactor);
        buildTail(skeleton, modelType, limbRadius, scaleFactor);
        buildHead(skeleton, modelType, limbRadius, scaleFactor);

        // this is just so we can display it turned or not
        skeleton.standing = (modelType != SettingsModel.MODEL_TYPE_ANIMAL);

        return(skeleton);
     }

}
