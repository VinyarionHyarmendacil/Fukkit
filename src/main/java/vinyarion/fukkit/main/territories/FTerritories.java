package vinyarion.fukkit.main.territories;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;

import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FData;

public class FTerritories {

	public static void init() {
		File dir = FData.instance().dir("maps");
		try {
			political = new FMap("political", dir);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static FMap political;

}
