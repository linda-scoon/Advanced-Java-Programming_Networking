public class ChatClient2 {

	public static void main(String[] args) {
		ClientHandler handler = new ClientHandler("Client");
		handler.connect();
	}

}
