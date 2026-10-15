package kahoot.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kahoot.mensagens.JogadorInfo;
import kahoot.mensagens.MensagemErro;
import kahoot.mensagens.Placar;

public class Servidor {

	public static final int PORTO = 8080;
	private List<Jogo> jogos = new ArrayList<>();
	private List<PerguntaServidor> perguntas = new ArrayList<>();
	private Map<Integer, List<DealWithClient>> clientes = new HashMap<>();

	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		Servidor servidor = new Servidor();
		new Thread(() -> {
			try {
				servidor.startServing();
			} catch (IOException e) {
				System.out.println("Erro ao começar a servir!");
			}
		}).start();
		while (true) {
			String input = scanner.nextLine().trim();
			String[] palavras = input.split(" ");
			if (palavras[0].equals("new")) {
				servidor.criarJogo(Integer.parseInt(palavras[1]), Integer.parseInt(palavras[2]),
						Integer.parseInt(palavras[3]));
			} else if (palavras[0].equals("mostrarJogos")) {
				servidor.mostrarJogos();
			}
		}
	}

	public void startServing() throws IOException {
		ServerSocket ss = new ServerSocket(PORTO);
		try {
			while (true) {
				Socket socket = ss.accept();
				DealWithClient client = new DealWithClient(socket, this);
				client.start();
			}
		} finally {
			System.out.println("Servidor foi encerrado!");
			ss.close();
		}

	}

	public int criarJogoID() {
		boolean existeID = true;
		int jogoID = (int) (Math.random() * 10000);
		if (!jogos.isEmpty()) {
			while (existeID == true) {
				for (Jogo jogo : jogos) {
					if (jogo.getJogoID() == jogoID) {
						existeID = true;
						break;
					} else {
						existeID = false;
					}
				}
				jogoID = (int) (Math.random() * 10000);
			}
		}
		return jogoID;
	}

	public void criarPerguntas() {
		perguntas = LeitorJSON.lerJson("perguntas.json");
	}

	public List<PerguntaServidor> escolherPerguntasAleatorias(int numDePerguntas) {
		List<PerguntaServidor> copia = new ArrayList<>(perguntas);
		Collections.shuffle(copia);
		for(int i=0; i<copia.size(); i++) {
			if((i%2)==0) {
				copia.get(i).setIndividual(true);
			}
		}
		return copia.subList(0, numDePerguntas);
	}

	public synchronized void criarJogo(int numDeEquipas, int jogadoresPorEquipa, int numDePerguntas) {
		int jogoID = criarJogoID();
		criarPerguntas();
		List<PerguntaServidor> perguntasAleatorias = escolherPerguntasAleatorias(numDePerguntas);
		Jogo jogo = new Jogo(jogoID, numDeEquipas, jogadoresPorEquipa, perguntasAleatorias);
		jogos.add(jogo);
		System.out.println("Jogo criado com ID: " + jogoID + "!");
	}

	public synchronized Jogo getJogoPorID(int jogoID) {
		Jogo jogo = null;
		for(Jogo j : jogos) {
			if(j.getJogoID() == jogoID) {
				jogo = j;
				break;
			}
		}
		return jogo;
	}

	public synchronized void mostrarJogos() {

		if (jogos.isEmpty()) {
			System.out.println("Não existem jogos!");
			return;
		}
		for (Jogo jogo : jogos) {
			jogo.mostrarJogo();
		}
	}

	public synchronized void addClienteAoJogo(int jogoID, DealWithClient cliente) {																	
		clientes.putIfAbsent(jogoID, new ArrayList<>());
		clientes.get(jogoID).add(cliente);

	}

	public synchronized void enviarMensagens(int jogoID, Object obj) {
		List<DealWithClient> lista = clientes.get(jogoID);
		if(lista!=null) {
			for(DealWithClient cliente : lista) {
				cliente.enviarMensagen(obj);
			}
		}
	}
	
	public synchronized void enviarPlacar(int jogoID) {
		Jogo jogo = getJogoPorID(jogoID);
		if(jogo!=null) {
			GameState gs = jogo.getGameState();
			List<DealWithClient> lista = clientes.get(jogoID);	
			for(DealWithClient clientes : lista) {
				JogadorInfo jogador = clientes.getJogadorInfo();
				int equipaID = jogador.getEquipaID();
				Placar placar = gs.getInfoPlacar(equipaID, jogador.getNome());
				clientes.enviarMensagen(placar);
			}
		}
		
	}
	
	public synchronized void terminarJogo(int jogoID) {
		List<DealWithClient> lista = clientes.get(jogoID);
		
		if(lista!=null) {
			for(DealWithClient cliente : lista) {
				cliente.enviarMensagen(new MensagemErro("Jogo acabou pois alguem se desconectou!"));
				cliente.interrupt();
			}
			System.out.println("Jogo acabou!!");
			clientes.remove(jogoID);
			jogos.removeIf(j->j.getJogoID() == jogoID);
		}
	}
	
}
