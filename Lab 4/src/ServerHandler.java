import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * @author Flourish
 *
 */
public class ServerHandler extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private CopyOnWriteArrayList<ServerRunnable> connections;
	private Socket client;
	private JTextArea textPad;

	/**
	 * @param title
	 */
	public ServerHandler(String title) {
		super(title);
		width = 400;
		height = 400;
		connections = new CopyOnWriteArrayList<>();

		createScreen();

		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * creates window components
	 */
	public void createScreen() {

		// create text area
		textPad = new JTextArea();
		textPad.setBackground(Color.LIGHT_GRAY);
		textPad.setEditable(false);

		// add panel to frame
		add(new JScrollPane(textPad), BorderLayout.CENTER);
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
				textPad.append("\n" + msg);
			}
		});
	}

	/**
	 * creates connections to client
	 */
	@Override
	public void run() {
		int port = 8888;
		try (ServerSocket server = new ServerSocket(port);) {

			// establishing connection
			postMsg("Waiting for connection ...");

			while (true) {

				client = server.accept();
				postMsg("Connected to " + client);

				// creating new client thread
				ServerRunnable runner = new ServerRunnable(this, client);


				connections.add(runner);
				runner.start();

				// for debugging
				System.out.println(connections);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the connections
	 */
	public CopyOnWriteArrayList<ServerRunnable> getConnections() {
		return connections;
	}
}
