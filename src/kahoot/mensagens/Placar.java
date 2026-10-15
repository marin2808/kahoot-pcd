package kahoot.mensagens;
import java.io.Serializable;
import java.util.List;


public class Placar implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int posicaoEquipa;
	private int pontuacaoEquipa;
	private int pontuacaoDaRonda;
	private List<String> placar;
	
	public Placar(int posicaoEquipa, int pontuacaoEquipa, int pontuacaoDaRonda, List<String> placar) {
		this.posicaoEquipa = posicaoEquipa;
		this.pontuacaoEquipa = pontuacaoEquipa;
		this.pontuacaoDaRonda = pontuacaoDaRonda;
		this.placar = placar;
	}
	
	public int getPosicaoEquipa() {
		return posicaoEquipa;
	}
	
	public int getPontuacaoEquipa() {
		return pontuacaoEquipa;
	}
	
	public int getPontuacaoDaRonda() {
		return pontuacaoDaRonda;
	}
	
	public List<String> getPlacar() {
		return placar;
	}

}
