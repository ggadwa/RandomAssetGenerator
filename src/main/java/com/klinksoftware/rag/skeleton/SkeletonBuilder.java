package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //
        
    public void buildLimbLeg(Skeleton skeleton,int limbIdx,int parentBoneIdx,float legRadius,float rotOffset,float footLength,float footRot,int toeCount,boolean flipped)
    {
        int                 n,hipBoneIdx,kneeBoneIdx,ankleBoneIdx,footBoneIdx,
                            heelBoneIdx,knuckleBoneIdx,toeBoneIdx;
        float               fx,meshScale,toeRadius,toeDistance;
        Bone                parentBone;
        RagPoint            pnt,vct,pushVct,footVct,footPnt,
                            knuckleVct,knucklePnt,toePnt;
        
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
        
        hipBoneIdx=skeleton.addChildBone(parentBoneIdx,("hip_"+Integer.toString(limbIdx)),-1,(legRadius*(1.0f+(GeneratorMain.random.nextFloat()*0.3f))),new RagPoint(pnt.x,pnt.y,pnt.z));
        kneeBoneIdx=skeleton.addChildBone(hipBoneIdx,("knee_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.4f)),(pnt.y+(vct.y*0.4f)),(pnt.z+(vct.z*0.4f))));
        ankleBoneIdx=skeleton.addChildBone(kneeBoneIdx,("ankle_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.8f)),(pnt.y+(vct.y*0.8f)),(pnt.z+(vct.z*0.8f))));
        heelBoneIdx=skeleton.addChildBone(ankleBoneIdx,("heel_"+Integer.toString(limbIdx)),-1,legRadius,new RagPoint((pnt.x+(vct.x*0.95f)),(pnt.y+(vct.y*0.95f)),(pnt.z+(vct.z*0.95f))));
        
        meshScale=0.7f+(GeneratorMain.random.nextFloat()*0.3f);
        skeleton.addLimb(("leg_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_LEG,Limb.LIMB_AXIS_Y,flipped,8,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{hipBoneIdx,kneeBoneIdx,ankleBoneIdx,heelBoneIdx});
        
            // the foot bones
            // feet are always parallel to ground, towards front
       
        footVct=new RagPoint(0.0f,0.0f,footLength);
        footVct.rotateY(footRot);
        
        footPnt=skeleton.bones.get(heelBoneIdx).pnt.copy();
        footPnt.addPoint(footVct);
        footPnt.y=0.0f;
        footBoneIdx=skeleton.addChildBone(heelBoneIdx,("foot_"+Integer.toString(limbIdx)),-1,(legRadius*(1.2f+(GeneratorMain.random.nextFloat()*0.3f))),footPnt);
        skeleton.addLimb(("foot_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_FOOT,Limb.LIMB_AXIS_Z,flipped,5,5,new RagPoint(1.0f,0.7f,1.0f),new int[]{heelBoneIdx,footBoneIdx});

            // toe limbs
            
        if (toeCount==0) return;
        
        toeRadius=(legRadius/toeCount)*0.8f;
        if (toeRadius<(legRadius*0.1f)) toeRadius=legRadius*0.1f;
        
        toeDistance=toeRadius*1.1f;
        
        knuckleVct=footVct.copy();
        knuckleVct.normalize();
        knuckleVct.scale(footLength*0.4f);
        knucklePnt=new RagPoint((footPnt.x+knuckleVct.x),0.0f,(footPnt.z+knuckleVct.z));
        
        toePnt=new RagPoint((knucklePnt.x+knuckleVct.x),0.0f,(knucklePnt.z+knuckleVct.z));

        fx=knucklePnt.x-((toeCount*0.5f)*toeDistance);

        for (n=0;n!=toeCount;n++) {
            knuckleBoneIdx=skeleton.addChildBone(heelBoneIdx,("toe_knuckle_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,toeRadius,new RagPoint(fx,knucklePnt.y,knucklePnt.z));
            toeBoneIdx=skeleton.addChildBone(knuckleBoneIdx,("toe_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,toeRadius,new RagPoint(fx,toePnt.y,toePnt.z));
            skeleton.addLimb(("toe_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),Limb.LIMB_TYPE_TOE,Limb.LIMB_AXIS_Z,flipped,4,4,new RagPoint(1.0f,0.7f,1.0f),new int[]{knuckleBoneIdx,toeBoneIdx});
            
            fx+=toeDistance;
        }
    }

        //
        // arm limb
        //
    
    public void buildLimbArm(Skeleton skeleton,int limbIdx,int parentBoneIdx,float armRadius,float armLength,float rotOffset,int fingerCount,boolean flipped)
    {
        int                 n,axis,
                            shoulderBoneIdx,elbowBoneIdx,wristBoneIdx,
                            handBoneIdx,knuckleBoneIdx,fingerBoneIdx;
        float               fy,meshScale,handRadius,fingerRadius,fingerDistance;
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
        
        meshScale=0.7f+(GeneratorMain.random.nextFloat()*0.3f);
        skeleton.addLimb(("arm_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_ARM,axis,flipped,8,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{shoulderBoneIdx,elbowBoneIdx,wristBoneIdx});
        
            // hand limb
            
        handRadius=armRadius*(1.0f+(GeneratorMain.random.nextFloat()*0.3f));
        handPnt=new RagPoint((pnt.x+vct.x),(pnt.y+vct.y),(pnt.z+vct.z));
        
        handBoneIdx=skeleton.addChildBone(wristBoneIdx,("hand_"+Integer.toString(limbIdx)),-1,handRadius,handPnt);
        skeleton.addLimb(("hand_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_HAND,axis,flipped,5,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{handBoneIdx});

            // finger limbs
            
        if (fingerCount==0) return;
        
        fingerRadius=(armRadius/fingerCount)*0.8f;
        if (fingerRadius<(armRadius*0.1f)) fingerRadius=armRadius*0.1f;
        
        fingerDistance=fingerRadius*1.1f;
        
        knuckleVct=vct.copy();
        knuckleVct.normalize();
        knuckleVct.scale(handRadius*0.4f);
        knucklePnt=new RagPoint((handPnt.x+knuckleVct.x),(handPnt.y+knuckleVct.y),(handPnt.z+knuckleVct.z));
        
        fingerVct=vct.copy();
        fingerVct.normalize();
        fingerVct.scale(armRadius+(GeneratorMain.random.nextFloat()*armRadius));
        fingerPnt=new RagPoint((knucklePnt.x+fingerVct.x),(knucklePnt.y+fingerVct.y),(knucklePnt.z+fingerVct.z));

        fy=knucklePnt.y-((fingerCount*0.5f)*fingerDistance);
        
        for (n=0;n!=fingerCount;n++) {
            knuckleBoneIdx=skeleton.addChildBone(handBoneIdx,("finger_knuckle_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,fingerRadius,new RagPoint(knucklePnt.x,fy,knucklePnt.z));
            fingerBoneIdx=skeleton.addChildBone(knuckleBoneIdx,("finger_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),-1,fingerRadius,new RagPoint(fingerPnt.x,fy,fingerPnt.z));
            
            skeleton.addLimb(("finger_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),Limb.LIMB_TYPE_FINGER,axis,flipped,4,4,new RagPoint(1.0f,1.0f,meshScale),new int[]{knuckleBoneIdx,fingerBoneIdx});
            
            fy+=fingerDistance;
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
        skeleton.addLimb(("whip_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_WHIP,axis,false,8,5,new RagPoint(1.0f,1.0f,1.0f),new int[]{whip0BoneIdx,whip1BoneIdx,whip2BoneIdx,whip3BoneIdx});
    }
    
        //
        // head limb
        //
        
    public void buildLimbHead(Skeleton skeleton,int limbIdx,int parentBoneIdx,float neckLength,float neckRadius,float jawRadius,float headRadius,boolean hasJaw)
    {
        /*
        let pnt,jawBackPnt,jawFrontPnt,neckPnt,vct;
        let ;
        let ;
        let 
        let neckStartLength,headOffset,headLength,headRot;
        let scaleMin,scaleMax;
        
        int                 neckStartBoneIdx,neckEndBoneIdx,jawBackBoneIdx,jawFrontBoneIdx,headBottomBoneIdx,headTopBoneIdx;
        Bone                parentBone;
        
        parentBone=skeleton.bones.get(parentBoneIdx);
        
        neckLength*=this.sizeFactor;
        neckRadius*=this.sizeFactor;
        jawRadius*=this.sizeFactor;
        headRadius*=this.sizeFactor;
        
            // create the neck
            
        neckStartLength=(parentBone.gravityLockDistance-(neckLength*0.5))*this.sizeFactor;
        
        pnt=parentBone.pnt.copy();
        vct=new RagPoint(0.0f,-neckStartLength,0.0f);
        vct.rotateX(-(GeneratorMain.random.nextFloat()*25.0f));
        pnt.addPoint(vct);
            
        neckStartBoneIdx=skeleton.addChildBone(parentBoneIdx,("neck_bottom_"+Integer.toString(limbIdx)),-1,(neckRadius*(0.8f+(GeneratorMain.random.nextFloat()*0.2f))),pnt));
        
        neckPnt=pnt.copy();
        vct=new RagPoint(0.0f,-neckLength,0.0f);
        vct.rotateX(-(GeneratorMain.random.nextFloat()*25.0f));
        neckPnt.addPoint(vct);
        
        neckEndBoneIdx=skeleton.addChildBone(neckStartBoneIdx,("neck_top_"+Integer.toString(limbIdx)),-1,neckRadius,neckPnt));

        skeleton.addLimb(("neck_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_NECK,Limb.LIMB_AXIS_Y,false,5,5,new RagPoint(1.0f,0.3f,1.0f),new int[]{neckStartBoneIdx,neckEndBoneIdx});
        
            // default placements
            
        headOffset=10;
        headRot=-(GeneratorMain.random.nextFloat()*25.0f);
        
            // create the jaw
            
        if (hasJaw) {
            jawRadius=(headRadius*(0.3f+(GeneratorMain.random.nextFloat()*0.3f));
            
            jawBackPnt=neckPnt.copy();
            vct=new RagPoint(0.0f,-headOffset,-((headRadius*0.6f)-(jawRadius*0.5f)));
            jawBackPnt.addPoint(vct);
            
            jawBackBoneIdx=skeleton.addChildBone(neckEndBoneIdx,("jaw_back_"+Integer.toString(limbIdx)),-1,jawRadius,jawBackPnt));

            jawFrontPnt=neckPnt.copy();
            vct=new RagPoint(0.0f,-headOffset,((headRadius*(0.5f+(GeneratorMain.random.nextFloat()*0.3f))-(jawRadius*0.5)));
            jawFrontPnt.addPoint(vct);

            jawFrontBoneIdx=skeleton.addChildBone(jawBackBoneIdx,("jaw_front_"+Integer.toString(limbIdx)),-1,jawRadius,jawFrontPnt));
            
            scaleMax=new RagPoint((0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)));
            scaleMin=scaleMax.copy();
            scaleMin.y=0.1;
            
            skeleton.addLimb(("jaw_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_JAW,Limb.LIMB_AXIS_Z,false,6,6,new RagPoint(1.0f,1.0f,meshScale),new int[]{jawBackBoneIdx,jawFrontBoneIdx});
        }
        
            // create the head
        
        headLength=headRadius*(0.4f+(GeneratorMain.random.nextFloat()*0.4f));
        
        pnt=neckPnt.copy();
        vct=new RagPoint(0.0f,-headOffset,0.0f);        // no rot here
        pnt.addPoint(vct);
        
        headBottomBoneIdx=skeleton.addChildBone(neckEndBoneIdx,("head_bottom_"+Integer.toString(limbIdx)),-1,headRadius,pnt));
        
        pnt=pnt.copy();
        vct=new RagPoint(0.0f,-headLength,0.0f);
        vct.rotateX(headRot);
        pnt.addPoint(vct);
        
        headTopBoneIdx=skeleton.addChildBone(headBottomBoneIdx,("head_top_"+Integer.toString(limbIdx)),-1,headRadius,pnt));
        
        scaleMin=new RagPoint((0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)));
        scaleMax=scaleMin.copy();
        if (hasJaw) scaleMax.y=0.1;
        
        skeleton.addLimb(("head_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_HEAD,Limb.LIMB_AXIS_Y,false,10,10,new RagPoint(1.0f,1.0f,meshScale),new int[]{headBottomBoneIdx,headTopBoneIdx});
        */
    }
    
        //
        // general body
        //
        
    public void buildBody(Skeleton skeleton,float hunchAng)
    {
        int         hipBoneIdx,waistBoneIdx,torsoBoneIdx,topTorsoBoneIdx;
        float       high,gravityLockDistance,meshScale,
                    radius,extraRadius,
                    hipHigh,waistHigh,torsoHigh,torsoTopHigh;
        RagPoint    hipPnt,waistPnt,torsoPnt,topTorsoPnt;
        
        high=(float)((double)GeneratorMain.settings.get("height"));
        radius=(float)((double)GeneratorMain.settings.get("radius"));
        extraRadius=radius*0.5f;
        
            // random heights, as a section of
            // the full height
            
        hipHigh=high*(0.45f+(GeneratorMain.random.nextFloat()*0.2f));
        waistHigh=(high-hipHigh)*(0.3f+(GeneratorMain.random.nextFloat()*0.03f));
        torsoHigh=(high-hipHigh)*(0.3f+(GeneratorMain.random.nextFloat()*0.03f));
        torsoTopHigh=high-(hipHigh+waistHigh+torsoHigh);

            // the spine
        
        hipPnt=new RagPoint(0,hipHigh,0);
        gravityLockDistance=radius*(GeneratorMain.random.nextFloat()*extraRadius);
        hipBoneIdx=skeleton.addChildBone(0,"Hip",-1,gravityLockDistance,hipPnt);            // 0 is always the root bone
        
        waistPnt=new RagPoint(0,waistHigh,0);
        if (hunchAng!=0.0f) waistPnt.rotateX(hunchAng-(0.5f+(GeneratorMain.random.nextFloat()*0.05f)));
        waistPnt.y+=hipPnt.y;
        waistPnt.z+=hipPnt.z;
        gravityLockDistance=radius*(GeneratorMain.random.nextFloat()*extraRadius);
        waistBoneIdx=skeleton.addChildBone(hipBoneIdx,"Waist",-1,gravityLockDistance,waistPnt);
        
        torsoPnt=new RagPoint(0,torsoHigh,0);
        if (hunchAng!=0.0f) torsoPnt.rotateX(hunchAng-(0.5f+(GeneratorMain.random.nextFloat()*0.05f)));
        torsoPnt.y+=waistPnt.y;
        torsoPnt.z+=waistPnt.z;
        gravityLockDistance=radius*(GeneratorMain.random.nextFloat()*extraRadius);
        torsoBoneIdx=skeleton.addChildBone(waistBoneIdx,"Torso",-1,gravityLockDistance,torsoPnt);
        
        topTorsoPnt=new RagPoint(0,torsoTopHigh,0);
        if (hunchAng!=0.0f) topTorsoPnt.rotateX(hunchAng-(0.5f+(GeneratorMain.random.nextFloat()*0.05f)));
        topTorsoPnt.y+=torsoPnt.y;
        topTorsoPnt.z+=torsoPnt.z;
        gravityLockDistance=radius*(GeneratorMain.random.nextFloat()*extraRadius);
        topTorsoBoneIdx=skeleton.addChildBone(torsoBoneIdx,"Torso_Top",-1,gravityLockDistance,topTorsoPnt);

            // the body limb
            
        meshScale=0.7f+(GeneratorMain.random.nextFloat()*0.3f);
        skeleton.addLimb("body",Limb.LIMB_TYPE_BODY,Limb.LIMB_AXIS_Y,false,12,12,new RagPoint(1.0f,1.0f,meshScale),new int[]{hipBoneIdx,waistBoneIdx,torsoBoneIdx,topTorsoBoneIdx});
    }

    public void buildArms(Skeleton skeleton,boolean standing)
    {
        int             boneIdx,fingerCount;
        float           radius,limbRadius,armLength;
        boolean         topArms,midArms;
        
            // some settings
            
        radius=(float)((double)GeneratorMain.settings.get("radius"));
        limbRadius=radius*(0.1f+(GeneratorMain.random.nextFloat()*0.1f));
        armLength=radius+(GeneratorMain.random.nextFloat()*radius);
        fingerCount=GeneratorMain.random.nextInt(5);
        
            // determine number of arms
        
        topArms=false;
        midArms=false;
        
        if (!standing) {
            topArms=(GeneratorMain.random.nextFloat()<0.3f);
        }
        else {
            topArms=(GeneratorMain.random.nextFloat()<0.9f);
            midArms=(GeneratorMain.random.nextFloat()<0.2f);
        }
        
            // the arm pairs
            
        if (topArms) {
            boneIdx=skeleton.findBoneIndex("Torso_Top");
        
            if (GeneratorMain.random.nextFloat()<0.8f) {
                buildLimbArm(skeleton,1,boneIdx,limbRadius,armLength,90.0f,fingerCount,false);
                buildLimbArm(skeleton,2,boneIdx,limbRadius,armLength,270.0f,fingerCount,true);
            }
            else {
                buildLimbWhip(skeleton,1,boneIdx,limbRadius,armLength,90.0f);
                buildLimbWhip(skeleton,2,boneIdx,limbRadius,armLength,270.0f);
            }
        }
        
        if (midArms) {
            boneIdx=skeleton.findBoneIndex("Torso");
        
            if (GeneratorMain.random.nextFloat()<0.8f) {
                buildLimbArm(skeleton,3,boneIdx,limbRadius,armLength,90.0f,fingerCount,false);
                buildLimbArm(skeleton,4,boneIdx,limbRadius,armLength,270.0f,fingerCount,true);
            }
            else {
                buildLimbWhip(skeleton,3,boneIdx,limbRadius,armLength,90.0f);
                buildLimbWhip(skeleton,4,boneIdx,limbRadius,armLength,270.0f);
            }
        }
    }
    
        //
        // legs
        //
        
    public void buildLegs(Skeleton skeleton,boolean standing)
    {
        int         boneIdx,toeCount;
        float       radius,limbRadius,footRot,footLength;
        
            // some settings
            
        radius=(float)((double)GeneratorMain.settings.get("radius"));
        limbRadius=radius*(0.1f+(GeneratorMain.random.nextFloat()*0.1f));
        footRot=GeneratorMain.random.nextFloat()*15.0f;
        footLength=limbRadius+(GeneratorMain.random.nextFloat()*(limbRadius*2.0f));
        toeCount=GeneratorMain.random.nextInt(5);

            // hip legs
            
        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbLeg(skeleton,1,boneIdx,limbRadius,90.0f,footLength,footRot,toeCount,false);
        buildLimbLeg(skeleton,2,boneIdx,limbRadius,270.0f,footLength,-footRot,toeCount,true);

            // front legs
            
        if (!standing) {
            boneIdx=skeleton.findBoneIndex("Torso_Top");
            buildLimbLeg(skeleton,3,boneIdx,limbRadius,90.0f,footLength,footRot,toeCount,false);
            buildLimbLeg(skeleton,4,boneIdx,limbRadius,270.0f,footLength,-footRot,toeCount,true);
        }
    }
    
        //
        // tails
        //
        
    public void buildTail(Skeleton skeleton,boolean stranding)
    {
        int         boneIdx;
        float       radius,limbRadius,whipLength;
        
        if (GeneratorMain.random.nextFloat()<0.7f) return;
        
        radius=(float)((double)GeneratorMain.settings.get("radius"));
        limbRadius=radius*(0.1f+(GeneratorMain.random.nextFloat()*0.1f));
        whipLength=radius+(GeneratorMain.random.nextFloat()*radius);
        
        boneIdx=skeleton.findBoneIndex("Hip");
        buildLimbWhip(skeleton,5,boneIdx,limbRadius,whipLength,180.0f);
    }
    
        //
        // heads
        //
        
    public void buildHead(Skeleton skeleton)
    {
        int         boneIdx;
        float       radius,headRadius,jawRadius,neckLength,neckRadius;
        
        radius=(float)((double)GeneratorMain.settings.get("radius"));
        headRadius=radius-(GeneratorMain.random.nextFloat()*(radius*0.7f));
        neckRadius=headRadius*(0.3f+(GeneratorMain.random.nextFloat()*0.5f));
        jawRadius=headRadius*(0.9f+(GeneratorMain.random.nextFloat()*0.3f));
        neckLength=headRadius*(0.1f+(GeneratorMain.random.nextFloat()*0.3f));
        
        boneIdx=skeleton.findBoneIndex("Torso_Top");
        buildLimbHead(skeleton,0,boneIdx,neckLength,neckRadius,jawRadius,headRadius,true);
    }
    
        //
        // build skeleton bones
        //

    public Skeleton build()
    {
        float               hunchAng;
        boolean             standing;
        Skeleton            skeleton;
        
        skeleton=new Skeleton();
        
            // get a hunch angle which determines
            // if we are on 2 or 4 feet
        
        standing=(GeneratorMain.random.nextFloat()<0.7f);
        if (!standing) {        
            hunchAng=(60.0f+(GeneratorMain.random.nextFloat()*95.0f));
        }
        else {
            hunchAng=GeneratorMain.random.nextFloat()*30.0f;
        }
        
            // build the skeleton

        buildBody(skeleton,hunchAng);
        buildLegs(skeleton,standing);
        buildArms(skeleton,standing);
        buildTail(skeleton,standing);
        buildHead(skeleton);
        
        return(skeleton);
     }

}
