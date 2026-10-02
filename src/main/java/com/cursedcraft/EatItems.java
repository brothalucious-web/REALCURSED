package com.cursedcraft;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Eat everything: sneak + right-click while looking at the air to munch whatever is in your main hand.
 * (Looking at a block is ignored so normal sneak-placing still works.)
 */
public final class EatItems {
    private EatItems() {}

    public static void register() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!CursedConfig.EAT_ITEMS) return InteractionResult.PASS;
            if (level.isClientSide() || !(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;
            // Already edible/drinkable? Let vanilla handle it.
            if (stack.has(DataComponents.CONSUMABLE)) return InteractionResult.PASS;
            // Only when looking at nothing, so we never fight block placement.
            if (player.pick(player.blockInteractionRange(), 0f, false).getType() != HitResult.Type.MISS) {
                return InteractionResult.PASS;
            }

            ServerLevel sl = (ServerLevel) level;
            String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();

            // Things that are very much not food
            if (id.equals("bedrock") || id.contains("command_block")) {
                msg(sp, "That's not food. Why would you do that?");
                sp.hurtServer(sl, sl.damageSources().magic(), 2f);
                return InteractionResult.SUCCESS;
            }

            applyEffect(sp, sl, stack, id);

            player.getFoodData().eat(2, 0.5f);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1f, 0.8f + level.random.nextFloat() * 0.6f);
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
            return InteractionResult.SUCCESS;
        });
    }

    private static void applyEffect(ServerPlayer p, ServerLevel sl, ItemStack stack, String id) {
        if (stack.is(ItemTags.SWORDS)) {
            msg(p, "Sharp. Strong. Ow.");
            p.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 30, 1));
            p.hurtServer(sl, sl.damageSources().magic(), 2f);
        } else if (stack.is(ItemTags.PICKAXES)) {
            msg(p, "Crunchy. You feel like mining.");
            p.addEffect(new MobEffectInstance(MobEffects.HASTE, 20 * 60, 1));
        } else if (id.equals("shield")) {
            p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 30, 1));
        } else if (id.equals("tnt")) {
            msg(p, "Oh no.");
            sl.explode(null, p.getX(), p.getY(), p.getZ(), 2.5f, Level.ExplosionInteraction.NONE);
        } else if (id.equals("crafting_table")) {
            msg(p, "You feel crafty.");
            p.giveExperienceLevels(3);
        } else if (id.equals("sand") || id.equals("red_sand") || id.equals("gravel")) {
            msg(p, "*gritty crunch*");
        } else if (id.equals("cobblestone") || id.equals("stone")) {
            msg(p, "Filling, but heavy.");
            p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20 * 20, 1));
            p.getFoodData().eat(6, 0.6f);
        } else if (id.equals("elytra")) {
            msg(p, "You taste the wind.");
            p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * 4, 2));
            p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 15, 0));
        } else if (stack.has(DataComponents.EQUIPPABLE)) {
            EquipmentSlot slot = stack.get(DataComponents.EQUIPPABLE).slot();
            switch (slot) {
                case HEAD -> { msg(p, "Your head feels bigger."); p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 20, 0)); }
                case FEET -> { msg(p, "Spring in your step."); p.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 20 * 30, 2)); }
                default -> p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20 * 20, 0));
            }
        } else if (id.endsWith("_spawn_egg")) {
            msg(p, "Something is moving around in there.");
            p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 10, 0));
        } else if (stack.getItem() instanceof BlockItem) {
            msg(p, "Chewy.");
            p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20 * 5, 0));
        } else {
            msg(p, "Tastes like " + id.replace('_', ' ') + ".");
        }
        sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX(), p.getEyeY(), p.getZ(), 6, 0.3, 0.3, 0.3, 0.0);
    }

    static void msg(ServerPlayer p, String text) {
        p.sendSystemMessage(Component.literal(text));
    }
}
