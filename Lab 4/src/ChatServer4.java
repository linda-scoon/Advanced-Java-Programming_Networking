public class ChatServer4 {

	public static void main(String[] args) {

		ServerHandler serverHandler = new ServerHandler("Chat Window");
		Thread t = new Thread(serverHandler);
		t.start();
	}
}
