package vinyarion.fukkit.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.common.collect.Lists;

import io.netty.buffer.Unpooled;
import lotr.common.LOTRNetHandlerPlayServer;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommandLoot;
import vinyarion.fukkit.main.data.FAnvilRecipes;
import vinyarion.fukkit.main.data.FLootTables;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.game.FChatRP;
import vinyarion.fukkit.main.game.FLoot;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.handlers.Invisibility;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.playerdata.FScoreboard;
import vinyarion.fukkit.main.remotescript.FPHIExecutor;
import vinyarion.fukkit.main.util.AsyncTaskThread;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.Serialization;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.memory.RTC;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.forging.rings.FRingForging;
import vinyarion.fukkit.rpg.palantir.FEntityPlayerPalantir;
import vinyarion.fukkit.rpg.palantir.FFakeNetworkManager;
import vinyarion.fukkit.rpg.palantir.FPalantir;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;

public class FNetHandlerPlayServer extends LOTRNetHandlerPlayServer {

	public static class FNetData {
		private static Map<FNetHandlerPlayServer, FNetData> datas = new WeakHashMap<FNetHandlerPlayServer, FNetData>();
		public FNetData(FNetHandlerPlayServer handler) {
			this.handler = handler;
			this.server = handler.playerEntity.mcServer;
			this.fukkit_scoreboard = new FScoreboard(handler);
			datas.put(handler, this);
		}
		public final FNetHandlerPlayServer handler;
		public final MinecraftServer server;
		public final FScoreboard fukkit_scoreboard;
		public Entity pointed = null;
		public Vec3 lookingTarget = null;
		public MovingObjectPosition objectMouseOver = null;
		public boolean tirathurin = false;
		public boolean notifyNeedsTirathurin = true;

		public int swingsSinceWatched = 0;
		public int swingsSinceLastBlock = 0;
		public int lastAttacker = 0;
		
		public int swingsSinceLastHit = 0;
		public int combo = 1;
		
		public static FNetData createIfNotPresent(FNetHandlerPlayServer data) {
			if(!data.hasdata()) {
				return new FNetData(data);
			} else {
				return data.data();
			}
		}
	}

	private static final boolean use_rtc = true;
	public static FNetHandlerPlayServer replace(LOTRNetHandlerPlayServer lotrold) {
		FNetHandlerPlayServer replacement;
		if(lotrold instanceof FNetHandlerPlayServer) {
			return (FNetHandlerPlayServer)lotrold;
		}
		if(use_rtc) {
			replacement = RTC.rtc(lotrold, FNetHandlerPlayServer.class);
		} else {
			replacement = new FNetHandlerPlayServer(lotrold);
		}
		return replacement;
	}

	private static ThreadLocal<EntityPlayerMP> currentPlayer = new ThreadLocal<EntityPlayerMP>();
	public static EntityPlayerMP currentPlayer() {
		return currentPlayer.get();
	}
    
    public static FNetHandlerPlayServer of(EntityPlayer player) {
    	return (FNetHandlerPlayServer)((EntityPlayerMP)player).playerNetServerHandler;
    }

	public FNetData data() {
		return FNetData.datas.computeIfAbsent(this, FNetData::createIfNotPresent);
	}

	public boolean hasdata() {
		return FNetData.datas.containsKey(this);
	}

	protected FNetHandlerPlayServer(FEntityPlayerPalantir totem) {
		super(totem.mcServer, new FFakeNetworkManager(), totem);
		new FNetData(this);
	}
	
	public FNetHandlerPlayServer(LOTRNetHandlerPlayServer delegate) {
		super(delegate.playerEntity.mcServer, delegate.netManager, delegate.playerEntity);
		new FNetData(this);
	}
	
	public void onNetworkTick() {
		super.onNetworkTick();
	}
	
	public NetworkManager func_147362_b() {
		return super.func_147362_b();
	}
	
	public void kickPlayerFromServer(String string) {
		if(this.playerEntity == null) {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Player ??????????????? was kicked for " + Colors.color(string));
		} else {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Player " + this.playerEntity.getCommandSenderName() + " was kicked for " + Colors.color(string));
		}
		super.kickPlayerFromServer(Colors.color(string));
	}
	
	public void onConnectionStateTransition(EnumConnectionState state1, EnumConnectionState state2) {
		super.onConnectionStateTransition(state1, state2);
	}
	
	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
		super.setPlayerLocation(x, y, z, yaw, pitch);
	}
	
	public void onDisconnect(IChatComponent component) {
		super.onDisconnect(component);
	}
	
	public void sendPacket(Packet packet) {
		if(null != null)
		if(Misc.debug)
		if(packet instanceof S3BPacketScoreboardObjective || packet instanceof S3CPacketUpdateScore || packet instanceof S3DPacketDisplayScoreboard) {
			for(String s : Serialization.serialize(packet).split("\n")) {
				this.playerEntity.addChatMessage(Colors.MAKE(s));
			}
		} else if(!(packet instanceof S02PacketChat || packet instanceof S20PacketEntityProperties || packet instanceof S03PacketTimeUpdate || packet instanceof S13PacketDestroyEntities || packet instanceof S00PacketKeepAlive || packet instanceof S18PacketEntityTeleport || packet instanceof S1CPacketEntityMetadata || packet instanceof S29PacketSoundEffect || packet instanceof S15PacketEntityRelMove || packet instanceof S12PacketEntityVelocity || packet instanceof S19PacketEntityHeadLook || packet instanceof S17PacketEntityLookMove || packet instanceof S16PacketEntityLook)){
			this.playerEntity.addChatMessage(Colors.MAKE(packet.getClass().getSimpleName()));
		}
		if(packet instanceof S2FPacketSetSlot && FPalantir.is3PV(this.playerEntity)) {
			
			return;
		}
		if(packet instanceof S04PacketEntityEquipment) {
			packet = Invisibility.INSTANCE.intercept(this, (S04PacketEntityEquipment)packet);
		} else if(packet instanceof S1DPacketEntityEffect) {
			packet = Invisibility.INSTANCE.intercept(this, (S1DPacketEntityEffect)packet);
			if(packet == null) return;
		}
		if(packet instanceof S02PacketChat) {
			S02PacketChat chat = (S02PacketChat)packet;
			try {
				PacketBuffer fbuf = new PacketBuffer(Unpooled.buffer());
				chat.writePacketData(fbuf);
				IChatComponent comp = IChatComponent.Serializer.func_150699_a(fbuf.readStringFromBuffer(32767));
				if(comp instanceof ChatComponentTranslation) {
					ChatComponentTranslation trans = (ChatComponentTranslation)comp;
					if(trans.getKey().startsWith("multiplayer.player.joined") ||trans.getKey().startsWith("multiplayer.player.left")) {
						if(FMod.config.doWelcomeMessages) {
							return;
						}
					}
				}
				if(FMod.config.isolateRPChat && FChatRP.rp.contains(this.playerEntity.getCommandSenderName()) && !Colors.uncolor(comp.getUnformattedText()).startsWith("&f[&6RP&f] ")) {
					return;
				}
			} catch (IOException e) { }
		}
		super.sendPacket(packet);
	}
	
	//  Handling
	
	public void processKeepAlive(C00PacketKeepAlive packet) {
		super.processKeepAlive(packet);
	}
	
	public void processChatMessage(C01PacketChatMessage packet) {
		super.processChatMessage(packet);
	}
	
	public void processUseEntity(C02PacketUseEntity packet) {
		FNetData data = this.data();
		if(FPalantir.is3PV(this.playerEntity)) return;
		if(packet.func_149565_c() == C02PacketUseEntity.Action.ATTACK) {
			Entity e = packet.func_149564_a(this.playerEntity.worldObj);
			if(e instanceof EntityLivingBase) {
				EntityLivingBase elb = (EntityLivingBase)e;
				if((elb.getAITarget() == this.playerEntity
						 || elb.getLastAttacker() == this.playerEntity
						 || (elb.getAITarget() != null && elb.getAITarget() instanceof EntityPlayer))
						 && data.swingsSinceLastHit <= 1) {
					data.combo++;
					if(data.combo >= 3) {
						int combo = data.combo;
						String color = combo <= 3 ? Colors.WHITE : combo <= 5 ? Colors.YELLOW : combo <= 7 ? Colors.RED : combo <= 8 ? Colors.LIGHTPURPLE : combo <= 11 ? Colors.AQUA : Colors.GOLD;
						FLOTRHooks.alignmentSprite(
							this.playerEntity, 
							"combo!", 
							combo, 
							elb.posX, 
							elb.posY + 3, 
							elb.posZ
						);
					}
				} else {
					data.combo = 1;
				}
			} else {
				data.combo = 1;
			}
			data.swingsSinceLastHit = 0;
		}
		super.processUseEntity(packet);
	}
	
	public void processPlayer(C03PacketPlayer packet) {
		super.processPlayer(packet);
		this.getMouseOver();
		FPlayerDataRPG.of(this.playerEntity).onTick();
		this.data().fukkit_scoreboard.update();
	}
	
	public void processPlayerDigging(C07PacketPlayerDigging packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		super.processPlayerDigging(packet);
	}
	
	public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		FNetData data = this.data();
		if(data.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			TileEntity te = this.playerEntity.worldObj.getTileEntity(data.objectMouseOver.blockX, data.objectMouseOver.blockY, data.objectMouseOver.blockZ);
			if(data.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				if(te != null) {
					FTileEntityData ted = FTileEntityData.of(te);
					if(this.playerEntity.getGameProfile().getId().equals(ted.owner)) {
						// All OK. Players can't lock themselves out.
					} else {
						boolean ok = ted.isBlacklist;
						for(String n : ted.users) {
							if(n.equalsIgnoreCase(this.playerEntity.getCommandSenderName())) {
								ok = !ted.isBlacklist;
								break;
							}
						}
						if(!ok) {
							this.playerEntity.addChatMessage(Colors.chat("You do not have permission to use this tile entity."));
							return;
						}
					}
				}
			}
			Block b = this.playerEntity.worldObj.getBlock(data.objectMouseOver.blockX, data.objectMouseOver.blockY, data.objectMouseOver.blockZ);
			if(FCommandLoot.getChestApplicable(te)) {
				IInventory chest = (IInventory)te;
				if(FCommandLoot.isCurrentlyUnused(te)) {
					NBTTagCompound tag = FLootTables.instance().getData(te);
					if(tag != null) {
						FLoot loot = FLootTables.instance().getLoot(tag.getString("table"));
						if(loot != null) {
							String now = Time.instantNow();
							if(tag.hasKey("lastrefresh")) {
								String lft = tag.getString("lastrefresh");
								if(Time.hasPassed(lft, loot.cooldown)) {
									tag.setString("lastrefresh", now);
									loot.giveTo(chest);
								} else {
									this.playerEntity.addChatMessage(Colors.CHAT("This chest refills in " + Time.differenceReadable(lft, loot.cooldown)));
								}
							} else {
								tag.setString("lastrefresh", now);
								loot.giveTo(chest);
							}
						}
					}
				}
			}
		}
		currentPlayer.set(this.playerEntity);
		super.processPlayerBlockPlacement(packet);
		currentPlayer.remove();
		this.getMouseOver();
	}
	
	public void processHeldItemChange(C09PacketHeldItemChange packet) {
		FPlayerDataRPG skills = FPlayerDataRPG.of(this.playerEntity);
//		if(null == null) { // TODO : 
//			FCommand.NotifyDev(MinecraftServer.getServer(), this.playerEntity.getCommandSenderName()+" inv "+this.playerEntity.inventory.currentItem+" -> "+packet.func_149614_c());
//			super.processHeldItemChange(packet);
//			return;
//		}
		if(skills.useShortcuts.getValue() && this.playerEntity.isSneaking()) {
			int newslot = packet.func_149614_c();
			int oldslot = this.playerEntity.inventory.currentItem;
			int dir = (newslot == 0 && oldslot == 8) ? 1 : 
				(newslot == 8 && oldslot == 0) ? -1 : 
				(Math.abs(newslot - oldslot) == 1) ? newslot - oldslot : 
				0;
			if(dir != 0) {
				skills.moveActivePointer(dir);
				this.sendPacket(new S09PacketHeldItemChange(oldslot));
			} else {
				super.processHeldItemChange(packet);
			}
		} else {
			super.processHeldItemChange(packet);
		}
	}
	
	public void processAnimation(C0APacketAnimation packet) {
		FNetData data = this.data();
		if(FPalantir.is3PV(this.playerEntity)) {
			if(this.playerEntity.inventory.currentItem == 4) {
				FPalantir.exit3PV(this.playerEntity);
			}
			return;
		}
		if(packet.func_149421_d() == 1) {
			data.swingsSinceWatched++;
			data.swingsSinceLastBlock++;
			data.swingsSinceLastHit++;
		}
		if(data.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS && this.playerEntity.isSneaking()) {
			ItemStack held = this.playerEntity.getHeldItem();
			if(FAttributes.mulitShot.isOn(held) && held.getItem() instanceof ItemBow) {
				int shots = FAttributes.mulitShot.increment(this.playerEntity, held);
				this.playerEntity.addChatMessage(Colors.CHAT("Bow will now shoot " + shots + " arrows"));
			}
			FPlayerDataRPG skills = FPlayerDataRPG.of(this.playerEntity);
			if(skills.useShortcuts.getValue().booleanValue()) {
//				if(null == null) { // TODO : 
//					if(skills.getActiveSkill().isNonEmpty()) {
//						FCommand.NotifyDev(MinecraftServer.getServer(), this.playerEntity.getCommandSenderName()+" inv "+skills.getActiveSkill().skill.name);
//					} else {
//						FCommand.NotifyDev(MinecraftServer.getServer(), this.playerEntity.getCommandSenderName()+" inv "+null);
//					}
//					super.processAnimation(packet);
//					return;
//				}
				skills.useActiveSkill();
			}
		}
		super.processAnimation(packet);
	}
	
	public void processEntityAction(C0BPacketEntityAction packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		super.processEntityAction(packet);
	}
	
	public void processInput(C0CPacketInput packet) {
		super.processInput(packet);
	}
	
	public void processCloseWindow(C0DPacketCloseWindow packet) {
		super.processCloseWindow(packet);
	}
	
	public void processClickWindow(C0EPacketClickWindow packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		if(playerEntity.openContainer != null) {
			int slot = packet.func_149544_d();
			if(slot >= 0 && slot < playerEntity.inventory.getSizeInventory()) {
				Slot ps = playerEntity.openContainer.getSlot(slot);
				if(ps.getStack() != null) {
					for(int i = 0; i < 4; i ++) {
						ItemStack armor = playerEntity.getCurrentArmor(i);
						if(armor == ps.getStack() && FAttributes.curseOfBinding.isOn(armor)) {
							return;
						}
					}
				}
			}
		}
		super.processClickWindow(packet);
		FPlayer.updatePlayerCrafting(this.playerEntity);
		FAnvilRecipes.instance().updatePlayer(this.playerEntity);
		if(this.playerEntity.openContainer != null) {
			if(this.playerEntity.openContainer instanceof LOTRContainerAnvil) {
				LOTRContainerAnvil anvil = (LOTRContainerAnvil)this.playerEntity.openContainer;
				ItemStack out = anvil.getSlot(3).getStack();
				if(FRingForging.ringSmithHammer.equals(out)) {
					out.setStackDisplayName(Colors.color("&r" + FRingForging.ringSmithHammer.display + "&r"));
				}
			}
		}
		FPlayer.resendEntireInventory(this.playerEntity);
	}
	
	public void processConfirmTransaction(C0FPacketConfirmTransaction packet) {
		super.processConfirmTransaction(packet);
	}
	
	public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packet) {
		super.processCreativeInventoryAction(packet);
	}
	
	public void processEnchantItem(C11PacketEnchantItem packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		super.processEnchantItem(packet);
	}
	
	public void processUpdateSign(C12PacketUpdateSign packet) {
		if(FPalantir.is3PV(this.playerEntity)) return;
		FNetData data = this.data();
		{
			this.playerEntity.func_143004_u();
	        WorldServer worldserver = data.server.worldServerForDimension(this.playerEntity.dimension);
	        if (worldserver.blockExists(packet.func_149588_c(), packet.func_149586_d(), packet.func_149585_e())) {
	            TileEntity tileentity = worldserver.getTileEntity(packet.func_149588_c(), packet.func_149586_d(), packet.func_149585_e());
	            if (tileentity instanceof TileEntitySign) {
	                TileEntitySign tileentitysign = (TileEntitySign)tileentity;
	                if (!tileentitysign.func_145914_a() || tileentitysign.func_145911_b() != this.playerEntity) {
	                    data.server.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
	                    return;
	                }
	            }
	            int i;
	            int j;
	            for (j = 0; j < 4; ++j) {
	                boolean flag = true;
	                if (packet.func_149589_f()[j].length() > 15) {
	                    flag = false;
	                } else {
	                    for (i = 0; i < packet.func_149589_f()[j].length(); ++i) {
	                        if (!ChatAllowedCharacters.isAllowedCharacter(packet.func_149589_f()[j].charAt(i)))
	                        {
	                            flag = false;
	                        }
	                    }
	                }
	                if (!flag) {
	                    packet.func_149589_f()[j] = "!?";
	                }
	            }
	            if (tileentity instanceof TileEntitySign) {
	                j = packet.func_149588_c();
	                int k = packet.func_149586_d();
	                i = packet.func_149585_e();
	                TileEntitySign tileentitysign1 = (TileEntitySign)tileentity;
	                
	                String[] ret = packet.func_149589_f();
	        		if(FPermissions.instance().hasPermission(this.playerEntity, FPermissions.Others.perm_ColorSign)) {
		        		tileentitysign1.signText[0] = Colors.color(ret[0]);
		        		tileentitysign1.signText[1] = Colors.color(ret[1]);
		        		tileentitysign1.signText[2] = Colors.color(ret[2]);
		        		tileentitysign1.signText[3] = Colors.color(ret[3]);
	        		} else {
		        		tileentitysign1.signText[0] = ret[0];
		        		tileentitysign1.signText[1] = ret[1];
		        		tileentitysign1.signText[2] = ret[2];
		        		tileentitysign1.signText[3] = ret[3];
	        		}
	        		
	                //System.arraycopy(ret, 0, tileentitysign1.signText, 0, 4);
	                tileentitysign1.markDirty();
	                worldserver.markBlockForUpdate(j, k, i);
	            }
	        }
		}
//		super.processUpdateSign(packet);
	}
	
	public void processPlayerAbilities(C13PacketPlayerAbilities packet) {
		super.processPlayerAbilities(packet);
	}
	
	public void processTabComplete(C14PacketTabComplete packet) {
		super.processTabComplete(packet);
	}
	
	public void processClientSettings(C15PacketClientSettings packet) {
		super.processClientSettings(packet);
	}
	
	public void processClientStatus(C16PacketClientStatus packet) {
		super.processClientStatus(packet);
	}
	
	public void processVanilla250Packet(C17PacketCustomPayload packet) {
		FNetData data = this.data();
		try {
			String header = packet.func_149559_c();
			String contents = new String(packet.func_149558_e());
			if(header.startsWith("PHIC")) {
				FPHIExecutor.execute(this.playerEntity, contents);
				return;
			} else if(header.startsWith("PHIS")) {
				FPHIExecutor.script(this.playerEntity, contents);
				return;
			} else if(header.startsWith("PHIL")) {
				FPHIExecutor.commandlist(this.playerEntity, contents);
				return;
			} else if(header.startsWith("PHIG")) {
				FPHIExecutor.groovy(this.playerEntity, contents);
				return;
			} else if(header.equals("TÍRATHURIN")) {
				data.tirathurin = true;
			}
		} catch(Exception e) { }
		super.processVanilla250Packet(packet);
	}
	
	public void updateAttackTime() {
		super.updateAttackTime();
	}
	
	public MovingObjectPosition rayTrace(double d) {
		Vec3 vec3 = this.getPosition();
		Vec3 vec31 = this.playerEntity.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d, vec31.yCoord * d, vec31.zCoord * d);
        return this.playerEntity.worldObj.func_147447_a(vec3, vec32, false, false, true);
    }
	
	private Vec3 getPosition() {
		return Vec3.createVectorHelper(this.playerEntity.posX, this.playerEntity.boundingBox.minY + this.playerEntity.getEyeHeight(), this.playerEntity.posZ);
	}
	
    public void getMouseOver() {
		FNetData data = this.data();
        data.pointed = null;
        double d0 = (double)this.playerEntity.theItemInWorldManager.getBlockReachDistance();
        data.objectMouseOver = this.rayTrace(d0);
        double d1 = d0;
        Vec3 vec3 = this.getPosition();
//        if (this.playerEntity.capabilities.isCreativeMode)  {
//            d0 = 6.0D;
//            d1 = 6.0D;
//        } else {
//            if (d0 > 3.0D) {
//                d1 = 3.0D;
//            }
//            d0 = d1;
//        }
        if (data.objectMouseOver != null) {
            d1 = data.objectMouseOver.hitVec.distanceTo(vec3);
        }
        Vec3 vec31 = this.playerEntity.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
        data.lookingTarget = vec32;
        data.pointed = null;
        Vec3 vec33 = null;
        float f1 = 1.0F;
        List list = this.playerEntity.worldObj.getEntitiesWithinAABBExcludingEntity(this.playerEntity, this.playerEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
        double d2 = d1;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (entity.canBeCollidedWith()) {
                float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (0.0D < d2 || d2 == 0.0D) {
                        data.pointed = entity;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity == this.playerEntity.ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                data.pointed = entity;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            data.pointed = entity;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        if (data.pointed != null && (d2 < d1 || data.objectMouseOver == null)) {
            data.objectMouseOver = new MovingObjectPosition(data.pointed, vec33);
        }
    }
	
}
