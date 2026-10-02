package com.cursedcraft;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/** Cursed death drops and the chaos block-drop randomizer. */
public final class CursedEvents {
    private CursedEvents() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!CursedConfig.CURSED_DEATH_DROPS) return;
            if (!(entity.level() instanceof ServerLevel sl)) return;

            if (entity.getType() == EntityType.COW) {
                entity.spawnAtLocation(sl, new ItemStack(Items.BONE));
            } else if (entity.getType() == EntityType.CREEPER) {
                // Free hug: hearts and a tiny heal for nearby players
                sl.sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1, entity.getZ(), 8, 0.4, 0.4, 0.4, 0.0);
                for (Player p : sl.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(6))) {
                    p.heal(2f);
                }
            }
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!CursedConfig.CHAOS_BLOCK_DROPS || !(level instanceof ServerLevel sl)) return true;
            if (player.hasInfiniteMaterials()) return true;
            var random = BuiltInRegistries.ITEM.getRandom(sl.getRandom());
            if (random.isEmpty()) return true;
            sl.removeBlock(pos, false);
            Block.popResource(sl, pos, new ItemStack(random.get().value()));
            return false; // we handled the break ourselves
        });
    }
}
