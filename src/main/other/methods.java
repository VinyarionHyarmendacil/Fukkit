import java.util.UUID;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.fac.LOTRFaction;
import lotr.common.block.LOTRBlockCraftingTable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.event.entity.EntityEvent;

public class methods {

	public static void changeCraftingTable(LOTRBlockCraftingTable table, LOTRFaction newFaction) {
		ReflectionHelper.setPrivateValue(LOTRBlockCraftingTable.class, table, newFaction, "tableFaction");
	}

	@SubscribeEvent
	public void entityConstructing(EntityEvent.EntityConstructing event) {
		if(event.entity instanceof EntityLiving) {
			// TO DO : Add any other checks you want as well, e.g., check if it's a horse.
			EntityLiving living = (EntityLiving)event.entity;
			IAttributeInstance speed = living.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			// TO DO : Do whatever you want, some useful things could be
			speed.getBaseValue();
			speed.getAttributeValue();
			speed.setBaseValue(0);
			// This UUID will identify your modifier from others. Make it unique, but not random. Hold a static reference to this
			// and use IAttributeInstance.getModifier(UUID) instead of holding references to each AttributeModifier you create.
			/* public static final */ UUID uuid = new UUID(0x0000000000000000, 0x0000000000000000);
			// amount is the value you want to modify the attribute with
			// operation is :
			//     0 for simple addition:
			//         result = value + amount
			//     1 for addition of a multiplied value, sort of like adding % coupons:
			//         result = value + (value * amount)
			//     2 for simple 'scaled' multiplication:
			//         result = value * (1.0D + amount)
			AttributeModifier modifier = new AttributeModifier(uuid, "Your Modifier Name", /* amount */ -0.37D, /* operation */ 2);
			speed.applyModifier(modifier);
			modifier = speed.getModifier(uuid);
			speed.removeAllModifiers();
			speed.removeModifier(modifier);
		}
	}

	static {
		// TO DO : For miniquests, we will need to change this method via ASM in the same classes as the factions:
		// public LOTRMiniQuest createMiniQuest() {
		//     return LOTRMiniQuestFactory.FACTION.createQuest(this);
		// }
		// It's pretty much the same as the factions, except the types are different.
	}

}
