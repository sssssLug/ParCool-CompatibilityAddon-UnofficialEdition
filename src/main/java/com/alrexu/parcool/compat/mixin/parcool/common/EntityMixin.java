package com.alrexu.parcool.compat.mixin.parcool.common;


import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.capability.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {


    /**
     * Fix a bug: When you hanged down and shot, the movement in Y of the projectile would be tweaked in a wrong
     * way, because Minecraft thought you were falling down.
     * This may be an issue with Parcool mod.*/
    @ModifyReturnValue(method = "onGround", at = @At("RETURN"))
    private boolean checkOnGround(boolean original) {
        return parcool_compat_addon$checkHanging(original);
    }

    @Unique
    private boolean parcool_compat_addon$checkHanging(boolean z) {
        if ( ((Entity) (Object) this) instanceof Player player) {
            Parkourability instance = Parkourability.get(player);
            if (instance != null) {

                /*if(instance.isDoingAny(HangDown.class*//*, ClingToCliff.class*//*)) {
                    return true;
                }*/

                if (instance.get(HangDown.class).isDoing()) {
                    return true;
                }
            }
        }

        return z;
    }
}
