package net.ultimatekillaura;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;

public class KillAuraModule {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Random random = Random.create();
    
    public boolean enabled = false;
    public Entity currentTarget = null;
    public double reach = 4.8;
    private long lastAttack = 0;
    
    public void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        currentTarget = findClosestTarget();
        
        if (currentTarget != null) {
            rotateToTarget(currentTarget);
            
            if (System.currentTimeMillis() - lastAttack > 100) {
                client.player.swingHand(Hand.MAIN_HAND);
                client.interactionManager.attackEntity(client.player, currentTarget);
                lastAttack = System.currentTimeMillis();
            }
        }
    }
    
    private Entity findClosestTarget() {
        Entity closest = null;
        double closestDist = reach;
        
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity) || entity == mc.player || entity.isRemoved()) continue;
            
            double dist = mc.player.distanceTo(entity);
            if (dist < closestDist) {
                closest = entity;
                closestDist = dist;
            }
        }
        
        return closest;
    }
    
    private void rotateToTarget(Entity target) {
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        
        double deltaX = targetPos.x - eyePos.x;
        double deltaY = targetPos.y - eyePos.y;
        double deltaZ = targetPos.z - eyePos.z;
        
        double distXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distXZ));
        
        float yawDiff = MathHelper.wrapDegrees(yaw - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(pitch - mc.player.getPitch());
        
        mc.player.setYaw(mc.player.getYaw() + MathHelper.clamp(yawDiff * 0.2f, -10, 10));
        mc.player.setPitch(mc.player.getPitch() + MathHelper.clamp(pitchDiff, -5, 5));
    }
}