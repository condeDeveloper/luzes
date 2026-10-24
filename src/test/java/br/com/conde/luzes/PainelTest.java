package br.com.conde.luzes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("O painel de luzes")
class PainelTest {

    @Test
    @DisplayName("o botao inverte a luz dele e a dos vizinhos de lado")
    void oEfeitoDoBotao() {
        Painel meio = Painel.apagado(3, 3).apertando(4);

        assertThat(meio).hasToString("""
                . # .
                # # #
                . # .
                """);
    }

    @Test
    @DisplayName("no canto o botao pega tres luzes, na borda quatro, no meio cinco")
    void quantasLuzesCadaBotaoPega() {
        assertThat(Painel.apagado(3, 3).apertando(0).quantasAcesas()).isEqualTo(3);
        assertThat(Painel.apagado(3, 3).apertando(1).quantasAcesas()).isEqualTo(4);
        assertThat(Painel.apagado(3, 3).apertando(4).quantasAcesas()).isEqualTo(5);
    }

    @Test
    @DisplayName("apertar o mesmo botao duas vezes nao faz nada")
    void duasVezesNaoFazNada() {
        Painel painel = Painel.de(3, 3, "#.#.#.#.#");

        for (int casa = 0; casa < 9; casa++) {
            assertThat(painel.apertando(casa).apertando(casa)).isEqualTo(painel);
        }
    }

    @Test
    @DisplayName("a ordem dos toques nao muda o resultado")
    void aOrdemNaoImporta() {
        Painel painel = Painel.de(3, 3, "##...#..#");

        for (int um = 0; um < 9; um++) {
            for (int outro = 0; outro < 9; outro++) {
                assertThat(painel.apertando(um).apertando(outro))
                        .as("apertando %d e %d", um, outro)
                        .isEqualTo(painel.apertando(outro).apertando(um));
            }
        }
    }

    @Test
    @DisplayName("apertar um conjunto e o mesmo que apertar um por um")
    void conjuntoDeBotoes() {
        Painel painel = Painel.apagado(3, 3);

        assertThat(painel.apertando(0b000010011L))
                .isEqualTo(painel.apertando(0).apertando(1).apertando(4));
    }

    @Test
    @DisplayName("o desenho vai e volta")
    void desenho() {
        assertThat(Painel.de(2, 3, "#.# .##")).hasToString("""
                # . #
                . # #
                """);
        assertThat(Painel.aceso(2, 2).quantasAcesas()).isEqualTo(4);
        assertThat(Painel.apagado(2, 2).tudoApagado()).isTrue();
    }

    @Test
    @DisplayName("painel grande demais, luz fora da grade e desenho torto nao viram painel")
    void entradaInvalida() {
        assertThatThrownBy(() -> new Painel(8, 8, 0L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("long");
        assertThatThrownBy(() -> new Painel(2, 2, 0b10000L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("fora");
        assertThatThrownBy(() -> Painel.de(2, 2, "###"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Painel.apagado(2, 2).apertando(4))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("fora do painel");
    }
}
