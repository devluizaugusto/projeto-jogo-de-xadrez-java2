package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Dama;
import xadrez.pecas.Peao;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor vezJogador;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaDeXadrez anPassant;
	private PecaDeXadrez promocao;

	List<Peca> pecasNoTabuleiro = new ArrayList<>();
	List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		vezJogador = Cor.BRANCO;
		initialSetup();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getVezJogador() {
		return vezJogador;
	}

	public boolean getXeque() {
		return xeque;
	}

	public boolean getXequeMate() {
		return xequeMate;
	}

	public PecaDeXadrez getAnPassant() {
		return anPassant;
	}

	public PecaDeXadrez getPromocao() {
		return promocao;
	}

	public PecaDeXadrez[][] getPecas() {
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}

		return mat;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private void localNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.localPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void initialSetup() {
		// PEÇAS BRANCAS
		localNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		localNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		localNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		localNovaPeca('d', 1, new Dama(tabuleiro, Cor.BRANCO));
		localNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		localNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		localNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		localNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		localNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

		// PEÇAS PRETAS
		localNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		localNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		localNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		localNovaPeca('d', 8, new Dama(tabuleiro, Cor.PRETO));
		localNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		localNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		localNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		localNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		localNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		localNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));

	}

	public boolean[][] possiveisMovimentos(PosicaoXadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaDeXadrez performanceMovimentoPeca(PosicaoXadrez posicaoOrigem, PosicaoXadrez posicaoDestino) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao destino = posicaoDestino.toPosicao();
		validarPosicaoOrigem(origem);
		validarPosicaoDestino(origem, destino);
		Peca capturarPeca = fazerMovimento(origem, destino);

		if (testarXeque(vezJogador)) {
			desfazerMovimento(origem, destino, capturarPeca);
			throw new ExcecaoXadrez("VOCE NAO PODE SE COLOCAR EM XEQUE!");
		}

		PecaDeXadrez moverPeca = (PecaDeXadrez) tabuleiro.peca(destino);

		// #JOGADA ESPECIAL PROMOÇÃO
		promocao = null;
		if (moverPeca instanceof Peao) {
			if ((moverPeca.getCor() == Cor.BRANCO && destino.getLinha() == 0)
					|| (moverPeca.getCor() == Cor.PRETO && destino.getLinha() == 7)) {
				promocao = (PecaDeXadrez) tabuleiro.peca(destino);
				promocao = trocaPecaPromocao("D");
			}
		}

		xeque = (testarXeque(oponente(vezJogador))) ? true : false;

		if (testarXequeMate(oponente(vezJogador))) {
			xequeMate = true;
		} else {
			proximoTurno();
		}

		// MOVIMENTO ESPECIAL ANPASSANT
		if (moverPeca instanceof Peao
				&& (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			anPassant = moverPeca;
		} else {
			anPassant = null;
		}
		return (PecaDeXadrez) capturarPeca;
	}

	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removerPeca(origem);
		p.incrementarContagemMov();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.localPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		// MOVIMENTO ESPECIAL ANPASSANT
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);

			}
		}
		// #MOVIMENTO ESPECIAL ROOK
		// ROOK PEQUENO
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez rook = (PecaDeXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.localPeca(rook, destinoT);
			rook.incrementarContagemMov();
		}

		// ROOK GRANDE
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez rook = (PecaDeXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.localPeca(rook, destinoT);
			rook.incrementarContagemMov();
		}
		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removerPeca(destino);
		p.decrementarContagemMov();
		tabuleiro.localPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.localPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// MOVIMENTO ESPECIAL ANPASSANT
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == anPassant) {
				PecaDeXadrez peao = (PecaDeXadrez) tabuleiro.removerPeca(destino);
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.localPeca(peao, posicaoPeao);
			}
		}

		// #MOVIMENTO ESPECIAL ROOK
		// ROOK PEQUENO
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez rook = (PecaDeXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.localPeca(rook, origemT);
			rook.decrementarContagemMov();
		}

		// ROOK GRANDE
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez rook = (PecaDeXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.localPeca(rook, origemT);
			rook.decrementarContagemMov();
		}
	}

	private void validarPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.existePeca(posicao)) {
			throw new ExcecaoXadrez("NAO HA PECA NA POSICAO DE ORIGEM!");
		}

		if (vezJogador != ((PecaDeXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new ExcecaoXadrez("A PECA ESCOLHIDA NAO E SUA!");
		}

		if (!tabuleiro.peca(posicao).haAlgumMovimentoPossivel()) {
			throw new ExcecaoXadrez("NAO HA MOVIMENTOS POSSIVEIS PARA A PECA ESCOLHIDA!");
		}
	}

	private void validarPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentosPossiveis(destino)) {
			throw new ExcecaoXadrez("HA PECA ESCOLHIDA NAO PODE SE MOVER PARA POSICAO DE DESTINO!");
		}
	}

	public void proximoTurno() {
		turno++;
		vezJogador = (vezJogador == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaDeXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaDeXadrez) p;
			}
		}
		throw new IllegalStateException("NÃO HA NENHUM REI DA COR " + cor + " NO TABULEIRO!");
	}

	private boolean testarXeque(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoXadrez().toPosicao();
		List<Peca> pecaOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecaOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testarXequeMate(Cor cor) {
		if (!testarXeque(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaDeXadrez) p).getPosicaoXadrez().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = fazerMovimento(origem, destino);
						boolean testXeque = testarXeque(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if (!testXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public PecaDeXadrez trocaPecaPromocao(String tipo) {
		if (promocao == null) {
			throw new IllegalStateException("NAO HA PECA PARA SER PROMOVIDA!");
		}
		if (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("D")) {
			return promocao;
		}

		Posicao pos = promocao.getPosicaoXadrez().toPosicao();
		Peca p = tabuleiro.removerPeca(pos);
		pecasNoTabuleiro.remove(p);

		PecaDeXadrez novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.localPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}

	private PecaDeXadrez novaPeca(String tipo, Cor cor) {
		if(tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if(tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.equals("D")) return new Dama(tabuleiro, cor);
		return new Torre(tabuleiro, cor);		
		
	}
}


