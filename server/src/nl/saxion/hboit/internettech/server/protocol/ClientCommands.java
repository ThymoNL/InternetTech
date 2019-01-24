package nl.saxion.hboit.internettech.server.protocol;

public class ClientCommands {
	private static ClientCommands instance;

	private ClientCommands() {}

	public static ClientCommands getParser() {
		if (instance == null) {
			instance = new ClientCommands();
		}

		return instance;
	}

	private String[] decode(String data) {
		return data.split(" ", 2);
	}

	private String command(String command, String data) throws UnexpectedCommandException {
		String[] decoded = decode(data);

		if (decoded[0].equals(command))
			return decoded[1];

		throw new UnexpectedCommandException(command, decoded[0]);
	}

	public String helo(String data) throws UnexpectedCommandException {
		return command("HELO", data);
	}

	public String bcst(String data) throws UnexpectedCommandException {
		return command("BCST", data);
	}

	public String dm(String data) throws UnexpectedCommandException {
		return command("DM", data);
	}

	public String wspr(String data) throws UnexpectedCommandException {
		return command("WSPR", data);
	}

	public String mkg(String data) throws UnexpectedCommandException {
		return command("MKG", data);
	}

	public String kick(String data) throws UnexpectedCommandException {
		return command("KICK", data);
	}
}
