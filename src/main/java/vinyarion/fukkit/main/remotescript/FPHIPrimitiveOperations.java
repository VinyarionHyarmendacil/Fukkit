package vinyarion.fukkit.main.remotescript;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FPHIPrimitiveOperations {
	
	public static Number add(String type, Number operand1, Number operand2) {
		BigDecimal d1 = new BigDecimal(operand1.toString());
		BigDecimal d2 = new BigDecimal(operand2.toString());
		return castToType(type, d1.add(d2));
	}
	
	public static Number sub(String type, Number operand1, Number operand2) {
		BigDecimal d1 = new BigDecimal(operand1.toString());
		BigDecimal d2 = new BigDecimal(operand2.toString());
		return castToType(type, d1.subtract(d2));
	}
	
	public static Number mul(String type, Number operand1, Number operand2) {
		BigDecimal d1 = new BigDecimal(operand1.toString());
		BigDecimal d2 = new BigDecimal(operand2.toString());
		return castToType(type, d1.multiply(d2));
	}
	
	public static Number div(String type, Number operand1, Number operand2) {
		BigDecimal d1 = new BigDecimal(operand1.toString());
		BigDecimal d2 = new BigDecimal(operand2.toString());
		return castToType(type, d1.divide(d2, RoundingMode.HALF_EVEN));
	}
	
	public static Number mod(String type, Number operand1, Number operand2) {
		BigDecimal d1 = new BigDecimal(operand1.toString());
		BigDecimal d2 = new BigDecimal(operand2.toString());
		return castToType(type, d1.remainder(d2));
	}
	
	static Number castToType(String type, Number operand) {
		if(type.equals("b")) {
			return (Byte)operand;
		} else if(type.equals("s")) {
			return (Short)operand;
		} else if(type.equals("i")) {
			return (Integer)operand;
		} else if(type.equals("l")) {
			return (Long)operand;
		} else if(type.equals("f")) {
			return (Float)operand;
		} else if(type.equals("d")) {
			return (Double)operand;
		}
		return null;
	}
	
	static Number castToType(String type, BigDecimal operand) {
		if(type.equals("b")) {
			return (Byte)operand.byteValue();
		} else if(type.equals("s")) {
			return (Short)operand.shortValue();
		} else if(type.equals("i")) {
			return (Integer)operand.intValue();
		} else if(type.equals("l")) {
			return (Long)operand.longValue();
		} else if(type.equals("f")) {
			return (Float)operand.floatValue();
		} else if(type.equals("d")) {
			return (Double)operand.doubleValue();
		}
		return null;
	}
	
	public static Boolean more(Number n1, Number n2) {
		BigDecimal d1 = new BigDecimal(n1.toString());
		BigDecimal d2 = new BigDecimal(n2.toString());
		return d1.subtract(d2).signum() == 1;
	}
	
	public static Boolean less(Number n1, Number n2) {
		BigDecimal d1 = new BigDecimal(n1.toString());
		BigDecimal d2 = new BigDecimal(n2.toString());
		return d1.subtract(d2).signum() == -1;
	}
	
	public static Boolean least(Number n1, Number n2) {
		BigDecimal d1 = new BigDecimal(n1.toString());
		BigDecimal d2 = new BigDecimal(n2.toString());
		return d1.subtract(d2).signum() != -1;
	}
	
	public static Boolean most(Number n1, Number n2) {
		BigDecimal d1 = new BigDecimal(n1.toString());
		BigDecimal d2 = new BigDecimal(n2.toString());
		return d1.subtract(d2).signum() != 1;
	}
	
}
