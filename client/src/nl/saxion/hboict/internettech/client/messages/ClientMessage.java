package nl.saxion.hboict.internettech.client.messages;

public class ClientMessage {
	private ClientMessage.MessageType type;
	private String line;

	public ClientMessage(ClientMessage.MessageType type) {
		this.type = type;
		this.line = "";
	}

	public ClientMessage(ClientMessage.MessageType type, String line) {
		this.type = type;

		if (line.startsWith("/"))
			this.line = line.substring(line.indexOf(" ")); // Remove command from line
		else
			this.line = line;
	}

	public String toString() {
		return this.type + " " + this.line;
	}

	public enum MessageType {
		HELO,
		BCST,
		LSU,
		DM,
		MKG,
		LSG,
		JOIN,
		WSPR,
		LEAVE,
		KICK,

		PONG,
		QUIT
	}
}
