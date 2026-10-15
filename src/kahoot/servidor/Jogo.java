package kahoot.servidor;

import java.util.ArrayList;
import java.util.List;

import kahoot.mensagens.PerguntaCliente;

public class Jogo {

	private int jogoID;
	private int numDeEquipas;
	private int jogadoresPorEquipa;
	private GameState gameState;
	private List<Equipa> equipas;
	private List<PerguntaServidor> perguntas;
	private List<String> usernames = new ArrayList<>();

	public Jogo(int jogoID, int numDeEquipas, int jogadoresPorEquipa, List<PerguntaServidor> perguntas) {
		this.jogoID = jogoID;
		this.numDeEquipas = numDeEquipas;
		this.jogadoresPorEquipa = jogadoresPorEquipa;
		this.equipas = new ArrayList<>();
		this.perguntas = perguntas;
		this.gameState = new GameState(this);
		System.out.println("Jogo: " + perguntas);
	}

	public int getJogoID() {
		return jogoID;
	}

	public int getNumDeEquipas() {
		return numDeEquipas;
	}

	public int getJogadoresPorEquipa() {
		return jogadoresPorEquipa;
	}

	public GameState getGameState() {
		return gameState;
	}

	public List<PerguntaServidor> getPerguntas() {
		return perguntas;
	}

	public List<Equipa> getEquipas() {
		return equipas;
	}

	public synchronized boolean jogoCheio() {
		boolean cheio = false;
		int jogadores = 0;
		for (Equipa equipa : equipas) {
			jogadores = jogadores + equipa.numDejogadoresNaEquipa();
		}
		System.out.println("Jogadores: " + jogadores);
		if (jogadores == (numDeEquipas * jogadoresPorEquipa)) {
			cheio = true;
		}
		return cheio;
	}

	public synchronized void addEquipa(Equipa equipa) {
		if (equipas.size() <= numDeEquipas) {
			equipas.add(equipa);
		} else {
			System.out.println("Jogo Cheio!");
		}
	}

	public synchronized boolean registarJogador(Jogador jogador) {
		boolean registou = false;

		if (jogoCheio()) {
			System.out.println("Jogo esta cheio!");
			return false;
		}

		Equipa equipa = null;
		for (Equipa e : equipas) {
			if (e.getID() == jogador.getEquipaID()) {
				equipa = e;
				break;
			}
		}

		if (equipa == null && equipas.size()<numDeEquipas) {
			Equipa nova = new Equipa(jogador.getEquipaID(), getJogoID(), getJogadoresPorEquipa());
			addEquipa(nova);
			equipa = nova;
		}else if(equipa == null && equipas.size()>=numDeEquipas){
			System.out.println("Nº de equipas ja chegou ao limite!");
			return false;
		}

		registou = equipa.addJogador(jogador);
		return registou;

	}
	
	public synchronized PerguntaServidor getPerguntaServidor(PerguntaCliente pergunta) {
		PerguntaServidor pergServidor = null;
		for(PerguntaServidor ps : perguntas) {
			if(ps.getPergunta().equals(pergunta.getPergunta())) {
				pergServidor = ps;
			}
		}return pergServidor;
	}
	
	public synchronized boolean registarUsername(String username) { 
		for(String nome: usernames) {
			if(nome.equals(username)) {
				return false;
			}
		}
		usernames.add(username);
		return true;
	}

	public void mostrarJogo() {
		System.out.println("\nInformações do Jogo " + jogoID + ":\n");
		System.out.println("Num de equipas: " + numDeEquipas);
		System.out.println("Num de jogadores por equipa: " + jogadoresPorEquipa);
		System.out.println("Perguntas: " + perguntas);
	}

}
