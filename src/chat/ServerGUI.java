package chat;

import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;

public class ServerGUI  {

	private JFrame jFServer;

	private static JTextArea tAChat;
	
	private static Server server = new Server();
	
	
	
	public ServerGUI() {
		initialize();
		jFServer.setVisible(true);
		server.avvio();
	}


	//metodo inizializzatore
	private void initialize() {
		jFServer = new JFrame();
		jFServer.setResizable(false);
		jFServer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (server.connessi()) {
					server.inviaTutti("#!--- close ---!#");
					server.chiudiTutti();	
				}
				
				jFServer.dispose();
				System.exit(0);
			}
		});
		jFServer.setTitle("Server");
		jFServer.setBounds(100, 100, 305, 473);
		jFServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFServer.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 38, 269, 386);
		jFServer.getContentPane().add(scrollPane);
		
		tAChat = new JTextArea();
		tAChat.setLineWrap(true);
		tAChat.setWrapStyleWord(true);
		tAChat.setEditable(false);
		scrollPane.setViewportView(tAChat);
	}
	
	public static void appendi(String msg) {
		tAChat.append(msg+"\n");
	}
	
	public static void errore(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Errore!", 0);
	}
	
	
	public static void main(String[] args) {
		new ServerGUI();
	}
	
}
