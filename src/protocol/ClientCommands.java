package protocol;

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
		String[] command = data.split(" ");

		return command;
	}

	public String helo(String data) {
		String[] command = decode(data);

		if (command[0].equals("HELO")) {
			return command[1];
		}

		return null;
	}


}
