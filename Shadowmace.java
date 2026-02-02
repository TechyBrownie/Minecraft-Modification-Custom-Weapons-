package com.example.weapons;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ShadowMaceItem extends SwordItem {

    private static final Random RANDOM = new Random();

    public ShadowMaceItem(Tier tier, int attackDamage, float attackSpeed, Properties props) {
        super(tier, attackDamage, attackSpeed, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();

        if (!level.isClientSide && attacker instanceof Player player) {
            boolean shadowCrit = false;

            if (player.hasEffect(MobEffects.INVISIBILITY)) {
                target.hurt(level.damageSources().playerAttack(player), 6.0F);
                shadowCrit = true;
            }

            if (RANDOM.nextFloat() < 0.20F) {
                target.hurt(level.damageSources().playerAttack(player), 8.0F);
                shadowCrit = true;
            }

            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));

            if (shadowCrit) {
                level.playSound(null, player.blockPosition(),
                        net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        1.0F, 0.5F);
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }

            Vec3 look = player.getLookAngle().normalize().scale(6);
            Vec3 newPos = player.position().add(look);
            player.teleportTo(newPos.x, newPos.y, newPos.z);

            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0));
            player.getCooldowns().addCooldown(this, 20 * 12);

            level.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F, 1.0F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
