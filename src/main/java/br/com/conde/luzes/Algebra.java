package br.com.conde.luzes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * O jogo das luzes visto como sistema linear sobre dois valores (aceso e
 * apagado, com soma sem vai-um).
 *
 * <p>Duas coisas tornam isso possivel: apertar um botao duas vezes nao faz nada,
 * e a ordem dos toques nao importa. Entao uma jogada nao e uma sequencia, e um
 * CONJUNTO de botoes, e o painel final e a soma dos efeitos. Resolver e achar o
 * conjunto; dizer se da para resolver e perguntar se o painel esta no alcance.
 *
 * <p>A eliminacao e feita uma vez por formato de painel e guarda, de brinde, os
 * <b>padroes silenciosos</b>: conjuntos de botoes que, apertados, nao mudam luz
 * nenhuma. Sao eles que fazem o jogo ter solucoes diferentes, e sao eles que
 * dizem quais paineis nao tem solucao nenhuma.
 */
public final class Algebra {

    private final int linhas;
    private final int colunas;
    private final int casas;
    private final long[] toquesDaLinha;
    private final int[] pivoDaLinha;
    private final int posto;
    private final List<Long> silenciosos;

    public Algebra(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.casas = linhas * colunas;
        this.toquesDaLinha = new long[casas];
        this.pivoDaLinha = new int[casas];

        long[] equacao = new long[casas];
        long[] toques = new long[casas];
        for (int casa = 0; casa < casas; casa++) {
            equacao[casa] = Painel.efeitoDe(linhas, colunas, casa);
            toques[casa] = 1L << casa;          // de quais botoes esta equacao veio
        }

        int quantas = 0;
        int[] deQualLinha = new int[casas];
        boolean[] usada = new boolean[casas];
        for (int coluna = 0; coluna < casas; coluna++) {
            int escolhida = -1;
            for (int i = 0; i < casas; i++) {
                if (!usada[i] && (equacao[i] >>> coluna & 1) == 1) {
                    escolhida = i;
                    break;
                }
            }
            if (escolhida < 0) {
                continue;
            }
            usada[escolhida] = true;
            for (int i = 0; i < casas; i++) {
                if (i != escolhida && (equacao[i] >>> coluna & 1) == 1) {
                    equacao[i] ^= equacao[escolhida];
                    toques[i] ^= toques[escolhida];
                }
            }
            deQualLinha[quantas] = escolhida;
            pivoDaLinha[quantas] = coluna;
            quantas++;
        }
        this.posto = quantas;
        // as linhas guardadas continuaram sendo mexidas depois de escolhidas:
        // o que vale e como elas ficaram no fim, nao como estavam na hora
        for (int i = 0; i < quantas; i++) {
            toquesDaLinha[i] = toques[deQualLinha[i]];
        }

        List<Long> mudos = new ArrayList<>();
        for (int i = 0; i < casas; i++) {
            if (equacao[i] == 0 && toques[i] != 0) {
                mudos.add(toques[i]);           // apertar isso nao mexe em luz nenhuma
            }
        }
        this.silenciosos = List.copyOf(mudos);
    }

    public int casas() {
        return casas;
    }

    /** Quantas das equacoes sao independentes. */
    public int posto() {
        return posto;
    }

    /** Quantos padroes silenciosos independentes existem. */
    public int silenciosos() {
        return silenciosos.size();
    }

    /** Os conjuntos de botoes que nao mudam nada, um bit por casa. */
    public List<Long> padroesSilenciosos() {
        return silenciosos;
    }

    /** Todos os padroes silenciosos, inclusive o de nao apertar nada: 2^k deles. */
    public List<Long> todosOsSilenciosos() {
        List<Long> todos = new ArrayList<>();
        for (int combinacao = 0; combinacao < (1 << silenciosos.size()); combinacao++) {
            long junto = 0;
            for (int i = 0; i < silenciosos.size(); i++) {
                if ((combinacao >>> i & 1) == 1) {
                    junto ^= silenciosos.get(i);
                }
            }
            todos.add(junto);
        }
        return todos;
    }

    /** Dos 2^casas paineis possiveis, a fracao que tem solucao: 1 em 2^k. */
    public long paineisSoluveis() {
        return 1L << posto;
    }

    /**
     * Um painel tem solucao se, para todo padrao silencioso, o numero de luzes
     * acesas em cima dele for par. E o mesmo que dizer que o painel esta no
     * alcance dos botoes, mas essa forma nao precisa resolver nada.
     */
    public boolean temSolucaoPelaParidade(Painel painel) {
        for (long silencioso : silenciosos) {
            if (Long.bitCount(painel.luzes() & silencioso) % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    /** Um conjunto de botoes que apaga o painel, se existir. */
    public Optional<Long> umaSolucao(Painel painel) {
        confereFormato(painel);
        if (!temSolucaoPelaParidade(painel)) {
            return Optional.empty();
        }
        // cada linha que sobrou da eliminacao fixa um botao; os livres ficam de fora
        long botoes = 0;
        for (int i = 0; i < posto; i++) {
            if (Long.bitCount(toquesDaLinha[i] & painel.luzes()) % 2 == 1) {
                botoes |= 1L << pivoDaLinha[i];
            }
        }
        if (!painel.apertando(botoes).tudoApagado()) {
            throw new IllegalStateException("a eliminacao devolveu um conjunto que nao apaga");
        }
        return Optional.of(botoes);
    }

    /** A solucao com menos toques, somando cada padrao silencioso a uma solucao qualquer. */
    public Optional<Long> solucaoMinima(Painel painel) {
        Optional<Long> alguma = umaSolucao(painel);
        if (alguma.isEmpty()) {
            return Optional.empty();
        }
        long melhor = alguma.get();
        for (long silencioso : todosOsSilenciosos()) {
            long candidata = alguma.get() ^ silencioso;
            if (Long.bitCount(candidata) < Long.bitCount(melhor)) {
                melhor = candidata;
            }
        }
        return Optional.of(melhor);
    }

    /** Quantos toques, no minimo, apagam o painel. */
    public int toquesMinimos(Painel painel) {
        return solucaoMinima(painel).map(Long::bitCount).orElse(-1);
    }

    private void confereFormato(Painel painel) {
        if (painel.linhas() != linhas || painel.colunas() != colunas) {
            throw new IllegalArgumentException(
                    "esta algebra e do painel " + linhas + "x" + colunas);
        }
    }
}
