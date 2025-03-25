package earth.terrarium.chipped.mixins;

import earth.terrarium.chipped.Chipped;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(Block.class)
public class BlockBehaviourMixin {

    @Inject(method = "getDroppedStacks", at = @At("HEAD"), cancellable = true)
    private void chipped$overrideLootTable(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        // Only override loot for Chipped blocks
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        if (!id.getNamespace().equals(Chipped.MOD_ID)) return;

        World world = builder.get(LootContextParameters.ORIGIN).getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        Identifier lootTableId = new Identifier(Chipped.MOD_ID, "blocks/" + id.getPath());
        LootTable lootTable = serverWorld.getServer().getLootManager().getTable(lootTableId);

        if (lootTable != LootTable.EMPTY) {
            LootContext context = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
            cir.setReturnValue(lootTable.generateLoot(context));
        } else {
            cir.setReturnValue(List.of(new ItemStack(state.getBlock().asItem())));
        }
    }
}
