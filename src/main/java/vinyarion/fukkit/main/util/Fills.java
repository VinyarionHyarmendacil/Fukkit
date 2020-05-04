package vinyarion.fukkit.main.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Fills {

	public static <T> int flood(List<Coord> points, List<Coord> borders, ICoordTable<T> source, BiPredicate<? super T, ? super T> equalifier, int xStart, int yStart, T newValue) {
		return flood(points, borders, source, equalifier, new Coord(xStart, yStart), newValue);
	}

	public static <T> int flood(List<Coord> points, List<Coord> borders, ICoordTable<T> source, BiPredicate<? super T, ? super T> equalifier, Coord coords, T newValue) {
		if(!source.acceptsNull())
			Objects.requireNonNull(newValue, "newValue");
		T oldValue = source.getFromCoords(coords);
		if(equalifier.test(oldValue, newValue))
			return 0;
		source.setFromCoords(coords, newValue);
		Queue<Coord> queue = new ArrayDeque<Coord>();
		queue.add(coords);
		points.add(coords);
		int count = 1;
		while(!queue.isEmpty()) {
			Coord coord = queue.poll();
			count++;
			System.err.print(count + " " + coord.x + " " + coord.y);
			for(Coord dir : Coord.cardinals) {
				Coord coordHere = coord.offset(dir);
				T valueHere = source.getFromCoords(coordHere);
				if(equalifier.test(valueHere, oldValue)) {
					if(!points.contains(coordHere)) {
						source.setFromCoords(coordHere, newValue);
						queue.add(coordHere);
						points.add(coordHere);
					}
				} else {
					borders.add(coord);
				}
			}
		}
		return count;
	}

	public static class Coord {

		public static final Coord[] cardinals = new Coord[]{
			new Coord(0, 1),
			new Coord(1, 0),
			new Coord(0, -1),
			new Coord(-1, 0)
		};

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int x;

		public int y;

		public Coord offset(Coord other) {
			return this.offset(other.x, other.y);
		}

		public Coord offset(int x, int y) {
			return new Coord(this.x + x, this.y + y);
		}

		public boolean equals(Object object) {
			if(!(object instanceof Coord) || object == null)
				return false;
			Coord other = (Coord)object;
			return this.x == other.x && this.y == other.y;
		}

	}

	public static interface ICoordTable<T> {

		public boolean acceptsNull();

		public default T getFromCoords(Coord coords) {
			return this.getFromCoords(coords.x, coords.y);
		}

		public T getFromCoords(int x, int y);

		public default void setFromCoords(Coord coords, T value) {
			this.setFromCoords(coords.x, coords.y, value);
		}

		public void setFromCoords(int x, int y, T value);

	}

	public static final Function<BufferedImage, ICoordTable<Integer>> bufferedImage = image -> new ICoordTable<Integer>(){
		public boolean acceptsNull() {
			return false;
		}
		public Integer getFromCoords(int x, int y) {
			return image.getRGB(x, y);
		}
		public void setFromCoords(int x, int y, Integer value) {
			image.setRGB(x, y, value.intValue());
		}
	};
	public static final BiPredicate<Integer, Integer> qualifierInt = (i1, i2) -> i1.intValue() == i2.intValue();

}
