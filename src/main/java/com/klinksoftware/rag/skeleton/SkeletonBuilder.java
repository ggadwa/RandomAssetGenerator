package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //

    public void buildLimbLeg(Skeleton skeleton, int limbIdx, int parentBoneIdx, float legRadius, float rotOffset, float footLength, float footRot, int toeCount)    {
        int n, hipBoneIdx, kneeBoneIdx, ankleBoneIdx, footBoneIdx;
        int heelBoneIdx, knuckleBoneIdx, toeBoneIdx;
        float meshScale, footRadius, toeRadius;
        Bone parentBone;
        RagPoint pnt, vct, pushVct, footVct, footPnt;
        RagPoint knuckleVct, knucklePnt, toeVct, toePnt, toeAdd;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        pushVct=new RagPoint(0.0f,0.0f,(parentBone.gravityLockDistance-(legRadius*0.5f)));
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

        meshScale=0.7f+(AppWindow.random.nextFloat()*0.3f);
        skeleton.addLimb(("leg_top_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 5, 10, new RagPoint(1.0f, 1.0f, meshScale), hipBoneIdx, kneeBoneIdx);

        meshScale = 0.7f + (AppWindow.random.nextFloat() * 0.3f);
        skeleton.addLimb(("leg_bottom_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 5, 10, new RagPoint(1.0f, 1.0f, meshScale), kneeBoneIdx, ankleBoneIdx);

        meshScale = 0.7f + (AppWindow.random.nextFloat() * 0.3f);
        skeleton.addLimb(("ankle_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 5, 10, new RagPoint(1.0f, 1.0f, meshScale), ankleBoneIdx, heelBoneIdx);

            // the foot bones
            // feet are always parallel to ground, towards front
        footRadius=(legRadius*(1.2f+(AppWindow.random.nextFloat()*0.3f)));

        footVct=new RagPoint(0.0f,0.0f,footLength);
        footVct.rotateY(footRot);

        footPnt=skeleton.bones.get(heelBoneIdx).pnt.copy();
        footPnt.addPoint(footVct);
        footPnt.y=0.0f;

        footBoneIdx = skeleton.addChildBone(heelBoneIdx, ("foot_" + Integer.toString(limbIdx)), -1, footRadius, footPnt);
        skeleton.addLimb(("foot_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_GLOBE, Limb.LIMB_AXIS_Z, 5, 5, new RagPoint(1.0f, 0.7f, 1.0f), heelBoneIdx, footBoneIdx);

            // toe limbs

        if (toeCount==0) return;

        toeRadius=(footRadius*1.5f)/(float)toeCount;

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
            skeleton.addLimb(("toe_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "limb", Limb.MESH_TYPE_GLOBE, Limb.LIMB_AXIS_Z, 4, 4, new RagPoint(1.0f, 0.7f, 1.0f), knuckleBoneIdx, toeBoneIdx);

            knucklePnt.addPoint(toeAdd);
            toePnt.addPoint(toeAdd);
        }
    }

        //
        // arm limb
        //

    public void buildLimbArm(Skeleton skeleton, int limbIdx, int parentBoneIdx, float armRadius, float armLength, float rotOffset, int fingerCount)    {
        int                 n,axis,
                            shoulderBoneIdx,elbowBoneIdx,wristBoneIdx,
                            handBoneIdx,knuckleBoneIdx,fingerBoneIdx;
        float               fy,meshScale,handRadius,fingerRadius;
        Bone                parentBone;
        RagPoint            pnt,vct,pushVct,handPnt,
                            knuckleVct,knucklePnt,fingerVct,fingerPnt;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;

        pushVct=new RagPoint(0.0f,0.0f,(parentBone.gravityLockDistance-(armRadius*0.5f)));
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

        meshScale=0.7f+(AppWindow.random.nextFloat()*0.3f);
        skeleton.addLimb(("arm_top_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, axis, 8, 5, new RagPoint(1.0f, 1.0f, meshScale), shoulderBoneIdx, elbowBoneIdx);

        meshScale = 0.7f + (AppWindow.random.nextFloat() * 0.3f);
        skeleton.addLimb(("arm_bottom_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, axis, 8, 5, new RagPoint(1.0f, 1.0f, meshScale), elbowBoneIdx, wristBoneIdx);

            // hand limb

        handRadius=armRadius*(1.1f+(AppWindow.random.nextFloat()*0.5f));
        handPnt=new RagPoint((pnt.x+vct.x),(pnt.y+vct.y),(pnt.z+vct.z));

        handBoneIdx=skeleton.addChildBone(wristBoneIdx,("hand_"+Integer.toString(limbIdx)),-1,handRadius,handPnt);
        skeleton.addLimb(("hand_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_GLOBE, axis, 5, 5, new RagPoint(1.0f, 1.0f, meshScale), wristBoneIdx, handBoneIdx);

            // finger limbs

        if (fingerCount==0) return;

        fingerRadius=(handRadius*1.5f)/(float)fingerCount;

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

            skeleton.addLimb(("finger_" + Integer.toString(limbIdx) + "_" + Integer.toString(n)), "limb", Limb.MESH_TYPE_GLOBE, axis, 4, 4, new RagPoint(1.0f, 1.0f, meshScale), knuckleBoneIdx, fingerBoneIdx);

            fy+=fingerRadius;
        }
    }

        //
        // whip limbs
        //

    public void buildLimbWhip(Skeleton skeleton,int limbIdx,int parentBoneIdx,float whipRadius,float whipLength,float rotOffset)
    {
        int                 axis,
                            whip0BoneIdx,whip1BoneIdx,whip2BoneIdx,whip3BoneIdx;
        Bone                parentBone;
        RagPoint            pnt,vct,pushVct;

        parentBone=skeleton.bones.get(parentBoneIdx);

            // size and position around body

        pushVct=new RagPoint(0.0f,0.0f,(parentBone.gravityLockDistance-(whipRadius*0.5f)));
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

        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?Limb.LIMB_AXIS_Z:Limb.LIMB_AXIS_X;
        skeleton.addLimb(("whip_top_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_GLOBE, axis, 8, 5, new RagPoint(1.0f, 1.0f, 1.0f), whip0BoneIdx, whip1BoneIdx);
        skeleton.addLimb(("whip_middle_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_GLOBE, axis, 8, 5, new RagPoint(1.0f, 1.0f, 1.0f), whip1BoneIdx, whip2BoneIdx);
        skeleton.addLimb(("whip_end_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_GLOBE, axis, 8, 5, new RagPoint(1.0f, 1.0f, 1.0f), whip2BoneIdx, whip3BoneIdx);
    }

        //
        // head limb
        //

    public void buildLimbHead(Skeleton skeleton,int limbIdx,int parentBoneIdx,float headRadius)
    {
        int                 neckBoneIdx,headBottomBoneIdx,headTopBoneIdx;
        float               parentOffsetY,neckRadius,neckLength,headLength;
        Bone                parentBone;
        RagPoint            pnt,vct,scale;

        parentBone=skeleton.bones.get(parentBoneIdx);
        parentOffsetY=(parentBone.gravityLockDistance*0.5f);

            // create the neck and head bones

        neckRadius=headRadius*(0.3f+(AppWindow.random.nextFloat()*0.5f));
        neckLength=headRadius*(0.5f+(AppWindow.random.nextFloat()*0.5f));

        pnt=parentBone.pnt.copy();
        vct=new RagPoint(0.0f,(parentOffsetY+neckLength),0.0f);
        vct.rotateX(-(AppWindow.random.nextFloat()*15.0f));
        pnt.addPoint(vct);

        neckBoneIdx=skeleton.addChildBone(parentBoneIdx,("neck_"+Integer.toString(limbIdx)),-1,neckRadius,pnt);

            // the head bones

        pnt=pnt.copy();
        pnt.y+=(neckLength*0.5f);

        headBottomBoneIdx=skeleton.addChildBone(neckBoneIdx,("head_bottom_"+Integer.toString(limbIdx)),-1,headRadius,pnt);

        headLength=headRadius*(0.9f+(AppWindow.random.nextFloat()*0.4f));

        pnt=pnt.copy();
        vct=new RagPoint(0.0f,headLength,0.0f);
        vct.rotateX(-(AppWindow.random.nextFloat()*25.0f));
        pnt.addPoint(vct);

        headTopBoneIdx=skeleton.addChildBone(headBottomBoneIdx,("head_top_"+Integer.toString(limbIdx)),-1,headRadius,pnt);

            // the limb over the neck and head

        scale = new RagPoint(1.0f, 1.0f, 1.0f);
        skeleton.addLimb(("neck_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 10, 10, scale, parentBoneIdx, neckBoneIdx);

        scale = new RagPoint(1.0f, 1.0f, 1.0f);
        skeleton.addLimb(("jaw_" + Integer.toString(limbIdx)), "limb", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 10, 10, scale, neckBoneIdx, headBottomBoneIdx);

        scale = new RagPoint((0.7f + (AppWindow.random.nextFloat() * 0.3f)), (0.7f + (AppWindow.random.nextFloat() * 0.3f)), (0.7f + (AppWindow.random.nextFloat() * 0.3f)));
        skeleton.addLimb(("head_" + Integer.toString(limbIdx)), "head", Limb.MESH_TYPE_GLOBE, Limb.LIMB_AXIS_Y, 10, 10, scale, headBottomBoneIdx, headTopBoneIdx);
    }

        //
        // general body
        //

    public void buildBody(Skeleton skeleton, float hunchAng, boolean thin)    {
        int hipBoneIdx, waistBoneIdx, torsoBoneIdx, topTorsoBoneIdx;
        float high, gravityLockDistance, meshScaleY, meshScaleZ;
        float minRadius, extraRadius;
        float hipHigh, waistHigh, torsoHigh, torsoTopHigh;
        RagPoint hipPnt, waistPnt, torsoPnt, topTorsoPnt;

        high = 3.0f + AppWindow.random.nextFloat(4.0f);
        minRadius = 0.5f + AppWindow.random.nextFloat(2.5f);
        extraRadius = minRadius * 0.75f;

            // random heights, as a section of
            // the full height

        hipHigh = high * (0.4f + (AppWindow.random.nextFloat(0.1f)));
        waistHigh = (high - hipHigh) * (0.1f + (AppWindow.random.nextFloat(0.1f)));
        torsoHigh = (high - hipHigh) * (0.1f + (AppWindow.random.nextFloat(0.1f)));
        torsoTopHigh=high-(hipHigh+waistHigh+torsoHigh);

            // the spine

        hipPnt=new RagPoint(0,hipHigh,0);
        gravityLockDistance = minRadius + (AppWindow.random.nextFloat(extraRadius));
        hipBoneIdx=skeleton.addChildBone(0,"Hip",-1,gravityLockDistance,hipPnt);            // 0 is always the root bone

        waistPnt=new RagPoint(0,waistHigh,0);
        if (hunchAng!=0.0f) waistPnt.rotateX(hunchAng-(0.5f+(AppWindow.random.nextFloat()*0.05f)));
        waistPnt.y+=hipPnt.y;
        waistPnt.z+=hipPnt.z;
        gravityLockDistance = minRadius;
        waistBoneIdx=skeleton.addChildBone(hipBoneIdx,"Waist",-1,gravityLockDistance,waistPnt);

        torsoPnt=new RagPoint(0,torsoHigh,0);
        if (hunchAng!=0.0f) torsoPnt.rotateX(hunchAng-(0.5f+(AppWindow.random.nextFloat()*0.05f)));
        torsoPnt.y+=waistPnt.y;
        torsoPnt.z+=waistPnt.z;
        gravityLockDistance = minRadius + (AppWindow.random.nextFloat(extraRadius));
        torsoBoneIdx=skeleton.addChildBone(waistBoneIdx,"Torso",-1,gravityLockDistance,torsoPnt);

        topTorsoPnt=new RagPoint(0,torsoTopHigh,0);
        if (hunchAng!=0.0f) topTorsoPnt.rotateX(hunchAng-(0.5f+(AppWindow.random.nextFloat()*0.05f)));
        topTorsoPnt.y+=torsoPnt.y;
        topTorsoPnt.z+=torsoPnt.z;
        gravityLockDistance = minRadius + (AppWindow.random.nextFloat(extraRadius));
        topTorsoBoneIdx=skeleton.addChildBone(torsoBoneIdx,"Torso_Top",-1,gravityLockDistance,topTorsoPnt);

        // hip limb
        if (!thin) {
            meshScaleZ = 0.5f + (AppWindow.random.nextFloat(0.3f));  // big
            meshScaleY = 0.7f + (AppWindow.random.nextFloat(0.3f));
        } else {
            meshScaleZ = 0.2f + (AppWindow.random.nextFloat(0.2f));  // thin
            meshScaleY = 0.4f + (AppWindow.random.nextFloat(0.2f));
        }
        skeleton.addLimb("hip", "body", Limb.MESH_TYPE_GLOBE, Limb.LIMB_AXIS_Y, 12, 12, new RagPoint(1.0f, meshScaleY, meshScaleZ), hipBoneIdx, waistBoneIdx);

        if (!thin) {
            meshScaleZ = 0.5f + (AppWindow.random.nextFloat(0.3f));  // big
            meshScaleY = 0.7f + (AppWindow.random.nextFloat(0.3f));
        } else {
            meshScaleZ = 0.2f + (AppWindow.random.nextFloat(0.2f));  // thin
            meshScaleY = 0.4f + (AppWindow.random.nextFloat(0.2f));
        }
        skeleton.addLimb("waist", "body", Limb.MESH_TYPE_CYLINDER, Limb.LIMB_AXIS_Y, 12, 12, new RagPoint(1.0f, meshScaleY, meshScaleZ), waistBoneIdx, torsoBoneIdx);

        if (!thin) {
            meshScaleZ = 0.5f + (AppWindow.random.nextFloat(0.3f));  // big
            meshScaleY = 0.7f + (AppWindow.random.nextFloat(0.3f));
        } else {
            meshScaleZ = 0.2f + (AppWindow.random.nextFloat(0.2f));  // thin
            meshScaleY = 0.4f + (AppWindow.random.nextFloat(0.2f));
        }
        skeleton.addLimb("torso", "body", Limb.MESH_TYPE_GLOBE, Limb.LIMB_AXIS_Y, 12, 12, new RagPoint(1.0f, meshScaleY, meshScaleZ), torsoBoneIdx, topTorsoBoneIdx);
    }

        //
        // arms
        //

    private void buildArmsRandomSet(Skeleton skeleton,int boneIdx,int armCount,int limbNameOffset,float limbRadius,float armLength,int fingerCount)
    {
        int         n;
        float       ang;

        for (n=0;n!=armCount;n++) {
            ang=(float)(AppWindow.random.nextInt(4)*90)+(25.0f-(AppWindow.random.nextFloat()*50.0f));
            ang=AppWindow.random.nextFloat()*360.0f;
            if (AppWindow.random.nextFloat()<0.8f) {
                buildLimbArm(skeleton, (n + limbNameOffset), boneIdx, limbRadius, armLength, ang, fingerCount);
            }
            else {
                buildLimbWhip(skeleton,(n+limbNameOffset),boneIdx,limbRadius,armLength,ang);
            }
        }
    }

    private void buildArmsBilateralSet(Skeleton skeleton,int boneIdx,int limbNameOffset,float limbRadius,float armLength,int fingerCount)
    {
        if (AppWindow.random.nextFloat()<0.8f) {
            buildLimbArm(skeleton, (limbNameOffset + 1), boneIdx, limbRadius, armLength, 90.0f, fingerCount);
            buildLimbArm(skeleton, (limbNameOffset + 2), boneIdx, limbRadius, armLength, 270.0f, fingerCount);
        }
        else {
            buildLimbWhip(skeleton,(limbNameOffset+1),boneIdx,limbRadius,armLength,90.0f);
            buildLimbWhip(skeleton,(limbNameOffset+2),boneIdx,limbRadius,armLength,270.0f);
        }
    }

    private void buildArms(Skeleton skeleton,boolean standing,boolean forceBilateral)
    {
        int             n,boneIdx,armCount,fingerCount;
        float           ang,radius,limbRadius,armLength;
        boolean         topArms,midArms;

            // some settings

        radius=1.0f; // TODO - randomize
        limbRadius=radius*(0.15f+(AppWindow.random.nextFloat()*0.2f));
        armLength=(radius*1.5f)+(AppWindow.random.nextFloat()*radius);
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
            boneIdx=skeleton.findBoneIndex("Torso_Top");

            if (!forceBilateral) {
                buildArmsRandomSet(skeleton,boneIdx,armCount,0,limbRadius,armLength,fingerCount);
            }
            else {
                buildArmsBilateralSet(skeleton,boneIdx,0,limbRadius,armLength,fingerCount);
            }
        }

        if (midArms) {
            boneIdx=skeleton.findBoneIndex("Torso");

            if (!forceBilateral) {
                buildArmsRandomSet(skeleton,boneIdx,armCount,armCount,limbRadius,armLength,fingerCount);
            }
            else {
                buildArmsBilateralSet(skeleton,boneIdx,2,limbRadius,armLength,fingerCount);
            }
        }
    }

        //
        // legs
        //

    public void buildLegs(Skeleton skeleton,boolean standing,boolean forceBilateral)
    {
        int         boneIdx,toeCount;
        float       radius,limbRadius,footRot,footLength;

            // some settings

        radius=1.0f; // TODO randomize
        limbRadius=radius*(0.2f+(AppWindow.random.nextFloat()*0.2f));
        footRot=AppWindow.random.nextFloat()*15.0f;
        footLength=limbRadius+(AppWindow.random.nextFloat()*(limbRadius*2.0f));
        toeCount=AppWindow.random.nextInt(5);

            // hip legs

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbLeg(skeleton, 1, boneIdx, limbRadius, 90.0f, footLength, footRot, toeCount);
        buildLimbLeg(skeleton, 2, boneIdx, limbRadius, 270.0f, footLength, -footRot, toeCount);

            // front legs

        if (!standing) {
            boneIdx=skeleton.findBoneIndex("Torso_Top");
            buildLimbLeg(skeleton, 3, boneIdx, limbRadius, 90.0f, footLength, footRot, toeCount);
            buildLimbLeg(skeleton, 4, boneIdx, limbRadius, 270.0f, footLength, -footRot, toeCount);
        }
    }

        //
        // tails
        //

    public void buildTail(Skeleton skeleton,boolean stranding)
    {
        int         boneIdx;
        float       radius,limbRadius,whipLength;

        if (AppWindow.random.nextFloat()<0.7f) return;

        radius=1.0f; // TODO randomize
        limbRadius=radius*(0.1f+(AppWindow.random.nextFloat()*0.1f));
        whipLength=radius+(AppWindow.random.nextFloat()*radius);

        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbWhip(skeleton,5,boneIdx,limbRadius,whipLength,180.0f);
    }

        //
        // heads
        //

    public void buildHead(Skeleton skeleton)
    {
        int         boneIdx;
        float       radius,headRadius;

        radius=1.0f; // TODO randomize
        headRadius=radius*(0.3f+(AppWindow.random.nextFloat()*0.5f));

        boneIdx=skeleton.findBoneIndex("Torso_Top");
        buildLimbHead(skeleton,0,boneIdx,headRadius);
    }

        //
        // build skeleton bones
        //

    public Skeleton build()
    {
        float hunchAng;
        boolean standing, thin, forceBilateral;
        Skeleton skeleton;

        skeleton = new Skeleton();

        standing = AppWindow.settingsModel.isStanding();
        thin = AppWindow.settingsModel.isThin();
        forceBilateral = AppWindow.settingsModel.isForceBilateral();

        // skeleton hunch angle
        if (!standing) {
            hunchAng = (60.0f + (AppWindow.random.nextFloat(95.0f)));
        }
        else {
            hunchAng = AppWindow.random.nextFloat(20.0f);
        }

            // build the skeleton

        buildBody(skeleton, hunchAng, thin);
        buildLegs(skeleton, standing, ((!standing) || (forceBilateral)));
        buildArms(skeleton, standing, forceBilateral);
        buildTail(skeleton, standing);
        buildHead(skeleton);

        return(skeleton);
     }

}
