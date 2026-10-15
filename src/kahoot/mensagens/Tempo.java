package kahoot.mensagens;

import java.io.Serializable;

public class Tempo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tempo;
	
	public Tempo(int tempo) {
		this.tempo = tempo;
	}
	
	public int getTempo() {
		return tempo;
	}
}
