package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("O posto do formato")
class PostoTest {

    @ParameterizedTest(name = "{0}x{0} tem {1} padroes silenciosos")
    @CsvSource({
            " 1, 0", " 2, 0", " 3, 0", " 4, 4", " 5, 2", " 6, 0", " 7, 0", " 8, 0",
            " 9, 8", "10, 0", "11, 6", "12, 0", "13, 0", "14, 4", "15, 0", "16, 8",
    })
    @DisplayName("a sequencia nao sobe nem desce: 4, 2, 8, 6, 4, 8 entre dez zeros")
    void aSequencia(int lado, int silenciosos) {
        assertThat(Posto.silenciososDe(lado, lado)).isEqualTo(silenciosos);
    }

    @Test
    @DisplayName("o posto mais os silenciosos dao o numero de casas")
    void postoMaisSilenciosos() {
        for (int lado = 1; lado <= 10; lado++) {
            assertThat(Posto.postoDe(lado, lado) + Posto.silenciososDe(lado, lado))
                    .isEqualTo(lado * lado);
        }
    }

    @Test
    @DisplayName("ele concorda com a algebra em todo formato que cabe num long")
    void concordaComAAlgebra() {
        for (int linhas = 1; linhas <= 6; linhas++) {
            for (int colunas = 1; colunas <= 6; colunas++) {
                Algebra algebra = new Algebra(linhas, colunas);
                assertThat(Posto.postoDe(linhas, colunas))
                        .as("painel %dx%d", linhas, colunas)
                        .isEqualTo(algebra.posto());
            }
        }
    }

    @Test
    @DisplayName("na faixa de uma linha ha regra: o padrao silencioso aparece de tres em tres")
    void aFaixaTemRegra() {
        for (int colunas = 1; colunas <= 30; colunas++) {
            assertThat(Posto.silenciososDe(1, colunas))
                    .as("faixa de %d casas", colunas)
                    .isEqualTo(colunas % 3 == 2 ? 1 : 0);
        }
    }

    @Test
    @DisplayName("e na faixa de duas linhas tambem, com periodo quatro")
    void aFaixaDeDuasLinhas() {
        int[] esperado = {1, 0, 2, 0};

        for (int colunas = 1; colunas <= 24; colunas++) {
            assertThat(Posto.silenciososDe(2, colunas))
                    .as("painel 2x%d", colunas)
                    .isEqualTo(esperado[(colunas - 1) % 4]);
        }
    }
}
