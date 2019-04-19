package nl.saxion.hboict.internettech.client.messages;

public class ServerMessage {
	private String line;

	public ServerMessage(String line) {
		this.line = line;
	}

	public MessageType getMessageType() {
		MessageType result = MessageType.UNKOWN;

		try {
			if (line != null && line.length() > 0) {
				String[] splits = this.line.split("\\s+");
				String lineTypePart = splits[0];

				if (lineTypePart.startsWith("-") || lineTypePart.startsWith("+")) {
					lineTypePart = lineTypePart.substring(1);
				}

				result = ServerMessage.MessageType.valueOf(lineTypePart);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("[ERROR] Unknown command");
		}

		return result;
	}

	public String getPayload() {
		if (getMessageType().equals(MessageType.UNKOWN)) {
			return line;
		} else if (getMessageType().equals(MessageType.BCST)) {
			String bcst = line.substring(getMessageType().name().length() + 1);

			return bcst.replaceFirst(" ", "> ");
		} else if (line != null && line.length() >= getMessageType().name().length() + 1) {
			int offset = 0;
			if (getMessageType().equals(MessageType.OK) || getMessageType().equals(MessageType.ERR)) {
				offset = 1;
			}

			return line.substring(getMessageType().name().length() + 1 + offset);
		} else {
			return "";
		}
	}

	public String toString() {
		return this.line;
	}

	public enum MessageType {
		HELO,
		BCST,
		PING,
		DSCN,
		OK,
		ERR,
		UNKOWN;

		private MessageType() {
		}
	}
}
