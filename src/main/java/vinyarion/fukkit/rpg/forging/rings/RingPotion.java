package vinyarion.fukkit.rpg.forging.rings;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import vinyarion.fukkit.main.attrib.FRingAttribEffect;
import vinyarion.fukkit.main.util.Time;

public class RingPotion {
	
	public RingPotion(FRingAttribEffect rae, ItemStack stack, NBTTagCompound nbt) {
		this.rae = rae;
		this.stack = stack;
		this.nbt = nbt;
		this.potionId = rae.potion;
		this.seconds = nbt.getInteger("seconds");
		this.amplifier = nbt.getInteger("amplifier");
	}
	
	private FRingAttribEffect rae;
	private ItemStack stack;
	private NBTTagCompound nbt;
	private int potionId;
	private int seconds;
	private int amplifier;
	
	public PotionEffect tick() {
		this.seconds--;
		this.nbt.setInteger("seconds", this.seconds);
		this.rae.setOwnedLine(stack, rae.display + " " + (this.amplifier + 1) + " for " + Time.toClockTime(this.seconds));
		if(seconds <= 0) {
			this.rae.removeFrom(stack);
		}
		return new PotionEffect(this.potionId, 21, this.amplifier, false);
	}
	
	public int seconds() {
		return this.seconds;
	}
	
	public int potionId() {
		return this.potionId;
	}
	
	public int amplifier() {
		return this.amplifier;
	}
	
}
