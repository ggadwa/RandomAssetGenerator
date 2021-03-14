package com.klinksoftware.rag.skeleton;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.RagPoint;

public class SkeletonBuilder
{
        //
        // leg limb
        //
        
    public void buildLimbLeg(Skeleton skeleton,int limbIdx,int parentBoneIdx,float radius,float rotOffset,float footLength,float footRot,int toeCount,boolean flipped)
    {
        /*
        let pnt,vct,pushVct,legRadius;
        let hipBoneIdx,kneeBoneIdx,ankleBoneIdx,footBoneIdx,heelBoneIdx,knuckleBoneIdx,toeBoneIdx;
        let n,toeRadius,toeDistance,fx,meshScale;
        let footVct,footPnt,knuckleVct,knucklePnt,toePnt;
        let skeleton=this.model.skeleton;
        let bones=skeleton.bones;
        let parentBone=bones[parentBoneIdx];
        
        legRadius=radius*this.sizeFactor;
        
            // size and position around body

        pushVct=new PointClass(0.0,0.0,((parentBone.gravityLockDistance*0.8)-(legRadius*0.5)));
        pushVct.rotateY(null,rotOffset);
        
        pnt=parentBone.position.copy();
        pnt.addPoint(pushVct);
        
            // legs always face down
            
        vct=new PointClass(0.0,-parentBone.position.y,0.0);
        
            // leg bones
            // we might already have a hip, so don't rebuild if we do
        
        hipBoneIdx=bones.push(new ModelBoneClass(('Hip_'+limbIdx),parentBoneIdx,new PointClass(pnt.x,pnt.y,pnt.z)))-1;
        kneeBoneIdx=bones.push(new ModelBoneClass(('Knee_'+limbIdx),hipBoneIdx,new PointClass((pnt.x+(vct.x*0.4)),(pnt.y+(vct.y*0.4)),(pnt.z+(vct.z*0.4)))))-1;
        ankleBoneIdx=bones.push(new ModelBoneClass(('Ankle_'+limbIdx),kneeBoneIdx,new PointClass((pnt.x+(vct.x*0.8)),(pnt.y+(vct.y*0.8)),(pnt.z+(vct.z*0.8)))))-1;
        heelBoneIdx=bones.push(new ModelBoneClass(('Heel_'+limbIdx),ankleBoneIdx,new PointClass((pnt.x+(vct.x*0.95)),(pnt.y+(vct.y*0.95)),(pnt.z+(vct.z*0.95)))))-1;
        
        bones[hipBoneIdx].gravityLockDistance=Math.trunc(legRadius*(1.0f+(GeneratorMain.random.nextFloat()*0.3f));
        bones[kneeBoneIdx].gravityLockDistance=legRadius;
        bones[ankleBoneIdx].gravityLockDistance=legRadius;
        bones[heelBoneIdx].gravityLockDistance=legRadius;
        
        meshScale=0.7f+(GeneratorMain.random.nextFloat()*0.3f);
        skeleton.addLimb(("leg_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_LEG,Limb.LIMB_AXIS_Y,flipped,8,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{hipBoneIdx,kneeBoneIdx,ankleBoneIdx,heelBoneIdx});
        
            // the foot bones
            // feet are always parallel to ground, towards front
       
        footVct=new PointClass(0.0,0.0,footLength);
        footVct.rotateY(null,footRot);
        
        footPnt=bones[heelBoneIdx].position.copy();
        footPnt.addPoint(footVct);
        footPnt.y=0.0;
        footBoneIdx=bones.push(new ModelBoneClass(('Foot_'+limbIdx),heelBoneIdx,footPnt))-1;

        bones[footBoneIdx].gravityLockDistance=legRadius*(1.2f+(GeneratorMain.random.nextFloat()*0.3f));
        
        skeleton.addLimb(("foot_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_FOOT,Limb.LIMB_AXIS_Z,flipped,5,5,new RagPoint(1.0f,0.7f,1.0f),new int[]{heelBoneIdx,footBoneIdx});

            // toe limbs
            
        if (toeCount===0) return;
        
        toeRadius=Math.trunc((legRadius/toeCount)*0.8);
        if (toeRadius<100) toeRadius=100;
        
        toeDistance=Math.trunc(toeRadius*1.1);
        
        knuckleVct=footVct.copy();
        knuckleVct.normalize();
        knuckleVct.scale(Math.trunc(footLength*0.4));
        knucklePnt=new PointClass((footPnt.x+knuckleVct.x),0,(footPnt.z+knuckleVct.z));
        
        toePnt=new PointClass((knucklePnt.x+knuckleVct.x),0,(knucklePnt.z+knuckleVct.z));

        fx=knucklePnt.x-Math.trunc(toeCount*0.5)*toeDistance;

        for (n=0;n!==toeCount;n++) {
            knuckleBoneIdx=bones.push(new ModelBoneClass(('Toe_Knuckle_'+limbIdx+'_'+n),footBoneIdx,new PointClass(fx,knucklePnt.y,knucklePnt.z)))-1;
            toeBoneIdx=bones.push(new ModelBoneClass(('Toe_'+limbIdx+'_'+n),knuckleBoneIdx,new PointClass(fx,toePnt.y,toePnt.z)))-1;
            
            bones[knuckleBoneIdx].gravityLockDistance=toeRadius;
            bones[toeBoneIdx].gravityLockDistance=toeRadius;
            
            skeleton.addLimb(("leg_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),Limb.LIMB_TYPE_TOE,Limb.LIMB_AXIS_Z,flipped,4,4,new RagPoint(1.0f,0.7f,1.0f),new int[]{knuckleBoneIdx,toeBoneIdx});
            
            fx+=toeDistance;
        }
        */
    }

        //
        // arm limb
        //
    
    public void buildLimbArm(Skeleton skeleton,int limbIdx,int parentBoneIdx,float radius,float length,float rotOffset,int fingerCount,boolean flipped)
    {
        /*
        let armRadius,armLength,axis,pnt,vct,pushVct;
        let shoulderBoneIdx,elbowBoneIdx,wristBoneIdx,handBoneIdx,knuckleBoneIdx,fingerBoneIdx;
        let handPnt,handRadius,armLimbIdx,handLimbIdx;
        let n,fy,fingerRadius,fingerDistance,meshScale;
        let knucklePnt,knuckleVct,fingerPnt,fingerVct;
        let skeleton=this.model.skeleton;
        let bones=skeleton.bones;
        let parentBone=bones[parentBoneIdx];
        
            // size and position around body
            
        armRadius=radius*this.sizeFactor;
        armLength=length*this.sizeFactor;
        
        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?constants.LIMB_AXIS_Z:constants.LIMB_AXIS_X;
        
        pushVct=new PointClass(0.0,0.0,(parentBone.gravityLockDistance-Math.trunc(armRadius*0.5)));
        pushVct.rotateY(null,rotOffset);
        
        pnt=parentBone.position.copy();
        pnt.addPoint(pushVct);
        
            // arms face out
            
        vct=new PointClass(0.0,0.0,armLength);
        vct.rotateY(null,rotOffset);
       
            // arm limb
            
        shoulderBoneIdx=bones.push(new ModelBoneClass(('Shoulder_'+limbIdx),parentBoneIdx,new PointClass(pnt.x,pnt.y,pnt.z)))-1;
        elbowBoneIdx=bones.push(new ModelBoneClass(('Elbow_'+limbIdx),shoulderBoneIdx,new PointClass((pnt.x+(vct.x*0.45)),(pnt.y+(vct.y*0.45)),(pnt.z+(vct.z*0.45)))))-1;
        wristBoneIdx=bones.push(new ModelBoneClass(('Wrist_'+limbIdx),elbowBoneIdx,new PointClass((pnt.x+(vct.x*0.9)),(pnt.y+(vct.y*0.9)),(pnt.z+(vct.z*0.9)))))-1;
        
        bones[shoulderBoneIdx].gravityLockDistance=Math.trunc(armRadius*1.5);
        bones[elbowBoneIdx].gravityLockDistance=armRadius;
        bones[wristBoneIdx].gravityLockDistance=armRadius;
        
        meshScale=0.7+(GeneratorMain.random.nextFloat()*0.3f);
        skeleton.addLimb(("arm_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_ARM,axis,flipped,8,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{shoulderBoneIdx,elbowBoneIdx,wristBoneIdx});
        
            // hand limb
            
        handRadius=armRadius*(1.0f+(GeneratorMain.random.nextFloat()*0.3f));
        handPnt=new PointClass((pnt.x+vct.x),(pnt.y+vct.y),(pnt.z+vct.z));
        
        handBoneIdx=bones.push(new ModelBoneClass(('Hand_'+limbIdx),wristBoneIdx,handPnt))-1;
        
        bones[handBoneIdx].gravityLockDistance=handRadius;
        
        skeleton.addLimb(("hand_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_HAND,axis,flipped,5,5,new RagPoint(1.0f,1.0f,meshScale),new int[]{handBoneIdx});

            // finger limbs
            
        if (fingerCount===0) return;
        
        fingerRadius=Math.trunc((armRadius/fingerCount)*0.8);
        if (fingerRadius<100) fingerRadius=100;
        
        fingerDistance=Math.trunc(fingerRadius*1.1);
        
        knuckleVct=vct.copy();
        knuckleVct.normalize();
        knuckleVct.scale(Math.trunc(handRadius*0.4));
        knucklePnt=new PointClass((handPnt.x+knuckleVct.x),(handPnt.y+knuckleVct.y),(handPnt.z+knuckleVct.z));
        
        fingerVct=vct.copy();
        fingerVct.normalize();
        fingerVct.scale(armRadius+(GeneratorMain.random.nextFloat()*armRadius));
        fingerPnt=new PointClass((knucklePnt.x+fingerVct.x),(knucklePnt.y+fingerVct.y),(knucklePnt.z+fingerVct.z));

        fy=knucklePnt.y-Math.trunc(fingerCount*0.5)*fingerDistance;
        
        for (n=0;n!==fingerCount;n++) {
            knuckleBoneIdx=bones.push(new ModelBoneClass(('Finger_Knuckle_'+limbIdx+'_'+n),handBoneIdx,new PointClass(knucklePnt.x,fy,knucklePnt.z)))-1;
            fingerBoneIdx=bones.push(new ModelBoneClass(('Finger'+limbIdx+'_'+n),knuckleBoneIdx,new PointClass(fingerPnt.x,fy,fingerPnt.z)))-1;
            
            bones[knuckleBoneIdx].gravityLockDistance=fingerRadius;
            bones[fingerBoneIdx].gravityLockDistance=fingerRadius;
            
            skeleton.addLimb(("finger_"+Integer.toString(limbIdx)+"_"+Integer.toString(n)),Limb.LIMB_TYPE_FINGER,axis,flipped,4,4,new RagPoint(1.0f,1.0f,meshScale),new int[]{knuckleBoneIdx,fingerBoneIdx});
            
            fy+=fingerDistance;
        }
        */
    }
    
        //
        // whip limbs
        //
    
    public void buildLimbWhip(Skeleton skeleton,int limbIdx,int parentBoneIdx,float radius,float length,float rotOffset)
    {
        /*
        let whipRadius,whipLength,axis,pnt,vct,pushVct;
        let whip0BoneIdx,whip1BoneIdx,whip2BoneIdx,whip3BoneIdx;
        let skeleton=this.model.skeleton;
        let bones=skeleton.bones;
        let parentBone=bones[parentBoneIdx];
        
            // size and position around body
            
        whipRadius=radius*this.sizeFactor;
        whipLength=length*this.sizeFactor;
        
        pushVct=new PointClass(0.0,0.0,(parentBone.gravityLockDistance-Math.trunc(whipRadius*0.5)));
        pushVct.rotateY(null,rotOffset);
        
        pnt=parentBone.position.copy();
        pnt.addPoint(pushVct);
        
            // whips face out
            
        axis=(((rotOffset>315)||(rotOffset<45))||((rotOffset>135)&&(rotOffset<225)))?constants.LIMB_AXIS_Z:constants.LIMB_AXIS_X;
            
        vct=new PointClass(0.0,0.0,whipLength);
        vct.rotateY(null,rotOffset);
        
            // whip limb
            
        whip0BoneIdx=bones.push(new ModelBoneClass(('Whip_'+limbIdx+'_0'),parentBoneIdx,new PointClass(pnt.x,pnt.y,pnt.z)))-1;
        whip1BoneIdx=bones.push(new ModelBoneClass(('Whip_'+limbIdx+'_1'),whip0BoneIdx,new PointClass((pnt.x+(vct.x*0.33)),(pnt.y+(vct.y*0.33)),(pnt.z+(vct.z*0.33)))))-1;
        whip2BoneIdx=bones.push(new ModelBoneClass(('Whip_'+limbIdx+'_2'),whip1BoneIdx,new PointClass((pnt.x+(vct.x*0.66)),(pnt.y+(vct.y*0.66)),(pnt.z+(vct.z*0.66)))))-1;
        whip3BoneIdx=bones.push(new ModelBoneClass(('Whip_'+limbIdx+'_3'),whip2BoneIdx,new PointClass((pnt.x+vct.x),(pnt.y+vct.y),(pnt.z+vct.z))))-1;

        bones[whip0BoneIdx].gravityLockDistance=whipRadius;
        bones[whip1BoneIdx].gravityLockDistance=Math.trunc(whipRadius*0.8);
        bones[whip2BoneIdx].gravityLockDistance=Math.trunc(whipRadius*0.6);
        bones[whip3BoneIdx].gravityLockDistance=Math.trunc(whipRadius*0.3);

        skeleton.addLimb(("whip_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_WHIP,axis,false,8,5,new RagPoint(1.0f,1.0f,1.0f),new int[]{whip0BoneIdx,whip1BoneIdx,whip2BoneIdx,whip3BoneIdx});
        */
    }
    
        //
        // head limb
        //
        
    public void buildLimbHead(Skeleton skeleton,int limbIdx,int parentBoneIdx,float neckLength,float neckRadius,float jawRadius,float headRadius,boolean hasJaw)
    {
        /*
        let pnt,jawBackPnt,jawFrontPnt,neckPnt,vct;
        let neckStartBoneIdx,neckEndBoneIdx;
        let jawBackBoneIdx,jawFrontBoneIdx;
        let headBottomBoneIdx,headTopBoneIdx;
        let neckStartLength,headOffset,headLength,headRot;
        let scaleMin,scaleMax;
        let skeleton=this.model.skeleton;
        let bones=skeleton.bones;
        let parentBone=bones[parentBoneIdx];
        
        neckLength*=this.sizeFactor;
        neckRadius*=this.sizeFactor;
        jawRadius*=this.sizeFactor;
        headRadius*=this.sizeFactor;
        
            // create the neck
            
        neckStartLength=(parentBone.gravityLockDistance-(neckLength*0.5))*this.sizeFactor;
        
        pnt=parentBone.position.copy();
        vct=new PointClass(0,-neckStartLength,0);
        vct.rotateX(null,-(GeneratorMain.random.nextFloat()*25.0f));
        pnt.addPoint(vct);
            
        neckStartBoneIdx=bones.push(new ModelBoneClass(('Neck_Bottom_'+limbIdx),parentBoneIdx,pnt))-1;
        
        neckPnt=pnt.copy();
        vct=new PointClass(0,-neckLength,0);
        vct.rotateX(null,-(GeneratorMain.random.nextFloat()*25.0f));
        neckPnt.addPoint(vct);
        
        neckEndBoneIdx=bones.push(new ModelBoneClass(('Neck_Top_'+limbIdx),neckStartBoneIdx,neckPnt))-1;
        
        bones[neckStartBoneIdx].gravityLockDistance=neckRadius*(0.8f+(GeneratorMain.random.nextFloat()*0.2f));
        bones[neckEndBoneIdx].gravityLockDistance=neckRadius;

        skeleton.addLimb(("neck_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_NECK,Limb.LIMB_AXIS_Y,false,5,5,new RagPoint(1.0f,0.3f,1.0f),new int[]{neckStartBoneIdx,neckEndBoneIdx});
        
            // default placements
            
        headOffset=10;
        headRot=-(GeneratorMain.random.nextFloat()*25.0f);
        
            // create the jaw
            
        if (hasJaw) {
            jawRadius=Math.trunc(headRadius*(0.3f+(GeneratorMain.random.nextFloat()*0.3f));
            
            jawBackPnt=neckPnt.copy();
            vct=new PointClass(0,-headOffset,-((headRadius*0.6)-(jawRadius*0.5)));
            jawBackPnt.addPoint(vct);
            
            jawBackBoneIdx=bones.push(new ModelBoneClass(('Jaw_Back_'+limbIdx),neckEndBoneIdx,jawBackPnt))-1;
            
            bones[jawBackBoneIdx].gravityLockDistance=jawRadius;

            jawFrontPnt=neckPnt.copy();
            vct=new PointClass(0,-headOffset,((headRadius*(0.5f+(GeneratorMain.random.nextFloat()*0.3f))-(jawRadius*0.5)));
            jawFrontPnt.addPoint(vct);

            jawFrontBoneIdx=bones.push(new ModelBoneClass(('Jaw_Front_'+limbIdx),jawBackBoneIdx,jawFrontPnt))-1;
            
            bones[jawFrontBoneIdx].gravityLockDistance=jawRadius;
            
            scaleMax=new PointClass((0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)));
            scaleMin=scaleMax.copy();
            scaleMin.y=0.1;
            
            skeleton.addLimb(("jaw_"+Integer.toString(limbIdx)),Limb.LIMB_TYPE_JAW,Limb.LIMB_AXIS_Z,false,6,6,new RagPoint(1.0f,1.0f,meshScale),new int[]{jawBackBoneIdx,jawFrontBoneIdx});
        }
        
            // create the head
        
        headLength=Math.trunc(headRadius*(0.4f+(GeneratorMain.random.nextFloat()*0.4f));
        
        pnt=neckPnt.copy();
        vct=new PointClass(0,-headOffset,0);        // no rot here
        pnt.addPoint(vct);
        
        headBottomBoneIdx=bones.push(new ModelBoneClass(('Head_Bottom_'+limbIdx),neckEndBoneIdx,pnt))-1;
        
        pnt=pnt.copy();
        vct=new PointClass(0,-headLength,0);
        vct.rotateX(null,headRot);
        pnt.addPoint(vct);
        
        headTopBoneIdx=bones.push(new ModelBoneClass(('Head_Top_'+limbIdx),headBottomBoneIdx,pnt))-1;
        
        bones[headBottomBoneIdx].gravityLockDistance=headRadius;
        bones[headTopBoneIdx].gravityLockDistance=headRadius;
        
        scaleMin=new PointClass((0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)),(0.7f+(GeneratorMain.random.nextFloat()*0.3f)));
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
        limbRadius=radius*0.1f;
        limbRadius+=(GeneratorMain.random.nextFloat()*(limbRadius*0.2f));
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
        float       limbRadius,footRot,footLength;
        
            // some settings
            
        limbRadius=((float)((double)GeneratorMain.settings.get("radius")))*0.1f;
        limbRadius+=(GeneratorMain.random.nextFloat()*(limbRadius*0.2f));
        footRot=GeneratorMain.random.nextFloat()*15.0f;
        footLength=limbRadius+(GeneratorMain.random.nextFloat()*limbRadius);
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
        limbRadius=radius*0.1f;
        limbRadius+=(GeneratorMain.random.nextFloat()*(limbRadius*0.2f));
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
