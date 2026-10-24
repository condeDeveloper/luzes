package br.com.conde.luzes;

/**
 * Montar a forca bruta do 5x5 sao 33.554.432 tentativas. Os testes que precisam
 * dela usam a mesma, senao o CI passa o tempo refazendo o mesmo trabalho.
 */
final class Forcas {

    private static Forca doCincoPorCinco;

    private Forcas() {
    }

    static synchronized Forca cincoPorCinco() {
        if (doCincoPorCinco == null) {
            doCincoPorCinco = new Forca(5, 5);
        }
        return doCincoPorCinco;
    }
}
