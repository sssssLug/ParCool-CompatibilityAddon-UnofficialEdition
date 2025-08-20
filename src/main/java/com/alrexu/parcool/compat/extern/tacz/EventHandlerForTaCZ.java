package com.alrexu.parcool.compat.extern.tacz;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.impl.*;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerForTaCZ {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onUpdateParCoolAnimInfo(ParCoolAnimationInfoEvent event) {
        if (event.getPlayer().getMainHandItem().getItem() instanceof AbstractGunItem) {
            Animator animator = event.getAnimator();

            if (animator instanceof DiveAnimationHostAnimator
                    || animator instanceof DiveIntoWaterAnimator
//                    || animator instanceof FastSwimAnimator
                    || animator instanceof DodgeAnimator
                    || animator instanceof RollAnimator
//                    || animator instanceof KongVaultAnimator
//                    || animator instanceof SpeedVaultAnimator
//                    || animator instanceof ChargeJumpAnimator
//                    || animator instanceof JumpChargingAnimator
                    || animator instanceof ClingToCliffAnimator
//                    || animator instanceof BackwardWallJumpAnimator
//                    || animator instanceof WallJumpAnimator
                    || animator instanceof ClimbUpAnimator
                    || animator instanceof FlippingAnimator
//                    || animator instanceof HangAnimator
//                    || animator instanceof JumpFromBarAnimator
//                    || animator instanceof VerticalWallRunAnimator
                    || animator instanceof WallSlideAnimator
//                    || animator instanceof TapAnimator
            ) {
                return;
            }

            if (animator instanceof FastRunningAnimator) {
                event.getOption().cancel(AnimationPart.LEFT_LEG);
                event.getOption().cancel(AnimationPart.RIGHT_LEG);
                event.getOption().cancel(AnimationPart.LEFT_ARM);
                event.getOption().cancel(AnimationPart.RIGHT_ARM);
                return;
            }

            if (animator instanceof KongVaultAnimator
                    || animator instanceof SpeedVaultAnimator
                    || animator instanceof ChargeJumpAnimator
                    || animator instanceof CatLeapAnimator
                    || animator instanceof BackwardWallJumpAnimator
                    || animator instanceof WallJumpAnimator
                    || animator instanceof JumpFromBarAnimator
                    || animator instanceof RideZiplineAnimator) {
                event.getOption().cancel(AnimationPart.RIGHT_ARM);
                return;
            }

            if (animator instanceof CrawlAnimator) {
                event.getOption().cancelAnimation();
                return;
            }
            event.getOption().cancel(AnimationPart.LEFT_ARM);
            event.getOption().cancel(AnimationPart.RIGHT_ARM);
        }
    }

    /*@SubscribeEvent
    public static void onAction(ParCoolActionEvent event) {
        Action action = event.getAction();
    }*/


    @SubscribeEvent
    public static void onGunToShoot(GunShootEvent event) {
        LivingEntity shooter = event.getShooter();
        if(shooter instanceof Player player) {
            Parkourability instance = Parkourability.get(player);
            if(instance == null) return;

            ClingToCliff cliff = instance.get(ClingToCliff.class);
            if(cliff != null && cliff.isDoing()) {
                if(cliff.getFacingDirection() == ClingToCliff.FacingDirection.RightAgainstWall) return;
                event.setCanceled(true);
                return;
            }

            if(instance.isDoingAny(
                    BreakfallReady.class,
                    Tap.class,
                    HorizontalWallRun.class,
                    VerticalWallRun.class,
                    QuickTurn.class,
                    RideZipline.class
            )) return;

            if(instance.isDoingAny(
                    HangDown.class,
                    WallJump.class,
                    JumpFromBar.class
            )) {
                com.tacz.guns.resource.modifier.AttachmentCacheProperty cache = IGunOperator.fromLivingEntity(shooter).getCacheProperty();
                if(cache != null) {
                    float weight = (float) cache.getCache("weight_modifier");
                    if(weight <= 6.4F) return;
                }
                event.setCanceled(true);
                return;
            }

            if(instance.isDoingAny(
                    FastRun.class) || !instance.isDoingNothing()) {
                event.setCanceled(true);
            }
        }
    }
}
