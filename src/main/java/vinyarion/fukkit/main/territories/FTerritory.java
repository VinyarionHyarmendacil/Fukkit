package vinyarion.fukkit.main.territories;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

import vinyarion.fukkit.main.util.Fills;
import vinyarion.fukkit.main.util.Fills.Coord;

public class FTerritory {

	//                             0xAARRGGBB
	public static final int temp = 0xFFFFFFFF;

	public FTerritory(String id, String display, int xCapital, int yCapital, int color, BufferedImage image) {
		List<Coord> points = Lists.newArrayList();
		List<Coord> borders = Lists.newArrayList();
		List<Integer> neighbors = Lists.newArrayList();
		Fills.<Integer>flood(points, borders, Fills.bufferedImage.apply(image), Fills.qualifierInt, xCapital, yCapital, temp);
		int actualColor = image.getRGB(xCapital, yCapital);
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for(Coord point : points) {
			minX = Integer.min(minX, point.x);
			minY = Integer.min(minY, point.y);
			maxX = Integer.max(maxX, point.x);
			maxY = Integer.max(maxY, point.y);
		}
		this.id = id;
		this.display = display;
		this.xCapital = xCapital;
		this.yCapital = yCapital;
		this.color = color;
		this.xOffset = minX;
		this.yOffset = minY;
		this.width = maxX - minX;
		this.height = maxY - minY;
		this.data = new byte[width * height];
		for(Coord point : points) {
			image.setRGB(point.x, point.y, actualColor);
			this.set(point.x, point.y, (byte)1);
		}
		for(Coord border : borders) {
			this.set(border.x, border.y, (byte)2);
		}
		if(java.lang.Boolean.TRUE.booleanValue()) {
			borders.clear();
			for(Coord point : points) {
				int surr = 0;
				if(this.get(point.x+1, point.y) == (byte)2) surr++;
				if(this.get(point.x-1, point.y) == (byte)2) surr++;
				if(this.get(point.x, point.y+1) == (byte)2) surr++;
				if(this.get(point.x, point.y-1) == (byte)2) surr++;
				if(surr >= 2) borders.add(point);
			}
			for(Coord border : borders) {
				this.set(border.x, border.y, (byte)2);
			}
		}
	}

	public final String id;
	public final String display;
	public final int xCapital;
	public final int yCapital;
	public final int color;
	
	public final int xOffset;
	public final int yOffset;
	public final int width;
	public final int height;
	public final byte[] data;

	public void set(int x, int y, byte b) {
		if(x >= xOffset && y >= yOffset && x < xOffset+width && y < yOffset+height) {
			data[(x - xOffset) + (y - yOffset) * width] = b;
		}
	}

	public byte get(int x, int y) {
		if(x >= xOffset && y >= yOffset && x < xOffset+width && y < yOffset+height) {
			return data[(x - xOffset) + (y - yOffset) * width];
		}
		return 0;
	}

	public boolean contains(int x, int y) {
		return this.get(x, y) != 0;
	}

}
