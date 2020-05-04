package vinyarion.fukkit.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRConfig;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRNetHandlerPlayServer;
import lotr.common.LOTRPlayerData;
import lotr.common.inventory.LOTRContainerAnvil;
import lotr.common.item.LOTRItemBow;
import lotr.common.item.LOTRItemCrossbow;
import lotr.common.world.map.LOTRCustomWaypoint;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import vinyarion.fukkit.main.FNetHandlerPlayServer.FNetData;
import vinyarion.fukkit.main.attrib.FAttribHealthBoost;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.attrib.FRingAttribBase;
import vinyarion.fukkit.main.attrib.FRingAttribEffect;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.InventoryChestHijacked;
import vinyarion.fukkit.main.chestguis.InventoryPouchHijacked;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.data.FAnvilRecipes;
import vinyarion.fukkit.main.data.FArmorSetEffects;
import vinyarion.fukkit.main.data.FBannedItems;
import vinyarion.fukkit.main.data.FBlockLogger;
import vinyarion.fukkit.main.data.FChestGuis;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.data.FLootTables;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FPlayerDrops;
import vinyarion.fukkit.main.data.FRegions;
import vinyarion.fukkit.main.data.FSeenData;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.game.FAesthetics;
import vinyarion.fukkit.main.game.FChatRP;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.game.FRegion;
import vinyarion.fukkit.main.game.FWands;
import vinyarion.fukkit.main.handlers.Invisibility;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.script.FStaticScripts;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Effects;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.Updates;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.forging.rings.FRingForging;
import vinyarion.fukkit.rpg.forging.rings.RingPotion;
import vinyarion.fukkit.rpg.palantir.FPalantir;
import vinyarion.fukkit.rpg.skill.FSkills;
import vinyarion.fukkit.rpg.skill.FSkillsConstants;
import vinyarion.fukkit.rpg.skill.FSkillsHandler;
import vinyarion.fukkit.sm.cmd.FCommandDiscordBot;
import vinyarion.fukkit.sm.cmd.FCommandTPA;
import vinyarion.fukkit.sm.cmd.FCommandWarzone;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FEvents {
	
	private int i = 0;
	private int j = 0;
	private final int max = 2048;
	private final int maxO = 4;
	
	@EventHandler @SubscribeEvent
	public void doTick(ServerTickEvent chat) { try {
		FCommandTPA.tick();
		if(i++ % 2000 == 0) {
			FData.changed();
			FData.instance().save();
			for(String name : MinecraftServer.getServer().getAllUsernames()) {
				FPlayerData.forPlayer(name).save();
			}
		}
//		if(Misc.every(20, "tick", "server")) {
//			for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
//				EntityPlayerMP player = (EntityPlayerMP)o;
//				MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S38PacketPlayerListItem(player.getCommandSenderName(), true, player.ping));
//				if(player.dimension == 100) {
//					LOTRLevelData.sendPlayerLocationsToPlayer(player, player.worldObj);
//				}
//			}
//		}
		FMod.later.resolve();
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
			FDiscordBot.getInstance().tickDispatcher();
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void doCommandEvent(CommandEvent cmd) { try {
		if(cmd.sender instanceof EntityPlayerMP && cmd.command == MinecraftServer.getServer().getCommandManager().getCommands().get("me")) {
			cmd.setCanceled(true);
			if(FChatRP.muted.contains(cmd.sender.getCommandSenderName())) return;
			EntityPlayerMP player = (EntityPlayerMP)cmd.sender;
			for(String p : cmd.parameters) {
				System.out.println(p);
			}
			IChatComponent c = FCommandTitles.getClickableFormattedTitle(player);
			String rawmsg = FCommand.rest(cmd.parameters, 0);
			IChatComponent msg = new ChatComponentText(FCommandTitles.colorIfPerm(player, rawmsg));
//            IChatComponent ichatcomponent = func_147176_a(p_71515_1_, p_71515_2_, 0, p_71515_1_.canCommandSenderUseCommand(1, "me"));
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.emote", new Object[] {c, msg}));
		}
		if(cmd.sender instanceof EntityPlayerMP && cmd.command == MinecraftServer.getServer().getCommandManager().getCommands().get("tell") && cmd.parameters.length >= 2) {
			FPlayerData rec = FPlayerData.forPlayer(cmd.parameters[0]);
			if(rec != null && cmd.parameters.length > 0) {
				rec.tagVolatile().setString("lastmsg", cmd.sender.getCommandSenderName());
			}
			rec = FPlayerData.forPlayer(cmd.sender.getCommandSenderName());
			if(rec != null && cmd.parameters.length > 0) {
				rec.tagVolatile().setString("lastmsg", cmd.parameters[0]);
			}
			/* Replace /tell behavior */ {
				if(cmd.parameters.length < 2) {
					return;
//		            throw new WrongUsageException("commands.message.usage", new Object[0]);
		        } else {
		            try {
		            	EntityPlayerMP target = FCommand.getPlayer(cmd.sender, cmd.parameters[0]);
			            if(target == null) {
			            	return;
	//		                throw new PlayerNotFoundException();
			            } else if(target == cmd.sender) {
			            	return;
	//		                throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
			            } else {
			            	IChatComponent msg = FCommand.func_147176_a(cmd.sender, cmd.parameters, 1, !(cmd.sender instanceof EntityPlayer));
			            	msg.getChatStyle().setColor(EnumChatFormatting.GRAY);
			        		IChatComponent pns = FPlayer.PNS((EntityPlayerMP)cmd.sender);
			        		IChatComponent pns2 = FPlayer.PNS(target);
			                target.addChatMessage(pns.appendSibling(Colors.MAKE(" &6to you&f" + Colors.TALK + "&7")).appendSibling(msg));
			                cmd.sender.addChatMessage(Colors.MAKE("&6You to &f").appendSibling(pns2.createCopy().appendSibling(Colors.MAKE(Colors.TALK + "&7"))).appendSibling(msg));
							cmd.setCanceled(true);
			            }
		            } catch(Exception e) {
		            	return;
		            }
		        }
			}
		}
		if(cmd.command == MinecraftServer.getServer().getCommandManager().getCommands().get("list")) {
			cmd.setCanceled(true);
			List<IChatComponent> text = new ArrayList<IChatComponent>();
			text.add(Colors.CHAT("There are " + MinecraftServer.getServer().getCurrentPlayerCount() + " players online:"));
			for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				EntityPlayerMP player = (EntityPlayerMP)o;
				IChatComponent pns = FPlayer.PNS(player);
				String group = FPlayerData.forPlayer(player.getCommandSenderName()).tag().getString(FPlayerData.GROUPS_TAG);
				pns = Colors.MAKE("  &r" + group + "&r: ").appendSibling(pns);
				if(player.capabilities.isCreativeMode) {
					pns.appendSibling(Colors.MAKE("&r, [&6Creative&r]"));
				}
				if(player.capabilities.allowFlying) {
					pns.appendSibling(Colors.MAKE("&r, [&6Fly&r]"));
				}
				if(FChatRP.rp.contains(player.getCommandSenderName())) {
					pns.appendSibling(Colors.MAKE("&r, [&6RP&r]"));
				}
				if(FChatRP.muted.contains(player.getCommandSenderName())) {
					pns.appendSibling(Colors.MAKE("&r, [&6Muted&r]"));
				}
				text.add(pns);
			}
			text = text.stream().sorted((c1, c2) -> c1.getUnformattedText().compareTo(c2.getUnformattedText())).collect(Collectors.toList());
			for(IChatComponent t : text) {
				cmd.sender.addChatMessage(t);
			}
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void doChatEvent(ServerChatEvent chat) { try {
		IChatComponent pns = FPlayer.PNS(chat.player);
		String key = chat.component.getKey();
		if(key.equals("chat.type.text")) {
			String msg = FCommandTitles.colorIfPerm(chat.player, chat.message);
			if(FHooks.isfmsg()) {
				chat.component = new ChatComponentTranslation("disconnect.genericReason", pns.appendText(Colors.TALK));
			} else {
				chat.component = new ChatComponentTranslation("disconnect.genericReason", pns.appendText(Colors.TALK).appendSibling(ForgeHooks.newChatWithLinks(msg)));
			}
		}
		if(FChatRP.cancel(chat)) {
			chat.setCanceled(true);
			return;
		}
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel() && key.equals("chat.type.text")) {
			FDiscordBot.getInstance().mc2discord(chat.player, Colors.clean(chat.message));
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerLogOn(PlayerLoggedInEvent event) { try {
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		FPlayerData data = FPlayerData.load(player);
		if(FMod.config.doFirstWelcomeMessages) if(!data.tag().hasKey("login")) {
			data.doNewPlayer(player);
			player.addChatMessage(Colors.CHAT("Welcome, " + player.getCommandSenderName() + " to the server! Enjoy your stay!"));
			IChatComponent djm = Colors.CHAT("Click here to join out Discord server!");
			String invite = FServer.instance().tag.getString(FCommandDiscordBot.DISCORDINVITE);
			if(invite.length() > 0) {
				djm.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Colors.MAKE("&lhttps://discord.gg/" + invite)));
				djm.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/" + invite));
			}
			player.addChatMessage(djm);
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Colors.MAKE( "&f[&a+&f] &a" + player.getCommandSenderName() + "&r has joined for the first time!"));
			FStaticScripts.playerJoinFirst().command(player);
		} else if(FMod.config.doWelcomeMessages) {
			player.addChatMessage(Colors.CHAT("Welcome back, " + player.getCommandSenderName()));
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Colors.MAKE( "&f[&a+&f] &a" + player.getCommandSenderName() + "&r has joined!"));
		}
		data.tag().setInteger("loginCount", data.tag().getInteger("loginCount")+1);
		data.tag().setLong("login", System.currentTimeMillis());
		FStaticScripts.playerJoin().command(player);
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
			FDiscordBot.getInstance().queueChannel(":heavy_plus_sign: **" + player.getCommandSenderName() + "** logged **on**. *" + MinecraftServer.getServer().getCurrentPlayerCount() + " online*.");
		}
		if(Updates.result != null) {
//			player.addChatMessage(Colors.MAKE(Updates.result)); // TODO : mebby, disable for now
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerTick(PlayerTickEvent event) { try {
		if(event.phase == TickEvent.Phase.END) return;
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		FPlayerData data = FPlayerData.forPlayer(player);
		{
			NetHandlerPlayServer old = player.playerNetServerHandler;
			if(!(old instanceof FNetHandlerPlayServer) && old instanceof LOTRNetHandlerPlayServer) {
				LOTRNetHandlerPlayServer lotrold = (LOTRNetHandlerPlayServer)player.playerNetServerHandler;
				FNetHandlerPlayServer replacement = FNetHandlerPlayServer.replace(lotrold);
				try {
					ReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, replacement, "playerNetServerHandler", "field_71135_a", "a");
					FMod.log(Level.INFO, "Set net handler for " + player.getCommandSenderName() + "!");
				} catch(Exception e) {
					e.printStackTrace();
					FMod.log(Level.INFO, "Failed to set net handler for " + player.getCommandSenderName() + "!");
				}
			}
			if(data == null) {
				data = FPlayerData.load(player);
				if(data == null) {
					player.playerNetServerHandler.kickPlayerFromServer("Internal server error\nFor some reason, your player data was loaded improperly,\nbut is not corrupt. Please log back on. If the issue persists, contact the server staff or developer.");
					return;
				}
			}
		}
		data.tick();
		
		if(player.openContainer != null && player.openContainer instanceof LOTRContainerAnvil) {
			LOTRContainerAnvil anvil = (LOTRContainerAnvil)player.openContainer;
			ItemStack out = anvil.getSlot(3).getStack();
			player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, 3, out));
		}
		
		if(data.tagVolatile().hasKey(InventoryChestHijacked.NextFukkitGui)) {
			String nfg = data.tagVolatile().getString(InventoryChestHijacked.NextFukkitGui);
			data.tagVolatile().removeTag(InventoryChestHijacked.NextFukkitGui);
			ChestGui cg =  FChestGuis.instance().get(nfg);
			if(cg != null) {
				if(cg.isPouch()) {
					InventoryPouchHijacked.displayTo(player, cg);
				} else {
					InventoryChestHijacked.displayTo(player, cg);
				}
			}
		}
		
		if(data.every(20)) {
//            player.playerNetServerHandler.sendPacket(new S21PacketChunkData(player.getServerForPlayer().getChunkFromBlockCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ)), false, 0));
//			FRecipes.instance().updatePlayer(player);
			LOTRPlayerData lp = LOTRLevelData.getData(player);
			if(LOTRConfig.enableFastTravel && FMod.config.onlyCustomWaypoints) {
				if(lp.getTargetFTWaypoint() != null && !(lp.getTargetFTWaypoint() instanceof LOTRCustomWaypoint)
//TODO						&& !FPermissions.instance().hasPermission(player, FPermissions.Others.perm_UseMapFT)
						) {
					lp.setTargetFTWaypoint(null);
					player.addChatMessage(Colors.make(Colors.RED + "You can't use non-custom waypoints!"));
				}
			}
			FAnvilRecipes.instance().updatePlayer(player);
			if(player.posY < -128.0D && FMod.config.tpVoidPeople) {
				FWarps.instance().warp(player, "spawn");
				FCommand.Info(player, "You were in the void, so you were teleported to spawn!");
			}
			boolean had = player.isPotionActive(Potion.invisibility);
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(FAttributes.regionBound.isOn(stack)) {
					String region = FAttributes.regionBound.update(player, stack);
					if(!FPermissions.instance().hasPermission(player, "region." +  region)) {
						if(!FRegions.instance().isWithin(player, region)) {
							player.inventory.setInventorySlotContents(i, null);
						}
					}
				}
			}
			for(FRingAttribEffect rae : FAttributes.ringPotions) {
				ItemStack stack;
				if((stack = FRingAttribBase.getRingAttributeStack(player, rae)) != null) {
					RingPotion rp = rae.update(player, stack);
					PotionEffect effect = rp.tick();
					effect.getCurativeItems().clear();
					player.addPotionEffect(effect);
				}
			}
			String armorSet = null;
			int worn = 0;
			for(int i = 0; i < 4; i++) {
				ItemStack stack = player.inventory.armorItemInSlot(i);
				if(FAttributes.armorSet.isOn(stack)) {
					String itemSet = FAttributes.armorSet.update(player, stack);
					if(armorSet == null) {
						armorSet = itemSet;
						worn++;
					} else if(itemSet.equals(armorSet)) {
						worn++;
					}
				}
				if(FAttributes.genericEffect.isOn(stack)) {
					for(String es : FAttributes.genericEffect.update(player, stack)) {
						int mod = 0;
						if(es.contains(":")) {
							String[] esa = es.split(":");
							es = esa[0];
							mod = Misc.isInt(esa[1]) ? Integer.parseInt(esa[1]) : 0;
						}
						PotionEffect effect = new PotionEffect(Effects.of(es), 21, mod, false);
						effect.getCurativeItems().clear();
						player.addPotionEffect(effect);
					}
				}
			}
			ItemStack held = player.getHeldItem();
			if(FAttributes.genericEffect.isOn(held)) {
				for(String es : FAttributes.genericEffect.update(player, held)) {
					PotionEffect effect = new PotionEffect(Effects.of(es), 21, 0, false);
					effect.getCurativeItems().clear();
					player.addPotionEffect(effect);
				}
			}
			String[] exh = null;
			if(armorSet != null && worn == 4) {
				String[] effects = FArmorSetEffects.instance().get(armorSet).split(";");
				for(String es : effects) {
					if(es.length() == 0) {
						continue;
					}
					int mod = 0;
					String[] esa = null;
					if(es.contains("@")) {
						esa = es.split("@");
						es = esa[0];
						mod = Misc.isInt(esa[1]) ? Integer.parseInt(esa[1]) : 0;
					}
					if(es.equals("healthBoost")) {
						exh = esa;
						continue;
					}
					PotionEffect effect = new PotionEffect(Effects.of(es), 21, mod, false);
					effect.getCurativeItems().clear();
					player.addPotionEffect(effect);
				}
			}
			boolean war = false;
			for(FRegion region : FRegions.instance().get()) {
				if(region.applies(player, (int)player.posX, (int)player.posY, (int)player.posZ)) {
					if(!region.bit(FRegion.BIT_WARZONE)) {
						war = !player.capabilities.isCreativeMode;
						break;
					}
				}
			}
			FCommandWarzone.warzone(player, war);
			IAttributeInstance iai = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			if(exh != null) {
				String es = exh[0];
				int level = Misc.isInt(exh[1]) ? Integer.parseInt(exh[1]) : 0;
				AttributeModifier mod = new AttributeModifier(FAttribHealthBoost.uuid_armorset, "fmod_set", level, 0);
				try {
					iai.applyModifier(mod.setSaved(false));
				} catch(Exception e) {
					
				}
			} else {
				try {
					iai.removeModifier(iai.getModifier(FAttribHealthBoost.uuid_armorset));
				} catch(Exception e) {
					
				}
			}
			for(int i = 0; i < 4; i++) {
				ItemStack stack = player.getCurrentArmor(i);
				UUID uuid = FAttribHealthBoost.armors[i];
				if(FAttributes.healthBoost.isOn(stack)) {
					int level = FAttributes.healthBoost.update(player, stack);
					AttributeModifier mod = new AttributeModifier(uuid, FAttribHealthBoost.names[i], level, 0);
					try {
						iai.applyModifier(mod.setSaved(false));
					} catch(Exception e) { }
				} else {
					try {
						iai.removeModifier(iai.getModifier(uuid));
					} catch(Exception e) { }
				}
			}
			if(player.getHealth() > player.getMaxHealth()) {
				player.setHealth(player.getMaxHealth());
			}
			Invisibility.INSTANCE.every20ticks(player);
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerLogOff(PlayerLoggedOutEvent event) { try {
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		if(FPalantir.is3PV(player)) {
			FPalantir.exit3PV(player);
		}
		FPlayerData data = FPlayerData.forPlayer(player.getCommandSenderName());
		if(data != null) {
			data.destroy();
			if(FMod.config.doWelcomeMessages) {
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Colors.MAKE("&f[&c-&f] &c" + player.getCommandSenderName() + "&r left!"));
			}
		} else if(FMod.config.doWelcomeMessages) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(Colors.MAKE("&f[&6?&f] &6" + player.getCommandSenderName() + "&r failed to join!"));
		}
		FStaticScripts.playerLeave().command(player);
		FSeenData.instance().lastVisit(player);
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
			FDiscordBot.getInstance().queueChannel(":heavy_minus_sign: **" + player.getCommandSenderName() + "** logged **off**. *" + (MinecraftServer.getServer().getCurrentPlayerCount() - 1) + " online*.");
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerDie(LivingDeathEvent event) { try {
		if(event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
			IChatComponent dmsg = player.func_110142_aN().func_151521_b();
			String unf = dmsg.getUnformattedText();
			unf = Colors.getTextWithoutFormattingCodes(unf);
			if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
				FDiscordBot.getInstance().queueChannel(":recycle: " + unf);
			}
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerDrops(PlayerDropsEvent event) { try {
		for(EntityItem item : event.drops.toArray(new EntityItem[event.drops.size()])) {
			ItemStack stack = item.getEntityItem();
			if(!FPlayerDrops.instance().shouldDrop(event.entityPlayer, stack)) {
				event.drops.remove(item);
				event.entityPlayer.inventory.addItemStackToInventory(stack);
			} else if(FAttributes.curseOfVanishing.isOn(stack)) {
				event.drops.remove(item);
			}
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerNeedRespawns(PlayerEvent.Clone event) { try {
		EntityPlayerMP from = (EntityPlayerMP)event.original;
		EntityPlayerMP to = (EntityPlayerMP)event.entityPlayer;
		if(event.wasDeath) {
			if(!to.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
				List<ItemStack> overwrites = new ArrayList<ItemStack>();
				for(int i = 0; i < 36; i++) {
					ItemStack stack = from.inventory.getStackInSlot(i);
					if(FAttributes.soulbound.isOn(stack)) {
						if(to.inventory.getStackInSlot(i) == null) {
							to.inventory.setInventorySlotContents(i, stack);
							to.addChatMessage(Colors.CHAT("Your " + stack.getDisplayName() + "&r stays with you."));
						} else {
							overwrites.add(stack);
						}
					}
				}
				for(int i = 0; i < 4; i++) {
					ItemStack stack = from.inventory.armorItemInSlot(i);
					if(FAttributes.soulbound.isOn(stack)) {
						if(to.inventory.armorInventory[i] == null) {
							to.inventory.armorInventory[i] = stack;
							to.addChatMessage(Colors.CHAT("Your " + stack.getDisplayName() + "&r stays with you."));
						} else {
							overwrites.add(stack);
						}
					}
				}
				for(ItemStack stack : overwrites) {
					if(!to.inventory.addItemStackToInventory(stack)) {
						FMod.log(Level.ERROR, "An item of " + to.getCommandSenderName() + " could not be refunded. Stack was: " + stack.writeToNBT(new NBTTagCompound()).toString());
						to.addChatMessage(Colors.CHAT("Sorry! For some reason, your Soulbound " + stack.getDisplayName() + "&r&a could not be given back. Please talk to staff and get a new one."));
					}
				}
			}
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerPlaceBlockMulti(MultiPlaceEvent event) { try {
		if(FRegions.instance().denyBlockPlace((EntityPlayerMP)event.player, event.x, event.y, event.z)) {
			event.setCanceled(true);
		}
		if(event.isCanceled()) {
			event.player.addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		} else {
			FBlockLogger.log(event.player.getCommandSenderName() + ":place.multi:" + String.valueOf(event.itemInHand) + "," + event.block.getUnlocalizedName() + "@" + event.x + "," + event.y + "," + event.z);
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerPlaceBlock(PlaceEvent event) { try {
		if(FRegions.instance().denyBlockPlace((EntityPlayerMP)event.player, event.x, event.y, event.z)) {
			event.setCanceled(true);
		}
		boolean canceled = event.isCanceled();
		if(canceled) {
			event.player.addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		} else {
			FBlockLogger.log(event.player.getCommandSenderName() + ":place:" + String.valueOf(event.itemInHand) + "," + event.block.getUnlocalizedName() + "@" + event.x + "," + event.y + "," + event.z);
		}
		if(!canceled && event.placedBlock == Blocks.sponge) {
			int x = event.x;
			int y = event.y;
			int z = event.z;
			for(int[] d : new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}}) {
				Block here = event.world.getBlock(x + d[0], y + d[1], z + d[2]);
				if(here == Blocks.water || here == Blocks.flowing_water) {
					for(int dx = -3; dx <= event.x + 3; dx++) {
						for(int dy = -3; dy <= event.y + 3; dy++) {
							for(int dz = -3; dz <= event.z + 3; dz++) {
								here = event.world.getBlock(dx, dy, dz);
								if((here == Blocks.water || here == Blocks.flowing_water) && !(x == dx && y == dy && z == dz)) {
									event.world.setBlockToAir(dx, dy, dz);
								}
							}
						}
					}
					event.world.setBlockToAir(x, y, z);
					break;
				}
			}
		}
		if(!canceled) {
			
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerBreakBlock(BreakEvent event) { try {
		if(FRegions.instance().denyBlockBreak((EntityPlayerMP)event.getPlayer(), event.x, event.y, event.z)) {
			event.setCanceled(true);
		}
		{
			TileEntity te = event.getPlayer().worldObj.getTileEntity(event.x, event.y, event.z);
			if(te != null && te instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest)te;
				if(FLootTables.instance().isLootChest(chest)) {
					event.setCanceled(true);
				}
			}
		}
		if(event.isCanceled()) {
			event.getPlayer().addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		} else {
			FBlockLogger.log(event.getPlayer().getCommandSenderName() + ":break:" + event.block.getUnlocalizedName() + "@" + event.x + "," + event.y + "," + event.z + "_with_" + String.valueOf(event.getPlayer().getHeldItem()));
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerBucket(FillBucketEvent event) { try {
		if(FRegions.instance().denyBucketFill((EntityPlayerMP)event.entityPlayer, event.target.blockX, event.target.blockY, event.target.blockZ)) {
			event.setCanceled(true);
		}
		if(event.isCanceled()) {
			event.entityPlayer.addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		} else {
			FBlockLogger.log(event.entityPlayer.getCommandSenderName() + ":bucket:" + String.valueOf(event.current) + "@" + String.valueOf(event.target));
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerItemPickup(EntityItemPickupEvent event) { try {
		boolean canceled = event.isCanceled();
		if(!canceled && FBannedItems.instance().bannedItems.contains(event.item.getEntityItem().getItem())
//				&& !FPermissions.instance().hasPermission(event.entityPlayer, "pickup." + event.item.getEntityItem().getItem().getUnlocalizedName())
				) {
			event.setCanceled(true);
		}
		if(!canceled) {
			if(FRingForging.helpBook.equals(event.item.getEntityItem()) && !FPermissions.instance().hasPermission(event.entityPlayer, "recipe.RingForgingRecipes_randomness")) {
				FPermissions.instance().addPermission("recipe.RingForgingRecipes_randomness", event.entityPlayer);
			}
		}
		if(canceled) {
			event.entityPlayer.addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		}
		event.setCanceled(canceled);
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerAttack(AttackEntityEvent event) { try {
		boolean canceled = event.isCanceled();
		EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
		Entity target = event.target;
		if(target instanceof EntityItemFrame) {
			EntityItemFrame frame = (EntityItemFrame)target;
			if(!canceled && FRegions.instance().denyItemFrame(player, frame, frame.chunkCoordX, frame.chunkCoordY, frame.chunkCoordZ)) {
				canceled = true;
			}
		}
		event.setCanceled(canceled);
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerInteractEntity(EntityInteractEvent event) { try {
		boolean canceled = event.isCanceled();
		EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
		Entity target = event.target;
		if(target instanceof EntityItemFrame) {
			EntityItemFrame frame = (EntityItemFrame)target;
			if(!canceled && FRegions.instance().denyItemFrame(player, frame, frame.chunkCoordX, frame.chunkCoordY, frame.chunkCoordZ)) {
				canceled = true;
			}
		}
		event.setCanceled(canceled);
	} catch(Exception exception) { Misc.report(exception); }}

	@EventHandler @SubscribeEvent
	public void playerInteractContainer(PlayerInteractEvent event) { try {
		boolean canceled = event.isCanceled();
		EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
		if(!canceled && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && 
				event.world.getBlock(event.x, event.y, event.z) instanceof BlockContainer && FRegions.instance().denyContainer(player, event.x, event.y, event.z)) {
			canceled = true;
		}
		event.setCanceled(canceled);
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void attack(LivingAttackEvent event) { try {
		boolean canceled = event.isCanceled();
		EntityLivingBase elb = event.entityLiving;
		if(event.source.getDamageType().equals("whip")) {
			canceled = true;
		}
		if(event.source instanceof EntityDamageSource) {
			EntityDamageSource eds = (EntityDamageSource)event.source;
			if(eds.getEntity() instanceof EntityLivingBase) {
				EntityLivingBase elba = (EntityLivingBase)eds.getEntity();
				if(eds.getEntity() instanceof EntityPlayerMP) {
					EntityPlayerMP attacker = (EntityPlayerMP)eds.getEntity();
					if(!canceled && FRegions.instance().denyAttack(attacker, (int)attacker.posX, (int)attacker.posY, (int)attacker.posZ)) {
						canceled = true;
					}
					if(!canceled && FCommandWarzone.isSafe(attacker)) {
						canceled = true;
					}
				}
				if(elb instanceof EntityPlayerMP) {
					EntityPlayerMP target = (EntityPlayerMP)event.entityLiving;
					if(!canceled && FRegions.instance().denyDamage(target, (int)target.posX, (int)target.posY, (int)target.posZ)) {
						canceled = true;
					}
				}
			}
		}
		if(event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP target = (EntityPlayerMP)event.entityLiving;
			if(!canceled && FRegions.instance().denyAttack((EntityPlayerMP)target, (int)target.posX, (int)target.posY, (int)target.posZ)) {
				canceled = true;
			}
			if(!canceled && FCommandWarzone.isSafe(target)) {
				canceled = true;
			}
		}
		if(event.source instanceof EntityDamageSourceIndirect) {
			EntityDamageSourceIndirect ids = (EntityDamageSourceIndirect)event.source;
			if(!canceled && ids.getDamageType().equals("thrown") && ids.isProjectile()) {
				event.entityLiving.attackEntityFrom(new EntityDamageSource("whip", ids.getEntity()), event.ammount);
			}
		}
		if(canceled) {
			event.setCanceled(true);
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void hurt(LivingHurtEvent event) { try {
		boolean canceled = event.isCanceled();
		EntityLivingBase elb = event.entityLiving;
		if(event.source instanceof EntityDamageSource) {
			EntityDamageSource eds = (EntityDamageSource)event.source;
			if(eds.getEntity() instanceof EntityLivingBase) {
				EntityLivingBase elba = (EntityLivingBase)eds.getEntity();
				if(eds.getEntity() instanceof EntityPlayerMP) {
					EntityPlayerMP attacker = (EntityPlayerMP)eds.getEntity();
					ItemStack held = attacker.getHeldItem();
					if(!canceled && FAttributes.mace.isOn(held)) {
						if(eds.getDamageType().endsWith(".mace")) {
							canceled = true;
							eds.damageType = eds.getDamageType().substring(0, eds.getDamageType().length() - ".mace".length());
						}
					}
					if(!canceled && FAttributes.durability.isOn(held)) {
						canceled = FAttributes.durability.update(attacker, held);
					}
					if(!canceled && FAttributes.extraDamage.isOn(held)) {
						event.ammount += 0.33F * (float)Math.max(1, FAttributes.extraDamage.update(attacker, held));
					}
					ItemStack strstack = FRingAttribBase.getRingAttributeStack(attacker, FAttributes.ringStrength);
					if(!canceled && strstack != null) {
						event.ammount += 0.33F * (float)Math.max(1, FAttributes.ringStrength.update(attacker, strstack).amplifier() + 1);
					}
					if(!canceled && FAttributes.effectAttack.isOn(held)) {
						String[] effects = FAttributes.effectAttack.update(attacker, held);
						for(String es : effects) {
							int seconds = 0;
							int mod = 0;
							String[] esa = null;
							if(es.contains("@")) {
								esa = es.split("@");
								es = esa[0];
								mod = Misc.isInt(esa[1]) ? Integer.parseInt(esa[1]) : 0;
							}
							if(es.contains("#")) {
								esa = es.split("#");
								es = esa[0];
								seconds = Misc.isInt(esa[1]) ? Integer.parseInt(esa[1]) : 0;
							}
							PotionEffect effect = new PotionEffect(Effects.of(es), seconds * 20 + 1, mod, false);
							effect.getCurativeItems().clear();
							elb.addPotionEffect(effect);
						}
					}
					if(!canceled && eds.getDamageType().equals("whip") && FAttributes.whip.isOn(held)) {
						event.ammount += 1.0F * (float)Math.max(1, FAttributes.whip.update(attacker, held));
						eds.damageType = "thrown";
					}
					if(!canceled && FAttributes.mace.isOn(held)) {
						double width = FAttributes.mace.update(attacker, held);
						for(Object o : elb.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, 
								AxisAlignedBB.getBoundingBox(
								elb.posX - width, elb.posY - 0.5D, elb.posZ - width, 
								elb.posX + width, elb.posY + 0.5D, elb.posZ + width))) {
							EntityLivingBase hit = (EntityLivingBase)o;
							if(hit.getEntityId() == elb.getEntityId() || hit.getEntityId() == attacker.getEntityId()) continue;
							float dist = elb.getDistanceToEntity(hit);
							float max = 1.41421356F * (float)width + 1.41421356F;
							hit.attackEntityFrom(new EntityDamageSource(eds.getDamageType() + ".mace", attacker), (max - dist) * event.ammount);
						}
					}
					if(elb instanceof EntityPlayerMP) {
						EntityPlayerMP target = (EntityPlayerMP)elb;
						if(!canceled && FAttributes.armorBreak.isOn(held)) {
							if(Misc.percent(15.0D)) {
								target.inventory.damageArmor(event.ammount * FAttributes.armorBreak.update(attacker, held));
								attacker.addChatMessage(new ChatComponentText("Damaged " + target.getCommandSenderName() + "'s armor!"));
							}
						}
					}
//					attacker.addChatMessage(Colors.make(""+event.ammount));
					FSkillsHandler.handleLivingHurt(attacker, elb, held, event);
//					attacker.addChatMessage(Colors.make(""+event.ammount));
				}
				if(elb instanceof EntityPlayerMP) {
					EntityPlayerMP target = (EntityPlayerMP)elb;
					if(!canceled) {
						if(!eds.isUnblockable() && target.isBlocking() && event.ammount > 0.0F) {
							float dev = Misc.deviationInLooking(target, elba);
							float red = event.ammount * (90.0F - dev) / 180.0F;
							if(dev < 90.0F) {
								event.ammount = event.ammount - red;
							}
							event.ammount = event.ammount * 2.0F - 1.0F;
							FNetData netdata = FNetHandlerPlayServer.of(target).data();
							netdata.swingsSinceLastBlock = 0;
							netdata.lastAttacker = elba.getEntityId();
						}
					}
				}
				double s = ((double)event.ammount / 4.0D + 1.0D);
				FAesthetics.particle(
					elb.worldObj, 
					FAesthetics.Particle.blockdust(Blocks.redstone_block, 0xFFFFFFFF), 
					elb.posX, 
					elb.posY + 1.0D, 
					elb.posZ, 
					0.2D, 
					0.3D, 
					0.2D, 
					0.04D * s, 
					Misc.rand.nextInt(32) + 32
				);
			}
		}
		if(elb instanceof EntityPlayerMP) {
			EntityPlayerMP target = (EntityPlayerMP)event.entityLiving;
			if(!canceled) {
				if(event.source.getDamageType().equals("fall")) {
					ItemStack boot = target.getCurrentArmor(0);
					if(FAttributes.spikedBoots.isOn(boot)) {
						int max = FAttributes.spikedBoots.update(target, boot);
						float dmg = Math.min(event.ammount, (float)max);
						if(dmg > 0.0F) {
							for(Object o : target.worldObj.getEntitiesWithinAABBExcludingEntity(target, target.boundingBox.expand(0.3, -0.3, 0.3).offset(0, -1, 0))) {
								if(o instanceof EntityLivingBase) {
									EntityLivingBase hit = (EntityLivingBase)o;
									hit.attackEntityFrom(new EntityDamageSource("spikedboots", target), dmg);
								}
							}
						}
					}
				}
			}
			FSkillsHandler.handleLivingDamage(elb, target, elb.getHeldItem(), event);
		}
		if(canceled) {
			event.ammount = -1.0F;
			event.setCanceled(true);
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerUseItem(PlayerInteractEvent event) { try {
		boolean canceled = event.isCanceled();
		if(FRegions.instance().denyInteraction((EntityPlayerMP)event.entityPlayer, event.x, event.y, event.z, event.action)) {
			canceled = !(event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() instanceof ItemBow);
		}
		if(canceled) {
			event.entityPlayer.addChatMessage(Colors.CHAT("Sorry, but you can't do this."));
		}
		if(event.action == Action.RIGHT_CLICK_AIR && event.face == -1 && event.x == 0 && event.y == 0 && event.z == 0) {
			ItemStack held = event.entityPlayer.getHeldItem();
			if(!canceled && FAttributes.cooldown.isOn(held)) {
				canceled = FAttributes.cooldown.update(event.entityPlayer, held);
			}
			if(!canceled && FAttributes.durability.isOn(held)) {
				canceled = FAttributes.durability.update(event.entityPlayer, held);
			}
			if(!canceled && FAttributes.ringWorn.isOn(held)) {
				FAttributes.ringWorn.update(event.entityPlayer, held);
			}
			if(!canceled && FAttributes.gainRecipe.isOn(held)) {
				String recipe = FAttributes.gainRecipe.update(event.entityPlayer, held);
				if(recipe.length() > 0 && !recipe.equals("null")) {
					FPermissions.instance().addPermission("recipe." + recipe, event.entityPlayer);
					FPlayer.decrementHeld(event.entityPlayer, 1);
					event.entityPlayer.addChatMessage(Colors.CHAT("Learned how to craft " + recipe + "!"));
				}
			}
			if(!canceled && FAttributes.executeCommand.isOn(held)) {
				String[] cmds = FAttributes.executeCommand.update(event.entityPlayer, held);
				if(cmds.length > 0) {
					FPermissions.isOverride.set(true);
					for(String cmd : cmds) {
						MinecraftServer.getServer().getCommandManager().executeCommand(event.entityPlayer, cmd);
//						MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd);
					}
					FPermissions.isOverride.remove();
				}
			}
			if(!canceled) {
				FWands.instance().wand(event);
				FSkillsHandler.handleRightClickItem((EntityPlayerMP)event.entityPlayer, event.entityPlayer.getHeldItem());
			}
		}
		event.setCanceled(canceled);
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void playerFireBow(ArrowLooseEvent event) { try {
		EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
		World world = player.worldObj;
		ItemStack held = player.getHeldItem();
		FPlayerDataRPG rpg = FPlayerDataRPG.of(player);
		FAttributes.mulitShot.listen(event);
		if(rpg.selectedPassiveSkills.containsKey(FSkills.bowPassiveDeadshot)) {
			if(!(held.getItem() instanceof LOTRItemCrossbow)) {
				float dead = FSkillsConstants.bowDeadshotPct[rpg.selectedPassiveSkills.get(FSkills.bowPassiveDeadshot)];
				if(held.getItem() instanceof LOTRItemBow) {
					FAttributes.mulitShot.fireLOTRArrow(held, player, world, event.charge, dead, true);
				} else {
					FAttributes.mulitShot.fireVanillaArrow(held, player, world, event.charge, dead, true);
				}
				event.setCanceled(true);
			}
		}
	} catch(Exception exception) { Misc.report(exception); }}
	
	@EventHandler @SubscribeEvent
	public void naturalSpawn(net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn event) { try {
		if(FRegions.instance().denyNaturalSpawn(event)) {
			event.setResult(Result.DENY);
		}
	} catch(Exception exception) { Misc.report(exception); }}
}
