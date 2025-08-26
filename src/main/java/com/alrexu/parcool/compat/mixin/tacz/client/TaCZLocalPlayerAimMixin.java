package com.alrexu.parcool.compat.mixin.tacz.client;


import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.Parkourability;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = LocalPlayerAim.class, remap = false)
public abstract class TaCZLocalPlayerAimMixin {

    @Shadow
    @Final
    private LocalPlayer player;

    @Inject(method = "aim",
//            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/TimelessAPI;getClientGunIndex(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"),
            at = @At(value = "HEAD"),
            cancellable = true)
    private void hijackAiming(boolean isAim, CallbackInfo ci) {
        if (isAim && parcool_compat_addon$isBusyAtParkour(this.player)) ci.cancel();
    }

    @Unique
    private static boolean parcool_compat_addon$isBusyAtParkour(LocalPlayer player) {
        Parkourability instance = Parkourability.get(player);
        if (instance == null) return false;

        return instance.isDoingAny(
                Dive.class,
                SkyDive.class,
                WallSlide.class,
                Flipping.class,
                Tap.class,
//                FastRun.class,
                FastSwim.class,
                ClingToCliff.class
        );
    }
}
