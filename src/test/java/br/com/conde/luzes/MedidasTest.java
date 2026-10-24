package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Prende os numeros que estao escritos no README. Se uma conta mudar, um destes
 * testes quebra e o texto tem que ser reescrito junto.
 */
@DisplayName("Os numeros do README")
class MedidasTest {

    @ParameterizedTest(name = "{0}x{0} todo aceso: {1} toques")
    @CsvSource({"1, 1", "2, 4", "3, 5", "4, 4", "5, 15", "6, 28", "7, 33"})
    @DisplayName("tabela TAMANHOS: o custo do painel todo aceso nao cresce com o painel")
    void tabelaTamanhos(int lado, int toques) {
        assertThat(new Algebra(lado, lado).toquesMinimos(Painel.aceso(lado, lado)))
                .isEqualTo(toques);
    }

    @Test
    @DisplayName("o 4x4 todo aceso custa 4 toques, menos que o 3x3, que custa 5")
    void oQuatroCustaMenosQueOTres() {
        assertThat(new Algebra(4, 4).toquesMinimos(Painel.aceso(4, 4)))
                .isLessThan(new Algebra(3, 3).toquesMinimos(Painel.aceso(3, 3)));
    }

    @ParameterizedTest(name = "{0}x{1}: {2} com solucao, economia media {3}, maior {4}")
    @CsvSource({
            "2, 3,      16, 0.38,  2",
            "4, 4,    4096, 1.57,  8",
            "5, 5, 8388608, 1.60, 14",
    })
    @DisplayName("tabela SILENCIOSOS: o que custa pegar a primeira solucao em vez da melhor")
    void tabelaSilenciosos(int linhas, int colunas, int comSolucao, double media, int maior) {
        Algebra algebra = new Algebra(linhas, colunas);
        long soma = 0;
        int maiorVisto = 0;
        int quantos = 0;

        for (long luzes = 0; luzes < (1L << (linhas * colunas)); luzes++) {
            Painel painel = new Painel(linhas, colunas, luzes);
            if (!algebra.temSolucaoPelaParidade(painel)) {
                continue;
            }
            int desperdicio = Long.bitCount(algebra.umaSolucao(painel).orElseThrow())
                    - algebra.toquesMinimos(painel);
            soma += desperdicio;
            maiorVisto = Math.max(maiorVisto, desperdicio);
            quantos++;
        }

        assertThat(quantos).isEqualTo(comSolucao);
        assertThat((double) soma / quantos).isEqualTo(media, within(0.005));
        assertThat(maiorVisto).isEqualTo(maior);
    }

    @Test
    @DisplayName("no 5x5 todo aceso as quatro solucoes tem o mesmo tamanho")
    void asQuatroSolucoesDoTudoAceso() {
        Algebra algebra = new Algebra(5, 5);
        Painel aceso = Painel.aceso(5, 5);
        long alguma = algebra.umaSolucao(aceso).orElseThrow();

        for (long silencioso : algebra.todosOsSilenciosos()) {
            assertThat(Long.bitCount(alguma ^ silencioso)).isEqualTo(15);
        }
    }

    @Test
    @DisplayName("tabela FORCA: 1 em 4 dos paineis 5x5 tem solucao")
    void tabelaForca() {
        assertThat(Forcas.cincoPorCinco().comSolucao() * 4)
                .isEqualTo(Forcas.cincoPorCinco().paineis());
        assertThat(new Algebra(5, 5).paineisSoluveis()).isEqualTo(8388608);
    }

    @Test
    @DisplayName("as tabelas rodam inteiras sem estourar")
    void asTabelasRodam() {
        Medidor.tamanhos();
    }
}
