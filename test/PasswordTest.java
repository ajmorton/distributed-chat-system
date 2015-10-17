package test;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import common.PasswordHash;

public class PasswordTest
{
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, UnsupportedEncodingException
	{
		Console console = System.console();
		
		String pwHash = PasswordHash.createHash(console.readPassword("Enter a new password: "));
		
		if (!PasswordHash.validatePassword(console.readPassword("Confirm password: "), pwHash)) {
			System.out.println("Passwords do not match");
		}
		
		PrintWriter pw = new PrintWriter("/Users/rob/Desktop/chatHash.hash", "UTF-8");
		pw.println(pwHash.split(":")[2]);
		pw.close();
	}
}
