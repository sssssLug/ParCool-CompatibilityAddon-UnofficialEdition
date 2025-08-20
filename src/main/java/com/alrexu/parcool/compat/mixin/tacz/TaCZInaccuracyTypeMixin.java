package com.alrexu.parcool.compat.mixin.tacz;


import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InaccuracyType.class, remap = false)
public class TaCZInaccuracyTypeMixin {

    /*
    * Remove players' accuracy benefit when parkour, but they can still aim to zoom vision.*/
    @Inject(method = "getInaccuracyType", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/tacz/guns/api/entity/IGunOperator;getSynAimingProgress()F"), cancellable = true)
    private static void hijackAiming(LivingEntity livingEntity, CallbackInfoReturnable<InaccuracyType> cir) {
        if (livingEntity instanceof Player && parcool_compat_addon$isBusyAtParkour((Player) livingEntity)) cir.setReturnValue(InaccuracyType.MOVE);
    }

    @Unique
    private static boolean parcool_compat_addon$isBusyAtParkour(Player player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability != null) {

            if (parkourability.isDoingAny(
                    Crawl.class,
                    HorizontalWallRun.class,
                    VerticalWallRun.class
            )) return false;

            return !parkourability.isDoingNothing();
        }


        return false;

    }
}
