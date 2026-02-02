package com.example.weapons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FrostMaceItem extends SwordItem {

    public FrostMaceItem(Tier tier, int attackDamage, float attackSpeed, Properties props) {
        super(tier, attackDamage, attackSpeed, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();

        if (!level.isClientSide && attacker instanceof Player player) {
            target.setTicksFrozen(200);
            target.setDeltaMovement(target.getDeltaMovement().multiply(0.1, 0.1, 0.1));

            BlockPos center = target.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(
                    center.offset(-2, -1, -2),
                    center.offset(2, -1, 2))) {
                if (level.getBlockState(pos).isAir()) continue;
                level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
