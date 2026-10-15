package kahoot.servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import kahoot.mensagens.Resposta;

public class Barreira {
	
	private int numDeThreads;
	private int threadsAEspera;
	private List<Resposta> respostas = new ArrayList<>();
	private Temporizador temporizador;
	private int waitPeriod;
	private boolean respostasProcessadas = false;
	private boolean liberada = false;
	private Lock lock = new ReentrantLock();
	private Condition responderam = lock.newCondition();
	
	public Barreira(int numDeThreads, int waitPeriod) {
		this.numDeThreads = numDeThreads;
		this.threadsAEspera = 0;
		this.waitPeriod = waitPeriod;
		iniciarTemporizador();
	}
	
	public List<Resposta> await() throws InterruptedException {
		lock.lock();
		try {
			threadsAEspera++;
			System.out.println("Thread a espera na barreira. Total: " + threadsAEspera + "/" + numDeThreads);
			if(numDeThreads == threadsAEspera || liberada) {
				System.out.println("Espera liberada!");
				threadsAEspera = 0;
				temporizador.terminarTemporizador();
				liberada = true;
				responderam.signalAll();
				if(respostasProcessadas == false) {
					respostasProcessadas = true;
					return respostas;
				}
				return null;
			}else {
				while(threadsAEspera != 0 && liberada == false) {
					System.out.println("A espera!");
					responderam.await();
				}
				if(respostasProcessadas == false) {
					respostasProcessadas = true;
					System.out.println("Thread processar respostas!");
					return respostas;
				}
				return null;
			}
		}finally {
			lock.unlock();
		}
	}
	
	public void addResposta(Resposta resposta) {
		lock.lock();
		try {
			respostas.add(resposta);
			System.out.println("Foi adicionado a resposta a barreira!");
		}finally {
			lock.unlock();
		}	
	}
	
	private void iniciarTemporizador() {		
		temporizador = new Temporizador(waitPeriod, new Runnable() {
			public void run() {
				liberarBarreira();
			}
		});
		temporizador.start();
	}
	
	public void liberarBarreira() {
		lock.lock();
		try {
			System.out.println("Timeout da barreira!");
			threadsAEspera = 0;
			liberada = true;
			responderam.signalAll();
		}finally {
			lock.unlock();
		}
	}

}
