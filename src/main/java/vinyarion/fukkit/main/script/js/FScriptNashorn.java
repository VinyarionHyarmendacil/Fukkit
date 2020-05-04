package vinyarion.fukkit.main.script.js;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.script.FScript;
import vinyarion.fukkit.main.util.Misc;

import static vinyarion.fukkit.main.util.Functions.*;

public abstract class FScriptNashorn extends FScript {

//	private static final List<String> ALLOWED_CLASSES = Arrays.asList(
//		// TODO : idk
//    );

	public static FScriptNashorn of(String scriptIn) {
		NashornScriptEngineFactory nashorn = new NashornScriptEngineFactory();
//	    ScriptEngine engine = nashorn.getScriptEngine(s -> !ALLOWED_CLASSES.stream().anyMatch(s::equals));
	    ScriptEngine engine = nashorn.getScriptEngine(s->true);
		    ScriptContext context = engine.getContext();
		    context.removeAttribute("load", context.getAttributesScope("load"));
		    context.removeAttribute("quit", context.getAttributesScope("quit"));
		    context.removeAttribute("loadWithNewGlobal", context.getAttributesScope("loadWithNewGlobal"));
		    context.removeAttribute("exit", context.getAttributesScope("exit"));
		try {
			engine.eval(scriptIn);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		boolean threaded = engine.getContext().getAttribute("thread") != null;
		return threaded ? new Threaded(engine) : new Unthreaded(engine);
	}

	public static class Improv {
		public static FScriptNashorn of(String data) {
			return FScriptNashorn.of(untick(data));
		}
		public static String untick(String data) {
			StringBuffer sb = new StringBuffer(data);
			while(sb.charAt(0) == '`') {
				sb.delete(0, 1);
			}
			while(sb.charAt(sb.length() - 1) == '`') {
				sb.delete(sb.length() - 1, sb.length());
			}
			return sb.toString();
		}
		public static boolean isImprov(String name) {
			return name == null ? false : 
				name.length() < 2 ? false : 
				name.startsWith("``") ? name.endsWith("``") : false;
		}
	}

	private FScriptNashorn(ScriptEngine engine) {
		super(Collections.EMPTY_LIST);
		this.engine = engine;
		ScriptContext context = engine.getContext();
        context.setAttribute("queue", (Function1Arg<String>)this::queue, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("command", (Function1Arg<String>)this::execute, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("pause", (Function1Arg<Integer>)this::pause, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("syncpause", (Function1Arg<Integer>)this::syncpause, ScriptContext.ENGINE_SCOPE);
	}

	protected ScriptEngine engine;

	// TODO : methods

	public void queue(String string) {
		EntityPlayer player = local.get();
		FMod.later.queue(()->{
			local.set(player);
			try {
				engine.eval(string+"()");
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		});
	}

	public void execute(String string) {
		FScript.executeCommand(string);
	}

	public final boolean percent(Number pct) {
		return 100 * Misc.rand.nextDouble() < pct.doubleValue();
	}

	public void command(EntityPlayer player) {
		this.spawn();
	}

	public final Pause pause(int time) {
		return new Pause(time);
	}
	public final Pause syncpause(int time) {
		return new SyncPause(time);
	}
	public static class Pause {
		protected int time;
		public Pause(int time) {
			this.time = time;
		}
		public Pause and(int time) {
			this.time = time;
			return this;
		}
		public Pause milli() { return millis(); }
		public Pause millis() {
			Instant start = Instant.now();
			resleep: {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) { }
				Instant end = Instant.now();
				Duration d = Duration.between(start, end);
				if(d.toMillis() < time) {
					time -= d.toMillis();
					start = Instant.now();
					break resleep;
				}
			}
			
			return this;
		}
		public Pause tick() { return ticks(); }
		public Pause ticks() {
			time *= 50;
			return millis();
		}
		public Pause second() { return seconds(); }
		public Pause seconds() {
			time *= 20;
			return ticks();
		}
		public Pause minute() { return minutes(); }
		public Pause minutes() {
			time *= 60;
			return seconds();
		}
	}
	public class SyncPause extends Pause {
		public SyncPause(int time) {
			super(time);
		}
		public Pause ticks() {
			if(FMod.later.isResolver()) {
				time *= 50;
				return millis();
			} else {
				FMod.later.waitCycles(time);
				return this;
			}
		}
	}

	// TODO : threading
	public abstract void spawn();

	protected void run() {
		try {
			engine.eval("main()");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public static class Threaded extends FScriptNashorn {
		public Threaded(ScriptEngine engine) {
			super(engine);
		}
		public void spawn() {
			Thread t = new Thread(()->{
				try {
					engine.eval("thread()");
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			});
			t.setDaemon(true);
			t.start();
			this.run();
		}
	}

	public static class Unthreaded extends FScriptNashorn {
		public Unthreaded(ScriptEngine engine) {
			super(engine);
		}
		public void spawn() {
			this.run();
		}
	}

	public static class DialogExit extends RuntimeException {
		private static final long serialVersionUID = -6492612639448118674L;
		public DialogExit() {}
	}

}
