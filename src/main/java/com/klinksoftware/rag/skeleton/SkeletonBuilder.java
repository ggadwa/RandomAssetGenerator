package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //

    public void buildLimbLeg(Skeleton skeleton, int limbIdx, int parentBoneIdx, float legRadius, float footLength, float footRadius, float footRot, int toeCount, float toeLength, float rotOffset, float scaleFactor) {
        int n, hipBoneIdx, kneeBoneIdx, ankleBoneIdx, footBoneIdx;
        int heelBoneIdx, knuckleBoneIdx, toeBoneIdx;
        float toeRadius;
        Bone parentBone;
        RagPoint pnt, vct, pushVct, footVct, footPnt, meshScale;
        RagPoint knuckleVct, knucklePnt, toeVct, toePnt, toeAdd;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        pushVct = new RagPoint(0.0f, 0.0f, (parentBone.radius - (legRadius * 0.5f)));
        pushVct.rotateY(rotOffset);

        pnt=parentBone.pnt.copy();
        pnt.addPoint(pushVct);

            // legs always face down

        vct=new RagPoint(0.0f,-parentBone.pnt.y,0.0f);

            // leg bones
            // we might already have a hip, so don't rebuild if we do

        hipBoneIdx = skeleton.addChildBone(parentBoneIdx, ("hip_" + Integer.toString(limbIdx)), -1, (legRadius * (1.0f + (AppWindow.random.nextFloat(0.3f)))), new RagPoint(pnt.x, pnt.y, pnt.z));
        kneeBoneIdx=skeleton.addChildBone(hipBoneIdx,("knee_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.4f)),(pnt.y+(vct.y*0.4f)),(pnt.z+(vct.z*0.4f))));
        ankleBoneIdx=skeleton.addChildBone(kneeBoneIdx,("ankle_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.8f)),(pnt.y+(vct.y*0.8f)),(pnt.z+(vct.z*0.8f))));
        heelBoneIdx = skeleton.addChildBone(ankleBoneIdx, ("heel_" + Integer.toString(limbIdx)), -1, legRadius, new RagPoint((pnt.x + (vct.x * 0.95f)), (pnt.y + (vct.y * 0.95f)), (pnt.z + (vct.z * 0.95f))));

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        skeleton.addLimb(("leg_top_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, hipBoneIdx, kneeBoneIdx);
        skeleton.addLimb(("leg_bottom_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, kneeBoneIdx, ankleBoneIdx);
        skeleton.addLimb(("ankle_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, ankleBoneIdx, heelBoneIdx);

            // the foot bones
            // feet are always parallel to ground, towards front
        footVct = new RagPoint(0.0f, 0.0f, footLength);
        footVct.rotateY(footRot);

        footPnt=skeleton.bones.get(heelBoneIdx).pnt.copy();
        footPnt.addPoint(footVct);
        footPnt.y=0.0f;

        meshScale = new RagPoint(1.0f, (0.5f + AppWindow.random.nextFloat(0.2f)), scaleFactor);
        footBoneIdx = skeleton.addChildBone(heelBoneIdx, ("foot_" + Integer.toString(limbIdx)), -1, footRadius, footPnt);
        skeleton.addLimb(("foot_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_FOOT, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, heelBoneIdx, footBoneIdx);

            // toe limbs
        if (toeCount==0) return;

        toeRadius = ((footRadius * (1.5f + AppWindow.random.nextFloat(0.6f))) * meshScale.x) / (float) toeCount;

        knuckleVct = new RagPoint(-(((float) toeCount * (toeRadius * 2.0f)) / 2.0f), 0.0f, (footLength * 0.9f));
        knuckleVct.rotateY(footRot);
        knucklePnt = new RagPoint((footPnt.x + knuckleVct.x), (toeRadius * 0.5f), (footPnt.z + knuckleVct.z));

        toeVct = new RagPoint(0.0f, 0.0f, toeLength);
        toeVct.rotateY(footRot);
        toePnt = new RagPoint((knucklePnt.x + toeVct.x), (toeRadius * 0.5f), (knucklePnt.z + toeVct.z));

        toeAdd = new RagPoint((toeRadius * 2.0f), 0.0f, 0.0f);
        toeAdd.rotateY(footRot);

        for (n=0;n!=toeCount;n++) {
            knuckleBoneIdx=skeleton.addChildBone(heelBoneIdx,("toe_knuckle_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,toeRadius,knucklePnt);
            toeBoneIdx=skeleton.addChildBone(knuckleBoneIdx,("toe_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,toeRadius,toePnt);
            skeleton.addLimb(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_FOOT, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, footBoneIdx, knuckleBoneIdx);
            skeleton.addLimb(("toe_tip_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_FOOT, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, Limb.LIMB_AXIS_Z, meshScale, knuckleBoneIdx, toeBoneIdx);

            knucklePnt.addPoint(toeAdd);
            toePnt.addPoint(toeAdd);
        }
    }

        //
        // arm limb
        //

    public void buildLimbArm(Skeleton skeleton, int limbIdx, int parentBoneIdx, float armRadius, float armLength, float handRadius, int fingerCount, float fingerLength, float rotOffset, float scaleFactor) {
        int n, axis;
        int shoulderBoneIdx, elbowBoneIdx, wristBoneIdx;
        int handBoneIdx, knuckleBoneIdx, fingerBoneIdx;
        float fingerRadius;
        Bone parentBone;
        RagPoint pnt, vct, pushVct, handPnt, meshScale;
        RagPoint knuckleVct, knucklePnt, fingerVct, fingerPnt, fingerAdd;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        pushVct = new RagPoint(0.0f, 0.0f, parentBone.radius);
        pushVct.rotateY(rotOffset);

        pnt=parentBone.pnt.copy();
        pnt.addPoint(pushVct);

            // arms face out

        vct=new RagPoint(0.0f,0.0f,armLength);
        vct.rotateY(rotOffset);

            // arm limb

        shoulderBoneIdx=skeleton.addChildBone(parentBoneIdx,("shoulder_"+Integer.toString(limbIdx)),-1,(armRadius*1.5f),new RagPoint(pnt.x,pnt.y,pnt.z));
        elbowBoneIdx = skeleton.addChildBone(shoulderBoneIdx, ("elbow_" + Integer.toString(limbIdx)), -1, armRadius, new RagPoint((pnt.x + (vct.x * 0.45f)), pnt.y, (pnt.z + (vct.z * 0.45f))));
        wristBoneIdx = skeleton.addChildBone(elbowBoneIdx, ("wrist_" + Integer.toString(limbIdx)), -1, armRadius, new RagPoint((pnt.x + (vct.x * 0.9f)), pnt.y, (pnt.z + (vct.z * 0.9f))));

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        skeleton.addLimb(("arm_top_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_ARM, Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderBoneIdx, elbowBoneIdx);
        skeleton.addLimb(("arm_bottom_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_ARM, Limb.MESH_TYPE_CYLINDER, axis, meshScale, elbowBoneIdx, wristBoneIdx);

            // hand limb

        handPnt = new RagPoint((pnt.x + vct.x), pnt.y, (pnt.z + vct.z));

        handBoneIdx=skeleton.addChildBone(wristBoneIdx,("hand_"+Integer.toString(limbIdx)),-1,handRadius,handPnt);
        skeleton.addLimb(("hand_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, axis, meshScale, wristBoneIdx, handBoneIdx);

            // finger limbs

        if (fingerCount==0) return;

        fingerRadius = ((handRadius * (1.5f + AppWindow.random.nextFloat(0.6f))) * meshScale.y) / (float) fingerCount;

        knuckleVct = new RagPoint(0.0f, -(((float) fingerCount * (fingerRadius * 2.0f)) / 2.0f), (handRadius * 0.9f));
        knuckleVct.rotateY(rotOffset);
        knucklePnt = new RagPoint((handPnt.x + knuckleVct.x), (handPnt.y + knuckleVct.y), (handPnt.z + knuckleVct.z));

        fingerVct = new RagPoint(0.0f, 0.0f, fingerLength);
        fingerVct.rotateY(rotOffset);
        fingerPnt = new RagPoint((knucklePnt.x + fingerVct.x), (knucklePnt.y + fingerVct.y), (knucklePnt.z + fingerVct.z));

        fingerAdd = new RagPoint(0.0f, (fingerRadius * 2.0f), 0.0f);

        for (n=0;n!=fingerCount;n++) {
            knuckleBoneIdx = skeleton.addChildBone(handBoneIdx, ("finger_knuckle_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, fingerRadius, knucklePnt);
            fingerBoneIdx = skeleton.addChildBone(knuckleBoneIdx, ("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), -1, fingerRadius, fingerPnt);

            skeleton.addLimb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER, axis, meshScale, handBoneIdx, knuckleBoneIdx);
            skeleton.addLimb(("finger_tip_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, knuckleBoneIdx, fingerBoneIdx);

            knucklePnt.addPoint(fingerAdd);
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

        skeleton.addLimb(("whip_start_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_WHIP, Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip0BoneIdx, whip1BoneIdx);
        skeleton.addLimb(("whip_middle_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_WHIP, Limb.MESH_TYPE_CYLINDER, axis, meshScale, whip1BoneIdx, whip2BoneIdx);
        skeleton.addLimb(("whip_end_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_WHIP, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, whip2BoneIdx, whip3BoneIdx);
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

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        if (hasNeck) {
            skeleton.addLimb(("neck_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_NECK, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, neckBotBoneIdx, neckTopBoneIdx);
            skeleton.addLimb(("jaw_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_NECK, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, neckTopBoneIdx, headBottomBoneIdx);
        }
        skeleton.addLimb(("head_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_HEAD, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Y, meshScale, headBottomBoneIdx, headTopBoneIdx);
    }

        //
        // general body
        //

    public void buildBody(Skeleton skeleton, int modelType, float hunchAng, float scaleFactor) {
        int hipBoneIdx, waistBoneIdx, torsoBottomBoneIdx, torsoShoulderBoneIdx, torsoTopBoneIdx;
        int axis;
        float hipHigh, hipAdd, torsoAdd, radius;
        float minRadius, extraRadius;
        RagPoint hipPnt, waistPnt, torsoBottomPnt, torsoShoulderPnt, torsoTopPnt;
        RagPoint meshScale;

        switch (modelType) {
            case SettingsModel.MODEL_TYPE_ANIMAL:
                hipHigh = 1.5f + AppWindow.random.nextFloat(2.0f);
                meshScale = new RagPoint(1.0f, scaleFactor, 1.0f);
                break;
            case SettingsModel.MODEL_TYPE_BLOB:
                hipHigh = 0.0f;
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
            default:
                hipHigh = 2.5f + AppWindow.random.nextFloat(1.5f);
                meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
                break;
        }

        hipAdd = 0.5f + AppWindow.random.nextFloat(0.5f);
        torsoAdd = 0.5f + AppWindow.random.nextFloat(0.4f);
        minRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        extraRadius = minRadius * 0.2f;

            // the spine

        hipPnt = new RagPoint(0, hipHigh, 0);
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        hipBoneIdx = skeleton.addChildBone(0, "Hip", -1, radius, hipPnt);            // 0 is always the root bone

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

        // body limbs
        axis = (modelType == SettingsModel.MODEL_TYPE_ANIMAL) ? Limb.LIMB_AXIS_Z : Limb.LIMB_AXIS_Y;

        skeleton.addLimb("hip", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, axis, meshScale, hipBoneIdx, waistBoneIdx);
        skeleton.addLimb("waist", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER, axis, meshScale, waistBoneIdx, torsoBottomBoneIdx);
        skeleton.addLimb("torso_shoulder", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER, axis, meshScale, torsoBottomBoneIdx, torsoShoulderBoneIdx);
        skeleton.addLimb("torso_top", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, torsoShoulderBoneIdx, torsoTopBoneIdx);
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
        handRadius = limbRadius * (0.7f + (AppWindow.random.nextFloat(0.5f)));
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
        float footRot, footLength, footRadius, toeLength;

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

            // hip legs

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbLeg(skeleton, 1, boneIdx, limbRadius, footLength, footRadius, footRot, toeCount, toeLength, 90.0f, scaleFactor);
        buildLimbLeg(skeleton, 2, boneIdx, limbRadius, footLength, footRadius, -footRot, toeCount, toeLength, 270.0f, scaleFactor);

            // front legs

        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            boneIdx = skeleton.findBoneIndex("Torso_Shoulder");
            buildLimbLeg(skeleton, 3, boneIdx, limbRadius, footLength, footRadius, footRot, toeCount, toeLength, 90.0f, scaleFactor);
            buildLimbLeg(skeleton, 4, boneIdx, limbRadius, footLength, footRadius, -footRot, toeCount, toeLength, 270.0f, scaleFactor);
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
        if (modelType == SettingsModel.MODEL_TYPE_ANIMAL) {
            hunchAng = (60.0f + (AppWindow.random.nextFloat(60.0f)));
        }
        else {
            hunchAng = AppWindow.random.nextFloat(20.0f);
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
