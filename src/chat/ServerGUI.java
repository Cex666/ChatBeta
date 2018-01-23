package chat;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerGUI implements WindowListener {

	private JFrame jFServer;

	private static JTextArea tAChat;
	
	private static Server server = new Server();
	
	
	
	public ServerGUI() {
		initialize();
		server.avvio();
	}


	//metodo inizializzatore
	private void initialize() {
		jFServer = new JFrame();
		jFServer.setVisible(true);
		jFServer.setTitle("Server");
		jFServer.setBounds(100, 100, 305, 473);
		jFServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFServer.getContentPane().setLayout(null);
		jFServer.addWindowListener(this);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 38, 269, 386);
		jFServer.getContentPane().add(scrollPane);
		
		tAChat = new JTextArea();
		scrollPane.setViewportView(tAChat);
	}
	
	public static void appendi(String msg) {
		tAChat.append(msg+"\n");
	}
	
	public static void errore(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Errore!", 0);
	}
	
	//metodo per la chiusura della finestra
	public void windowClosing(WindowEvent e) {
		
		if (server.connessi()) {
			server.inviaTutti("#!--- close ---!#");
			server.chiudiTutti();	
		}
		
		jFServer.dispose();
		System.exit(0);
	}
		
		/* metodi per l'implements di windowListener (altrimenti continua a dare errore, ma siccome
		*  non servono restano vuoti) */
		public void windowIconified(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
	
	public static void main(String[] args) {
		new ServerGUI();
	}
}
