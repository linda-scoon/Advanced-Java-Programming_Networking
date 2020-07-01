import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ServerRunnable
This class creates a thread of a client.

This for statement loops through a list of  this.threads and calls the send method in all other threads except itself to send a message to all other clients.
The output stream has to be in a method so that all other threads can have access to it. Th threads gain access through the ServerHandler class. 

The server handler class runs as a thread so that new connections can concurrently be added to the list . 

For reference see: https://youtu.be/AUpytdHcwUg
 */

/**
 * @author Flourish
 *
 */
public class ServerRunnable extends Thread {

//	private CopyOnWriteArrayList<Socket> connections;
	private File userLogins;
	private String userName;
	private CopyOnWriteArrayList<User> users;
	private String passWord;
	private Socket client;
	private ServerHandler serverHnd;
	private PrintWriter output;

	/**
	 * 
	 */
	public ServerRunnable(ServerHandler serverHnd, Socket client) {
//		connections = new CopyOnWriteArrayList<>();
		this.client = client;
		this.serverHnd = serverHnd;

		// for logins
		users = new CopyOnWriteArrayList<User>();
		userLogins = new File("userLogins.bin");
		userName = "";
		passWord = "";
	}

	@Override
	public void run() {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		) {
			this.output = new PrintWriter(client.getOutputStream(), true);
			// alert player of connection and log in
			output.println("Server - You are connected");

			handleLogin(input, output);

			// alert player of successful login
			output.println("Log in successful");

			// echoing text
			String text;
			while ((text = input.readLine()) != null) {

				List<ServerRunnable> sRuns = serverHnd.getConnections();
				for (ServerRunnable sr : sRuns) {

					if (!sr.getUserName().equals(this.getUserName())) {
					// if client leaves chat, alert others
					if (text.equalsIgnoreCase(".bye")) {
							text = "Left the chat";
							sRuns.remove(this);
					}

						// check for chat monopoly in current thread
					if (isMonopolising(text)) {
							send("<<SERVER MESSAGE>>" + sr.getUserName() + "-too prolix!!!");// message to current
																								// writer
							send("Disconnecting now...");
							sRuns.remove(this);
							this.getClient().close();
						}

					// echo message to all clients
					sr.send(getUserName() + ": " + text);
					System.out.println(sr.getUserName() + ": " + text);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String msg) {
		output.println(msg);

	}

	/**
	 * Checks for chat monopoly
	 * 
	 * @param text
	 * @return
	 */
	protected boolean isMonopolising(String text) {
		int maxTextLength = 160;
		if (text.length() > maxTextLength) {
			return true;
		}
		return false;
	}

	/**
	 * Prompts user for login credentials and verifies login
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	protected void handleLogin(BufferedReader in, PrintWriter out) throws IOException {

		// Checking if file exists
		if (userLogins.exists()) {
			loadLogIns();// loading data

			// for debugging
			System.out.println(users);
		}

		// if no user exists
		if (users.isEmpty()) {
			signUp(in, out);
			return;
		}

		// get username
		out.println("ENTER USERNAME: ");
		while ((this.userName = in.readLine()) == null) {// wait for input
		}
		// for debugging
		System.out.println(userName);

		// check for user
		boolean validated = false;
		for (User u : users) {
			if (u.isValidUserName(userName)) {// if user was not found sign up

				// get password
				out.println("ENTER PASSWORD: ");
				while ((passWord = in.readLine()) == null) {// wait for input
				}
				// for debugging
				System.out.println(passWord);

				// check password
				if (u.isValidPassword(passWord)) {
					validated = true;
					break;
				} else {
					out.println("Password error");
				}
			}
		}
		if (!validated) {
			out.println("log in not found.");
			signUp(in, out);
		}
	}

	/**
	 * Saves new user credentials
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void signUp(BufferedReader in, PrintWriter out) throws IOException {

		out.println("Setup account");

		// get userName
		out.println("ENTER USERNAME");
		while ((userName = in.readLine()) == null) {// wait for input
		}

		// get password
		out.println("ENTER PASSWORD: ");
		while ((passWord = in.readLine()) == null) {// wait for input
		}

		// save credentials
		User user = new User(userName, passWord);
		users.add(user);
		saveToFile(users);
	}

	/**
	 * saves Arraylist to file
	 * 
	 * @param user
	 */
	private synchronized void saveToFile(CopyOnWriteArrayList<User> users) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(userLogins));
			out.writeObject(users);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/**
 * loads Arraylist from file
 */
private synchronized void loadLogIns() {
	try {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(userLogins));
		users = (CopyOnWriteArrayList<User>) in.readObject();
		in.close();
	} catch (ClassNotFoundException | IOException e) {
		e.printStackTrace();
	}

	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the client
	 */
	public Socket getClient() {
		return client;
	}

	@Override
	public String toString() {
		return "ServerRunnable [userLogins=" + userLogins + ", userName=" + userName + ", users=" + users
				+ ", passWord=" + passWord + ", client=" + client + ", serverHnd=" + serverHnd + ", output=" + output
				+ "]";
	}
}
