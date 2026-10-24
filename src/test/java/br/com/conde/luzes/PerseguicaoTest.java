package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A perseguicao das luzes")
class PerseguicaoTest {

    @ParameterizedTest(name = "{0}x{1}")
    @CsvSource({"2, 2", "2, 3", "3, 3", "3, 4", "4, 4", "4, 5"})
    @DisplayName("o numero de primeiras linhas que servem e sempre 2^silenciosos, ou zero")
    void quantasPrimeirasLinhasServem(int linhas, int colunas) {
        Algebra algebra = new Algebra(linhas, colunas);
        Perseguicao perseguicao = new Perseguicao(linhas, colunas);
        int esperado = 1 << algebra.silenciosos();

        for (long luzes = 0; luzes < (1L << (linhas * colunas)); luzes += 7) {
            Painel painel = new Painel(linhas, colunas, luzes);
            int servem = perseguicao.primeirasLinhasQueServem(painel);

            assertThat(servem)
                    .as("painel %d:%n%s", luzes, painel)
                    .isEqualTo(algebra.temSolucaoPelaParidade(painel) ? esperado : 0);
        }
    }

    @ParameterizedTest(name = "{0}x{1}")
    @CsvSource({"2, 2", "2, 3", "3, 3", "3, 4", "4, 4"})
    @DisplayName("ela acha o mesmo minimo que a algebra, painel por painel")
    void oMesmoMinimo(int linhas, int colunas) {
        Algebra algebra = new Algebra(linhas, colunas);
        Perseguicao perseguicao = new Perseguicao(linhas, colunas);

        for (long luzes = 0; luzes < (1L << (linhas * colunas)); luzes++) {
            Painel painel = new Painel(linhas, colunas, luzes);

            assertThat(perseguicao.toquesMinimos(painel))
                    .as("painel %d:%n%s", luzes, painel)
                    .isEqualTo(algebra.toquesMinimos(painel));
        }
    }

    @Test
    @DisplayName("o conjunto que ela monta apaga o painel de verdade")
    void oConjuntoApaga() {
        Perseguicao perseguicao = new Perseguicao(4, 4);

        for (long luzes = 0; luzes < (1L << 16); luzes += 11) {
            Painel painel = new Painel(4, 4, luzes);
            for (long primeira = 0; primeira < 16; primeira++) {
                long botoes = perseguicao.completando(painel, primeira);
                if (botoes != -1) {
                    assertThat(painel.apertando(botoes).tudoApagado()).isTrue();
                }
            }
        }
    }

    @Test
    @DisplayName("no 5x5 todo aceso, quatro das 32 primeiras linhas servem")
    void noCincoPorCinco() {
        Perseguicao perseguicao = new Perseguicao(5, 5);

        assertThat(perseguicao.primeirasLinhasQueServem(Painel.aceso(5, 5))).isEqualTo(4);
        assertThat(perseguicao.toquesMinimos(Painel.aceso(5, 5))).isEqualTo(15);
    }

    @Test
    @DisplayName("primeira linha que nao serve e dita como nao serve, nao como solucao")
    void primeiraLinhaQueNaoServe() {
        Perseguicao perseguicao = new Perseguicao(2, 3);
        Painel impossivel = new Painel(2, 3, 1L);

        for (long primeira = 0; primeira < 8; primeira++) {
            assertThat(perseguicao.completando(impossivel, primeira)).isEqualTo(-1);
        }
        assertThat(perseguicao.toquesMinimos(impossivel)).isEqualTo(-1);
    }
}
