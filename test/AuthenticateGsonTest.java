package test;

import com.google.gson.Gson;

import commands.Authenticate;
import commands.IdentityChange;

public class AuthenticateGsonTest
{
	public static void main(String[] args)
	{
		Gson gson = new Gson();
		
		IdentityChange idChg = new IdentityChange("mark");
		Authenticate auth = new Authenticate("asiufhiweahf");
		
		System.out.println("IdentityChange:");
		System.out.println(gson.toJson(idChg));
		
		System.out.println("\nAuthenticate:");
		System.out.println(gson.toJson(auth));
	}
}
