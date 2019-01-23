import java.util.List;

public class Group {
	private String name;
	private List<ClientHandler> users;

	public Group(String name) {
		this.name = name;
	}

	public void join(ClientHandler user) {
		users.add(user);
	}
}
