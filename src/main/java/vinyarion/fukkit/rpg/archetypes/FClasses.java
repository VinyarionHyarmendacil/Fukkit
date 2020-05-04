package vinyarion.fukkit.rpg.archetypes;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import vinyarion.fukkit.rpg.skill.FSkill.Category;

public class FClasses {

	// Start

	public static final FClass beginner = new FClass("Beginner", FClass.Type.NONE, FClass.Tier.NONE);

	public static final FClass swordsman = new FClass("Swordsman", FClass.Type.MELEE, FClass.Tier.BASIC);
	public static final FClass archer = new FClass("Archer", FClass.Type.RANGE, FClass.Tier.BASIC);
	public static final FClass mage = new FClass("Mage", FClass.Type.MAGIC, FClass.Tier.BASIC);

	// End

	public static final List<String> names = Lists.newArrayList();
	public static final List<FClass> skills = Lists.newArrayList();
	public static final Map<String, FClass> nameMap = Maps.newHashMap();
	
	static {
		for(Field f : FClasses.class.getDeclaredFields()) {
			if(FClass.class.isAssignableFrom(f.getType())) {
				try {
					f.setAccessible(true);
					FClass s = (FClass)f.get(null);
					s.id = f.getName();
					skills.add(s);
					names.add(s.id);
					nameMap.put(s.id, s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static FClass get(String name) {
		return nameMap.getOrDefault(name, beginner);
	}
	
	public static List<String> names() {
		return names;
	}

}
