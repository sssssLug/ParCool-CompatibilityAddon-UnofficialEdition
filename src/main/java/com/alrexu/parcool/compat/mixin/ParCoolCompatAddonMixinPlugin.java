package com.alrexu.parcool.compat.mixin;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ParCoolCompatAddonMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return (targetClassName.contains("com.tacz.guns") && mixinClassName.contains("compat.mixin.tacz") && isClassLoaded(targetClassName))
               || (targetClassName.contains("dev.kosmx.playerAnim") && mixinClassName.contains("compat.mixin.playeranimator") && isClassLoaded(targetClassName))
               || (targetClassName.contains("net.bettercombat") && mixinClassName.contains("compat.mixin.bettercombat") && isClassLoaded(targetClassName))
               || (targetClassName.contains("tschipp.carryon") && mixinClassName.contains("compat.mixin.carryon") && isClassLoaded(targetClassName))
               || (targetClassName.contains("com.vicmatskiv.pointblank") && mixinClassName.contains("compat.mixin.pointblank") && isClassLoaded(targetClassName))
               || mixinClassName.contains("com.alrexu.parcool");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private boolean isClassLoaded(String className) {
        try {
            Class.forName(className, false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
