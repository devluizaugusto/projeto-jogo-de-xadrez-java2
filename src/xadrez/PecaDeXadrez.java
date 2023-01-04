package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca{
	
	private int contagemMovimento;
	private Cor cor;
	
	public PecaDeXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public PosicaoXadrez getPosicaoXadrez() {
		return PosicaoXadrez.fromPosicao(posicao);
	}
	
	public int getContagemMovimento() {
		return contagemMovimento;
	}
	
	public void incrementarContagemMov() {
		contagemMovimento++;
	}
	
	public void decrementarContagemMov() {
		contagemMovimento--;
	}
	
	protected boolean haAlgumaPecaOponente(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
