package com.cursedcraft;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Eat mobs: sneak + right-click a mob with an empty hand to take a bite. */
public final class EatMobs {
    private EatMobs() {}

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (!CursedConfig.EAT_MOBS) return InteractionResult.PASS;
            if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp) || !player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity mob) || entity instanceof Player) return InteractionResult.PASS;

            ServerLevel sl = (ServerLevel) level;

            if (mob.isBaby()) {
                EatItems.msg(sp, "The baby looks at you sadly. You can't do it.");
                return InteractionResult.SUCCESS;
            }

            EntityType<?> t = mob.getType();
            if (t == EntityType.CREEPER) {
                EatItems.msg(sp, "*hissssss*");
                sl.explode(null, sp.getX(), sp.getY(), sp.getZ(), 2.0f, Level.ExplosionInteraction.NONE);
            } else if (t == EntityType.ZOMBIE) {
                EatItems.msg(sp, "Braaains. *burp*");
                sp.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 10, 0));
            } else if (t == EntityType.SKELETON) {
                EatItems.msg(sp, "*bone rattle*");
                sp.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 10, 0));
            } else if (t == EntityType.COW) {
                EatItems.msg(sp, "Moo.");
                sp.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 20, 0));
            } else if (t == EntityType.PIG) {
                EatItems.msg(sp, "*snort*");
                sp.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 15, 1));
            } else if (t == EntityType.SHEEP) {
                EatItems.msg(sp, "You feel fluffy.");
                sp.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 15, 0));
                sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 15, 0));
            } else if (t == EntityType.VILLAGER) {
                EatItems.msg(sp, "Hrmmm. That'll be one emerald for the dental bill.");
                sp.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 20 * 30, 0));
            } else if (t == EntityType.ENDER_DRAGON) {
                EatItems.msg(sp, "You can fly. Briefly.");
                sp.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * 5, 3));
            } else {
                EatItems.msg(sp, "Tastes weird.");
                sp.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 5, 0));
            }

            sp.getFoodData().eat(3, 0.5f);
            mob.hurtServer(sl, sl.damageSources().playerAttack(sp), 2f);
            // Bitten mobs flee
            mob.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 5, 2));
            return InteractionResult.SUCCESS;
        });
    }
}
