package br.com.conde.luzes;

/**
 * Só o posto, para painel grande demais para caber num long.
 *
 * <p>A {@link Algebra} guarda o painel inteiro num long e por isso para em 62
 * casas. Mas a pergunta "quantas solucoes esse formato tem" depende apenas do
 * posto da matriz, e isso da para calcular com as linhas em vetores de bits,
 * sem nunca montar um painel.
 *
 * <p>Serve para olhar a sequencia dos formatos de longe, que e onde fica
 * evidente que ela nao segue regra nenhuma que se adivinhe.
 */
public final class Posto {

    private Posto() {
    }

    /** Quantos padroes silenciosos independentes o formato tem. */
    public static int silenciososDe(int linhas, int colunas) {
        return linhas * colunas - postoDe(linhas, colunas);
    }

    /** Quantas das equacoes do formato sao independentes. */
    public static int postoDe(int linhas, int colunas) {
        int casas = linhas * colunas;
        if (casas < 1) {
            throw new IllegalArgumentException("o painel precisa ter pelo menos uma casa");
        }
        int palavras = (casas + 63) / 64;
        long[][] equacao = new long[casas][palavras];
        for (int casa = 0; casa < casas; casa++) {
            int linha = casa / colunas;
            int coluna = casa % colunas;
            liga(equacao[casa], casa);
            if (linha > 0) {
                liga(equacao[casa], casa - colunas);
            }
            if (linha < linhas - 1) {
                liga(equacao[casa], casa + colunas);
            }
            if (coluna > 0) {
                liga(equacao[casa], casa - 1);
            }
            if (coluna < colunas - 1) {
                liga(equacao[casa], casa + 1);
            }
        }

        int posto = 0;
        for (int coluna = 0; coluna < casas && posto < casas; coluna++) {
            int escolhida = -1;
            for (int i = posto; i < casas; i++) {
                if (ligada(equacao[i], coluna)) {
                    escolhida = i;
                    break;
                }
            }
            if (escolhida < 0) {
                continue;
            }
            long[] guarda = equacao[posto];
            equacao[posto] = equacao[escolhida];
            equacao[escolhida] = guarda;
            for (int i = 0; i < casas; i++) {
                if (i != posto && ligada(equacao[i], coluna)) {
                    for (int palavra = 0; palavra < palavras; palavra++) {
                        equacao[i][palavra] ^= equacao[posto][palavra];
                    }
                }
            }
            posto++;
        }
        return posto;
    }

    private static void liga(long[] vetor, int bit) {
        vetor[bit / 64] |= 1L << (bit % 64);
    }

    private static boolean ligada(long[] vetor, int bit) {
        return (vetor[bit / 64] >>> (bit % 64) & 1) == 1;
    }
}
