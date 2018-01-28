package chat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	// var di rete
	private Socket socket;
	private ServerSocket server;
	
	private  ArrayList<ServerThread> listaThread;	//lista quindi dei client corrispondeti a tale thread
	
	// valori default
	int porta = 50000;
	String ipServer = "localhost";
	
	// var che indica l'ultimo elemento della listaThread
	static int indiceUltimoElemento = 0;
	
	
	
	public void avvio() {
		listaThread = new ArrayList<ServerThread>();
		try {
			//avvio socket
			server = new ServerSocket(porta);
			ServerGUI.appendi("Server avviato");
			
			for(;;) {
				ServerGUI.appendi("Attendo la connessione dei client");
				socket = server.accept();
				
				ServerGUI.appendi("Un client sta tentanto la connessione");
				ServerThread st = new ServerThread(socket);		//creo l'oggetto ServerThread
				listaThread.add(st);	
				indiceUltimoElemento = listaThread.size()-1;
				boolean trovato = false;	/* 
											 * pongo la variabile trovato a false che serve per 
											 * controllare la presenza di uno stesso nome utente
											 */
				
				//eseguo il ciclo del controllo
				if (indiceUltimoElemento!=0) {
					for (int i = 0; i<indiceUltimoElemento; i++) {		
						ServerThread stCompare = listaThread.get(i);	//ottengo il client all'indice i
					
						//comparo i due nomi utenti
						if (st.getNomeUtente().equals(stCompare.getNomeUtente())) {		
							st.send("Esiste"); 
							trovato = st.close();		//chiudo i flussi e pongo la variabile a true
							ServerGUI.appendi("Client disconnesso, nome utente già esistente");
							listaThread.remove(indiceUltimoElemento);	//rimuovo l'ultimo elemeto dalla lista
							break;							//fermo il ciclo per evitare elaborazioni inutili
						}
					}
				}
				
			
				
				
				//se la variabile trovato è rimasta false il client rimane collegato e viene inoltrato un messaggio a tutti eccetto l'ultimo
				if (!trovato) {										//aggiungo l'oggetto all'arrayList
									//ottengo l'indice dell'ultimo elemento
					
					st.send("[Server]: Benvenuto " + st.getNomeUtente() + " ti sei connesso correttamente al server");
					ServerGUI.appendi(st.getNomeUtente() + " connesso");
					for (int i = 0; i<indiceUltimoElemento; i++) {		
						listaThread.get(i).send("[Server]: "+st.getNomeUtente()+" si è connesso");
					}
					
					st.start();			//starto infine il client
				}
				
				
			}
		} catch(IOException e) {
			ServerGUI.errore("*** Porta occupata, server non avviabile, cambiare la porta! ***");
		}
	}
	
	public boolean connessi() {
			if (listaThread.size() == 0) 
				return false;
			else
				return true;
	}
	

	// invia messaggio a tutti
	public  void inviaTutti(String msg) {
		if (connessi()) {
			for (int i = 0; i<=indiceUltimoElemento; i++) {
				//prima di inviare controlla che sia connesso con i relativi flussi aperti, altrimenti o rimuove dalla lista
				if (listaThread.get(i).connesso())
					listaThread.get(i).send(msg);
				else
					listaThread.remove(i);
			}
		}
	}
	
	//rimuove un elemento dall'arrayList in base al nome utente
	public void rimuoviElemento(String nomeUtente) {
		if (connessi()) {
			for (int i = 0; i<=indiceUltimoElemento; i++) {
				if (nomeUtente.equals(listaThread.get(i).getNomeUtente())) {
					listaThread.remove(i);
				}
			}
			indiceUltimoElemento--;
		}
	}
	
	public void chiudiTutti() {
		if (connessi()) {
			for (int i = 0; i<=indiceUltimoElemento; i++) {
				listaThread.get(i).close();
			}
			listaThread.clear();
		}
	}
	
	/*public static void main(String[] args) {
		Server s = new Server();
		s.avvio();		//esegue all'infinito tale metodo, in caso uscisse da esso stoppa il server con 		
	}*/	
	
	class ServerThread extends Thread {
		
		//	var di rete (compresi i flussi)
		private Socket socket;
		private DataInputStream in;
		private DataOutputStream out;
		
		public String nomeUtente;		//identifica (oltre che valere come username) il singolo client
		
		public String msg;			//var utilizzata per inglobare il messaggio e spedirlo poi
		
		boolean chiuso = true;		//var che indica che il thread è chiuso
		
		//costruttore
		public ServerThread(Socket socket) {
			this.socket = socket;
			flussi();
			setNomeUtente();
			chiuso = false;
		}
		
		//apertura flussi
		public void flussi() {
			try  {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				ServerGUI.appendi("Flussi aperti");
			} catch(IOException e) {
				ServerGUI.errore("*** Errore apertura flussi ***");
			}
		}
		
		public void setNomeUtente() {
			try {
				nomeUtente = in.readUTF();
			} catch(IOException e) {
				ServerGUI.errore("*** Errore ricezione nome utente ***");
			}
		}
		
		public String getNomeUtente() {
			return nomeUtente;
		}
		
		public void send(String mess) {
			try {
				out.writeUTF(mess);
			} catch(IOException e) {
				ServerGUI.errore("*** Errore invio messaggio ***");
			}
		}
		
		public void read() {
			try {
				msg = in.readUTF();
			} catch(IOException e) {
				ServerGUI.errore("*** Errore lettura messaggio ***");
			}
		}
		
		//rivela se i flussi e il socket son connessi e aperti
		public boolean connesso() {
			if (socket != null && in != null && out != null)
				return true;
			else
				return false;
		}
		
		public boolean close() {
			try {
				socket.close();
				in.close();
				out.close();
				chiuso = true;
			} catch(IOException e) {
				ServerGUI.errore("*** Errore chiusura del client ***");
			}
			return chiuso;
		}
		
		public void run() {
			for(;;) {
				
				if (chiuso)
					break;
				
				read();
				if (msg.equals("#!--- close ---!#")) {
					break;
				}
				else {
					inviaTutti("["+nomeUtente+"]: "+msg);
					ServerGUI.appendi("["+nomeUtente+"]: "+msg);
				}
			}
			ServerGUI.appendi(nomeUtente+" si è disconnesso");
			inviaTutti("[Server]: "+nomeUtente+" si è disconnesso");
			close();
			rimuoviElemento(nomeUtente);	 //in caso il client chiuda la connessione durante la run viene rimosso dalla lista
		}
		
	}
}