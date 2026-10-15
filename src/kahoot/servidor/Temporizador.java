package kahoot.servidor;

import kahoot.mensagens.Tempo;

public class Temporizador extends Thread {

	private Servidor servidor;
	private int jogoID;
	private int tempo;
	private boolean terminarTemporizador = false;
	private Runnable timeout;
	private boolean mostrarTempo;

	public Temporizador(Servidor servidor, int jogoID, int tempo, Runnable timeout) {
		this.servidor = servidor;
		this.jogoID = jogoID;
		this.tempo = tempo;
		this.timeout = timeout;
		this.mostrarTempo = true;
	}

	public Temporizador(int tempo, Runnable timeout) {
		this.tempo = tempo;
		this.timeout = timeout;
		this.mostrarTempo = false;
	}

	public void run() {
		try {
			for (int i = tempo; i >= 0; i--) {
				if (terminarTemporizador) {
					return;
				}
				if (mostrarTempo) {
					servidor.enviarMensagens(jogoID, new Tempo(i));
				}
				Thread.sleep(1000);
			}
			if (timeout != null) {
				timeout.run();
			}
		} catch (InterruptedException e) {
			System.out.println("Temporizador foi interrompido");
			interrupt();
		}
	}

	public void terminarTemporizador() {
		terminarTemporizador = true;
		interrupt();
	}

}
