package nl.saxion.hboict.internettech.client;

import nl.saxion.hboict.internettech.client.color.ANSIColor;
import nl.saxion.hboict.internettech.client.messages.ClientMessage;
import nl.saxion.hboict.internettech.client.messages.ServerMessage;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.Stack;

public class Client {
	private String host;
	private int port;
	private boolean log;
	private boolean connected;

	private Socket socket;
	private ServerReader readerThread;
	private ServerWriter writerThread;
	private NonblockingBufferedReader nonblockReader;

	private Stack<ClientMessage> clientMessages;
	private Stack<ServerMessage> serverMessages;

	public Client(String host, int port, boolean log) {
		this.host = host;
		this.port = port;
		this.log = log;

		this.clientMessages = new Stack<>();
		this.serverMessages = new Stack<>();
	}

	/**
	 * Initializes connection to server
	 */
	public void start() {
		try {
			this.socket = new Socket(host, port);

			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			readerThread = new ServerReader(reader);
			new Thread(readerThread).start();

			OutputStream os = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(os);
			writerThread = new ServerWriter(writer);
			new Thread(writerThread).start();

			while (true) {
				if (!serverMessages.empty()) { // Wait for initial message
					ServerMessage serverMessage = serverMessages.pop();
					if (!serverMessage.getMessageType().equals(ServerMessage.MessageType.HELO)) {
						System.err.println("Expecting a HELO message but received: " + serverMessage.toString());
					} else {
						System.out.print("Please enter your username: ");
						Scanner scanner = new Scanner(System.in);
						String username = scanner.nextLine();
						ClientMessage heloMessage = new ClientMessage(ClientMessage.MessageType.HELO, username);
						clientMessages.push(heloMessage);

						while (serverMessages.empty()) {
							// TODO: Maybe sleep for x times and then error out?
							//Thread.sleep(50); // Wait for the server to respond
						}

						connected = validateServerMessage(heloMessage, serverMessages.pop());

						if (!connected) {
							System.err.println("Error logging into server");
						} else {
							System.out.println("Connected to server.");
							System.out.println("Commands start with '/'");
							System.out.println("Enter '/help' to list commands.");
							System.out.println("Otherwise enter a message to send globally:");
							nonblockReader = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));

							while (connected) {
								String line = nonblockReader.readLine();
								if (line != null) {
									ClientMessage clientMessage = null;
									if (line.equals("/quit")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.QUIT);
										connected = false;
										Thread.sleep(500); // Wait for server confirmation
									} else if (line.startsWith("/lsu")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.LSU);
									} else if (line.startsWith("/lsg")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.LSG);
									} else if (line.startsWith("/mkg")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.MKG, line);
									} else if (line.startsWith("/dm")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.DM, line);
									} else if (line.startsWith("/join")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.JOIN, line);
									} else if (line.startsWith("/whisper")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.WSPR, line);
									} else if (line.startsWith("/leave")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.LVG, line);
									} else if (line.startsWith("/kick")) {
										clientMessage = new ClientMessage(ClientMessage.MessageType.KICK, line);
									} else if (line.startsWith("/help")) {
										printHelp();
									} else if (line.startsWith("/")) { // Catch-all
										System.out.println("Invalid command");
									} else {
										clientMessage = new ClientMessage(ClientMessage.MessageType.BCST, line);
									}

									if (clientMessage != null) clientMessages.push(clientMessage);
								}

								if (!serverMessages.empty()) {
									ServerMessage received = serverMessages.pop();
									if (received.getMessageType().equals(ServerMessage.MessageType.BCST)) {
										System.out.println(received.getPayload());
									}
								}
							}

							disconnect(); // FIXME: Not all threads exit
							System.out.println("Disconnected.");
						}
					}
					break;
				} else { // No HELO yet, sleep for a bit
					//TODO: Maybe sleep for x times and then error out?
					//Thread.sleep(50);
				}
			}
		} catch (IOException e) {
			System.err.println("Error connecting to server.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean validateServerMessage(ClientMessage clientMessage, ServerMessage serverMessage) {
		boolean valid = false;

		try {
			byte[] hash = MessageDigest.getInstance("MD5").digest(clientMessage.toString().getBytes());
			String encodedHash = new String(Base64.getEncoder().encode(hash));
			if (serverMessage.getMessageType().equals(ServerMessage.MessageType.OK) && encodedHash.equals(serverMessage.getPayload())) {
				valid = true;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return valid;
	}

	private void disconnect() {
		if (readerThread != null)
			readerThread.kill();

		if (writerThread != null)
			writerThread.kill();

		if (nonblockReader != null)
			nonblockReader.close();

		connected = false;
	}

	private void printHelp() {
		System.out.println("Following commands are available:");
		System.out.println("\t/lsu						-> List all online users");
		System.out.println("\t/dm $user $msg				-> Send a direct message to someone");
		System.out.println("\t/kick $group $user			-> Kick a user from a group");
		System.out.println("\t/lsg						-> List all groups");
		System.out.println("\t/mkg $name					-> Create a group");
		System.out.println("\t/join $group				-> Join a group");
		System.out.println("\t/whisper $group $message	-> Send a message to a group");
		System.out.println("\t/leave $group				-> Leave a group");
	}

	private class ServerWriter implements Runnable {
		private volatile boolean running = true;
		private PrintWriter writer;

		ServerWriter(PrintWriter writer) {
			this.writer = writer;
		}

		@Override
		public void run() {
			while (this.running) {
				if (!clientMessages.empty()) {
					write(clientMessages.pop());
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void write(ClientMessage msg) {
			String line = msg.toString();
			if (log) {
				System.out.println(ANSIColor.GREEN + "<< " + line + ANSIColor.RESET);
			}

			writer.println(line);
			writer.flush();
		}

		void kill() {
			running = false;
		}
	}

	private class ServerReader implements Runnable {
		private volatile boolean running = true;
		private BufferedReader reader;

		ServerReader(BufferedReader reader) {
			this.reader = reader;
		}

		@Override
		public void run() {
			int errors = 0;
			while (this.running) {
				String line = read();

				if (line == null) {
					errors++;

					if (errors >= 3)
						disconnect();
				} else {
					ServerMessage message = new ServerMessage(line);

					if (message.getMessageType().equals(ServerMessage.MessageType.PING)) {
						ClientMessage pongMessage = new ClientMessage(ClientMessage.MessageType.PONG);
						clientMessages.push(pongMessage);
					}

					if (message.getMessageType().equals(ServerMessage.MessageType.DSCN)) {
						System.out.println("Client disconnected by server.");
						disconnect();
					}

					serverMessages.push(message);
					errors = 0;
				}
			}
		}

		private String read() {
			String line = null;

			try {
				line = reader.readLine();
				if (line != null && log) {
					System.out.println(ANSIColor.RED + ">> " + line + ANSIColor.RESET);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return line;
		}

		void kill() {
			running = false;
		}
	}
}
