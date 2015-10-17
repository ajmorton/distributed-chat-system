package test;

public class Super
{
	private String name;
	private String attr;
	
	public Super(String attr)
	{
		this.name = "Super";
		this.attr = attr;
	}
	
	public void printAttr()
	{
		System.out.println(this.attr);
	}
	
	public void printName()
	{
		System.out.println(this.name);
	}
	
	protected void setName(String name)
	{
		this.name = name;
	}
}
