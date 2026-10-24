package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A algebra do painel")
class AlgebraTest {

    @ParameterizedTest(name = "{0}x{1}: posto {2}, {3} silenciosos")
    @CsvSource({
            "2, 2,  4, 0",
            "2, 3,  4, 2",
            "3, 3,  9, 0",
            "4, 4, 12, 4",
            "5, 5, 23, 2",
            "6, 6, 36, 0",
    })
    @DisplayName("o posto e os padroes silenciosos de cada formato")
    void postoESilenciosos(int linhas, int colunas, int posto, int silenciosos) {
        Algebra algebra = new Algebra(linhas, colunas);

        assertThat(algebra.posto()).isEqualTo(posto);
        assertThat(algebra.silenciosos()).isEqualTo(silenciosos);
        assertThat(algebra.todosOsSilenciosos()).hasSize(1 << silenciosos);
    }

    @Test
    @DisplayName("apertar um padrao silencioso nao muda uma luz, em painel nenhum")
    void oSilencioEhSilencio() {
        Algebra algebra = new Algebra(4, 4);

        for (long luzes = 0; luzes < (1L << 16); luzes += 37) {
            Painel painel = new Painel(4, 4, luzes);
            for (long silencioso : algebra.todosOsSilenciosos()) {
                assertThat(painel.apertando(silencioso))
                        .as("painel %d com o silencioso %d", luzes, silencioso)
                        .isEqualTo(painel);
            }
        }
    }

    @Test
    @DisplayName("a solucao apaga mesmo, nos 65.536 paineis do 4x4")
    void aSolucaoApaga() {
        Algebra algebra = new Algebra(4, 4);
        int comSolucao = 0;

        for (long luzes = 0; luzes < (1L << 16); luzes++) {
            Painel painel = new Painel(4, 4, luzes);
            var solucao = algebra.umaSolucao(painel);
            if (solucao.isEmpty()) {
                continue;
            }
            assertThat(painel.apertando(solucao.get()).tudoApagado())
                    .as("painel %d", luzes)
                    .isTrue();
            comSolucao++;
        }

        assertThat(comSolucao).isEqualTo(4096);
    }

    @Test
    @DisplayName("a menor solucao e uma das 2^k, e nenhuma outra e menor")
    void aMenorSolucao() {
        Algebra algebra = new Algebra(4, 4);

        for (long luzes = 0; luzes < (1L << 16); luzes += 13) {
            Painel painel = new Painel(4, 4, luzes);
            var alguma = algebra.umaSolucao(painel);
            if (alguma.isEmpty()) {
                continue;
            }
            long minima = algebra.solucaoMinima(painel).orElseThrow();
            assertThat(painel.apertando(minima).tudoApagado()).isTrue();
            for (long silencioso : algebra.todosOsSilenciosos()) {
                assertThat(Long.bitCount(alguma.get() ^ silencioso))
                        .isGreaterThanOrEqualTo(Long.bitCount(minima));
            }
        }
    }

    @Test
    @DisplayName("o painel todo aceso 5x5 se apaga em 15 toques")
    void tudoAcesoNoCincoPorCinco() {
        assertThat(new Algebra(5, 5).toquesMinimos(Painel.aceso(5, 5))).isEqualTo(15);
    }

    @Test
    @DisplayName("quando nao ha solucao, nao ha solucao: -1 toques e nada devolvido")
    void semSolucao() {
        Algebra algebra = new Algebra(2, 3);
        Painel impossivel = Painel.de(2, 3, "#.. ...");

        assertThat(algebra.temSolucaoPelaParidade(impossivel)).isFalse();
        assertThat(algebra.umaSolucao(impossivel)).isEmpty();
        assertThat(algebra.solucaoMinima(impossivel)).isEmpty();
        assertThat(algebra.toquesMinimos(impossivel)).isEqualTo(-1);
    }

    @Test
    @DisplayName("o painel apagado se resolve sem apertar nada")
    void jaApagado() {
        Algebra algebra = new Algebra(3, 3);

        assertThat(algebra.toquesMinimos(Painel.apagado(3, 3))).isZero();
    }

    @Test
    @DisplayName("a algebra de um formato nao serve para outro")
    void formatoErrado() {
        Algebra algebra = new Algebra(3, 3);

        assertThatThrownBy(() -> algebra.umaSolucao(Painel.apagado(2, 2)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("3x3");
    }
}
