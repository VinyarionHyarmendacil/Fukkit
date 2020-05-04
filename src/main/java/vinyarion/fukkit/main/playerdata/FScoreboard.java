package vinyarion.fukkit.main.playerdata;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

import net.minecraft.entity.boss.EntityWither;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.skill.FSkill;

public class FScoreboard {
	
	private static final AtomicInteger scai = new AtomicInteger(Integer.MIN_VALUE);
	
	public FScoreboard(FNetHandlerPlayServer netHandler) {
		this.netHandler = netHandler;
	}
	
	private FNetHandlerPlayServer netHandler;
	
	private boolean witherShowsPrev = false;
	private boolean witherShows = false;
	private boolean witherChanged = false;
	public final EntityWither wither = this.supply(null);
	
	private EntityWither supply(World world) {
		EntityWither ret = new EntityWither(world);
		ret.setInvisible(true);
		return ret;
	}
	
	public void update() {
		if(this.witherShows = true) {
			if(!this.witherShowsPrev) {
				this.witherShowsPrev = true;
				this.wither.setWorld(this.netHandler.playerEntity.worldObj);
			}
			if(this.netHandler.playerEntity.ticksExisted % 20 == 0) {
				this.netHandler.sendPacket(new S0FPacketSpawnMob(this.wither));
			}
			this.displayBossHealth(50);
			this.displayBossName(Colors.color("&3&oHello there."));
			this.wither.dimension = this.netHandler.playerEntity.dimension;
			Vec3 vec = this.netHandler.playerEntity.getLookVec();
			int witherDistance = 64;
			vec.xCoord *= witherDistance;
			vec.yCoord *= witherDistance;
			vec.zCoord *= witherDistance;
			vec = vec.addVector(this.netHandler.playerEntity.posX, this.netHandler.playerEntity.posY, this.netHandler.playerEntity.posZ);
			this.wither.setPosition(vec.xCoord, vec.yCoord, vec.zCoord);
			this.netHandler.sendPacket(new S18PacketEntityTeleport(this.wither));
			if(this.witherChanged) {
				this.witherChanged = false;
				this.netHandler.sendPacket(new S20PacketEntityProperties(this.wither.getEntityId(), this.wither.getAttributeMap().getAllAttributes()));
			}
		} else {
			if(this.witherShowsPrev) {
				this.witherShowsPrev = false;
				this.netHandler.sendPacket(new S13PacketDestroyEntities(this.wither.getEntityId()));
			}
		}
		if(this.netHandler.playerEntity.ticksExisted % 10 == 0) {
			List<String> readout = Lists.newArrayList();
			Scoreboard sb = this.netHandler.playerEntity.worldObj.getScoreboard();
			ScoreObjective so = new ScoreObjective(sb, "fsb"+scai.getAndIncrement(), null);
			FPlayerDataRPG skills = FPlayerDataRPG.of(this.netHandler.playerEntity);
			if(skills.showScoreboard.getValue()) {
				so.setDisplayName("--   Skills   --");
				readout.add(Colors.DARKRED + "  Active:");
				int i = 0;
				for(Entry<FSkill, Integer> ski : skills.selectedActiveSkills) {
					if(i == skills.activePointer) {
						readout.add(Misc.truncateTo(ski.getKey().shortName, 12) + " " + Colors.BLUE + (ski.getValue()+1));
					} else {
						readout.add(Misc.truncateTo(ski.getKey().shortName, 12) + " " + Colors.GREEN + (ski.getValue()+1));
					}
					i++;
				}
				readout.add(Colors.DARKRED + "  Passive:");
				for(Entry<FSkill, Integer> ski : skills.selectedPassiveSkills) {
					readout.add(Misc.truncateTo(ski.getKey().shortName, 12) + " " + Colors.GREEN + (ski.getValue()+1));
				}
				readout.add("                ");
			}
			this.netHandler.sendPacket(new S3BPacketScoreboardObjective(so, 0));
			int gaps = 3;
			List<Score> scores = Lists.newArrayList();
			for(String string : readout) {
				if(string == null) {
					scores.add(new Score(sb, so, Misc.truncateTo(Misc.times("-", ++gaps), 16)));
				} else {
					scores.add(new Score(sb, so, Misc.truncateTo(string, 16)));
				}
			}
			int idx = readout.size();
			for(Score score : scores) {
				score.setScorePoints(--idx);
				this.netHandler.sendPacket(new S3CPacketUpdateScore(score, 0));
			}
			this.netHandler.sendPacket(new S3DPacketDisplayScoreboard(1, so));
		}
	}
	
	public void setShowWither(boolean show) {
		this.witherShows = show;
		this.witherChanged = true;
	}
	
	public void displayBossName(String string) {
		this.wither.setCustomNameTag(string);
		this.witherChanged = true;
	}
	
	public void displayBossHealth(float percent) {
		this.wither.setHealth(this.wither.getMaxHealth() * percent / 100F);
		this.witherChanged = true;
	}
	
}
