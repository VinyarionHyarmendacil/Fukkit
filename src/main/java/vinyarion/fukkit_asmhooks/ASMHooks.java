package vinyarion.fukkit_asmhooks;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import lotr.common.LOTRPlayerData;
import lotr.common.world.map.LOTRAbstractWaypoint;
import vinyarion.fukkit.core.hook.Hooks.*;

@Deprecated
public class ASMHooks {

	@Calls(owner = LOTRPlayerData.class, name = {"fastTravelTo"}, opcode = Ops.M_VIRTUAL)
	public static void fastTravel(LOTRPlayerData data, LOTRAbstractWaypoint waypoint) {
//		try {
//			MethodHandles.lookup().findVirtual(LOTRPlayerData.class, "fastTravelTo", MethodType.methodType(void.class, LOTRAbstractWaypoint.class)).invoke(data, waypoint);
//		} catch(Throwable e) {
//			e.printStackTrace();
//		}
	}

}
