package kahoot.cliente;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import kahoot.mensagens.JogadorInfo;
import kahoot.mensagens.MensagemErro;
import kahoot.mensagens.PerguntaCliente;
import kahoot.mensagens.Placar;
import kahoot.mensagens.Tempo;

public class Cliente {
	
	private JogadorInfo jogador;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private GUI gui;
	
	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine().trim();
		String[] palavras = input.split(" ");
		Cliente cliente = new Cliente(Integer.parseInt(palavras[2]), Integer.parseInt(palavras[3]), palavras[4]);
		int porto = Integer.parseInt(palavras[1]);
		try {
			InetAddress endereco = InetAddress.getByName(palavras[0]);
			cliente.conectarAoServidor(endereco, porto);
			cliente.escutar();
		} catch (IOException e) {
			System.out.println("Erro ao conectar ao servidor!");
		}
	}
	
	public Cliente(int jogoID,int equipaID, String nomeJogador) {
		this.jogador = new JogadorInfo(nomeJogador, equipaID, jogoID);
		this.gui = new GUI(this);
	}
	
	public JogadorInfo getJogador() {
		return jogador;
	}
	
	public void conectarAoServidor(InetAddress endereco, int porto) throws IOException {
		socket = new Socket(endereco, porto);			
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		out.writeObject(jogador);
	}
	
	public void escutar() {
		try {
			
			while(true) {
				Object objeto = in.readObject();
				
					if(objeto instanceof JogadorInfo) {
						gui.esperaDeJogadores();
						System.out.println("JogadorInfo");
					}
					else if(objeto instanceof MensagemErro) {
						MensagemErro msg = (MensagemErro) objeto;
						System.out.println(msg.getMensagemErro());
					}
					else if(objeto instanceof PerguntaCliente) {
						PerguntaCliente pergunta = (PerguntaCliente) objeto;
						System.out.println("PerguntaAtual");
						gui.mostrarPergunta(pergunta);
					}
					else if(objeto instanceof Placar) {
						Placar placar = (Placar) objeto;
						gui.mostrarPlacar(placar);
						System.out.println("Placar");
					}
					else if(objeto instanceof Tempo) {
						Tempo t = (Tempo) objeto;
						gui.atualizarTempo(t.getTempo());
					}
			}
		}catch (IOException | ClassNotFoundException e) {
			System.out.println("Conexão com o servidor perdida!");
		}
		
	}
	
	public void enviarMensagem(Object obj) {
		try {
			out.writeObject(obj);
		} catch (IOException e) {
			System.out.println("Erro ao enviar mensagem!");
		}
	}
	
	
	
	
	
	
	

}
