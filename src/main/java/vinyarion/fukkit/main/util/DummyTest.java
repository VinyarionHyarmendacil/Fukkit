package vinyarion.fukkit.main.util;

import java.net.URL;

import net.minecraft.launchwrapper.LaunchClassLoader;
import vinyarion.fukkit.main.util.memory.Memory;

@Deprecated public class DummyTest {

	public static void main(String[] args) {
		try {
			Parent p = new Parent();
			p.amethod();
			System.out.println(p);
			System.out.println(p.thing);
			Memory.forceCast(p, Child.class, true);
			p.amethod();
			System.out.println(p);
			System.out.println(p.thing);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	static class Parent {
		int thing = 2;
		void amethod() {}
	}
	static class Child extends Parent {
		void amethod() {
			thing += 3;
		}
	}
	static class HookedLCL extends LaunchClassLoader {
		public HookedLCL(URL[] sources) {
			super(sources);
		}
		
	}
}
