package kahoot.servidor;
import java.util.ArrayList;
import java.util.List;

public class Equipa{
	
	private int equipaID;
	private List<Jogador> jogadores;
	private int pontuacao;
	private int pontuacaoDaRonda;
	private int jogoID;
	private int numDeJogadoresPorEquipa;
	
	public Equipa(int equipaID, int jogoID, int numDeJogadoresPorEquipa) {
		this.equipaID = equipaID;
		this.jogadores = new ArrayList<>();
		this.pontuacao = 0;
		this.pontuacaoDaRonda = 0;
		this.jogoID = jogoID;
		this.numDeJogadoresPorEquipa = numDeJogadoresPorEquipa;
	}
	
	public synchronized boolean addJogador(Jogador jogador) {
		if(jogadores.size()< numDeJogadoresPorEquipa) {
			jogadores.add(jogador);
			return true;
		}else {
			System.out.println("Equipa esta cheia!");
			return false;
		}
	}
	
	public synchronized List<Jogador> getJogadores(){
		return jogadores;
	}
	
	public synchronized int numDejogadoresNaEquipa() {
		return jogadores.size();
	}
	
	public int getID() {
		return equipaID;
	}
	
	public synchronized int getPontuacao() {
		return pontuacao;
	}

	public int getJogoID() {
		return jogoID;
	}
	
	public synchronized void addPontuacao(int pontos) {
		this.pontuacao = this.pontuacao + pontos;
		this.pontuacaoDaRonda = this.pontuacaoDaRonda + pontos;
	}
	
	public synchronized Jogador getJogador(String nome) {
		for(Jogador j : jogadores) {
			if(j.getNome().equals(nome)){
				return j;
			}
		}
		return null;
	}

}
