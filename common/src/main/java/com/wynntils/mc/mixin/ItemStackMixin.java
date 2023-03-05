/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.handlers.item.ItemAnnotation;
import com.wynntils.mc.event.ItemTooltipFlagsEvent;
import com.wynntils.mc.extension.ItemStackExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackExtension {
    @Unique
    private ItemAnnotation wynntilsAnnotation;

    @Unique
    private String wynntilsOriginalName;

    @ModifyExpressionValue(
            method =
                    "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHideFlags()I"))
    private int redirectGetHideFlags(int original) {
        ItemStack itemStack = (ItemStack) (Object) this;
        ItemTooltipFlagsEvent.Mask event = MixinHelper.post(new ItemTooltipFlagsEvent.Mask(itemStack, original));

        return event.getMask();
    }

    @ModifyVariable(
            method =
                    "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private TooltipFlag onGetTooltipLines(TooltipFlag flags) {
        ItemStack itemStack = (ItemStack) (Object) this;
        ItemTooltipFlagsEvent.Advanced event = MixinHelper.post(new ItemTooltipFlagsEvent.Advanced(itemStack, flags));
        return event.getFlags();
    }

    @Override
    @Unique
    public ItemAnnotation getAnnotation() {
        return wynntilsAnnotation;
    }

    @Override
    @Unique
    public void setAnnotation(ItemAnnotation annotation) {
        this.wynntilsAnnotation = annotation;
    }

    @Override
    @Unique
    public String getOriginalName() {
        return this.wynntilsOriginalName;
    }

    @Override
    @Unique
    public void setOriginalName(String name) {
        this.wynntilsOriginalName = name;
    }
}
