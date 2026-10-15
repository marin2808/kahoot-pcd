package kahoot.servidor;

public class CountDownLatchModified {
	
	private int count;
	private int bonusFactor;
	private int bonusCount;
	private int waitPeriod;
	private Temporizador temporizador;
	
	public CountDownLatchModified(int bonusFactor, int bonusCount, int waitPeriod, int count) {
		this.count = count;
		this.bonusFactor = bonusFactor;
		this.bonusCount = bonusCount;
		this.waitPeriod = waitPeriod;
		iniciarTemporizador();
	}
	
	public synchronized void await() throws InterruptedException {
		try {
			while(count>0) {
				wait();
			}
		}finally {
			temporizador.terminarTemporizador();
		}	
	}
	
	public synchronized int countDown() {
		int bonus = 1;
		if(count>0) {
			if(bonusCount>0) {
				bonus=bonusFactor;
				bonusCount--;
			}
			count--;
			System.out.println("Diminuir countDown");
			if(count == 0) {
				notifyAll();
				System.out.println("Liberar latch");
			}
		}return bonus;
	}
	
	public synchronized void iniciarTemporizador() {
		System.out.println("Timeout do countDownLatch iniciado!");
		temporizador = new Temporizador(waitPeriod, new Runnable() {
			public void run() {
				liberarPorTimeout();
			}
		});
		temporizador.start();
	}
	
	public synchronized void liberarPorTimeout() {
		System.out.println("Timeout do countDownLatch finalizado!");
		count = 0;
		notifyAll();
	}
	

}
