package chat;

import java.net.*;
import java.io.*;

public class Client extends Thread {
	
	// var di rete e flussi
	private  Socket socket;
	public DataInputStream in;
	private DataOutputStream out;
	
	// var info server
	private int porta = 50000;
	private String ipServer = "localhost";
	
	//variabili booleane
	private boolean connesso = false;
	private boolean chiudi = false;
	
	String nomeUtente;
	
	public Client(String nomeUtente) {
		this.nomeUtente = nomeUtente;
	}
	
	public void connetti() {
		try {
			socket = new Socket(ipServer,porta);
			connesso = true;
		} catch(IOException e) {
			ClientGUI.errore("*** Problema nel connettersi al server ***");
			connesso = false;
			ClientGUI.visibile();
			ClientGUI.cambiaConnesso();
		}
	}
	
	public boolean connesso() {
		if (socket != null && in != null && out != null) 
			return true;
		else
			return false;
	}

	public void flussi() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch(IOException e) {
			ClientGUI.errore("*** Problema nell'apertura dei flussi ***");
		}
	}
	
	public void send(String msg) {
		if(out == null) {
			ClientGUI.errore("*** Client non connesso ***");
			return;
		}
			
		try {
			out.writeUTF(msg);
		} catch(IOException e) {
			ClientGUI.errore("*** Errore nell'invio del messaggio ***");
		}
	}
	
	public void receive() {
		try {
			String mess = in.readUTF();
			ClientGUI.aggiungi(mess);
		} catch(IOException e) {
			ClientGUI.errore("*** Errore lettura messaggio ***");
		}
	}
	
	public void threadAscolto() {
		ThreadAscolto ta = new ThreadAscolto();
		ta.start();
	}
	
	public void run() {
		connetti();
		if (connesso) {
			flussi();	
			send(nomeUtente);
			if (!rispNomeUtente())
				threadAscolto();
		}
	}
	
	public boolean rispNomeUtente() {
		boolean esiste = false;
		try {
			String risposta = in.readUTF();
			if (risposta.equalsIgnoreCase("Esiste")) {
				esiste = true;
				ClientGUI.cambiaConnesso();
				ClientGUI.errore("*** [Server] Reinserire il nome utente perchè è gia esistente, la connessione verrà chiusa ***");
				ClientGUI.visibile();
			}
			else {
				esiste = false;
				ClientGUI.aggiungi(risposta);
			}
		} catch(IOException e) {
			
		}
		return esiste;
	}
	
	public void chiudi() {
		if (connesso()) {
			try {
			
				socket.close();
				in.close();
				out.close();
			
				chiudi = true;
				ClientGUI.cambiaConnesso();
			} catch (IOException e) {
				ClientGUI.errore("*** Errore nel chiudere la connessione ***");
			}
		}
	}
	
	class ThreadAscolto extends Thread {
		
		public void run() {
			for(;;) {
				if (chiudi) 
					break;
		
				try {
					String mess = in.readUTF();
					if (mess.equals("#!--- close ---!#")) {
						chiudi = true;
						ClientGUI.aggiungi("Il server è stato stoppato");
						ClientGUI.visibile();
					}
					else
						ClientGUI.aggiungi(mess);
					
				} catch(IOException e) {
					ClientGUI.errore("*** Errore ricezione messaggio ***");
				}
			}
			chiudi();
		}
	}
}
