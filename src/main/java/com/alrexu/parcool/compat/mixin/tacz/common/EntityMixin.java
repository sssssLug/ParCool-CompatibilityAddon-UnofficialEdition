package com.alrexu.parcool.compat.mixin.tacz.common;


import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.capability.Parkourability;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {


    /**
     * Fix a bug: When you hanged down and shot a projectile (arrow etc.), the movement in Y of the projectile would be tweaked in a wrong
     * way, because Minecraft thought you were falling down.
     * This may be an issue with Parcool mod.
     * Unfortunately {@link EntityKineticBullet#shootFromRotation(Entity, float, float, float, float, Vector2d)} can't be mixed
     * because it will only be called in lambda, so changing the getter is RISKY but the only way.
     * If you're playing without tacz, just disable it, then mix {@link Projectile#shootFromRotation(Entity, float, float, float, float, float)} instead.*/
    @ModifyReturnValue(method = "onGround", at = @At("RETURN"))
    private boolean checkOnGround(boolean original) {
        return parcool_compat_addon$checkHanging(original);
    }

    @Unique
    private boolean parcool_compat_addon$checkHanging(boolean z) {
        if ( ((Entity) (Object) this) instanceof Player player
                /*
                * it will only be effective when you're holding firearms.*/
                && IGunOperator.fromLivingEntity(player).getDataHolder().currentGunItem != null
        ) {
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
