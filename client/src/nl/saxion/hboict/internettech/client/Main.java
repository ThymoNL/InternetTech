package nl.saxion.hboict.internettech.client;

public class Main {

	public static void main(String[] args) {
		String host = "localhost";
		int port = 1337;
		boolean log = true;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-h":
				case "--host":
					host = args[i + 1];
					break;
				case "-p":
				case "--port":
					port = Integer.parseInt(args[i + 1]);
					break;
				case "--no-logs":
					log = false;
					break;
				case "--usage":
				case "--help":
					System.out.println("Usage: chat-client [-h|--host HOSTNAME] [-p|--port PORT]");
					System.out.println("\t\t--no-logs: don't show the messages from and to the server in the console.");
					return;
			}
		}

		if (port < 1 || port > 65535) {
			System.err.println("Invalid port!");
			System.exit(1);
		}

		new Client(host, port, log).start();
	}
}
