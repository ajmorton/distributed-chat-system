package test;

public class Sub extends Super
{	
	public Sub(String attr) {
		super(attr);
		this.setName("Sub");
	}
	
	public void printName()
	{
		super.printName();
	}
}
