package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //

    public void buildLimbLeg(Skeleton skeleton, int limbIdx, int parentBoneIdx, float legRadius, float rotOffset, float footLength, float footRot, int toeCount, float scaleFactor) {
        int n, hipBoneIdx, kneeBoneIdx, ankleBoneIdx, footBoneIdx;
        int heelBoneIdx, knuckleBoneIdx, toeBoneIdx;
        float footRadius, toeRadius;
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

        hipBoneIdx=skeleton.addChildBone(parentBoneIdx,("hip_"+Integer.toString(limbIdx)),-1,(legRadius*(1.0f+(AppWindow.random.nextFloat()*0.3f))),new RagPoint(pnt.x,pnt.y,pnt.z));
        kneeBoneIdx=skeleton.addChildBone(hipBoneIdx,("knee_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.4f)),(pnt.y+(vct.y*0.4f)),(pnt.z+(vct.z*0.4f))));
        ankleBoneIdx=skeleton.addChildBone(kneeBoneIdx,("ankle_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.8f)),(pnt.y+(vct.y*0.8f)),(pnt.z+(vct.z*0.8f))));
        heelBoneIdx = skeleton.addChildBone(ankleBoneIdx, ("heel_" + Integer.toString(limbIdx)), -1, legRadius, new RagPoint((pnt.x + (vct.x * 0.95f)), (pnt.y + (vct.y * 0.95f)), (pnt.z + (vct.z * 0.95f))));

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        skeleton.addLimb(("leg_top_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, hipBoneIdx, kneeBoneIdx);
        skeleton.addLimb(("leg_bottom_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, kneeBoneIdx, ankleBoneIdx);
        skeleton.addLimb(("ankle_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_LEG, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, ankleBoneIdx, heelBoneIdx);

            // the foot bones
            // feet are always parallel to ground, towards front
        footRadius = (legRadius * (0.7f + AppWindow.random.nextFloat(0.3f)));

        footVct=new RagPoint(0.0f,0.0f,footLength);
        footVct.rotateY(footRot);

        footPnt=skeleton.bones.get(heelBoneIdx).pnt.copy();
        footPnt.addPoint(footVct);
        footPnt.y=0.0f;

        meshScale = new RagPoint(1.0f, (0.5f + AppWindow.random.nextFloat(0.2f)), scaleFactor);
        footBoneIdx = skeleton.addChildBone(heelBoneIdx, ("foot_" + Integer.toString(limbIdx)), -1, footRadius, footPnt);
        skeleton.addLimb(("foot_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_FOOT, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Z, meshScale, heelBoneIdx, footBoneIdx);

            // toe limbs

        if (toeCount==0) return;

        toeRadius = footRadius / (float) toeCount;

        knuckleVct = new RagPoint(-(toeRadius * ((float) toeCount / 2.0f)), 0.0f, (footLength * 0.4f));
        knuckleVct.rotateY(footRot);

        knucklePnt = new RagPoint((footPnt.x + knuckleVct.x), (toeRadius * 0.5f), (footPnt.z + knuckleVct.z));

        toeVct = new RagPoint(0.0f, 0.0f, (footLength * 0.3f));
        toeVct.rotateY(footRot);
        toePnt = new RagPoint((knucklePnt.x + toeVct.x), (toeRadius * 0.5f), (knucklePnt.z + toeVct.z));

        toeAdd=new RagPoint(toeRadius,0.0f,0.0f);
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

    public void buildLimbArm(Skeleton skeleton, int limbIdx, int parentBoneIdx, float armRadius, float armLength, float rotOffset, int fingerCount, float scaleFactor) {
        int n, axis;
        int shoulderBoneIdx, elbowBoneIdx, wristBoneIdx;
        int handBoneIdx, knuckleBoneIdx, fingerBoneIdx;
        float fy, handRadius, fingerRadius;
        Bone parentBone;
        RagPoint pnt, vct, pushVct, handPnt, meshScale;
        RagPoint knuckleVct, knucklePnt, fingerVct, fingerPnt;

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
        elbowBoneIdx=skeleton.addChildBone(shoulderBoneIdx,("elbow_"+Integer.toString(limbIdx)),-1,armRadius,new RagPoint((pnt.x+(vct.x*0.45f)),(pnt.y+(vct.y*0.45f)),(pnt.z+(vct.z*0.45f))));
        wristBoneIdx=skeleton.addChildBone(elbowBoneIdx,("wrist_"+Integer.toString(limbIdx)),-1,armRadius,new RagPoint((pnt.x+(vct.x*0.9f)),(pnt.y+(vct.y*0.9f)),(pnt.z+(vct.z*0.9f))));

        meshScale = new RagPoint((axis == Limb.LIMB_AXIS_Z ? scaleFactor : 1.0f), 1.0f, (axis == Limb.LIMB_AXIS_X ? scaleFactor : 1.0f));
        skeleton.addLimb(("arm_top_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_ARM, Limb.MESH_TYPE_CYLINDER, axis, meshScale, shoulderBoneIdx, elbowBoneIdx);
        skeleton.addLimb(("arm_bottom_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_ARM, Limb.MESH_TYPE_CYLINDER, axis, meshScale, elbowBoneIdx, wristBoneIdx);

            // hand limb

        handRadius = armRadius * (0.7f + (AppWindow.random.nextFloat() * 0.3f));
        handPnt=new RagPoint((pnt.x+vct.x),(pnt.y+vct.y),(pnt.z+vct.z));

        handBoneIdx=skeleton.addChildBone(wristBoneIdx,("hand_"+Integer.toString(limbIdx)),-1,handRadius,handPnt);
        skeleton.addLimb(("hand_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, axis, meshScale, wristBoneIdx, handBoneIdx);

            // finger limbs

        if (fingerCount==0) return;

        fingerRadius = handRadius / (float) fingerCount;

        knuckleVct=vct.copy();
        knuckleVct.normalize();
        knuckleVct.scale(handRadius*0.4f);
        knucklePnt=new RagPoint((handPnt.x+knuckleVct.x),(handPnt.y+knuckleVct.y),(handPnt.z+knuckleVct.z));

        fingerVct=vct.copy();
        fingerVct.normalize();
        fingerVct.scale(armRadius+(AppWindow.random.nextFloat()*armRadius));
        fingerPnt=new RagPoint((knucklePnt.x+fingerVct.x),(knucklePnt.y+fingerVct.y),(knucklePnt.z+fingerVct.z));

        fy=fingerPnt.y;

        for (n=0;n!=fingerCount;n++) {
            knuckleBoneIdx=skeleton.addChildBone(handBoneIdx,("finger_knuckle_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,fingerRadius,new RagPoint(knucklePnt.x,fy,knucklePnt.z));
            fingerBoneIdx=skeleton.addChildBone(knuckleBoneIdx,("finger_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,fingerRadius,new RagPoint(fingerPnt.x,fy,fingerPnt.z));

            skeleton.addLimb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER, axis, meshScale, handBoneIdx, knuckleBoneIdx);
            skeleton.addLimb(("finger_tip_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), Limb.UV_MAP_TYPE_HAND, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, axis, meshScale, knuckleBoneIdx, fingerBoneIdx);

            fy+=fingerRadius;
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

    public void buildLimbHead(Skeleton skeleton, int limbIdx, int parentBoneIdx, float neckRadius, float headRadius, float scaleFactor) {
        int neckBotBoneIdx, neckTopBoneIdx, headBottomBoneIdx, headTopBoneIdx;
        float neckLength, headLength;
        Bone parentBone;
        RagPoint pnt, vct, meshScale;

        parentBone = skeleton.bones.get(parentBoneIdx);

            // create the neck and head bones
        neckLength = headRadius * (0.5f + (AppWindow.random.nextFloat() * 0.5f));

        pnt = parentBone.pnt.copy();
        neckBotBoneIdx = skeleton.addChildBone(parentBoneIdx, ("neck_bottom_" + Integer.toString(limbIdx)), -1, neckRadius, pnt);

        pnt=parentBone.pnt.copy();
        vct = new RagPoint(0.0f, neckLength, 0.0f);
        vct.rotateX(-AppWindow.random.nextFloat(15.0f));
        pnt.addPoint(vct);
        neckTopBoneIdx = skeleton.addChildBone(parentBoneIdx, ("neck_top_" + Integer.toString(limbIdx)), -1, neckRadius, pnt);

            // the head bones

        pnt=pnt.copy();
        pnt.y+=(neckLength*0.5f);

        headBottomBoneIdx = skeleton.addChildBone(neckTopBoneIdx, ("head_bottom_" + Integer.toString(limbIdx)), -1, headRadius, pnt);

        headLength = headRadius * (0.9f + (AppWindow.random.nextFloat(0.6f)));

        pnt=pnt.copy();
        vct=new RagPoint(0.0f,headLength,0.0f);
        vct.rotateX(-AppWindow.random.nextFloat(25.0f));
        pnt.addPoint(vct);

        headTopBoneIdx=skeleton.addChildBone(headBottomBoneIdx,("head_top_"+Integer.toString(limbIdx)),-1,headRadius,pnt);

            // the limb over the neck and head

        meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        skeleton.addLimb(("neck_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_NECK, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, neckBotBoneIdx, neckTopBoneIdx);
        skeleton.addLimb(("jaw_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_NECK, Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, meshScale, neckTopBoneIdx, headBottomBoneIdx);
        skeleton.addLimb(("head_" + Integer.toString(limbIdx)), Limb.UV_MAP_TYPE_HEAD, Limb.MESH_TYPE_CYLINDER_CLOSE_ALL, Limb.LIMB_AXIS_Y, meshScale, headBottomBoneIdx, headTopBoneIdx);
    }

        //
        // general body
        //

    public void buildBody(Skeleton skeleton, float hunchAng, boolean standing, float scaleFactor) {
        int hipBoneIdx, waistBoneIdx, torsoBottomBoneIdx, torsoShoulderBoneIdx, torsoTopBoneIdx;
        float hipHigh, hipAdd, torsoAdd, radius;
        float minRadius, extraRadius;
        RagPoint hipPnt, waistPnt, torsoBottomPnt, torsoShoulderPnt, torsoTopPnt;
        RagPoint meshScale;

        hipHigh = (standing ? 2.5f : 1.5f) + AppWindow.random.nextFloat(1.5f);
        hipAdd = 0.5f + AppWindow.random.nextFloat(0.5f);
        torsoAdd = 0.5f + AppWindow.random.nextFloat(0.4f);
        minRadius = 0.4f + AppWindow.random.nextFloat(1.5f);
        extraRadius = minRadius * 0.2f;

            // the spine

        hipPnt = new RagPoint(0, hipHigh, 0);
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        hipBoneIdx = skeleton.addChildBone(0, "Hip", -1, radius, hipPnt);            // 0 is always the root bone

        waistPnt = new RagPoint(0, hipAdd, 0);
        if (hunchAng!=0.0f) waistPnt.rotateX(hunchAng-(0.5f+(AppWindow.random.nextFloat()*0.05f)));
        waistPnt.y+=hipPnt.y;
        waistPnt.z+=hipPnt.z;
        radius = minRadius;
        waistBoneIdx = skeleton.addChildBone(hipBoneIdx, "Waist", -1, radius, waistPnt);

        torsoBottomPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoBottomPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat() * 0.05f)));
        }
        torsoBottomPnt.y += waistPnt.y;
        torsoBottomPnt.z += waistPnt.z;
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoBottomBoneIdx = skeleton.addChildBone(waistBoneIdx, "Torso_Bottom", -1, radius, torsoBottomPnt);

        torsoShoulderPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoShoulderPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat() * 0.05f)));
        }
        torsoShoulderPnt.y += torsoBottomPnt.y;
        torsoShoulderPnt.z += torsoBottomPnt.z;
        radius = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoShoulderBoneIdx = skeleton.addChildBone(torsoBottomBoneIdx, "Torso_Shoulder", -1, radius, torsoShoulderPnt);

        torsoTopPnt = new RagPoint(0, torsoAdd, 0);
        if (hunchAng != 0.0f) {
            torsoTopPnt.rotateX(hunchAng - (0.5f + (AppWindow.random.nextFloat() * 0.05f)));
        }
        torsoTopPnt.y += torsoShoulderPnt.y;
        torsoTopPnt.z += torsoShoulderPnt.z;
        radius = minRadius;
        torsoTopBoneIdx = skeleton.addChildBone(torsoShoulderBoneIdx, "Torso_Top", -1, radius, torsoTopPnt);

        // scaling
        if (standing) {
            meshScale = new RagPoint(1.0f, 1.0f, scaleFactor);
        } else {
            meshScale = new RagPoint(1.0f, scaleFactor, 1.0f);
        }

        // body limbs
        skeleton.addLimb("hip", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER_CLOSE_BOTTOM, (standing ? Limb.LIMB_AXIS_Y : Limb.LIMB_AXIS_Z), meshScale, hipBoneIdx, waistBoneIdx);
        skeleton.addLimb("waist", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER, (standing ? Limb.LIMB_AXIS_Y : Limb.LIMB_AXIS_Z), meshScale, waistBoneIdx, torsoBottomBoneIdx);
        skeleton.addLimb("torso_shoulder", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER, (standing ? Limb.LIMB_AXIS_Y : Limb.LIMB_AXIS_Z), meshScale, torsoBottomBoneIdx, torsoShoulderBoneIdx);
        skeleton.addLimb("torso_top", Limb.UV_MAP_TYPE_BODY, Limb.MESH_TYPE_CYLINDER_CLOSE_TOP, (standing ? Limb.LIMB_AXIS_Y : Limb.LIMB_AXIS_Z), meshScale, torsoShoulderBoneIdx, torsoTopBoneIdx);
    }

        //
        // arms
        //

    private void buildArmsRandomSet(Skeleton skeleton, int boneIdx, int armCount, int limbNameOffset, float limbRadius, float armLength, int fingerCount, float scaleFactor) {
        int n;
        float ang;

        for (n=0;n!=armCount;n++) {
            ang=(float)(AppWindow.random.nextInt(4)*90)+(25.0f-(AppWindow.random.nextFloat()*50.0f));
            ang=AppWindow.random.nextFloat()*360.0f;
            if (AppWindow.random.nextFloat()<0.8f) {
                buildLimbArm(skeleton, (n + limbNameOffset), boneIdx, limbRadius, armLength, ang, fingerCount, scaleFactor);
            }
            else {
                buildLimbWhip(skeleton, (n + limbNameOffset), boneIdx, limbRadius, armLength, ang, scaleFactor);
            }
        }
    }

    private void buildArmsBilateralSet(Skeleton skeleton, int boneIdx, int limbNameOffset, float limbRadius, float armLength, int fingerCount, float scaleFactor) {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm(skeleton, (limbNameOffset + 1), boneIdx, limbRadius, armLength, 90.0f, fingerCount, scaleFactor);
            buildLimbArm(skeleton, (limbNameOffset + 2), boneIdx, limbRadius, armLength, 270.0f, fingerCount, scaleFactor);
        }
        else {
            buildLimbWhip(skeleton, (limbNameOffset + 1), boneIdx, limbRadius, armLength, 90.0f, scaleFactor);
            buildLimbWhip(skeleton, (limbNameOffset + 2), boneIdx, limbRadius, armLength, 270.0f, scaleFactor);
        }
    }

    private void buildArms(Skeleton skeleton, boolean standing, boolean forceBilateral, float limbRadius, float scaleFactor) {
        int boneIdx, armCount, fingerCount;
        float armLength;
        boolean topArms, midArms;

        // some settings
        armLength = 1.0f + AppWindow.random.nextFloat(1.5f);
        armCount=1+AppWindow.random.nextInt(3);
        fingerCount=AppWindow.random.nextInt(5);

            // determine number of arms

        topArms=false;
        midArms=false;

        if (!standing) {
            topArms=(AppWindow.random.nextFloat()<0.3f);
        }
        else {
            topArms=(AppWindow.random.nextFloat()<0.9f);
            midArms=(AppWindow.random.nextFloat()<0.2f);
        }

            // the arm pairs

        if (topArms) {
            boneIdx = skeleton.findBoneIndex("Torso_Shoulder");

            if (!forceBilateral) {
                buildArmsRandomSet(skeleton, boneIdx, armCount, 0, limbRadius, armLength, fingerCount, scaleFactor);
            }
            else {
                buildArmsBilateralSet(skeleton, boneIdx, 0, limbRadius, armLength, fingerCount, scaleFactor);
            }
        }

        if (midArms) {
            boneIdx = skeleton.findBoneIndex("Torso_Bottom");

            if (!forceBilateral) {
                buildArmsRandomSet(skeleton, boneIdx, armCount, armCount, limbRadius, armLength, fingerCount, scaleFactor);
            }
            else {
                buildArmsBilateralSet(skeleton, boneIdx, 2, limbRadius, armLength, fingerCount, scaleFactor);
            }
        }
    }

        //
        // legs
        //

    public void buildLegs(Skeleton skeleton, boolean standing, boolean forceBilateral, float limbRadius, float scaleFactor) {
        int boneIdx, toeCount;
        float footRot, footLength;

        // some settings
        footRot = AppWindow.random.nextFloat(15.0f);
        footLength = limbRadius + (AppWindow.random.nextFloat(limbRadius * 2.0f));
        toeCount=AppWindow.random.nextInt(5);

            // hip legs

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbLeg(skeleton, 1, boneIdx, limbRadius, 90.0f, footLength, footRot, toeCount, scaleFactor);
        buildLimbLeg(skeleton, 2, boneIdx, limbRadius, 270.0f, footLength, -footRot, toeCount, scaleFactor);

            // front legs

        if (!standing) {
            boneIdx = skeleton.findBoneIndex("Torso_Shoulder");
            buildLimbLeg(skeleton, 3, boneIdx, limbRadius, 90.0f, footLength, footRot, toeCount, scaleFactor);
            buildLimbLeg(skeleton, 4, boneIdx, limbRadius, 270.0f, footLength, -footRot, toeCount, scaleFactor);
        }
    }

        //
        // tails
        //

    public void buildTail(Skeleton skeleton, boolean stranding, float limbRadius, float scaleFactor) {
        int boneIdx;
        float whipLength;

        if (AppWindow.random.nextFloat()<0.7f) return;

        whipLength = 0.7f + AppWindow.random.nextFloat(1.0f);

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbWhip(skeleton, 5, boneIdx, limbRadius, whipLength, 180.0f, scaleFactor);
    }

        //
        // heads
        //

    public void buildHead(Skeleton skeleton, float limbRadius, float scaleFactor) {
        int boneIdx;
        float headRadius;

        headRadius = 0.4f + (AppWindow.random.nextFloat(0.6f));

        boneIdx=skeleton.findBoneIndex("Torso_Top");
        buildLimbHead(skeleton, 0, boneIdx, limbRadius, headRadius, scaleFactor);
    }

        //
        // build skeleton bones
        //

    public Skeleton build() {
        float hunchAng, scaleFactor, limbRadius;
        boolean standing, thin, forceBilateral;
        Skeleton skeleton;

        skeleton = new Skeleton();

        standing = AppWindow.settingsModel.isStanding();
        thin = AppWindow.settingsModel.isThin();
        forceBilateral = AppWindow.settingsModel.isForceBilateral();

        // skeleton hunch angle
        if (!standing) {
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
        buildBody(skeleton, hunchAng, standing, scaleFactor);
        buildLegs(skeleton, standing, ((!standing) || (forceBilateral)), limbRadius, scaleFactor);
        buildArms(skeleton, standing, forceBilateral, limbRadius, scaleFactor);
        buildTail(skeleton, standing, limbRadius, scaleFactor);
        buildHead(skeleton, limbRadius, scaleFactor);

        // this is just so we can display it turned or not
        skeleton.standing = standing;

        return(skeleton);
     }

}
