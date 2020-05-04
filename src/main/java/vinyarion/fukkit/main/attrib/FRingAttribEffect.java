package vinyarion.fukkit.main.attrib;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.forging.rings.RingPotion;

public class FRingAttribEffect extends FRingAttribBase<RingPotion> {
	
	public final String display;
	public final int potion;
	
	public FRingAttribEffect(String hexid, String name, String desc, String display, int potion) {
		super(hexid, name, desc);
		this.display = display;
		this.potion = potion;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound mytag = NBT.ensure(NBT.ensure(stack, "RingAttributes"), name);
		int level = params.length < 1 ? 0 : Integer.parseInt(params[0]);
		int seconds = params.length < 2 ? 0 : Integer.parseInt(params[1]);
		mytag.setInteger("amplifier", level);
		mytag.setInteger("seconds", seconds);
		this.setOwnedLine(stack, display + " " + (level + 1) + " for " + Time.toClockTime(seconds));
		return 0;
	}
	
	public RingPotion update(EntityPlayer player, ItemStack stack) {
		return new RingPotion(this, stack, stack.getTagCompound().getCompoundTag("RingAttributes").getCompoundTag(name));
	}
	
	public void removeFrom(ItemStack stack) {
		this.removeOwnedLine(stack);
		stack.getTagCompound().getCompoundTag("RingAttributes").removeTag(name);
	}
	
	public boolean isPotionBadEffect() {
		return ReflectionHelper.getPrivateValue(Potion.class, Potion.potionTypes[potion], "isBadEffect", "func_76398_f", "f");
	}
	
}
