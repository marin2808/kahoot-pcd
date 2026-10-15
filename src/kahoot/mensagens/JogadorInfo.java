package kahoot.mensagens;

import java.io.Serializable;

public class JogadorInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nome;
	private int equipaID;
	private int jogoID;
	
	public JogadorInfo(String nome, int equipaID, int jogoID) {
		this.nome = nome;
		this.equipaID = equipaID;
		this.jogoID = jogoID;
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getEquipaID() {
		return equipaID;
	}
	
	public int getJogoID() {
		return jogoID;
	}

}
