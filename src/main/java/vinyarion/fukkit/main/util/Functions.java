package vinyarion.fukkit.main.util;

public class Functions {
	@FunctionalInterface
	public static interface Function10Arg<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8,
		                A9 arg9, A10 arg10);
	}

	@FunctionalInterface
	public static interface Function1Arg<A1> {
		public void run(A1 arg1);
	}

	@FunctionalInterface
	public static interface Function2Arg<A1, A2> {
		public void run(A1 arg1, A2 arg2);
	}

	@FunctionalInterface
	public static interface Function3Arg<A1, A2, A3> {
		public void run(A1 arg1, A2 arg2, A3 arg3);
	}

	@FunctionalInterface
	public static interface Function4Arg<A1, A2, A3, A4> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4);
	}

	@FunctionalInterface
	public static interface Function5Arg<A1, A2, A3, A4, A5> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
	}

	@FunctionalInterface
	public static interface Function6Arg<A1, A2, A3, A4, A5, A6> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
	}

	@FunctionalInterface
	public static interface Function7Arg<A1, A2, A3, A4, A5, A6, A7> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7);
	}

	@FunctionalInterface
	public static interface Function8Arg<A1, A2, A3, A4, A5, A6, A7, A8> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8);
	}

	@FunctionalInterface
	public static interface Function9Arg<A1, A2, A3, A4, A5, A6, A7, A8, A9> {
		public void run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8,
		                A9 arg9);
	}

	@FunctionalInterface
	public static interface ReturnFunction0Arg<R> {
		public R run();
	}

	@FunctionalInterface
	public static interface ReturnFunction10Arg<R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8,
		             A9 arg9, A10 arg10);
	}

	@FunctionalInterface
	public static interface ReturnFunction1Arg<R, A1> {
		public R run(A1 arg1);
	}

	@FunctionalInterface
	public static interface ReturnFunction2Arg<R, A1, A2> {
		public R run(A1 arg1, A2 arg2);
	}

	@FunctionalInterface
	public static interface ReturnFunction3Arg<R, A1, A2, A3> {
		public R run(A1 arg1, A2 arg2, A3 arg3);
	}

	@FunctionalInterface
	public static interface ReturnFunction4Arg<R, A1, A2, A3, A4> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4);
	}

	@FunctionalInterface
	public static interface ReturnFunction5Arg<R, A1, A2, A3, A4, A5> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
	}

	@FunctionalInterface
	public static interface ReturnFunction6Arg<R, A1, A2, A3, A4, A5, A6> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
	}

	@FunctionalInterface
	public static interface ReturnFunction7Arg<R, A1, A2, A3, A4, A5, A6, A7> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7);
	}

	@FunctionalInterface
	public static interface ReturnFunction8Arg<R, A1, A2, A3, A4, A5, A6, A7, A8> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8);
	}

	@FunctionalInterface
	public static interface ReturnFunction9Arg<R, A1, A2, A3, A4, A5, A6, A7, A8, A9> {
		public R run(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8,
		             A9 arg9);
	}
}