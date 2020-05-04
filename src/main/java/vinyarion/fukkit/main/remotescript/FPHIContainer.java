package vinyarion.fukkit.main.remotescript;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class FPHIContainer<E extends Entity> implements ICommandSender {
	
	public final E entity;
	
	public FPHIContainer(E entity) {
		this.entity = entity;
	}
	
	public Object get(String key) {
		for(Method m : this.getClass().getMethods()) {
			if(m.getName().equals("get_" + key)) {
				try {
					return m.invoke(this);
				} catch (Exception e) { }
			}
		}
		return null;
	}
	
	public void set(String key, Object value) {
		for(Method m : this.getClass().getMethods()) {
			if(m.getName().equals("set_" + key)) {
				try {
					m.invoke(this, value);
				} catch (Exception e) { }
				return;
			}
		}
	}
	
	public double get_px() {
		return entity.posX;
	}
	
	public double get_py() {
		return entity.posY;
	}
	
	public double get_pz() {
		return entity.posZ;
	}
	
	public void set_px(double posX) {
		entity.posX = posX;
	}
	
	public void set_py(double posY) {
		entity.posY = posY;
	}
	
	public void set_pz(double posZ) {
		entity.posZ = posZ;
	}
	
	public double get_mx() {
		return entity.motionX;
	}
	
	public double get_my() {
		return entity.motionY;
	}
	
	public double get_mz() {
		return entity.motionZ;
	}
	
	public void set_mx(double motionX) {
		entity.motionX = motionX;
	}
	
	public void set_my(double motionY) {
		entity.motionY = motionY;
	}
	
	public void set_mz(double motionZ) {
		entity.motionZ = motionZ;
	}
	
	public String getCommandSenderName() {
		return null;
	}
	
	public IChatComponent func_145748_c_() {
		return null;
	}
	
	public void addChatMessage(IChatComponent p_145747_1_) {
		
	}
	
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
		return false;
	}
	
	public ChunkCoordinates getPlayerCoordinates() {
		return null;
	}
	
	public World getEntityWorld() {
		return null;
	}
	
	public static class ContainerPlayer<EEE extends EntityPlayer> extends ContainerLivingBase<EEE> {
		
		public ContainerPlayer(EEE entity) {
			super(entity);
		}
		
	}
	
	public static class ContainerLivingBase<EE extends EntityLivingBase> extends FPHIContainer<EE> {
		
		public ContainerLivingBase(EE entity) {
			super(entity);
		}
		
		public float health() {
			return entity.getHealth();
		}
		
		public void health(float health) {
			entity.setHealth(health);
		}
		
		public float health_max() {
			return entity.getMaxHealth();
		}
		
		public void health_max(float health) {
			entity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		}
		
		public void health_add(float health) {
			entity.heal(health);
		}
		
	}
	
}
