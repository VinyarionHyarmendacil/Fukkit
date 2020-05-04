package vinyarion.fukkit.main.territories;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;

import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.util.Misc;

public class FMap {

	public List<FTerritory> list;
	public Map<String, FTerritory> map;

	public FMap(String name, File dir) throws IOException {
		this.list = Lists.newArrayList();
		File map = FData.instance().file(dir, name, "png");
		File txt = FData.instance().file(dir, name, "txt");
		BufferedImage image = ImageIO.read(map);
		FData.readRawLines(txt).forEach(line -> {
			if(line.length() == 0 || line.trim().startsWith("#")) return;
			String[] args = line.split(" ");
			try {
				list.add(new FTerritory(
					args[0],
					FCommand.rest(args, 4),
					Integer.parseInt(args[1]),
					Integer.parseInt(args[2]),
					Misc.parseInt(args[3]),
					image
				));
				System.out.println(line);
			} catch(Exception e) {
				System.err.println("Bad territory definition: '"+line+"'");
				e.printStackTrace();
			}
		});
		this.map = list.stream().collect(Collectors.toMap(t -> t.id, t -> t));
	}

	public FTerritory getTerritory(int x, int y) {
		for(FTerritory t : list) {
			if(t.contains(x, y)) {
				return t;
			}
		}
		return null;
	}

}
