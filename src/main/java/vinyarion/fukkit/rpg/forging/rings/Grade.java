package vinyarion.fukkit.rpg.forging.rings;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.attrib.FRingAttribEffect;

public enum Grade {
	ERROR(-2, "Misforged"),
	BROKEN(-1, "Broken"),
	NORMAL(0, "Normal"), 
	COMMON(1, "Common"), 
	UNCOMMON(2, "Uncommon"), 
	HEROIC(3, "Heroic"), 
	RARE(4, "Rare"), 
	EPIC(5, "Epic"), 
	LEGENDARY(6, "Legendary"), 
	MYTHICAL(7, "Mythical"), 
	LEVEL_8(8, "Mythical +"), 
	LEVEL_9(9, "Mythical ++");
	
	static {
		for(Grade g : Grade.values()) {
			if(g.index == -1) continue;
			g.weightsForgeRing = Grade.Item.of(
				new Grade[]{
					BROKEN, g, g.previous(), g.next(), g.next().next(), ERROR
				}, new int[]{
					40, 30, 10, 9, 1, 10
				}
			);
			g.weightsForgeGem = Grade.Item.of(
				new Grade[]{
					BROKEN, g, g.previous(), g.next(), g.next().next()
				}, new int[]{
					50, 30, 10, 9, 1
				}
			);
			g.weightsForgeHammer = Grade.Item.of(
				new Grade[]{
					BROKEN, g, g.previous(), g.next(), g.next().next()
				}, new int[]{
					50, 30, 10, 9, 1
				}
			);
		}
	}
	
	public final int index;
	public final String name;
	private Grade.Item[] weightsForgeRing = new Grade.Item[0];
	private Grade.Item[] weightsForgeGem = new Grade.Item[0];
	private Grade.Item[] weightsForgeHammer = new Grade.Item[0];
	
	public static Grade of(int level) {
		level+=2;
		if(level <= 2) {
			return NORMAL;
		} else if(level >= Grade.values().length) {
			return LEVEL_9;
		} else {
			return Grade.values()[level];
		}
	}
	
	private Grade(int idx, String name) {
		this.index = idx;
		this.name = name;
	}
	
	public Grade next() {
		return of(this.index + 1);
	}
	
	public Grade previous() {
		return of(this.index - 1);
	}
	
	public Grade rand(Grade.Item[] weights) {
		Grade grade = ((Grade.Item)WeightedRandom.getRandomItem(rand, weights)).get();
		if(weights == weightsForgeHammer && grade.index >= MYTHICAL.index) {
			return MYTHICAL;
		}
		return grade;
	}
	
	public Grade.Item[] weightsForgeRing() {
		return weightsForgeRing;
	}
	
	public Grade.Item[] weightsForgeGem() {
		return weightsForgeGem;
	}
	
	public Grade.Item[] weightsForgeHammer() {
		return weightsForgeHammer;
	}
	
	private static final Random rand = new Random();
	
	public static class Item extends WeightedRandom.Item {
		
		private final Grade grade;
		
		private Item(Grade grade, int weight) {
			super(weight);
			this.grade = grade;
		}
		
		public Grade get() {
			return this.grade;
		}
		
		public static Grade.Item[] of(Grade[] grades, int[] weights) {
			int length = Math.min(grades.length, weights.length);
			Grade.Item[] items = new Grade.Item[length];
			for(int i = 0; i < length; i++) {
				items[i] = new Grade.Item(grades[i], weights[i]);
			}
			return items;
		}
		
	}
	
	public void addBad(ItemStack result) {
		List<FRingAttribEffect> bads = Lists.newArrayList();
		for(FRingAttribEffect rae : FAttributes.ringPotions) {
			if(rae.isPotionBadEffect()) {
				bads.add(rae);
			}
		}
		FRingAttribEffect put = bads.get(FRingForging.rr.nextInt(bads.size()));
		int time = FRingForging.rr.nextInt(240) + 240;
		put.addTo(result, "0", String.valueOf(time));
	}
	
}