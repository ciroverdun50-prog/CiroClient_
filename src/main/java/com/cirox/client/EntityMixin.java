package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private Box boundingBox;

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (CiroxClientMod.hitboxActive && ((Object) this) instanceof net.minecraft.entity.player.PlayerEntity) {
            Entity entity = (Entity) (Object) this;
            if (entity != net.minecraft.client.MinecraftClient.getInstance().player) {
                double exp = CiroxClientMod.hitboxExpansion;
                Box expandedBox = this.boundingBox.expand(exp, 0.0, exp);
                cir.setReturnValue(expandedBox);
            }
        }
    }
}
