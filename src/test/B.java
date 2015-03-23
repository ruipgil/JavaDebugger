package test;

public class B {
	double b = 3.14;
	
	public double bar(int x) {
		System.out.println("Inside B.baz 1/x");
		return (1/x);
	}
	
	public double baz(Object x) {
		System.out.println("Inside B.baz obj");
		System.out.println(x.toString());
		return b;
	}
}
