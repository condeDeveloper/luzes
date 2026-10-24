package br.com.conde.luzes;

/**
 * O jeito que se aprende jogando: "perseguir as luzes".
 *
 * <p>Escolhida a primeira linha de toques, o resto do painel fica decidido: em
 * cada linha seguinte, aperta-se o botao que esta logo abaixo de cada luz ainda
 * acesa, porque e a ultima chance de apagar aquela luz. No fim, ou o painel
 * apagou, ou aquela primeira linha nao servia.
 *
 * <p>Isso reduz o jogo inteiro a experimentar 2^colunas primeiras linhas, em vez
 * de 2^casas conjuntos. E o numero de primeiras linhas que funcionam nao e
 * arbitrario: ele e exatamente a quantidade de padroes silenciosos do painel,
 * coisa que a {@link Algebra} calcula por outro caminho.
 */
public final class Perseguicao {

    private final int linhas;
    private final int colunas;

    public Perseguicao(int linhas, int colunas) {
        if (colunas > 24) {
            throw new IllegalArgumentException("primeiras linhas demais para experimentar");
        }
        this.linhas = linhas;
        this.colunas = colunas;
    }

    /** O conjunto de botoes que a perseguicao monta a partir desta primeira linha. */
    public long completando(Painel painel, long primeiraLinha) {
        Painel atual = painel;
        long botoes = 0;
        for (int coluna = 0; coluna < colunas; coluna++) {
            if ((primeiraLinha >>> coluna & 1) == 1) {
                atual = atual.apertando(coluna);
                botoes |= 1L << coluna;
            }
        }
        for (int linha = 1; linha < linhas; linha++) {
            for (int coluna = 0; coluna < colunas; coluna++) {
                int acima = (linha - 1) * colunas + coluna;
                if (atual.acesa(acima)) {
                    int casa = linha * colunas + coluna;
                    atual = atual.apertando(casa);
                    botoes |= 1L << casa;
                }
            }
        }
        return atual.tudoApagado() ? botoes : -1;
    }

    /** Quantas primeiras linhas, das 2^colunas, levam o painel ao apagado. */
    public int primeirasLinhasQueServem(Painel painel) {
        int quantas = 0;
        for (long primeira = 0; primeira < (1L << colunas); primeira++) {
            if (completando(painel, primeira) != -1) {
                quantas++;
            }
        }
        return quantas;
    }

    /** O menor numero de toques entre todas as primeiras linhas que servem. */
    public int toquesMinimos(Painel painel) {
        int melhor = -1;
        for (long primeira = 0; primeira < (1L << colunas); primeira++) {
            long botoes = completando(painel, primeira);
            if (botoes == -1) {
                continue;
            }
            int toques = Long.bitCount(botoes);
            if (melhor == -1 || toques < melhor) {
                melhor = toques;
            }
        }
        return melhor;
    }
}
