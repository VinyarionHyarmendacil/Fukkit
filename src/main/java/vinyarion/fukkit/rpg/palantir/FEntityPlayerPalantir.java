package vinyarion.fukkit.rpg.palantir;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class FEntityPlayerPalantir 
extends FakePlayer {
//extends EntityLivingBase {

	public FEntityPlayerPalantir(EntityPlayer player) {
		super((WorldServer)player.worldObj, player.getGameProfile());
		this.playerNetServerHandler = new FFakeNetHandlerPlayServer(this);
	}

//	public FEntityPlayerPalantir(EntityPlayer player) {
//		super(player.worldObj);
//	}
//
//	@Override
//	public ItemStack getHeldItem() {
//		return null;
//	}
//	
//	@Override
//	public ItemStack getEquipmentInSlot(int p_71124_1_) {
//		return null;
//	}
//	
//	@Override
//	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_) {
//		
//	}
//	
//	@Override
//	public ItemStack[] getLastActiveItems() {
//		return new ItemStack[5];
//	}

}
