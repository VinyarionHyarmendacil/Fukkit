package vinyarion.fukkit.main.attrib;

import lotr.common.item.LOTRItemBow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribMultishot extends FAttribute<Integer> {
	
	public FAttribMultishot(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		tag.setInteger("level", 1);
		this.setOwnedLine(stack, "&3Fires 1 arrow");
		return 0;
	}
	
	public Integer update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		int level = tag.getInteger("level");
		return level;
	}
	
	public Integer increment(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		int level = tag.getInteger("level");
		level++;
		if(level > 4 || level < 1) {
			level = 1;
		}
		tag.setInteger("level", level);
		this.setOwnedLine(stack, (level == 1 ? "&3Fires " : "&3Volley of ") + level + (level == 1 ? " arrow" : " arrows"));
		return level;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
	public void listen(ArrowLooseEvent event) {
		ItemStack held = event.bow;
		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;
		if(!event.isCanceled() && FAttributes.mulitShot.isOn(held)) {
			int shots = FAttributes.mulitShot.update(player, held);
			boolean isLotRC = held.getItem() instanceof lotr.common.item.LOTRItemCrossbow;
			boolean isLotR = held.getItem() instanceof lotr.common.item.LOTRItemBow && !isLotRC;
			for(int i = 1; i < shots; i++) {
				if(!isLotRC) {
					if(isLotR) {
						this.fireLOTRArrow(held, player, world, event.charge, (float)i, false);
					} else {
						this.fireVanillaArrow(held, player, world, event.charge, (float)i, false);
					}
				}
			}
		}
	}
	
	public void fireVanillaArrow(ItemStack held, EntityPlayer player, World world, int charge, float shot, boolean deadeye) {
        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, held) > 0;
        if (flag || player.inventory.hasItem(Items.arrow)) {
            float f = (float)charge / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;
            if ((double)f < 0.1D) {
                return;
            }
            if (f > 1.0F) {
                f = 1.0F;
            }
            EntityArrow arrow = new EntityArrow(world, player, f * 2.0F);
            if(deadeye) {
    			Misc.resetArrowLooking(arrow, player);
    			arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, f * 3.0F, shot);
            } else {
            	arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, f * 3.0F, 2.0F * shot);
            }
            if (f == 1.0F) {
                arrow.setIsCritical(true);
            }
            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, held);
            if (k > 0) {
                arrow.setDamage(arrow.getDamage() + (double)k * 0.5D + 0.5D);
            }
            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, held);
            if (l > 0) {
                arrow.setKnockbackStrength(l);
            }
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, held) > 0) {
                arrow.setFire(100);
            }
            held.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (Misc.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (flag) {
                arrow.canBePickedUp = 2;
            } else {
                player.inventory.consumeInventoryItem(Items.arrow);
            }
            world.spawnEntityInWorld(arrow);
        }
	}
	
	public void fireLOTRArrow(ItemStack held, EntityPlayer player, World world, int charge, float shot, boolean deadeye) {
		int arrowSlot = -1;
		for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
			ItemStack invItem = player.inventory.mainInventory[i];
			if (invItem != null && (invItem.getItem() == Items.arrow || invItem.getItem() == lotr.common.LOTRMod.arrowPoisoned)) {
				arrowSlot =  i;
				break;
			}
		}
		ItemStack arrowItem = null;
		if (arrowSlot >= 0) {
			arrowItem = player.inventory.mainInventory[arrowSlot];
		}
		boolean flag = !player.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, held) == 0;
		if (arrowItem == null && !flag) {
			arrowItem = new ItemStack(Items.arrow);
		}
		LOTRItemBow itembow = (LOTRItemBow)held.getItem();
		if (arrowItem != null) {
			float f = (float) charge / (float) itembow.getMaxDrawTime();
			f = (f * f + f * 2.0F) / 3.0F;
			f = Math.min(f, 1.0F);
			if (f < 0.1F) {
				return;
			}
			EntityArrow arrow;
			float arrowRange = LOTRItemBow.getLaunchSpeedFactor(held);
			if (arrowItem.getItem() == lotr.common.LOTRMod.arrowPoisoned) {
				arrow = new lotr.common.entity.item.LOTREntityArrowPoisoned(world, player, f * 2.0F * arrowRange);
			} else {
				arrow = new EntityArrow(world, player, f * 2.0F * arrowRange);
			}
			if(deadeye) {
    			Misc.resetArrowLooking(arrow, player);
                arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, f * 3.0F * arrowRange, shot);
			} else {
				arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, f * 3.0F * arrowRange, 2.0F * shot);
			}
			if (arrow.getDamage() < 1.0D) {
				arrow.setDamage(1.0D);
			}
			if (f >= 1.0F) {
				arrow.setIsCritical(true);
			}
			LOTRItemBow.applyBowModifiers(arrow, held);
			held.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (Misc.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
			if (!flag) {
				arrow.canBePickedUp = 2;
			} else if (arrowSlot >= 0) {
				--arrowItem.stackSize;
				if (arrowItem.stackSize <= 0) {
					player.inventory.mainInventory[arrowSlot] = null;
				}
			}
			world.spawnEntityInWorld(arrow);
		}
	}
	
}
