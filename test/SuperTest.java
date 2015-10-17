package test;

import com.google.gson.Gson;

public class SuperTest
{
	public static void main(String[] args)
	{
		Super sup 	= new Super("BIG");
		Sub sub		= new Sub("small");
		
		System.out.println("Super:");
		sup.printName();
		sup.printAttr();
		
		System.out.println("\nSub:");
		sub.printName();
		sub.printAttr();
		
		Gson gson = new Gson();
		
		System.out.println(gson.toJson(sub));
	}
}
