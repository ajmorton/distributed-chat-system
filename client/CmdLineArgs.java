package client;

import java.util.ArrayList;
import java.util.List;

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
	@Parameter(description = "Host to connect to")
	public List<String> host = new ArrayList<>();
	
	@Parameter(names = "-p", description = "Port to connect over")
	public int port = 4444;
}
