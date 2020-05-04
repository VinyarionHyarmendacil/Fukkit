package vinyarion.fukkit.main.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lotr.common.fac.LOTRFaction;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRPlayerData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;

public class FCommandIfCondition extends FCommand {
	
	public FCommandIfCondition(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player|'server'> <type> <typeValue> <qualifier> <value> <command...>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 6) {
			throw new CommandException(this.getCommandUsage(sender), new Object[0]);
		}
		ICommandSender executor = args[0].equals("'server'") ? MinecraftServer.getServer() : playerFor(sender, args[0]);
		Assert(executor != null, "Must specify a player!");
		Type type = Type.valueOf(args[1]);
		Assert(type != null, "Must specify a type!");
		Querey p = type.values.get(args[2]);
		Assert(p != null, "Must specify a value to compare!");
		EntityPlayerMP player = executor instanceof EntityPlayerMP ? (EntityPlayerMP)executor : null;
		boolean pass = false;
		try {
			pass = p.check(player, args[3], args[4]);
		} catch(Exception e) {
			pass = false;
		}
		String command = rest(args, 5);
		if(pass) {
			FPermissions.run(()->MinecraftServer.getServer().getCommandManager().executeCommand(executor, command));
		} else {
			executor.addChatMessage(Colors.chat(p.respond(player, args[3], args[4])));
		}
		
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		if(args.length == 1) return players();
		if(args.length == 2) return Type.names;
		if(args.length == 3) {
			Type t = Type.valueOf(args[1]);
			if(t == null) return null;
			return t.valuekeys();
		}
		if(args.length == 4) {
			Type t = Type.valueOf(args[1]);
			if(t == null) return null;
			Querey v = t.querey(args[2]);
			return v.getQualifiers();
		}
		if(args.length == 5) {
			Type t = Type.valueOf(args[1]);
			if(t == null) return null;
			Querey v = t.querey(args[2]);
			return v.getValues();
		}
		if(args.length == 6) {
			return MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender);
		}
		return MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender, rest(args, 6));
	}

	public enum Type {
		alignment,
		pledge,
		permission;
//		This is what It used to be...
//		public Map<String, Triple<BiFunction<String, String, Boolean>, Pair<Supplier<List<String>>, Supplier<List<String>>>, BiFunction<String, String, String>>> values = Maps.newHashMap();
		public Map<String, Querey> values = Maps.newHashMap();
		public static List<String> names = Lists.newArrayList();
		static {
			for(Type t : Type.values()) {
				names.add(t.name());
			}
			for(LOTRFaction fac : LOTRFaction.values()) {
				alignment.add(new AlignmentComparison(fac), ">,>=,=,!=,<,<=", "0,10,-10");
				pledge.add(new PledgeQuerey(fac), "=,!=,+=,-=", "broken,unbroken");
			}
			permission.add(new PermissionCheck("check").withValues(()->Arrays.asList(FPermissions.instance().perms())), "has,has_not", "");
		}
		private Type add(Querey comp, String qualifiers, String values) {
			return add(comp.with(qualifiers, values));
		}
		private Type add(Querey comp) {
			values.put(comp.name(), comp);
			return this;
		}
		public Set<String> valuekeys() {
			return this.values.keySet();
		}
		public Querey querey(String key) {
			return this.values.get(key);
		}
	}
	public static interface Querey {
		public String name();
		public boolean check(EntityPlayerMP player, String qualifier, String value);
		public String respond(EntityPlayerMP player, String qualifier, String value);
		public default List<String> getQualifiers() {
			return Collections.emptyList();
		}
		public default List<String> getValues() {
			return Collections.emptyList();
		}
		public default Querey with(String qualifiers, String values) {
			return this.withQualifiers(qualifiers).withValues(values);
		}
		public default Querey withQualifiers(String qualifiers) {
			return this.withQualifiers(Lists.newArrayList(qualifiers.split(",")));
		}
		public default Querey withQualifiers(List<String> qualifiers) {
			return this.withQualifiers(()->qualifiers);
		}
		public default Querey withQualifiers(Supplier<List<String>> qualifiers) {
			final Querey delegate = this;
			return new Querey() {
				public String name() { return delegate.name(); }
				public boolean check(EntityPlayerMP player, String qualifier, String value) { return delegate.check(null, qualifier, value); }
				public String respond(EntityPlayerMP player, String qualifier, String value) { return delegate.respond(player, qualifier, value); }
				public List<String> getQualifiers() { return qualifiers.get(); }
			};
		}
		public default Querey withValues(String values) {
			return this.withValues(Lists.newArrayList(values.split(",")));
		}
		public default Querey withValues(List<String> values) {
			return this.withValues(()->values);
		}
		public default Querey withValues(Supplier<List<String>> values) {
			final Querey delegate = this;
			return new Querey() {
				public String name() { return delegate.name(); }
				public boolean check(EntityPlayerMP player, String qualifier, String value) { return delegate.check(null, qualifier, value); }
				public String respond(EntityPlayerMP player, String qualifier, String value) { return delegate.respond(player, qualifier, value); }
				public List<String> getValues() { return values.get(); }
			};
		}
	}
	public abstract static class NamedFactionQuerey extends NamedGenericQuerey<LOTRFaction> {
		public NamedFactionQuerey(LOTRFaction faction) {
			super(faction, f->f.codeName().toLowerCase());
		}
		public LOTRFaction faction;
	}
	public abstract static class NamedGenericQuerey<T> implements Querey {
		public NamedGenericQuerey(T thing, Function<T, String> func) {
			this.thing = thing;
			if(func == null) func = String::valueOf;
			this.func = func;
		}
		private Function<T, String> func;
		public T thing;
		public String name() {
			return func.apply(thing);
		}
	}
	public abstract static class StaticQuerey implements Querey {
		public StaticQuerey(String name) {
			this.name = name;
		}
		public String name;
		public String name() {
			return name;
		}
	}
	public static class PledgeQuerey extends NamedFactionQuerey {
		public PledgeQuerey(LOTRFaction faction) {
			super(faction);
		}
		public boolean check(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return false;
			LOTRPlayerData pd = LOTRLevelData.getData(player);
			LOTRFaction pledged = faction;
			boolean broken = "broken".equalsIgnoreCase(value);
			if(!broken && !"unbroken".equalsIgnoreCase(value)) return false;
			LOTRFaction has = broken ? pd.getBrokenPledgeFaction() : pd.getPledgeFaction();
			if(has == null) return false;
			if(qualifier.equals("=")) {
				return pledged == has;
			} else if(qualifier.equals("!=")) {
				return pledged != has;
			} else if(qualifier.equals("+=")) {
				return pledged.isGoodRelation(has);
			} else if(qualifier.equals("-=")) {
				return pledged.isBadRelation(has);
			}
			return false;
		}
		public String respond(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return "No player found! Tell staff about this.";
			LOTRPlayerData pd = LOTRLevelData.getData(player);
			LOTRFaction pledged = faction;
			boolean broken = "broken".equalsIgnoreCase(value);
			LOTRFaction has = broken ? pd.getBrokenPledgeFaction() : pd.getPledgeFaction();
			if(has == null) return "Invalid faction! tell staff about this!";
			if(qualifier.equals("=")) {
				String val = broken ? "You have broken your pledge to %s! You MUST have broken your pledge to %s." : "You are pledged to %s! You MUST be pledged to %s.";
				return String.format(val, has.factionName(), faction.factionName(), value);
			} else if(qualifier.equals("!=")) {
				String val = broken ? "You have broken your pledge to %s! You must NOT have broken your pledge to %s." : "You are pledged to %s! You must NOT be pledged to %s.";
				return String.format(val, has.factionName(), faction.factionName(), value);
			} else if(qualifier.equals("+=")) {
				String val = broken ? "You have broken your pledge to %s! You must have broken your pledge to an ALLY of %s." : "You are pledged to %s! You must be pledged to an ALLY of %s.";
				return String.format(val, has.factionName(), faction.factionName(), value);
			} else if(qualifier.equals("-=")) {
				String val = broken ? "You have broken your pledge to %s! You must have broken your pledge to an ENEMY of %s." : "You are pledged to %s! You must be pledged to an ENEMY of %s.";
				return String.format(val, has.factionName(), faction.factionName(), value);
			}
			return "Invalid qualifier! Tell staff about this.";
		}
	}
	public static class AlignmentComparison extends NamedFactionQuerey {
		public AlignmentComparison(LOTRFaction faction) {
			super(faction);
		}
		public boolean check(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return false;
			LOTRPlayerData pd = LOTRLevelData.getData(player);
			float align;
			try {
				align = Float.parseFloat(value);
			} catch(NumberFormatException e) {
				return false;
			}
			float has = pd.getAlignment(faction);
			if(qualifier.equals(">")) {
				return has > align;
			} else if(qualifier.equals(">=")) {
				return has >= align;
			} else if(qualifier.equals("=")) {
				return has == align;
			} else if(qualifier.equals("!=")) {
				return has != align;
			} else if(qualifier.equals("<")) {
				return has < align;
			} else if(qualifier.equals("<=")) {
				return has <= align;
			}
			return false;
		}
		public String respond(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return "No player found! Tell staff about this.";
			LOTRPlayerData pd = LOTRLevelData.getData(player);
			float align;
			try {
				align = Float.parseFloat(value);
			} catch(NumberFormatException e) {
				return "Invalid value! Tell staff about this.";
			}
			float has = pd.getAlignment(faction);
			if(qualifier.equals(">")) {
				return String.format("Not enough %s alignment! Must be more than %s.", faction.factionName(), value);
			} else if(qualifier.equals(">=")) {
				return String.format("Not enough %s alignment! Must be at least %s.", faction.factionName(), value);
			} else if(qualifier.equals("=")) {
				return String.format("Not right %s alignment! Must be precisely %s.", faction.factionName(), value);
			} else if(qualifier.equals("!=")) {
				return String.format("Not right %s alignment! Must be anything but %s.", faction.factionName(), value);
			} else if(qualifier.equals("<")) {
				return String.format("Not enough %s alignment! Must be less than %s.", faction.factionName(), value);
			} else if(qualifier.equals("<=")) {
				return String.format("Not enough %s alignment! Must be at most %s.", faction.factionName(), value);
			}
			return "Invalid qualifier! Tell staff about this.";
		}
	}
	public static class PermissionCheck extends StaticQuerey {
		public PermissionCheck(String s) {
			super(s);
		}
		public boolean check(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return false;
			FPlayerData fpd = FPlayerData.forPlayer(player);
			boolean has = FPermissions.instance().hasPermission(player, value);
			if(qualifier.equals("has")) {
				return has;
			} else if(qualifier.equals("has_not")) {
				return !has;
			} else {
				return false;
			}
		}
		public String respond(EntityPlayerMP player, String qualifier, String value) {
			if(player == null) return "No player found! Tell staff about this.";
			FPlayerData fpd = FPlayerData.forPlayer(player);
			boolean has = FPermissions.instance().hasPermission(player, value);
			if(qualifier.equals("has")) {
				return String.format("You MUST have the permission '%s'!", value);
			} else if(qualifier.equals("has_not")) {
				return String.format("You must NOT have the permission '%s'!", value);
			} else {
				return "Invalid qualifier! Tell staff about this.";
			}
		}
	}
	
}
