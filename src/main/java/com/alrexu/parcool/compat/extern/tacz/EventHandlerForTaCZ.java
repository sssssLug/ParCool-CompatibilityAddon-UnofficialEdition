package com.alrexu.parcool.compat.extern.tacz;

import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.impl.*;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
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
//                    || animator instanceof ClingToCliffAnimator
                    || animator instanceof BackwardWallJumpAnimator
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
                AbstractClientPlayer player = event.getPlayer();
                if (player instanceof IClientPlayerGunOperator operator && IClientPlayerGunOperator.fromLocalPlayer((LocalPlayer) player).isAim()) {
                    event.getOption().cancelAnimation();
                    return;
                }

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
                    || animator instanceof HangAnimator
//                    || animator instanceof ClingToCliffAnimator
                    || animator instanceof WallJumpAnimator
                    || animator instanceof JumpFromBarAnimator
                    || animator instanceof RideZiplineAnimator
            ) {
                event.getOption().cancel(AnimationPart.RIGHT_ARM);
                return;
            }

            if (animator instanceof ClingToCliffAnimator) {
                /*if (isLeftHandClinging(event.getPlayer())) {
                    event.getOption().cancel(AnimationPart.RIGHT_ARM);
                }*/
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


    /*
    * In TaCZ shooting event will be triggered in Two orders:
    * 1.(For players) Player key input -> clientside event |-> send packet -> server shoot logic -> serverside event |-> send packet -> clientside event -> (locked to avoid double client effect);
    *                                                      |-> client effect                                         |-> bullet created and shot
    * 2.(For livings) Some sever execution (Provided by third-party mods) -> server shoot logic -> serverside event |-> send packet -> clientside event -> client effect.
    *                                                                                                               |-> bullet created and shot
    * */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onGunToShoot(GunShootEvent event) {
        LivingEntity shooter = event.getShooter();
        //Maybe we can stop clientside only?
        /*if (!event.getLogicalSide().isClient()) {
            return;
        }*/

        if (!(shooter instanceof Player player)) {
            return;
        }


        Parkourability instance = Parkourability.get(player);
        if(instance == null) return;


        /*ClingToCliff cCliff = instance.get(ClingToCliff.class);
        if(cCliff != null && cCliff.isDoing()) {
            if(cCliff.getFacingDirection() == ClingToCliff.FacingDirection.RightAgainstWall) return;
            event.setCanceled(true);
            return;
        }*/

        if(instance.isDoingAny(
                Crawl.class,
                CatLeap.class,
                BreakfallReady.class,
                Tap.class,
                //barely
                HorizontalWallRun.class
        )) return;

        if(instance.isDoingAny(
                HangDown.class,
                WallJump.class,
                JumpFromBar.class,
                RideZipline.class,
                //barely
                VerticalWallRun.class
        )) {
            com.tacz.guns.resource.modifier.AttachmentCacheProperty cache = IGunOperator.fromLivingEntity(shooter).getCacheProperty();
            if(cache != null) {
                float weight = (float) cache.getCache("weight_modifier");
                IStamina stamina = IStamina.get(player);
                if(weight <= 6.4F && stamina != null && !stamina.isExhausted()) {
                    stamina.consume((int) (2 * weight));
                    return;
                }
            }
            event.setCanceled(true);
            return;
        }

        if(instance.isDoingAny(
                FastRun.class) || !instance.isDoingNothing()) {
            event.setCanceled(true);
        }
    }

    /**
     * Fix a client bug: When you hold sprint key (together with fast-run key) and try to aim,
     * then release sprint (and fast-run) key (at this point you'll stop fast-run but keep sprinting due to a Vanilla feature),
     * you will be able to shoot at a strange angle (Just in client animation, won't affect the real bullet on server).
     * This bug occurs much frequently with Parcool mod.
     * IDK the reason, but anyway this handler will fix it. And it won't stop you from sprinting, actually.*/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunFiring(GunFireEvent event) {
        LivingEntity shooter = event.getShooter();
        if(shooter instanceof Player player && player.isSprinting()) {
            player.setSprinting(false);
        }
    }


    /*private static boolean isLeftHandClinging(Player player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability != null) {
            ClingToCliff cCliff = parkourability.get(ClingToCliff.class);
            return cCliff != null && cCliff.getFacingDirection() == ClingToCliff.FacingDirection.RightAgainstWall;
        }

        return false;
    }*/
}
