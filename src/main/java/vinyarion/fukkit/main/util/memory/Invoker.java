package vinyarion.fukkit.main.util.memory;

public interface Invoker {
	
	public Return preInvoke(Object object, String method, Object[] args);
	
	public default Return onLegitReturn(Object object, String method, Return ret) {
		return ret;
	}
	
}
