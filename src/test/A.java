package test;

public class A {
	int a = 1;

	public double foo(B b) throws Exception {
		System.out.println("Inside A.foo");
		if (a == 1) {
			throw new Exception("dzjdkhzds");
			//return b.baz(0);
		} else {
			return b.baz(null);
		}
	}
}
