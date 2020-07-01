import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author Flourish
 *
 */
public class ClientHandler extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private JTextArea textPad;
	private JTextField textField;
	private String text;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	public ClientHandler(String title) {
		super(title);

		width = 400;
		height = 400;
		text = "";
		createScreen();

		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	/**
	 * creates UI
	 */
	public void createScreen() {
		// create text area
		textPad = new JTextArea();
		textPad.setEditable(false);

		// create textField
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				text = e.getActionCommand();
				sendMsg(text);
				textField.setText("");// clear textfield
			}
		});

		// add panels to frame
		add(textField, BorderLayout.NORTH);
		add(new JScrollPane(textPad), BorderLayout.CENTER);
	}

	/**
	 * Sends message to server
	 * 
	 * @param text
	 */
	protected void sendMsg(String text) {
		out.println(text);// send text to server
	}

	public void connect() {
		String host = "localhost";
		int port = 8888;
		try {
			// alerting user of connection establishment
			postMsg("Establishing connection. Please wait...");

			// Initialising client
			client = new Socket(host, port);

			// for reading text that has been echoed back from the server
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			// for sending text to the server
			out = new PrintWriter(client.getOutputStream(), true);

			// setting up communication
			String textMsg;
			while ((textMsg = in.readLine()) != null) {

				if (text.equalsIgnoreCase(".bye")) {// check for exit condition
					break;
				}
				postMsg(textMsg);// print text echoed from server
			}

			// End of chat
			postMsg("closed connection");
			textField.setEditable(false);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// closing all streams
			try {
				client.close();
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * posts message to screen
	 * 
	 * @param msg
	 */
	protected void postMsg(String msg) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textPad.append("\n" + msg);// post to screen
			}
		});
	}
}
