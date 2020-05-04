package vinyarion.fukkit.rpg.forging.rings;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerAnvil;
import lotr.common.item.LOTRItemGem;
import lotr.common.item.LOTRItemHammer;
import lotr.common.item.LOTRItemRing;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.attrib.FRingAttribEffect;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FAnvilRecipes;
import vinyarion.fukkit.main.data.FItems;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.game.FItem.Defaults;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRingForging extends FAnvilRecipe implements Permenant {
	
	public FRingForging(String name, boolean isPublic) {
		super(name, isPublic);
	}
	
	// Matchers
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return this.isUpgrades(left, top, bottom) || this.isHammers(left, top, bottom) || this.isGems(left, top, bottom) || this.isRings(left, top, bottom);
	}
	
	public boolean isUpgrades(ItemStack left, ItemStack top, ItemStack bottom) {
		return left != null && left.getItem() == LOTRMod.mithril && left.stackSize == 1 && 
				ringSmithHammer.equals(bottom) && FAttributes.forgingGrade.isOn(bottom) && 
				(bottom.getMaxDamage() - bottom.getItemDamage() > 9) && top == null;
	}
	
	public boolean isHammers(ItemStack left, ItemStack top, ItemStack bottom) {
		return ringSmithHammerUpgrade.equals(left) && left.stackSize == 1 && 
				ringSmithHammer.equals(top) && FAttributes.forgingGrade.isOn(top) && 
				bottom == null;
	}
	
	public boolean isGems(ItemStack left, ItemStack top, ItemStack bottom) {
		return left != null && gems.containsKey(left.getItem()) && left.stackSize == 1 && 
				ringSmithHammer.equals(bottom) && FAttributes.forgingGrade.isOn(bottom) && 
				(bottom.getMaxDamage() - bottom.getItemDamage() > 9) && top == null;
	}
	
	public boolean isRings(ItemStack left, ItemStack top, ItemStack bottom) {
		return left != null && rings.getOrDefault(left.getItem(), FItem.DUMMY).equals(left) && left.stackSize == 1 && 
				ringSmithHammer.equals(bottom) && FAttributes.forgingGrade.isOn(bottom) && 
				(bottom.getMaxDamage() - bottom.getItemDamage() > 9) && top == null;
	}
	
	// Display item in output
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		return this.isUpgrades(left, top, bottom) ? this.getResultUpgrades(left, top, bottom) : 
			this.isHammers(left, top, bottom) ? this.getResultHammers(left, top, bottom) : 
			this.isGems(left, top, bottom) ? this.getResultGems(left, top, bottom) : 
			this.getResultRings(left, top, bottom);
	}
	
	private ItemStack getResultUpgrades(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = ringSmithHammerUpgrade.stack();
		Grade hammer = getHammerGrade(bottom);
		FAttributes.forgingRandomness.addTo(ret, hammer.name());
		return ret;
	}
	
	private ItemStack getResultHammers(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = ringSmithHammer.stack();
		Grade hammer = getHammerGrade(left);
		FAttributes.forgingGrade.addTo(ret, String.valueOf(hammer.index));
		return ret;
	}
	
	private ItemStack getResultGems(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = gems.get(left.getItem()).stack();
		Grade hammer = getHammerGrade(bottom);
		FAttributes.forgingRandomness.addTo(ret, hammer.name());
		return ret;
	}
	
	private ItemStack getResultRings(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = rings.get(left.getItem()).stack();
		Grade hammer = getHammerGrade(bottom);
		FAttributes.forgingRandomness.addTo(ret, hammer.name());
		return ret;
	}
	
	// Execute the randomness
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		String[] params = FAttributes.forgingRandomness.update(anvil.thePlayer, result);
		boolean hammer = false;
		if(ringSmithHammerUpgrade.equals(result)) {
			result = clickResultUpgrades(anvil, result, params);
		} else if(ringSmithHammer.equals(result)) {
			result = clickResultHammers(anvil, result, params);
			hammer = true;
		} else if(gems.containsKey(result.getItem())) {
			result = clickResultGems(anvil, result, params);
			System.out.println(result);
		} else if(rings.containsKey(result.getItem())) {
			result = clickResultRings(anvil, result, params);
		} else {
			anvil.thePlayer.addChatMessage(Colors.make("Something went really wrong! Like, the kind of wrong the Party of Fourteen was in during their stay in the Misty Mountains!"));
		}
		Slot[] as = FAnvilRecipe.getLeftTopBottom(anvil);
		as[0].decrStackSize(1);
		if(hammer) {
			if(result != null) {
				result.setItemDamage(as[0].getStack().getItemDamage());
			}
			as[1].decrStackSize(1);
		} else {
			if(as[2].getStack().attemptDamageItem(1, rr)) {
				as[2].putStack(null);
			}
		}
		if(result != null) {
			FAttributes.forgingRandomness.removeFrom(result);
			Grade rg = FAttributes.forgingGrade.update(anvil.thePlayer, result);
			anvil.thePlayer.addChatMessage(Colors.make("You made a " + rg.name + " " + result.getDisplayName()));
		}
		anvil.playAnvilSound();
		return result;
	}
	
	private ItemStack clickResultUpgrades(LOTRContainerAnvil anvil, ItemStack result, String[] params) {
		Grade hammer = getHammerGrade(params);
		Grade grade = hammer.rand(hammer.weightsForgeHammer());
		if(grade == Grade.BROKEN) {
			anvil.thePlayer.addChatMessage(Colors.make("You made a mistake, and your ingot broke whilst you were forging it!"));
			return null;
		}
		FAttributes.forgingGrade.addTo(result, grade.name());
		return result;
	}
	
	private ItemStack clickResultHammers(LOTRContainerAnvil anvil, ItemStack result, String[] params) {
		return result;
	}
	
	private ItemStack clickResultGems(LOTRContainerAnvil anvil, ItemStack result, String[] params) {
		Grade hammer = getHammerGrade(params);
		Grade grade = hammer.rand(hammer.weightsForgeGem());
		if(grade == Grade.BROKEN) {
			anvil.thePlayer.addChatMessage(Colors.chat("You made a mistake, and your gem broke whilst you were forging it!"));
			return null;
		}
		FAttributes.forgingGrade.addTo(result, grade.name());
		return result;
	}
	
	private ItemStack clickResultRings(LOTRContainerAnvil anvil, ItemStack result, String[] params) {
		Grade hammer = getHammerGrade(params);
		Grade grade = hammer.rand(hammer.weightsForgeRing());
		if(grade == Grade.BROKEN) {
			anvil.thePlayer.addChatMessage(Colors.chat("You made a mistake, and your ring broke whilst you were forging it!"));
			return null;
		} else if(grade == Grade.ERROR) {
			anvil.thePlayer.addChatMessage(Colors.chat("You made a mistake, and your ring got a bad effect!"));
			grade.addBad(result);
		}
		FAttributes.forgingGrade.addTo(result, grade.name());
		FAttributes.forgingGrade.setOwnedLineAlternates(result, Colors.GRAY+Colors.ITALIC+String.valueOf(grade.index+1)+" open effect slots", "0");
		return result;
	}
	
	private Grade getHammerGrade(ItemStack hammer) {
		return FAttributes.forgingGrade.isOn(hammer) ? FAttributes.forgingGrade.update(null, hammer) : Grade.NORMAL;
	}
	
	private Grade getHammerGrade(String[] params) {
		Grade hammer = Grade.valueOf(params.length == 0 ? "NORMAL" : params[0]);
		return hammer == null ? Grade.NORMAL : hammer;
	}

	public static Map<Item, FItem> rings = Maps.newHashMap();
	public static Map<Item, FRingAttribEffect> ringEffects = Maps.newHashMap();
	public static Map<Item, FItem> powerrings = Maps.newHashMap();
	public static Map<Item, FItem> gems = Maps.newHashMap();
	public static List<String> gemNames = Lists.newArrayList();
	public static FItem ringSmithHammerUpgrade = FItem.DUMMY;
	public static FItem ringSmithHammer = FItem.DUMMY;
	public static FItem helpBook = FItem.DUMMY;
//	public static ItemStack helpBookStack = FItem.DUMMY.stack();
	public static final String helpBookString = 
		"{"
			+ "pages:["
				+ "0:\"Herein is set forth my record of Ring-making. Many desire to learn this art, but few ever learn skills enough to teach others. Thus I write this book, to keep for future readers the knowledge of the craft.\n\nI shall begin with the fundamentals.\","
				+ "1:\"Getting Started\n\nTo begin, you must have a Blacksmith's Hammer. To craft one, place a three iron ingots in an anvil. Next, turn it into a Ringsmith's Hammer by placing the Blacksmith's Hammer in the left anvil slot with two mithril ingots, one each in\","
				+ "2:\"the top and bottom slots.\","
				+ "3:\"General Principles\n\nIn order to forge rings, you need rings of course. Any of the five types will do. In general, 'forging [item] with [other]' means placing [item] in the left slot of the anvil, [other] in the top, and a Ringsmith's hammer on the bottom.\","
				+ "4:\"Rings\n\nHeat any ring first in order to modify it. This is done by forging it with Durnaur. To make a ring wearable again, but freeze its attributes, forge it with Edhelvir. This can be done as many times as desired. Once a ring is cooled, you can right\","
				+ "5:\"click whilst holding it to put it on. The effects currently on the ring will be given to you, and the remaining time will decrease. If the ring is removed from your hotbar, you will cease its use and wear.\","
				+ "6:\"Forging grades\n\nThere are several forging grades that Hammers, Upgrades, Rings, and Gems can have. These are:\nNormal, Common, Uncommon, Heroic, Rare, Epic, Legendary, Mythical, Mythical+, and Mythical++.\","
				+ "7:\"When forging Upgrades, Rings, and Gems, the grade of the result is dependent on the grade of the hammer used in the bottom slot:\n-There is a 50% chance you will make a mistake.\n-There is a 30% chance it will be the same grade.\","
				+ "8:\"-There is a 10% chance it will be the grade immediately inferior.\n-There is a 9% chance it will be the grade immediately superior.\n-There is a 1% chance it will be the grade twice superior.\","
				+ "9:\"If there is no such grade, the result will default to the same grade as the hammer (or the highest, in the last case). You can also reduce the chance you will make a mistake by having a good Luck stat.\","
				+ "10:\"Upgrades\n\nYour hammer starts out at the Normal grade. Upgrades are made by forging Mithril ingots. To upgrade your hammer, place the hammer on the top and the upgrade on the left. Your hammer also gets repaired in the process.\","
				+ "11:\"Higher grades give gems longer efects and rings more effect slots. For example: a Normal Ruby gives 1 minute of Strength, but a Rare Ruby gives 3. Or for rings: a Common ring has 2 slots, but a Legendary has 7.\","
				+ "12:\"Gems\n\nYou can forge gems by themselves to imbue them with powers. The powers are as follows:\n-Topaz gives Fire Resistance\n-Amythyst gives Haste\n-Sapphire gives Night Vision\n-Ruby gives Strength\","
				+ "13:\"-Amber gives Saturation\n-Diamond gives Resistance\n-Pearl gives Regeneration\n-Opal gives Speed\n-Emerald gives Jump\n-Coral gives Water Breathing\","
				+ "14:\"It is rumored that a terrible firey monster of old will drop a dust that can grant invisibility when forged, but what that creature is and the name of the powder is lost to time.\","
				+ "15:\"It is also believed that some way exists to force the effects of ill upon your enemies, but if it exists, it has not yet been found.\","
			+ "],"
			+ "author:\"A Smith of Ancient Times\","
			+ "title:\"Parf g\u00fbl echyr\","
			+ "display:{"
//				+ "Name:\"Parf g\u00fbl echyr\","
				+ "Lore:["
					+ "0:\"    This book seems like it is very old,\","
					+ "1:\"    so old that the pages are almost too\","
					+ "2:\"    fragile to even touch. Somehow, it\","
					+ "3:\"    seems that it is preserved by magic,\","
					+ "4:\"    but who would want to keep a book\","
					+ "5:\"    safe for thousands or years?\","
				+ "],"
			+ "},"
		+ "}";
	public static final Random rr = new Random();
	
	public static void initSpecialItems() {
		for(ForgingAttribute fa : ForgingAttribute.all) {
			FItems.instance().add(fa.data.item, fa.data.id, fa.data.display);
			fa.customItem = FItems.instance().get(fa.data.id);
			gems.put(fa.data.item, fa.customItem);
			gemNames.add(fa.data.item.getUnlocalizedName());
			for(FRingAttribEffect rae : FAttributes.ringPotions) {
				if(rae.potion == fa.data.effect.id) {
					ringEffects.put(fa.data.item, rae);
					break;
				}
			}
		}
		
		helpBook = FItems.instance().add(Items.written_book, "RingForgingHelpBook", "Parf g\u00fbl echyr");
		Defaults defs = helpBook.getDefaults();
		defs.size = 1;
		defs.damage = 0;
		defs.tag = NBT.parseCompound(helpBookString);
//		helpBookStack = helpBook.stack();
		
		ringSmithHammerUpgrade = FItems.instance().add(LOTRMod.mithril, "RingForging_ringSmithHammerUpgrade", "Ringsmith's Hammer Upgrade");
		ringSmithHammer = FItems.instance().add(LOTRMod.blacksmithHammer, "RingForging_ringSmithHammer", "Ringsmith's Hammer");
		
		rings.put(LOTRMod.mithrilRing, FItems.instance().add(LOTRMod.mithrilRing, "RingForging_ringMithril", "Malleable Mithril Ring"));
		powerrings.put(LOTRMod.mithrilRing, FItems.instance().add(LOTRMod.mithrilRing, "RingForging_ringMithrilPower", "Mithril Ring of Power"));
		
		rings.put(LOTRMod.goldRing, FItems.instance().add(LOTRMod.goldRing, "RingForging_ringGold", "Malleable Gold Ring"));
		powerrings.put(LOTRMod.goldRing, FItems.instance().add(LOTRMod.goldRing, "RingForging_ringGoldPower", "Gold Ring of Power"));
		
		rings.put(LOTRMod.silverRing, FItems.instance().add(LOTRMod.silverRing, "RingForging_ringSilver", "Malleable Silver Ring"));
		powerrings.put(LOTRMod.silverRing, FItems.instance().add(LOTRMod.silverRing, "RingForging_ringSilverPower", "Silver Ring of Power"));
		
		rings.put(LOTRMod.dwarvenRing, FItems.instance().add(LOTRMod.dwarvenRing, "RingForging_ringDwarven", "Malleable Dwarven Ring"));
		powerrings.put(LOTRMod.dwarvenRing, FItems.instance().add(LOTRMod.dwarvenRing, "RingForging_ringDwarvenPower", "Dwarven Ring of Power"));
		
		rings.put(LOTRMod.hobbitRing, FItems.instance().add(LOTRMod.hobbitRing, "RingForging_ringHobbit", "Malleable Hobbit Ring"));
		powerrings.put(LOTRMod.hobbitRing, FItems.instance().add(LOTRMod.hobbitRing, "RingForging_ringHobbitPower", "Hobbit Ring of Power"));

		FAnvilRecipes.instance().addPerm(new FRingForgingBook("RingForgingRecipes_book", true));
		FAnvilRecipes.instance().addPerm(new FRingForgingNormalSmithHammer("RingForgingRecipes_normalSmithHammer", true));
		FAnvilRecipes.instance().addPerm(new FRingForgingRingSmithHammer("RingForgingRecipes_ringSmithHammer", true, ringSmithHammer.stack()));
		FAnvilRecipes.instance().addPerm(new FRingForging("RingForgingRecipes_randomness", false));
		FAnvilRecipes.instance().addPerm(new FRingForgingEffectCombination("RingForgingRecipes_ringGemCombination", true));
		FAnvilRecipes.instance().addPerm(new FRingForgingEffectCool("RingForging_ringCooling", true));
		FAnvilRecipes.instance().addPerm(new FRingForgingEffectHeat("RingForging_ringHeating", true));
	}
	
}
