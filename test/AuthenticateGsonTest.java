package test;

import com.google.gson.Gson;

import commands.IdentityChange;

public class AuthenticateGsonTest
{
	public static void main(String[] args)
	{
		Gson gson = new Gson();
		
		IdentityChange idChg = new IdentityChange("mark");
		
		System.out.println("IdentityChange:");
		System.out.println(gson.toJson(idChg));
		
		System.out.println("\nAuthenticate:");
	}
}
