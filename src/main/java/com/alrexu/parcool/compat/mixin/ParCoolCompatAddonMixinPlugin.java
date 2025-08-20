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
        return (targetClassName.contains("com.tacz.guns") && mixinClassName.contains("compat.mixin.tacz") && ModList.get().isLoaded("tacz"))
               || (targetClassName.contains("dev.kosmx.playerAnim") && mixinClassName.contains("compat.mixin.playeranimator") && ModList.get().isLoaded("playeranimator"))
               || (targetClassName.contains("net.bettercombat") && mixinClassName.contains("compat.mixin.bettercombat") && ModList.get().isLoaded("bettercombat"))
               || (targetClassName.contains("tschipp.carryon") && mixinClassName.contains("compat.mixin.carryon") && ModList.get().isLoaded("carryon"))
               || (targetClassName.contains("com.vicmatskiv.pointblank") && mixinClassName.contains("compat.mixin.pointblank") && ModList.get().isLoaded("pointblank"));
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
}
