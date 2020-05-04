package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FArmorSetEffects;
import vinyarion.fukkit.main.util.Effects;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribEffectAttack extends FAttribute<String[]> {
	
	public FAttribEffectAttack(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "EffectAttack");
		String es = FCommand.rest(params,  0);
		tag.setString("effects", es);
		this.setOwnedLine(stack, "&2Inflicts " + es.split("\\;").length + " effects.");
		return 0;
	}
	
	public String[] update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("EffectAttack");
		String[] es = tag.getString("effects").split("\\;");
		this.setOwnedLine(stack, "&2Inflicts " + es.length + " effects.");
		return es;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("PoisonAttack");
		this.removeOwnedLine(stack);
	}
	
}
