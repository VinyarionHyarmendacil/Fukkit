package vinyarion.fukkit.main.game;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vinyarion.fukkit.main.cmd.EntityCommandSender;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;

public class FEntityWand extends Entity {
	
    private int field_145795_e = -1;
    private int field_145793_f = -1;
    private int field_145794_g = -1;
    private Block field_145796_h;
    private boolean inGround;
    public EntityLivingBase shootingEntity;
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    private static final String __OBFID = "CL_00001717";
    
    public FEntityWand(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }
    
    public FEntityWand(World world, double x, double y, double z, double accx, double accy, double accz) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setPosition(x, y, z);
        double d6 = (double)MathHelper.sqrt_double(accx * accx + accy * accy + accz * accz);
        this.accelerationX = accx / d6 * 0.1D;
        this.accelerationY = accy / d6 * 0.1D;
        this.accelerationZ = accz / d6 * 0.1D;
    }
    
    public FEntityWand(World world, EntityLivingBase shooter, double accx, double accy, double accz, double deviation) {
        super(world);
        this.shootingEntity = shooter;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(shooter.posX, shooter.boundingBox.minY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        if(deviation > 0.0D) {
          accx += this.rand.nextGaussian() * 0.04D * deviation;
          accy += this.rand.nextGaussian() * 0.04D * deviation;
          accz += this.rand.nextGaussian() * 0.04D * deviation;
        }
        double d3 = (double)MathHelper.sqrt_double(accx * accx + accy * accy + accz * accz);
        this.accelerationX = accx / d3 * 0.1D;
        this.accelerationY = accy / d3 * 0.1D;
        this.accelerationZ = accz / d3 * 0.1D;
    }
    
    public void onUpdate() {
        if (!this.worldObj.isRemote && (this.shootingEntity != null && this.shootingEntity.isDead || !this.worldObj.blockExists((int)this.posX, (int)this.posY, (int)this.posZ))) {
            this.setDead();
        } else {
            super.onUpdate();
//            this.setFire(1);
            if (this.inGround) {
                if (this.worldObj.getBlock(this.field_145795_e, this.field_145793_f, this.field_145794_g) == this.field_145796_h) {
                    ++this.ticksAlive;
                    if (this.ticksAlive >= 1200) {
                        this.setDead();
                    }
                    return;
                }
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksAlive = 0;
                this.ticksInAir = 0;
            } else {
                ++this.ticksInAir;
            }
            Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
            if (movingobjectposition != null) {
	            if (movingobjectposition.typeOfHit != MovingObjectType.MISS) {
	            	
	            }
            }
            // Gravity 
            double fall = this.gravity * (double)this.ticksExisted;
            vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY - fall, this.posZ + this.motionZ);
            if (movingobjectposition != null) {
                vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }
            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            for(int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity)list.get(i);
                if (entity1 instanceof FEntityWand && ((FEntityWand)entity1).shootingEntity == this.shootingEntity) continue;
                if (entity1.canBeCollidedWith() && (!entity1.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double)f, (double)f, (double)f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);
                    if (movingobjectposition1 != null) {
                        double d1 = vec3.distanceTo(movingobjectposition1.hitVec);
                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
            if (movingobjectposition != null) {
                this.onImpact(movingobjectposition);
            }
            this.posX += this.motionX;
            this.posY += this.motionY; this.posY -= fall;
            this.posZ += this.motionZ;
            float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) + 90.0F;
            for (this.rotationPitch = (float)(Math.atan2((double)f1, this.motionY) * 180.0D / Math.PI) - 90.0F; this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) { }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f2 = this.velocityFly;
            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f3, this.posY - this.motionY * (double)f3, this.posZ - this.motionZ * (double)f3, this.motionX, this.motionY, this.motionZ);
                }
                f2 = 0.8F;
            }
            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
            this.motionX *= (double)f2;
            this.motionY *= (double)f2;
            this.motionZ *= (double)f2;
            if (this.shootingEntity != null) {
            	if (this.getDistanceToEntity(shootingEntity) > 2.0D) {
            		this.spawnParticles();
            	}
            } else {
            	this.spawnParticles();
            }
            this.soundFlying();
            this.setPosition(this.posX, this.posY - fall, this.posZ);
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound tag) {
        tag.setShort("xTile", (short)this.field_145795_e);
        tag.setShort("yTile", (short)this.field_145793_f);
        tag.setShort("zTile", (short)this.field_145794_g);
        tag.setByte("inTile", (byte)Block.getIdFromBlock(this.field_145796_h));
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.setTag("direction", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
    }
    
    public void readEntityFromNBT(NBTTagCompound tag) {
        this.field_145795_e = tag.getShort("xTile");
        this.field_145793_f = tag.getShort("yTile");
        this.field_145794_g = tag.getShort("zTile");
        this.field_145796_h = Block.getBlockById(tag.getByte("inTile") & 255);
        this.inGround = tag.getByte("inGround") == 1;
        if (tag.hasKey("direction", 9)) {
            NBTTagList nbttaglist = tag.getTagList("direction", 6);
            this.motionX = nbttaglist.func_150309_d(0);
            this.motionY = nbttaglist.func_150309_d(1);
            this.motionZ = nbttaglist.func_150309_d(2);
        } else {
            this.setDead();
        }
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    public float getCollisionBorderSize() {
        return 1.0F;
    }

    public boolean attackEntityFrom(DamageSource source, float dmg) {
        return false;
    }
    
    public float getBrightness(float f) {
        return 1.0F;
    }

    public void entityInit() {
    	
    }
    
    private double randPMD(double max) {
    	return ((this.rand.nextDouble() * 2.0D) - 1.0D) * max;
    }
    
    private double randPMD() {
    	return (this.rand.nextDouble() * 2.0D) - 1.0D;
    }
    
    private float randPMF(float max) {
    	return ((this.rand.nextFloat() * 2.0f) - 1.f) * max;
    }
    
    private float randPMF() {
    	return (this.rand.nextFloat() * 2.0f) - 1.0f;
    }

    public float velocityFly = 1.95F; //default is 0.95F
    public String soundFly = null;
    public String particleFly = null;
    public String soundHit = null;
    public String particleHit = null;
    public String effectHit = "magicDamage";
    public double areaSize = 3.0D;
	public String effectValue = "4.0";
    
    private int soundTicks = -1;
	public boolean flame = false;
	public double gravity = 0;
    
    public void soundFlying() {
    	this.soundTicks++;
    	if (this.soundTicks > 0) {
    		return;
    	}
    	if (this.soundFly != null) {
			FAesthetics.soundDimPos(
				this.worldObj, 
				this.soundFly, 
				this.posX, 
				this.posY, 
				this.posZ, 
				1.0F, 
				1.0F
			);
		} else {
			FAesthetics.soundDimPos(
				this.worldObj, 
				"", 
				this.posX, 
				this.posY, 
				this.posZ, 
				1.0F, 
				1.0F
			);
		}
    }
    
    public void spawnParticles() {
		if (this.particleFly != null) {
			FAesthetics.particle(
				this.worldObj, 
	    		particleFly, 
	    		this.posX, 
	    		this.posY, 
	    		this.posZ, 
	    		0.04D, 
	    		0.04D, 
	    		0.04D,
	    		0.01D,
	    		4
	    	);
		} else {
			FAesthetics.particle(
				this.worldObj, 
				"fireworksSpark", 
	    		this.posX, 
	    		this.posY, 
	    		this.posZ, 
	    		0.04D, 
	    		0.04D, 
	    		0.04D, 
	    		0.01D, 
	    		4
	    	);
		}
    }
    
    public void onImpact(MovingObjectPosition position) {
    	
    	if(this.particleHit != null) {
    		FAesthetics.particle(
    			this.worldObj, 
        		particleHit, 
        		position.hitVec.xCoord, 
        		position.hitVec.yCoord, 
        		position.hitVec.zCoord, 
        		2.2, 
        		2.2, 
        		2.2, 
        		0.1D, 
        		80
        	);
    	} else {
    		FAesthetics.particle(
    			this.worldObj, 
				"fireworksSpark", 
        		position.hitVec.xCoord, 
        		position.hitVec.yCoord, 
        		position.hitVec.zCoord, 
        		1.2, 
        		1.2, 
        		1.2, 
        		0.1D, 
        		80
        	);
    	}
    	if (this.soundHit != null) {
			FAesthetics.soundDimPos(
				this.worldObj, 
				this.soundHit, 
				this.posX, 
				this.posY, 
				this.posZ, 
				1.0F, 
				1.0F
			);
		} else {
			FAesthetics.soundDimPos(
				this.worldObj, 
				"random.explode", 
				this.posX, 
				this.posY, 
				this.posZ, 
				1.0F, 
				1.0F
			);
		}
        if(this.effectHit.equals("magicDamage")) {
        	DamageSource ds = new EntityDamageSourceIndirect("indirectMagic", this.shootingEntity, this);
        	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
        			position.hitVec.xCoord - this.areaSize, 
            		position.hitVec.yCoord - this.areaSize * 0.666667D, 
            		position.hitVec.zCoord - this.areaSize, 
            		position.hitVec.xCoord + this.areaSize, 
            		position.hitVec.yCoord + this.areaSize * 0.666667D, 
            		position.hitVec.zCoord + this.areaSize);
        	System.out.println(aabb);
        	for(Object o : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, aabb)) {
        		Entity e = (Entity)o;
        		if(e instanceof EntityLivingBase) {
        			EntityLivingBase elb = (EntityLivingBase)e;
        			try {
        				elb.attackEntityFrom(ds, Float.parseFloat(this.effectValue));
        				if(this.flame) {
        					elb.setFire(3);
        				}
        			} catch(Exception ex) {
        				ex.printStackTrace();
        				FCommand.NotifyAll(MinecraftServer.getServer(), "Error in wands! " + ex.getMessage() + " : " + ex.getLocalizedMessage());
        			}
        		}
        	}
        } else if(this.effectHit.equals("command")) {
        	ICommandSender parent = (ICommandSender)(this.shootingEntity instanceof ICommandSender ? this.shootingEntity : null);
        	EntityCommandSender ecs = new EntityCommandSender(parent, this, position.hitVec);
			FPermissions.isOverride.set(true);
        	MinecraftServer.getServer().getCommandManager().executeCommand(ecs, this.effectValue);
			FPermissions.isOverride.remove();
        }
    	this.setDead();
    }
    
}