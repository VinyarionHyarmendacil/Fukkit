package vinyarion.fukkit.rpg.forging.rings;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lotr.common.LOTRMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import vinyarion.fukkit.main.game.FItem;

public class ForgingAttribute {
	
	public ForgingAttributeData data = new ForgingAttributeData();
	public FItem customItem;
	
	public static ForgingAttribute of(ItemStack stack) {
		for(ForgingAttribute fa : all) {
			if(fa.customItem.equals(stack)) return fa;
		}
		return null;
	}
	
	ForgingAttribute(String id, Item item, Potion effect, double[] tiertimes, String display) {
		this.data.customItemId = "RingForging_"+id;
		this.data.id = id;
		this.data.item = item;
		this.data.effect = effect;
		int[] times = new int[tiertimes.length];
		int[] ticks = new int[tiertimes.length];
		for(int i = 0; i < tiertimes.length; i++) {
			double t = tiertimes[i];
			times[i] = MathHelper.floor_double(t * 60D);
			ticks[i] = MathHelper.floor_double(t * 1200D);
		}
		this.data.tiertimes = times;
		this.data.tierticks = ticks;
		this.data.display = display;
	}
	
	public static ForgingAttribute topaz_fireresist = 
			new ForgingAttribute("topaz_fireresist", LOTRMod.topaz, Potion.fireResistance, 
			new double[]{1, 1.5, 2, 3, 4, 5, 6, 7, 10, 15}, 
			"Topaz of Fire Resistance");
	
	public static ForgingAttribute amethyst_haste = 
			new ForgingAttribute("amethyst_haste", LOTRMod.amethyst, Potion.digSpeed, 
			new double[]{1, 2, 3, 4, 5, 7, 10, 12, 15, 20}, 
			"Amethyst of Haste");
	
	public static ForgingAttribute sapphire_nightvision = 
			new ForgingAttribute("sapphire_nightvision", LOTRMod.sapphire, Potion.nightVision, 
			new double[]{2, 3, 5, 7, 10, 20, 30, 60, 90, 120}, 
			"Sapphire of Night Vision");
	
	public static ForgingAttribute ruby_strength = 
			new ForgingAttribute("ruby_strength", LOTRMod.ruby, Potion.damageBoost, 
			new double[]{1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 7}, 
			"Ruby of Strength");
	
	public static ForgingAttribute amber_saturation = 
			new ForgingAttribute("amber_saturation", LOTRMod.amber, Potion.field_76443_y, 
			new double[]{2, 3, 4, 5, 7, 10, 15, 20, 25, 30}, 
			"Amber of Saturation");
	
	public static ForgingAttribute diamond_resistance = 
			new ForgingAttribute("diamond_resistance", LOTRMod.diamond, Potion.resistance, 
			new double[]{0.5, 0.75, 1, 1.25, 1.50, 2, 2.5, 3, 3.5, 4}, 
			"Diamond of Resistance");
	
	public static ForgingAttribute pearl_regeneration = 
			new ForgingAttribute("pearl_regeneration", LOTRMod.pearl, Potion.regeneration, 
			new double[]{0.5, 0.75, 1, 1.5, 2, 2.5, 3, 3.5, 4, 5}, 
			"Pearl of Regeneration");
	
	public static ForgingAttribute opal_speed = 
			new ForgingAttribute("opal_speed", LOTRMod.opal, Potion.moveSpeed, 
			new double[]{1, 1.5, 2, 3, 4, 5, 6, 7, 10, 15}, 
			"Opal of Speed");
	
	public static ForgingAttribute emerald_jump = 
			new ForgingAttribute("emerald_jump", LOTRMod.emerald, Potion.jump, 
			new double[]{1, 1.5, 2, 2.5, 3, 3.5, 4, 5, 7, 10}, 
			"Emerald of Jumping");
	
	public static ForgingAttribute coral_waterbreathing = 
			new ForgingAttribute("coral_waterbreathing", LOTRMod.coral, Potion.waterBreathing, 
			new double[]{2, 3, 5, 7, 10, 15, 20, 30, 45, 60}, 
			"Coral of Water Breathing");
	
	public static ForgingAttribute flameofudun_invisibility = 
			new ForgingAttribute("flameofudun_invisibility", LOTRMod.balrogFire, Potion.invisibility, 
			new double[]{5, 10, 15, 20, 30, 40, 50, 70, 90, 120}, 
			"Invisibility Powder");
	
	public static ForgingAttribute[] all = new ForgingAttribute[]{ topaz_fireresist, amethyst_haste, sapphire_nightvision, ruby_strength, amber_saturation, diamond_resistance, pearl_regeneration, opal_speed, emerald_jump, coral_waterbreathing, flameofudun_invisibility };

}