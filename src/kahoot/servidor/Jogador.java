package kahoot.servidor;

public class Jogador{
	
	private String nome;
	private int equipaID;
	private int pontuacaoDaRonda;
	private int jogoID;
	
	public Jogador(String nome, int equipaID, int jogoID) {
		this.nome = nome;
		this.equipaID = equipaID;
		this.pontuacaoDaRonda = 0;
		this.jogoID = jogoID;
	}
	
	public synchronized void setPontuacaoDaRonda(int pontuacao) {
		pontuacaoDaRonda = pontuacao;
	}
	
	public synchronized int getPontuacaoDaRonda() {
		return pontuacaoDaRonda;
	}
	
	public synchronized void resetarPontuacaoDaRonda() {
		pontuacaoDaRonda = 0;
	}
	
	public int getJogoID() {
		return jogoID;
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getEquipaID() {
		return equipaID;
	}

}
