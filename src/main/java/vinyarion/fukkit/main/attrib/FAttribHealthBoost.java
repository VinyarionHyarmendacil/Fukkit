package vinyarion.fukkit.main.attrib;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribHealthBoost extends FAttribute<Integer> {
	
	public static final UUID uuid_head = new UUID(0x1425364758697000L, 0x1425364758697000L);
	public static final UUID uuid_body = new UUID(0x1425364758697011L, 0x1425364758697011L);
	public static final UUID uuid_legs = new UUID(0x1425364758697022L, 0x1425364758697022L);
	public static final UUID uuid_feet = new UUID(0x1425364758697033L, 0x1425364758697033L);
	public static final UUID[] armors = new UUID[]{uuid_head, uuid_body, uuid_legs, uuid_feet};
	public static final String[] names = new String[]{"fmod_head", "fmod_body", "fmod_legs", "fmod_feet"};
	
	public static final UUID uuid_armorset = new UUID(0x1425364758697044L, 0x1425364758697044L);
	public static final UUID uuid_command = new UUID(0x1425364758697055L, 0x1425364758697055L);
	
	public FAttribHealthBoost(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "HealthBoost");
		int level = Integer.parseInt(params[0]);
		tag.setInteger("level", level);
		this.setOwnedLine(stack, "&6Health lvl. " + level);
		return 0;
	}
	
	public Integer update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("HealthBoost");
		int level = tag.getInteger("level");
		this.setOwnedLine(stack, "&6Health lvl. " + level);
		return level;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("HealthBoost");
		this.removeOwnedLine(stack);
	}
	
}
