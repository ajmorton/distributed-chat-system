package server;

import com.beust.jcommander.Parameter;

/**
 * Container for command line arguments for JCommander.
 * The host string takes no flag and is required.
 * The port has a default of 4444, and is set otherwise with a "-p" flag.
 * @author rob
 *
 */
public class CmdLineArgs
{	
	@Parameter(names = "-p", description = "Port to connect over")
	public int port = 4444;
}
