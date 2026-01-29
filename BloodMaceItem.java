package com.example.weapons;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class BloodMaceItem extends SwordItem {

    public BloodMaceItem(Tier tier, int attackDamage, float attackSpeed, Properties props) {
        super(tier, attackDamage, attackSpeed, props);
    }

    // ---------------------------------------------------------
    // NORMAL ATTACK
    // ---------------------------------------------------------
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();

        // 5 Herzen = 10 Schaden
        target.hurt(level.damageSources().mobAttack(attacker), 10.0F);

        // Blut-Ring Effekt
        BloodEffects.spawnBloodRing(level, target);

        // Gift + Wither für 15 Sekunden
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 15, 1));
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 15, 1));

        // Stoppt, wenn Ziel 1 Herz erreicht
        if (target.getHealth() <= 2.0F) {
            target.removeEffect(MobEffects.POISON);
            target.removeEffect(MobEffects.WITHER);
        }

        // Heilung bei Kill
        if (!target.isAlive() && attacker instanceof Player player) {
            player.heal(8.0F); // 4 Herzen
        }

        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        return true;
    }

    // ---------------------------------------------------------
    // ULTIMATE ATTACK (Right Click)
    // ---------------------------------------------------------
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {

            // Spieler hochschleudern
            player.setDeltaMovement(player.getDeltaMovement().x, 2.5, player.getDeltaMovement().z);

            // Bloody Elytra geben
            player.getInventory().armor.set(2, new ItemStack(ModItems.BLOODY_ELYTRA.get()));

            // Cooldown
            player.getCooldowns().addCooldown(this, 20 * 20); // 20 Sekunden

            level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.0F, 0.6F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // ---------------------------------------------------------
    // WENN DER SPIELER MIT DER ULTIMATE JEMANDEN TRIFFT
    // ---------------------------------------------------------
    public static void onUltimateHit(Player player, LivingEntity target) {
        Level level = player.level();

        // Erdbeben-Effekt
        BloodEffects.createEarthquake(level, target.blockPosition(), 10);

        // Heilung bei Kill
        if (!target.isAlive()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
