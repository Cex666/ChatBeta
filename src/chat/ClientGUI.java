package chat;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI {

	// var frame (finestra)
	private JFrame jFClient;

	// var TextField per inserimento messaggi
	private JTextArea tAMessaggio;

	// var TextArea per la chat (dove appariranno i messaggi inviati e ricevuti)
	private static JTextArea tAChat;

	// var rete di default
	int porta = 50000;
	String ipServer = "localhost";

	// TextField per inserire nome utente
	private static JTextField tFNomeUtente;

	// var label nome utente globale per modificare visibilità
	private static JLabel lblNomeUtente;

	// bottone per connettere al server
	private static JButton btnConnetti;

	// thread
	private Client client;

	static boolean connesso = false;

	// var nome utente
	private static String nomeUtente;
	private JLabel lblSend;

	// Costruttore
	public ClientGUI() {
		initialize();
		jFClient.setVisible(true);
	}

	public static void visibile() {
		tFNomeUtente.setEnabled(true); // rendo la tField invisibili, non modificabile e la disabilito
		tFNomeUtente.setEditable(true);
		tFNomeUtente.setVisible(true);
		btnConnetti.setEnabled(true); // rendo il bottone invisibile e lo disabilito
		btnConnetti.setVisible(true);
		lblNomeUtente.setVisible(true);
	}

	public static void errore(String msgErr) {
		JOptionPane.showMessageDialog(null, msgErr, "ERRORE", 0);
	}

	public static void aggiungi(String msg) {
		tAChat.append(msg + "\n");
	}

	// metodo inizializzatore
	private void initialize() {
		jFClient = new JFrame("Client");
		jFClient.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (connesso) {
					client.send("#!--- close ---!#"); // messaggio per la chiusura dei flussi lato server
					// è da migliorare perchè lo stesso messaggio
					// potrebbe essere inviato dall'utente in chat);
					client.chiudi();
				}

				jFClient.dispose();
				System.exit(0);
			}
		});
		jFClient.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		jFClient.setResizable(false);
		jFClient.getContentPane().setBackground(Color.BLACK);
		jFClient.setBounds(100, 100, 381, 500);
		jFClient.getContentPane().setLayout(null);

		JScrollPane sPChat = new JScrollPane();
		sPChat.setBounds(10, 11, 236, 379);
		jFClient.getContentPane().add(sPChat);

		JScrollPane sPMessaggio = new JScrollPane();
		sPMessaggio.setBounds(10, 400, 236, 42);
		jFClient.getContentPane().add(sPMessaggio);

		tAChat = new JTextArea();
		tAChat.setWrapStyleWord(true);
		tAChat.setEditable(false);
		tAChat.setLineWrap(true);
		sPChat.setViewportView(tAChat);

		tAMessaggio = new JTextArea();
		tAMessaggio.setLineWrap(true);
		sPMessaggio.setViewportView(tAMessaggio);

		lblNomeUtente = new JLabel("Nome utente:");
		lblNomeUtente.setFont(new Font("Andalus", Font.BOLD, 14));
		lblNomeUtente.setForeground(Color.WHITE);
		lblNomeUtente.setBounds(257, 110, 98, 42);
		jFClient.getContentPane().add(lblNomeUtente);

		tFNomeUtente = new JTextField();
		tFNomeUtente.setBounds(256, 146, 99, 20);
		jFClient.getContentPane().add(tFNomeUtente);
		tFNomeUtente.setColumns(10);

		btnConnetti = new JButton("Connetti");
		btnConnetti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tFNomeUtente.getText().length() == 0) {
					errore("*** Il nome utente non può essere vuoto ***");
				} else if (tFNomeUtente.getText().equalsIgnoreCase("Server")) {
					errore("*** Il nome utente non può essere Server ***");
				} else if (tFNomeUtente.getText().contains("@") || tFNomeUtente.getText().contains("/")
						|| tFNomeUtente.getText().contains("\\")) {
					errore("*** Il nome utente non può contenere i seguenti caratteri: [@ / \\]");
				} else {

					nomeUtente = tFNomeUtente.getText(); // Affido alla stringa il nomeUtente
					tFNomeUtente.setEnabled(false); // rendo la tField invisibili, non modificabile e la disabilito
					tFNomeUtente.setEditable(false);
					tFNomeUtente.setVisible(false);
					btnConnetti.setEnabled(false); // rendo il bottone invisibile e lo disabilito
					btnConnetti.setVisible(false);
					lblNomeUtente.setVisible(false);
					client = new Client(nomeUtente);
					client.start();
					connesso = true;
				}
			}
		});
		btnConnetti.setBounds(256, 178, 99, 23);
		jFClient.getContentPane().add(btnConnetti);

		lblSend = new JLabel("Invia");
		lblSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tAMessaggio.getText().length() == 0) {
					errore("*** Il messaggio non può esser vuoto ***");
				} else {
					if (connesso) {
						String mess = tAMessaggio.getText();
						client.send(mess);
						tAMessaggio.setText("");
					} else
						errore("*** Client non connesso ***");
				}
			}
		});
		lblSend.setForeground(Color.WHITE);
		lblSend.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSend.setBounds(273, 406, 46, 14);
		jFClient.getContentPane().add(lblSend);
	}

	public static void cambiaConnesso() {
		connesso = false;
	}

	// main per avviare il tutto
	public static void main(String[] args) {
		new ClientGUI(); // avvio il costruttore
	}
}
