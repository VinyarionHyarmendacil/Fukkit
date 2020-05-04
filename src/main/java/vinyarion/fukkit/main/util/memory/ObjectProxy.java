package vinyarion.fukkit.main.util.memory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Maps;

public final class ObjectProxy {
	
	private ObjectProxy() {}
	
	private static final AtomicInteger counter = new AtomicInteger();
	
	public static final <T> Class<? extends T> generateProxyClass(Class<T> parentClass, Class<?>[] interfaces) {
		return generateProxyClass(parentClass.getClassLoader(), parentClass, interfaces);
	}
	
	public static final <T> Class<? extends T> generateProxyClass(ClassLoader classLoader, Class<T> parentClass, Class<?>[] interfaces) {
		ClassNode cn = new ClassNode();
		final int id = counter.getAndIncrement();
		String dotDollarName = parentClass.getPackage().getName() + "." + parentClass.getSimpleName() + "_" + id;
		cn.name = dotDollarName.replace(".", "/");
		cn.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
		/* Parent */ {
			cn.superName = Type.getInternalName(parentClass);
		}
		for(Class<?> itf : interfaces) {
			if(!itf.isInterface()) {
				throw new RuntimeException("Not an interface: " + itf.getCanonicalName());
			}
			cn.interfaces.add(Type.getInternalName(itf));
			for(Method im : itf.getDeclaredMethods()) {
				MethodNode pm = new MethodNode();
				pm.access = Opcodes.ACC_PUBLIC;
				pm.desc = Type.getMethodDescriptor(im);
				InsnList insns = new InsnList(); {
//					insns.add(insn);
					insns.add(getReturnNode(im));
				}
				pm.instructions.add(insns);
				cn.methods.add(pm);
			}
		}
		return (Class<? extends T>) Memory.define(dotDollarName, classLoader, null, cn);
	}
	
	private static final InsnNode getReturnNode(Method m) {
		return new InsnNode(Type.getReturnType(m).getOpcode(Opcodes.IRETURN));
	}
	
	private static final InsnList getOnCall(Method m) {
		InsnList list = new InsnList();
		LabelNode label = new LabelNode();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		Type[] args = Type.getArgumentTypes(m);
		for(int i = 0; i < m.getParameterCount(); i++) {
			list.add(new VarInsnNode(args[i].getOpcode(Opcodes.ILOAD), i+1));
		}
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 
				"vinyarion/fukkit/main/util/memory/ObjectProxy", "of", 
				"(Ljava/lang/Object;[Ljava/lang/Object;)Lvinyarion/fukkit/main/util/memory/Invoker;", false));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new JumpInsnNode(Opcodes.IFNULL, label));
		list.add(getReturnNode(m));
		list.add(label);
		return list;
	}
	
	private static final Map<Class<?>, Invoker> invokers = Maps.newHashMap();
	
	public static Return of(Object object, Method method, Object... args) {
		return invokers.get(object.getClass()).preInvoke(object, method.getName(), args);
	}
	
	static {
//		groovy.util.ProxyGenerator.INSTANCE
	}
	
}
