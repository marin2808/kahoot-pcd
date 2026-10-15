package kahoot.mensagens;

import java.io.Serializable;

public class MensagemErro implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mensagem;
	
	public MensagemErro(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getMensagemErro() {
		return mensagem;
	}
}
