package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A forca bruta, que e o juiz")
class ForcaTest {

    @ParameterizedTest(name = "{0}x{1}: {2} paineis, {3} com solucao")
    @CsvSource({
            "2, 2,     16,     16",
            "2, 3,     64,     16",
            "3, 3,    512,    512",
            "3, 4,   4096,   4096",
            "4, 4,  65536,   4096",
            "4, 5,1048576,1048576",
    })
    @DisplayName("ela nunca discorda da algebra, painel por painel")
    void naoDiscorda(int linhas, int colunas, int paineis, int comSolucao) {
        Forca forca = new Forca(linhas, colunas);

        assertThat(forca.paineis()).isEqualTo(paineis);
        assertThat(forca.comSolucao()).isEqualTo(comSolucao);
        assertThat(forca.discordanciasCom(new Algebra(linhas, colunas))).isZero();
    }

    @Test
    @DisplayName("nem nos 33.554.432 paineis do 5x5")
    void nemNoCincoPorCinco() {
        Forca forca = Forcas.cincoPorCinco();

        assertThat(forca.paineis()).isEqualTo(33554432);
        assertThat(forca.comSolucao()).isEqualTo(8388608);
        assertThat(forca.discordanciasCom(new Algebra(5, 5))).isZero();
    }

    @Test
    @DisplayName("o painel todo aceso 5x5 custa 15 toques, pela tentativa de tudo")
    void tudoAceso() {
        assertThat(Forcas.cincoPorCinco().toquesMinimos(Painel.aceso(5, 5))).isEqualTo(15);
    }

    @Test
    @DisplayName("painel sem solucao devolve -1, e ela sabe disso sem teoria nenhuma")
    void semSolucao() {
        Forca forca = new Forca(2, 3);
        Painel impossivel = new Painel(2, 3, 1L);

        assertThat(forca.temSolucao(impossivel)).isFalse();
        assertThat(forca.toquesMinimos(impossivel)).isEqualTo(-1);
    }

    @Test
    @DisplayName("acima de 25 casas ela desiste, e diz por que")
    void grandeDemais() {
        assertThatThrownBy(() -> new Forca(5, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tentativas");
    }

    @Test
    @DisplayName("a forca bruta de um formato nao serve para outro")
    void formatoErrado() {
        Forca forca = new Forca(2, 2);

        assertThatThrownBy(() -> forca.temSolucao(Painel.apagado(3, 3)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("2x2");
    }
}
