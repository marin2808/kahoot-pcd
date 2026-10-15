package kahoot.servidor;
import java.util.List;

public class PerguntaServidor {
	
	private String question;
	private List<String> options;
	private int correct;
	private int points;
	private boolean individual;
	
	public PerguntaServidor(String pergunta, List<String> opcoes, int opcaoCerta, int pontos) {
		this.question = pergunta;
		this.options = opcoes;
		this.correct = opcaoCerta;
		this.points = pontos;
		this.individual = false;
	}
	
	public String getPergunta() {
		return question;
	}
	
	public List<String> getOpcoes(){
		return options;
	}
	
	public int getOpcaoCerta() {
		return correct;
	}
	
	public int getPontuacao() {
		return points;
	}
	
	public boolean isIndividual() {
		return individual;
	}
	
	public void setIndividual(boolean i) {
		this.individual = i;
	}
	

}
