package vinyarion.fukkit.sm.blocklog;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;

public class BlockLogQuerey {

	private static final long ONE_HOUR = 1000 * 60 * 60;

	public BlockLogQuerey() { }

	long mintime = System.currentTimeMillis() - ONE_HOUR;
	long maxtime = Long.MAX_VALUE;
	String player = "*";
	String block = "*";
	Boolean broke = null;
	int meta = -1;
	int minx = -30000000;
	int maxx = 30000000;
	int miny = 0;
	int maxy = 255;
	int minz = -30000000;
	int maxz = 30000000;

	public BlockLogQuerey mintime(long mintime) {
		this.mintime = mintime;
		return this;
	}

	public BlockLogQuerey maxtime(long maxtime) {
		this.maxtime = maxtime;
		return this;
	}

	public BlockLogQuerey player(EntityPlayerMP player) {
		this.player = player.getCommandSenderName();
		return this;
	}

	public BlockLogQuerey block(Block block) {
		this.block = block.getUnlocalizedName();
		return this;
	}

	public BlockLogQuerey broke(boolean broke) {
		this.broke = broke;
		return this;
	}

	public BlockLogQuerey meta(int meta) {
		this.meta = meta;
		return this;
	}

	public BlockLogQuerey minx(int minx) {
		this.minx = minx;
		return this;
	}

	public BlockLogQuerey maxx(int maxx) {
		this.maxx = maxx;
		return this;
	}

	public BlockLogQuerey miny(int miny) {
		this.miny = miny;
		return this;
	}

	public BlockLogQuerey maxy(int maxy) {
		this.maxy = maxy;
		return this;
	}

	public BlockLogQuerey minz(int minz) {
		this.minz = minz;
		return this;
	}

	public BlockLogQuerey maxz(int maxz) {
		this.maxz = maxz;
		return this;
	}

	public String toStatement() {
		List<String> sqls = Lists.newArrayList(
			"time BETWEEN %s AND %s", 
			"x BETWEEN %s AND %s", 
			"y BETWEEN %s AND %s", 
			"z BETWEEN %s AND %s"
		);
		List<String> args = Lists.newArrayList(
			String.valueOf(mintime), 
			String.valueOf(maxtime), 
			String.valueOf(minx), 
			String.valueOf(maxx), 
			String.valueOf(miny), 
			String.valueOf(maxy), 
			String.valueOf(minz), 
			String.valueOf(maxz)
		);
		if(!player.equals("*")) {
			sqls.add(0, "player = %s");
			args.add(0, player);
		}
		if(meta != -1) {
			sqls.add("meta = %s");
			args.add(String.valueOf(meta));
		}
		if(!block.equals("*")) {
			sqls.add("block = %s");
			args.add(block);
		}
		if(broke != null) {
			sqls.add("broke = %s");
			args.add(String.valueOf(broke.booleanValue() ? 1 : 0));
		}
		String res = StringUtils.join(sqls, " AND ");
		return String.format("SELECT * FROM blocklog WHERE "+res+";", args.toArray());
	}

}
