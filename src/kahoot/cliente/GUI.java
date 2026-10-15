package kahoot.cliente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import kahoot.mensagens.PerguntaCliente;
import kahoot.mensagens.Placar;
import kahoot.mensagens.Resposta;

public class GUI {
	
	private Cliente cliente;
	private JFrame frame;
	private JLabel tempo = new JLabel("30");
	private List<JButton> botoes = new ArrayList<>();
	private boolean respostaEnviada = false;
	
	public GUI(Cliente cliente) {
		this.cliente = cliente;
		frame = new JFrame("Kahoot");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		frame.setVisible(true);
		
	}
	
	public void esperaDeJogadores() {
		
		frame.setLayout(new BorderLayout());
		
		JPanel titulo = new JPanel();
		titulo.setLayout(new BorderLayout());
		titulo.setBackground(new Color(204,169,221));
		titulo.setPreferredSize(new Dimension(500,125));
		
		JLabel kahoot = new JLabel("Kahoot");
		kahoot.setVerticalAlignment(JLabel.CENTER);
		kahoot.setHorizontalAlignment(JLabel.CENTER);
		kahoot.setForeground(Color.white);
		kahoot.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 36));
		titulo.add(kahoot, BorderLayout.CENTER);
		frame.add(titulo, BorderLayout.NORTH);
		
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new GridLayout(1,3));
		panelInfo.setBackground(new Color(204,169,221));
		
		Font fonte = new Font("Segoe UI", Font.PLAIN, 16);
		JLabel nomeJogador = new JLabel("Nome: " + cliente.getJogador().getNome());
		nomeJogador.setVerticalAlignment(JLabel.CENTER);
		nomeJogador.setHorizontalAlignment(JLabel.CENTER);
		nomeJogador.setFont(fonte);
		JLabel equipaID = new JLabel("Equipa ID: " + Integer.toString(cliente.getJogador().getEquipaID()));
		equipaID.setVerticalAlignment(JLabel.CENTER);
		equipaID.setHorizontalAlignment(JLabel.CENTER);
		equipaID.setFont(fonte);
		JLabel jogoID = new JLabel("Jogo ID: " + Integer.toString(cliente.getJogador().getJogoID()));
		jogoID.setVerticalAlignment(JLabel.CENTER);
		jogoID.setHorizontalAlignment(JLabel.CENTER);
		jogoID.setFont(fonte);
		
		panelInfo.add(nomeJogador);
		panelInfo.add(equipaID);
		panelInfo.add(jogoID);
		frame.add(panelInfo, BorderLayout.CENTER);
		
		JPanel info = new JPanel();
		info.setLayout(new BorderLayout());
		info.setBackground(new Color(204,169,221));
		info.setPreferredSize(new Dimension(500,250));
		
		
		JLabel espera = new JLabel("A espera dos jogadores...");
		espera.setVerticalAlignment(JLabel.CENTER);
		espera.setHorizontalAlignment(JLabel.CENTER);
		espera.setFont(fonte);
		
		info.add(espera, BorderLayout.CENTER);
		frame.add(info, BorderLayout.SOUTH);
		
		frame.revalidate();
        frame.repaint();
		
		
		
	}
	
	public void mostrarPergunta(PerguntaCliente pergunta) {
		
		respostaEnviada = false;
		botoes.clear();
		
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		
		JPanel panelPergunta = new JPanel();
		panelPergunta.setLayout(new BorderLayout());
		panelPergunta.setBackground(new Color(255,255,255));
		panelPergunta.setPreferredSize(new Dimension(500,125));
		
		JLabel pegunta = new JLabel(pergunta.getPergunta());
		pegunta.setVerticalAlignment(JLabel.CENTER);
		pegunta.setHorizontalAlignment(JLabel.CENTER);
		
		panelPergunta.add(pegunta, BorderLayout.CENTER);
		tempo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); 
		panelPergunta.add(tempo, BorderLayout.WEST);
		frame.add(panelPergunta, BorderLayout.NORTH);
		
		JPanel panelOpcoes = new JPanel();
		panelOpcoes.setLayout(new GridLayout(2,2));
		panelOpcoes.setBackground(new Color(255,255,255));
		List<String> opcoes = pergunta.getOpcoes();
		for(int i=0; i<opcoes.size(); i++) {
			JButton botao = new JButton(opcoes.get(i));
			botoes.add(botao);
			botao.setBackground(new Color(255,255,255));
			int indiceResposta = i;
			botao.addActionListener((ActionEvent e) -> {
				if(respostaEnviada == false) {
					respostaEnviada = true;
					Resposta resposta = new Resposta(indiceResposta, cliente.getJogador(), pergunta);
					cliente.enviarMensagem(resposta);
					bloquearBotoes();
					botao.setBackground(new Color(200,200,200));
				}
				
			});
			panelOpcoes.add(botao);
		}
		frame.add(panelOpcoes, BorderLayout.CENTER);
		frame.revalidate();
        frame.repaint();
	}
	
	public void mostrarPlacar(Placar infoPlacar) {
		
		List<JLabel> listaInfoEquipa = new ArrayList<>();
		
		frame.getContentPane().removeAll();
		frame.setLayout(new BorderLayout());
		
		JPanel titulo = new JPanel();
		titulo.setLayout(new BorderLayout());
		titulo.setBackground(new Color(255,255,255));
		titulo.setPreferredSize(new Dimension(500,125));
		JLabel info = new JLabel("Placar");
		info.setVerticalAlignment(JLabel.CENTER);
		info.setHorizontalAlignment(JLabel.CENTER);
		titulo.add(info, BorderLayout.CENTER);
		frame.add(titulo, BorderLayout.NORTH);
		
		JPanel infoEquipa = new JPanel();
		infoEquipa.setLayout(new GridLayout(2,3));
		infoEquipa.setBackground(new Color(255,255,255));
		infoEquipa.setPreferredSize(new Dimension(500,50));
		JLabel posicaoAtual = new JLabel("Posição atual");
		listaInfoEquipa.add(posicaoAtual);
		JLabel pontosAcumulados = new JLabel("Pontos acumulados");
		listaInfoEquipa.add(pontosAcumulados);
		JLabel pontosGanhos = new JLabel("Pontos ganhos");
		listaInfoEquipa.add(pontosGanhos);
		JLabel posicaoAtualEquipa = new JLabel(Integer.toString(infoPlacar.getPosicaoEquipa()) + "º");
		listaInfoEquipa.add(posicaoAtualEquipa);
		JLabel pontosAcumuladosEquipa = new JLabel(Integer.toString(infoPlacar.getPontuacaoEquipa()));
		listaInfoEquipa.add(pontosAcumuladosEquipa);
		JLabel pontosGanhosEquipa = new JLabel(Integer.toString(infoPlacar.getPontuacaoDaRonda()));
		listaInfoEquipa.add(pontosGanhosEquipa);
		for(JLabel l: listaInfoEquipa) {
			l.setVerticalAlignment(JLabel.CENTER);
			l.setHorizontalAlignment(JLabel.CENTER);
			infoEquipa.add(l);
		}
		frame.add(infoEquipa, BorderLayout.CENTER);
		
		JPanel ranking = new JPanel();
		ranking.setLayout(new BorderLayout());
		ranking.setBackground(new Color(255,255,255));
	
		JTextArea placar = new JTextArea();
		placar.setEditable(false);
		placar.setBackground(new Color(255,255,255));
		placar.setPreferredSize(new Dimension(500,200));
		JLabel tituloPlacar = new JLabel("Ranking: ");
		tituloPlacar.setVerticalAlignment(JLabel.CENTER);
		tituloPlacar.setHorizontalAlignment(JLabel.CENTER);
		tituloPlacar.setPreferredSize(new Dimension(500,50));
		ranking.add(tituloPlacar, BorderLayout.NORTH);
		placar.setText("\n");
		for(String linha: infoPlacar.getPlacar()) {
			placar.append(linha + "\n");
		}
		placar.setAlignmentX(Component.CENTER_ALIGNMENT);
		ranking.add(placar, BorderLayout.CENTER);
		
		frame.add(ranking, BorderLayout.SOUTH);
		frame.revalidate();
        frame.repaint();
		
	}
	
	public void atualizarTempo(int t) {
		tempo.setText(Integer.toString(t));
	}
	
	public void bloquearBotoes() {
		for(JButton botao: botoes) {
			botao.setEnabled(false);
		}
	}

}
