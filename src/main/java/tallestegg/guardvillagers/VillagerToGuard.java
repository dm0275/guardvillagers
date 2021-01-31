package tallestegg.guardvillagers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.entities.GuardEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VillagerToGuard {
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemstack = event.getItemStack();
        if (itemstack.getItem() instanceof SwordItem && event.getPlayer().isCrouching() || itemstack.getItem() instanceof CrossbowItem && event.getPlayer().isCrouching()) {
            Entity target = event.getTarget();
            if (target instanceof VillagerEntity) {
                VillagerEntity villager = (VillagerEntity) event.getTarget();
                if (!villager.isChild()) {
                    if (villager.getVillagerData().getProfession() == VillagerProfession.NONE || villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                        if (!GuardConfig.ConvertVillagerIfHaveHOTV || event.getPlayer().isPotionActive(Effects.HERO_OF_THE_VILLAGE) && GuardConfig.ConvertVillagerIfHaveHOTV) {
                            this.convertVillager(villager, event.getPlayer());
                            if (!event.getPlayer().abilities.isCreativeMode)
                                itemstack.shrink(1);
                        }
                    }
                }
            }
        }
    }

    private void convertVillager(LivingEntity entity, PlayerEntity player) {
        ItemStack itemstack = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        GuardEntity guard = GuardEntityType.GUARD.get().create(entity.world);
        VillagerEntity villager = (VillagerEntity) entity;
        if (guard == null) return;
        guard.copyLocationAndAnglesFrom(villager);
        guard.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack.copy());
        int i = GuardEntity.getRandomTypeForBiome(guard.world, guard.getPosition());
        guard.setGuardVariant(i);
        guard.enablePersistence();
        if (villager.hasCustomName()) {
            guard.setCustomName(villager.getCustomName());
            guard.setCustomNameVisible(villager.isCustomNameVisible());
        }
        guard.setCanPickUpLoot(true);
        guard.setDropChance(EquipmentSlotType.HEAD, 100.0F);
        guard.setDropChance(EquipmentSlotType.CHEST, 100.0F);
        guard.setDropChance(EquipmentSlotType.FEET, 100.0F);
        guard.setDropChance(EquipmentSlotType.LEGS, 100.0F);
        guard.setDropChance(EquipmentSlotType.MAINHAND, 100.0F);
        guard.setDropChance(EquipmentSlotType.OFFHAND, 100.0F);
        villager.world.addEntity(guard);
        villager.func_213742_a(MemoryModuleType.HOME);
        villager.func_213742_a(MemoryModuleType.JOB_SITE);
        villager.func_213742_a(MemoryModuleType.MEETING_POINT);
        villager.remove();
    }
}
