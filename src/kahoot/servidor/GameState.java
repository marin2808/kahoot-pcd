package kahoot.servidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kahoot.mensagens.JogadorInfo;
import kahoot.mensagens.PerguntaCliente;
import kahoot.mensagens.Placar;
import kahoot.mensagens.Resposta;

public class GameState {

	private int perguntaAtual;
	private List<Equipa> equipas;
	private List<PerguntaServidor> perguntas;
	private List<Resposta> respostasPerguntaAtual = new ArrayList<>();
	private Temporizador temporizador;
	private Jogo jogo;
	private boolean rondaAtiva = true;
	private CountDownLatchModified latch;
	private Map<Integer, Barreira> barreiras;

	public GameState(Jogo jogo) {
		this.jogo = jogo;
		this.perguntaAtual = 0;
		this.perguntas = jogo.getPerguntas();
		this.equipas = jogo.getEquipas();
	}

	public Jogo getJogo() {
		return jogo;
	}

	public PerguntaCliente getPerguntaAtual() {
		PerguntaServidor ps = perguntas.get(perguntaAtual);
		String pergunta = ps.getPergunta();
		List<String> opcoes = ps.getOpcoes();
		PerguntaCliente pc = new PerguntaCliente(pergunta, opcoes);
		return pc;
	}

	public void perguntaSeguinte() {
		if (perguntaAtual < perguntas.size()) {
			perguntaAtual++;
		}
	}

	public List<Equipa> getPlacar() {
		equipas.sort((a, b) -> b.getPontuacao() - a.getPontuacao());
		return equipas;
	}

	public int getPosicaoEquipa(int equipaID) {
		int i = 0;
		int posicao = 0;
		List<Equipa> equipa = getPlacar();
		for (Equipa e : equipa) {
			if (equipaID == e.getID()) {
				posicao = i;
			}
			i++;
		}
		return posicao + 1;
	}

	public int getPontuacaoEquipa(int equipaID) {
		int pontuacao = 0;
		for (Equipa e : equipas) {
			if (equipaID == e.getID()) {
				pontuacao = e.getPontuacao();
				break;
			}
		}
		return pontuacao;
	}

	public void submeterResposta(Resposta resposta, Servidor servidor) {
		PerguntaServidor pergunta = perguntas.get(perguntaAtual);
		if (pergunta.isIndividual()) {
			submeterRespostaIndividual(resposta, servidor);
		} else {
			submeterRespostaEquipa(resposta, servidor);
		}
	}

	public synchronized void submeterRespostaIndividual(Resposta resposta, Servidor servidor) {

		JogadorInfo jogadorInfo = resposta.getJogador();
		int equipaID = jogadorInfo.getEquipaID();
		System.out.println("submeter resposta individual");
		if (latch == null) {
			System.out.println("Latch não iniciou!");
			return;
		}

		for (Resposta r : respostasPerguntaAtual) {
			if (r.getJogador().getNome().equals(jogadorInfo.getNome())) {
				return;
			}
		}

		respostasPerguntaAtual.add(resposta);
		int bonus = latch.countDown();

		PerguntaServidor ps = perguntas.get(perguntaAtual);

		System.out.println("Verificar resposta individual do " + resposta.getJogador().getNome());
		System.out.println(
				"Resposta correta: " + ps.getOpcaoCerta() + ", resposta dada: " + resposta.getIndiceResposta());
		if (ps.getOpcaoCerta() == resposta.getIndiceResposta()) {
			int pontos = bonus * ps.getPontuacao();
			Equipa equipa = null;
			for (Equipa e : equipas) {
				if (e.getID() == equipaID) {
					equipa = e;
					e.addPontuacao(pontos);
					System.out.println("Foi adicionado " + pontos);
					break;
				}
			}
			Jogador jogador = equipa.getJogador(jogadorInfo.getNome());
			if (jogador != null) {
				jogador.setPontuacaoDaRonda(pontos);
			}
		} else {
			System.out.println("Resposta errada!");
		}
	}

	public void submeterRespostaEquipa(Resposta resposta, Servidor servidor) {
		JogadorInfo jogador = resposta.getJogador();
		int equipaID = jogador.getEquipaID();
		System.out.println("submeter resposta equipa");
		Barreira barreira = barreiras.get(equipaID);
		if (barreira == null) {
			System.out.println("Barreira desta equipa não existe!");
			return;
		}
		System.out.println("Barreira: " + barreira);
		barreira.addResposta(resposta);
		System.out.println("Resposta adicionada. Equipa: " + equipaID + ", Jogador: " + jogador.getNome());
		try {
			List<Resposta> lista = barreira.await();
			if (lista != null) {
				System.out.println("Barreira liberada! " + lista.size() + " respostas da equipa " + equipaID);
				verificarRespostaEquipa(equipaID, lista, servidor);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void verificarRespostaEquipa(int equipaID, List<Resposta> respostas, Servidor servidor) {

		PerguntaServidor ps = perguntas.get(perguntaAtual);
		int respostasCertas = 0;
		int pontuacaoMaior = 0;
		System.out.println("A verificar respostas da equipa " + equipaID);
		System.out.println("Número de respostas: " + respostas.size());
		for (Resposta r : respostas) {
			if (ps.getOpcaoCerta() == r.getIndiceResposta()) {
				Jogador jogador = null;
				for (Equipa equipa : equipas) {
					if (equipa.getID() == equipaID) {
						jogador = equipa.getJogador(r.getJogador().getNome());
					}
				}
				if (jogador != null) {
					jogador.setPontuacaoDaRonda(ps.getPontuacao());
				}
				respostasCertas++;
				if (pontuacaoMaior < ps.getPontuacao()) {
					pontuacaoMaior = ps.getPontuacao();
				}
			}
		}

		for (Equipa e : equipas) {
			if (e.getID() == equipaID) {
				if (respostasCertas == respostas.size()) {
					e.addPontuacao(ps.getPontuacao() * 2);
					System.out.println("Foi adicionado equipa" + ps.getPontuacao() * 2);
				} else {
					e.addPontuacao(pontuacaoMaior);
					System.out.println("Foi adicionado equipa" + pontuacaoMaior);
				}
				break;
			}
		}
		respostasPerguntaAtual.addAll(respostas);
		System.out.println("Respostas recebidas: " + respostasPerguntaAtual.size() + "/"
				+ (equipas.size() * jogo.getJogadoresPorEquipa()));
		if (respostasPerguntaAtual.size() >= (equipas.size() * jogo.getJogadoresPorEquipa())) { 
			terminarRonda(servidor);
		}
	}

	public int getPontuacaoDaRonda(String nome) {
		int pontos = 0;
		for (Equipa e : equipas) {
			Jogador jogador = e.getJogador(nome);
			if (jogador != null) {
				pontos = jogador.getPontuacaoDaRonda();
				break;
			}
		}
		return pontos;
	}

	public synchronized Placar getInfoPlacar(int equipaID, String nome) {

		List<String> placar = new ArrayList<>();
		int i = 1;
		for (Equipa e : getPlacar()) {
			placar.add(i + "º " + e.getID() + " -> " + e.getPontuacao() + " pontos");
			i++;
		}
		return new Placar(getPosicaoEquipa(equipaID), getPontuacaoEquipa(equipaID), getPontuacaoDaRonda(nome), placar);
	}

	public boolean iniciarJogo() {
		boolean iniciar = false;
		if (jogo.jogoCheio() == true) {
			iniciar = true;
		}
		return iniciar;
	}

	public void iniciarRonda(Servidor servidor) {
		rondaAtiva = true;
		iniciarSincronizacao();
		PerguntaCliente perguntaClienteAtual = getPerguntaAtual();
		servidor.enviarMensagens(jogo.getJogoID(), perguntaClienteAtual);
		temporizador = new Temporizador(servidor, jogo.getJogoID(), 30, new Runnable() {
			public void run() {
				terminarRonda(servidor);
			}
		});
		temporizador.start();
		PerguntaServidor ps = perguntas.get(perguntaAtual);
		if (ps.isIndividual()) {
			new Thread(() -> {
				try {
					System.out.println("A espera das respostas individuais!");
					latch.await();
					System.out.println("Wait do latch liberado!");
					terminarRonda(servidor);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println("Thread latch interrompida");
				}
			}).start();
		}
	}

	public synchronized void terminarRonda(Servidor servidor) {

		if (rondaAtiva == false) {
			return;
		} else {
			rondaAtiva = false;
			servidor.enviarPlacar(jogo.getJogoID());
			for (Equipa e : equipas) {
				for (Jogador j : e.getJogadores()) {
					j.resetarPontuacaoDaRonda();
				}
			}
			respostasPerguntaAtual.clear();
			new Thread(() -> {
				try {
					System.out.println("Antes do sleep");
					Thread.sleep(3000);
					System.out.println("Depois do sleep");
					temporizador.terminarTemporizador();
					perguntaSeguinte();
					if (perguntaAtual < perguntas.size()) {
						System.out.println("Iniciar Ronda!");
						iniciarRonda(servidor);
					} else {
						servidor.terminarJogo(jogo.getJogoID());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println("Thread do sleep interrompida!");
				}
			}).start();
			
		}
	}

	public synchronized void iniciarSincronizacao() {
		respostasPerguntaAtual.clear();
		PerguntaServidor pergunta = perguntas.get(perguntaAtual);
		if (pergunta.isIndividual()) {
			int numJogadores = jogo.getJogadoresPorEquipa() * jogo.getNumDeEquipas();
			latch = new CountDownLatchModified(2, 2, 30, numJogadores);
		} else {
			barreiras = new HashMap<>();
			for (Equipa equipa : equipas) {
				int numJogadores = equipa.numDejogadoresNaEquipa();
				Barreira barreira = new Barreira(numJogadores, 30);
				barreiras.put(equipa.getID(), barreira);
			}
		}
	}


}
