package kahoot.mensagens;
import java.io.Serializable;
import java.util.List;

public class PerguntaCliente implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pergunta;
	private List<String> opcoes;
	
	public PerguntaCliente(String pergunta, List<String> opcoes) {
		this.pergunta = pergunta;
		this.opcoes = opcoes;
	}
	
	public String getPergunta() {
		return pergunta;
	}
	
	public List<String> getOpcoes(){
		return opcoes;
	}


}
