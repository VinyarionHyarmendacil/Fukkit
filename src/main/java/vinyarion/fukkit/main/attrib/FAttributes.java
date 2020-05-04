package vinyarion.fukkit.main.attrib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttributes {
	
	/*
	 * Attribute hex allocation:
	 * 00 to 7F      ==> 'Normal'
	 * 80 to BF      ==> 'Rings'
	 * C0 to DF      ==> 'Special'
	 * E0 to FE      ==> TBD
	 * FF            ==> Additional:
	 * FF00 to FFFF  ==> TBD
	 * 
	 */
	
	/*     'Normal' attributes     */
	
	public static final FAttribDurability durability = new FAttribDurability("00", "Uses", 
			"<max,integer> <current,integer> <isRechargable,boolean>");
	
	public static final FAttribCooldown cooldown = new FAttribCooldown("01", "Cooldown", 
			"<ticks,integer>");
	
	public static final FAttribArmorBreak armorBreak = new FAttribArmorBreak("02", "ArmorBreak", 
			"<level,integer>");
	
	public static final FAttribExtraDamage extraDamage = new FAttribExtraDamage("03", "ExtraDamage", 
			"<level,integer>");
	
	public static final FAttribHealthBoost healthBoost = new FAttribHealthBoost("04", "HealthBoost", 
			"<level,integer>");
	
	public static final FAttribArmorParticles armorParticles = new FAttribArmorParticles("05", "ArmorParticles", 
			"<particle,String>");
	
	public static final FAttribEffectAttack effectAttack = new FAttribEffectAttack("06", "PoisonAttack", 
			"<level,int>");
	
	public static final FAttribSoulbound soulbound = new FAttribSoulbound("07", "Soulbound", 
			"None");
	
	public static final FAttribWhip whip = new FAttribWhip("08", "Whip", 
			"<level,int>");
	
	public static final FAttribMultishot mulitShot = new FAttribMultishot("09", "Multishot", 
			"None");
	
	public static final FAttribArmorSet armorSet = new FAttribArmorSet("0A", "ArmorSet", 
			"<name,String>");
	
	public static final FAttribEffectGeneric genericEffect = new FAttribEffectGeneric("0B", "GenericEffect", 
			"<name,String>");
	
	public static final FAttribSpikedBoots spikedBoots = new FAttribSpikedBoots("0C", "SpikedBoots", 
			"<max,int>");
	
	public static final FAttribVanishing curseOfVanishing = new FAttribVanishing("0D", "Vanishing", 
			"None");
	
	public static final FAttribBinding curseOfBinding = new FAttribBinding("0E", "Binding", 
			"None");
	
	public static final FAttribMace mace = new FAttribMace("0F", "Mace", 
			"<level,int>");
	
	/*     'Ring' attributes     */
	
	public static final FRingAttribWorn ringWorn = new FRingAttribWorn("80", "RingBeingWorn", 
			"None - but must have this attribute to be recognized as a ring!");
	
	public static final FRingAttribEffect ringInvis = new FRingAttribEffect("81", "RingInvis", 
			"None", "Invisibility", Potion.invisibility.id);
	
	public static final FRingAttribEffect ringStrength = new FRingAttribEffect("82", "RingStrength", 
			"<level,integer> <seconds,integer>", "Strength", Potion.damageBoost.id);
	
	public static final FRingAttribEffect ringWeak = new FRingAttribEffect("83", "RingWeak", 
			"<level,integer> <seconds,integer>", "Strength", Potion.weakness.id);
	
	public static final FRingAttribEffect ringHaste = new FRingAttribEffect("84", "RingHaste", 
			"<level,integer> <seconds,integer>", "Haste", Potion.digSpeed.id);
	
	public static final FRingAttribEffect ringFatigue = new FRingAttribEffect("85", "RingFatigue", 
			"<level,integer> <seconds,integer>", "Fatigue", Potion.digSlowdown.id);
	
	public static final FRingAttribEffect ringSpeed = new FRingAttribEffect("86", "RingSpeed", 
			"<level,integer> <seconds,integer>", "Speed", Potion.moveSpeed.id);
	
	public static final FRingAttribEffect ringSlow = new FRingAttribEffect("87", "RingSlow", 
			"<level,integer> <seconds,integer>", "Slowness", Potion.moveSlowdown.id);
	
	public static final FRingAttribEffect ringResist = new FRingAttribEffect("88", "RingResist", 
			"<level,integer> <seconds,integer>", "Resistance", Potion.resistance.id);
	
	public static final FRingAttribEffect ringSight = new FRingAttribEffect("89", "RingSight", 
			"<level,integer> <seconds,integer>", "Sight", Potion.nightVision.id);
	
	public static final FRingAttribEffect ringBlind = new FRingAttribEffect("8A", "RingBlind", 
			"<level,integer> <seconds,integer>", "Blindness", Potion.blindness.id);
	
	public static final FRingAttribEffect ringNausea = new FRingAttribEffect("8B", "RingNausea", 
			"<level,integer> <seconds,integer>", "Nausea", Potion.confusion.id);
	
	public static final FRingAttribEffect ringFireResist = new FRingAttribEffect("8C", "RingFireResist", 
			"<level,integer> <seconds,integer>", "Fire Resistance", Potion.fireResistance.id);
	
	public static final FRingAttribEffect ringJump = new FRingAttribEffect("8D", "RingJump", 
			"<level,integer> <seconds,integer>", "Jump", Potion.jump.id);
	
	public static final FRingAttribEffect ringHunger = new FRingAttribEffect("8E", "RingHunger", 
			"<level,integer> <seconds,integer>", "Hunger", Potion.hunger.id);
	
	public static final FRingAttribEffect ringWaterBreath = new FRingAttribEffect("8F", "RingWaterBreath", 
			"<level,integer> <seconds,integer>", "Water Breathing", Potion.waterBreathing.id);
	
	public static final FRingAttribEffect ringWither = new FRingAttribEffect("90", "RingWither", 
			"<level,integer> <seconds,integer>", "Wither", Potion.wither.id);
	
	public static final FRingAttribEffect ringPoison = new FRingAttribEffect("91", "RingPoison", 
			"<level,integer> <seconds,integer>", "Poison", Potion.poison.id);
	
	public static final FRingAttribEffect ringRegen = new FRingAttribEffect("92", "RingRegen", 
			"<level,integer> <seconds,integer>", "Regeneration", Potion.regeneration.id);
	
	public static final FRingAttribEffect ringHealth = new FRingAttribEffect("93", "RingHealth", 
			"<level,integer> <seconds,integer>", "Health Boost", Potion.field_76434_w.id);
	
	public static final FRingAttribEffect ringAbsorption = new FRingAttribEffect("94", "RingAbsorption", 
			"<level,integer> <seconds,integer>", "Absorption", Potion.field_76444_x.id);
	
	public static final FRingAttribEffect ringSaturation = new FRingAttribEffect("95", "RingSaturation", 
			"<level,integer> <seconds,integer>", "Saturation", Potion.field_76443_y.id);
	
	public static final FRingAttribEffect ringHeal = new FRingAttribEffect("96", "RingHeal", 
			"<level,integer> <seconds,integer>", "Healing", Potion.heal.id);
	
	public static final FRingAttribEffect ringHarm = new FRingAttribEffect("97", "RingHarm", 
			"<level,integer> <seconds,integer>", "Harming", Potion.harm.id);
	
	public static final FRingAttribPower ringPowers = new FRingAttribPower("98", "RingPower", 
			"None");
	
	public static final FRingAttribForgingRandomness forgingRandomness = new FRingAttribForgingRandomness("BE", "ForgingRandomness", 
			"Do not use, this is for internal use only!");
	
	public static final FRingAttribForgingGrade forgingGrade = new FRingAttribForgingGrade("BF", "ForgingGrade", 
			"<grade,integer>");
	
	/*     'Special' attributes     */
	
	public static final FAttribLearnRecipe gainRecipe = new FAttribLearnRecipe("C0", "LearnRecipe", 
			"<recipeName,String>");
	
	public static final FAttribExecuteCommand executeCommand = new FAttribExecuteCommand("C1", "ExecuteCommand", 
			"<Type commands, seperated by ';'>");
	
	public static final FAttribRegionBound regionBound = new FAttribRegionBound("C2", "RegionBound", 
			"<region,String>");
	
	/*     End of attributes     */
	
	public static List<FAttribute> attributes = new ArrayList<FAttribute>();
	public static List<String> names = new ArrayList<String>();
	public static List<FRingAttribEffect> ringPotions = new ArrayList<FRingAttribEffect>();
	
	static {
		for(Field f : FAttributes.class.getDeclaredFields()) {
			if(FAttribute.class.isAssignableFrom(f.getType())) {
				try {
					f.setAccessible(true);
					FAttribute a = (FAttribute)f.get(null);
					attributes.add(a);
					names.add(a.name);
					if(a instanceof FRingAttribEffect) {
						ringPotions.add((FRingAttribEffect)a);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static FAttribute get(String name) {
		for(FAttribute a : attributes) {
			if(a.name.equals(name)) {
				return a;
			}
		}
		return null;
	}
	
}
