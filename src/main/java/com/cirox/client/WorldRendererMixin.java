package com.cirox.client.mixin;

import com.cirox.client.module.*;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.impl.PlayerESPModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderWorld(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        PlayerESPModule esp = CiroxClientMod.getModuleManager().getModule(PlayerESPModule.class);
        MinecraftClient mc = MinecraftClient.getInstance();

        if (esp != null && esp.isEnabled() && mc.world != null && mc.player != null) {
            Vec3d cameraPos = camera.getPos();

            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player || player.isDead() || player.isSpectator()) continue;

                double x = MathHelper.lerp((double)tickDelta, player.lastRenderX, player.getX()) - cameraPos.x;
                double y = MathHelper.lerp((double)tickDelta, player.lastRenderY, player.getY()) - cameraPos.y;
                double z = MathHelper.lerp((double)tickDelta, player.lastRenderZ, player.getZ()) - cameraPos.z;

                Box box = player.getBoundingBox().offset(-player.getX(), -player.getY(), -player.getZ()).offset(x, y, z);
                
                // Renderizado de la caja de contorno para el ESP de jugadores
                VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
                WorldRenderer.drawBox(matrices, immediate.getBuffer(RenderLayer.getLines()), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 0.2f, 0.6f, 1.0f, 0.8f);
            }
        }
    }
}
