package br.com.conde.luzes;

/**
 * Um painel de luzes acesas e apagadas.
 *
 * <p>Cada casa e um botao. Apertar um botao inverte a luz dele e a dos vizinhos
 * de cima, de baixo, da esquerda e da direita. O objetivo e apagar tudo.
 *
 * <p>O estado cabe num long: sao no maximo 64 casas, e cada bit e uma luz. Essa
 * escolha nao e enfeite, e o que torna possivel varrer painel inteiro por forca
 * bruta e comparar com a algebra.
 */
public record Painel(int linhas, int colunas, long luzes) {

    public Painel {
        if (linhas < 1 || colunas < 1) {
            throw new IllegalArgumentException("o painel precisa ter pelo menos uma casa");
        }
        if ((long) linhas * colunas > 62) {
            throw new IllegalArgumentException("acima de 62 casas o estado nao cabe num long");
        }
        long fora = luzes >>> (linhas * colunas);
        if (fora != 0) {
            throw new IllegalArgumentException("ha luz acesa fora do painel");
        }
    }

    public static Painel apagado(int linhas, int colunas) {
        return new Painel(linhas, colunas, 0L);
    }

    public static Painel aceso(int linhas, int colunas) {
        return new Painel(linhas, colunas, mascara(linhas * colunas));
    }

    /** Monta a partir de um desenho, com '#' para aceso e qualquer outra coisa para apagado. */
    public static Painel de(int linhas, int colunas, String desenho) {
        String limpo = desenho.replaceAll("\s", "");
        if (limpo.length() != linhas * colunas) {
            throw new IllegalArgumentException(
                    "o desenho tem " + limpo.length() + " casas, e o painel tem "
                            + (linhas * colunas));
        }
        long luzes = 0;
        for (int casa = 0; casa < limpo.length(); casa++) {
            if (limpo.charAt(casa) == '#') {
                luzes |= 1L << casa;
            }
        }
        return new Painel(linhas, colunas, luzes);
    }

    public int casas() {
        return linhas * colunas;
    }

    public boolean acesa(int casa) {
        return (luzes >>> casa & 1) == 1;
    }

    public boolean tudoApagado() {
        return luzes == 0;
    }

    public int quantasAcesas() {
        return Long.bitCount(luzes);
    }

    /** O painel depois de apertar o botao desta casa. */
    public Painel apertando(int casa) {
        if (casa < 0 || casa >= casas()) {
            throw new IllegalArgumentException("casa fora do painel: " + casa);
        }
        return new Painel(linhas, colunas, luzes ^ efeitoDe(linhas, colunas, casa));
    }

    /** O painel depois de apertar um conjunto de botoes, um bit por casa. */
    public Painel apertando(long botoes) {
        Painel atual = this;
        for (int casa = 0; casa < casas(); casa++) {
            if ((botoes >>> casa & 1) == 1) {
                atual = atual.apertando(casa);
            }
        }
        return atual;
    }

    /** Quais luzes um botao inverte: ele mesmo e os quatro vizinhos. */
    public static long efeitoDe(int linhas, int colunas, int casa) {
        int linha = casa / colunas;
        int coluna = casa % colunas;
        long efeito = 1L << casa;
        if (linha > 0) {
            efeito |= 1L << (casa - colunas);
        }
        if (linha < linhas - 1) {
            efeito |= 1L << (casa + colunas);
        }
        if (coluna > 0) {
            efeito |= 1L << (casa - 1);
        }
        if (coluna < colunas - 1) {
            efeito |= 1L << (casa + 1);
        }
        return efeito;
    }

    public static long mascara(int casas) {
        return casas == 64 ? -1L : (1L << casas) - 1;
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int casa = 0; casa < casas(); casa++) {
            texto.append(acesa(casa) ? '#' : '.');
            texto.append((casa + 1) % colunas == 0 ? "\n" : " ");
        }
        return texto.toString();
    }
}
