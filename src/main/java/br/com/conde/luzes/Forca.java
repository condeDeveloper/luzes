package br.com.conde.luzes;

/**
 * O juiz que nao sabe algebra: tenta todos os conjuntos de botoes.
 *
 * <p>Sao 2^casas conjuntos. Para cada um, o painel que ele produz a partir do
 * tudo apagado, e o numero de toques. Dai sai, sem teoria nenhuma, quais
 * paineis tem solucao e com quantos toques no minimo.
 *
 * <p>So serve para painel pequeno, e e exatamente por isso que serve: e nos
 * pequenos que da para conferir se a algebra esta certa antes de confiar nela
 * nos grandes.
 */
public final class Forca {

    private static final byte IMPOSSIVEL = -1;

    private final int linhas;
    private final int colunas;
    private final byte[] toquesMinimos;
    private final int comSolucao;

    public Forca(int linhas, int colunas) {
        int casas = linhas * colunas;
        if (casas > 25) {
            throw new IllegalArgumentException(
                    "forca bruta so ate 25 casas: " + casas + " dariam " + (1L << casas)
                            + " tentativas");
        }
        this.linhas = linhas;
        this.colunas = colunas;
        this.toquesMinimos = new byte[1 << casas];
        java.util.Arrays.fill(toquesMinimos, IMPOSSIVEL);

        long[] efeito = new long[casas];
        for (int casa = 0; casa < casas; casa++) {
            efeito[casa] = Painel.efeitoDe(linhas, colunas, casa);
        }

        int achados = 0;
        for (int botoes = 0; botoes < (1 << casas); botoes++) {
            long painel = 0;
            for (int casa = 0; casa < casas; casa++) {
                if ((botoes >>> casa & 1) == 1) {
                    painel ^= efeito[casa];
                }
            }
            int toques = Integer.bitCount(botoes);
            int onde = (int) painel;
            if (toquesMinimos[onde] == IMPOSSIVEL) {
                achados++;
                toquesMinimos[onde] = (byte) toques;
            } else if (toques < toquesMinimos[onde]) {
                toquesMinimos[onde] = (byte) toques;
            }
        }
        this.comSolucao = achados;
    }

    public int paineis() {
        return toquesMinimos.length;
    }

    public int comSolucao() {
        return comSolucao;
    }

    public boolean temSolucao(Painel painel) {
        confereFormato(painel);
        return toquesMinimos[(int) painel.luzes()] != IMPOSSIVEL;
    }

    /** Toques minimos para apagar, ou -1 se nao der. */
    public int toquesMinimos(Painel painel) {
        confereFormato(painel);
        return toquesMinimos[(int) painel.luzes()];
    }

    /** Quantas vezes a algebra e a forca bruta discordam, painel por painel. */
    public int discordanciasCom(Algebra algebra) {
        int discordancias = 0;
        for (int luzes = 0; luzes < toquesMinimos.length; luzes++) {
            Painel painel = new Painel(linhas, colunas, luzes);
            if (algebra.temSolucaoPelaParidade(painel) != temSolucao(painel)
                    || algebra.toquesMinimos(painel) != toquesMinimos(painel)) {
                discordancias++;
            }
        }
        return discordancias;
    }

    private void confereFormato(Painel painel) {
        if (painel.linhas() != linhas || painel.colunas() != colunas) {
            throw new IllegalArgumentException(
                    "esta forca bruta e do painel " + linhas + "x" + colunas);
        }
    }
}
