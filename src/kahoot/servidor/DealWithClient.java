package kahoot.servidor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import kahoot.mensagens.JogadorInfo;
import kahoot.mensagens.MensagemErro;
import kahoot.mensagens.Resposta;

public class DealWithClient extends Thread{
	
	private Servidor servidor;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private JogadorInfo jogadorI;
	
	public DealWithClient(Socket socket, Servidor servidor) {
		this.socket = socket;
		this.servidor = servidor;
	}
	
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			while(!interrupted()) {
				Object objeto = in.readObject();
					if(objeto instanceof JogadorInfo) {
						JogadorInfo jogadorInfo = (JogadorInfo) objeto;
						Jogo jogo = servidor.getJogoPorID(jogadorInfo.getJogoID());
						boolean registou = false;
						if(jogo != null) {
							if(servidor.getJogoPorID(jogadorInfo.getJogoID()).registarUsername(jogadorInfo.getNome()) == false) {
								out.writeObject(new MensagemErro("Nome ja em uso!"));
								return;
							}
							Jogador jogador = new Jogador(jogadorInfo.getNome(), jogadorInfo.getEquipaID(), jogadorInfo.getJogoID());
							registou = jogo.registarJogador(jogador);
						}else{
							out.writeObject(new MensagemErro("Jogo não existe!"));
						}
						if(registou) {
							this.jogadorI = jogadorInfo;
							servidor.addClienteAoJogo(jogadorInfo.getJogoID(), this);
							out.writeObject(jogadorInfo);
							boolean iniciarJogo = servidor.getJogoPorID(jogadorInfo.getJogoID()).getGameState().iniciarJogo();
							if(iniciarJogo) {
								servidor.getJogoPorID(jogadorInfo.getJogoID()).getGameState().iniciarRonda(servidor);
							}
						}else{
							out.writeObject(new MensagemErro("O registo falhou!"));
						}
					}else if(objeto instanceof Resposta) {
						Resposta resposta = (Resposta) objeto;
						JogadorInfo jogadorInfo = resposta.getJogador();
						Jogo jogo = servidor.getJogoPorID(jogadorInfo.getJogoID());
						jogo.getGameState().submeterResposta(resposta, servidor);
					}
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Cliente foi desconectado!");
			servidor.terminarJogo(jogadorI.getJogoID());
		}finally {
			try {
				socket.close();
			}catch (IOException e) {
				System.out.println("Erro ao fechar conexão!");
			}
		}
	}
	
	public void enviarMensagen(Object obj) {
		try {
			out.writeObject(obj);
		} catch (IOException e) {
			System.out.println("Erro ao enviar mensagem para o cliente!");
		}	
	}
	
	public JogadorInfo getJogadorInfo() {
		return jogadorI;
	}
	

}
