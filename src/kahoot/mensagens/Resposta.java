package kahoot.mensagens;
import java.io.Serializable;

public class Resposta implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PerguntaCliente pergunta;
	private int indiceResposta;
	private JogadorInfo jogador;
	
	public Resposta(int indiceResposta, JogadorInfo jogador, PerguntaCliente pergunta) {
		this.indiceResposta = indiceResposta;
		this.jogador = jogador;
		this.pergunta = pergunta;
	}
	
	public int getIndiceResposta() {
		return indiceResposta;
	}
	
	public JogadorInfo getJogador() {
		return jogador;
	}
	
	public PerguntaCliente getPergunta() {
		return pergunta;
	}

}
