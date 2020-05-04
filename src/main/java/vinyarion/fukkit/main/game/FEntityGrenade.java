package vinyarion.fukkit.main.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FEntityGrenade extends EntityItem {
	
	public int mode;
	
	public FEntityGrenade(World w, double x, double y, double z, ItemStack s) {
		super(w, x, y, z, s);
	}
	
	public FEntityGrenade(World w, double x, double y, double z) {
		super(w, x, y, z);
	}
	
	public FEntityGrenade(World w) {
		super(w);
	}
	
	public void onUpdate() {
		super.onUpdate();
	}
	
	public boolean combineItems(EntityItem ei) {
		return false;
	}
	
	public void setAgeToCreativeDespawnTime() { }
	
	protected void dealFireDamage(int i) {
		this.explode();
	}
	
	public void applyEntityCollision(Entity e) {
		if(mode == -1) {
			this.explode();
		} else {
			super.applyEntityCollision(e);
		}
	}
	
	public void onCollideWithPlayer(EntityPlayer p) {
		if(mode == -1) {
			this.explode();
		} else {
			super.onCollideWithPlayer(p);
		}
	}
	
	public void explode() {
		this.setDead();
	}
	
}
