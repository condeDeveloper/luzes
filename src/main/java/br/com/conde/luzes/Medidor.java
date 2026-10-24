package br.com.conde.luzes;

import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="tamanhos"} (ou {@code forca},
 * {@code silenciosos}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("tamanhos")) {
            tamanhos();
        }
        if (tudo || tabela.equals("forca")) {
            forca();
        }
        if (tudo || tabela.equals("silenciosos")) {
            silenciosos();
        }
    }

    /** Quantos paineis de cada formato tem solucao, e com quantos toques. */
    public static void tamanhos() {
        System.out.println();
        System.out.println("TAMANHOS  (paineis quadrados: o posto decide tudo)");
        System.out.printf(Locale.ROOT, "%-10s %8s %8s %14s %16s %21s%n",
                "painel", "casas", "posto", "silenciosos", "com solucao", "tudo aceso (toques)");
        for (int n = 1; n <= 16; n++) {
            int casas = n * n;
            int posto = Posto.postoDe(n, n);
            int silenciosos = casas - posto;
            String toques = "-";
            if (casas <= 49) {
                Algebra algebra = new Algebra(n, n);
                int minimo = algebra.toquesMinimos(Painel.aceso(n, n));
                toques = minimo < 0 ? "nao da" : String.valueOf(minimo);
            }
            System.out.printf(Locale.ROOT, "%-10s %8d %8d %14d %16s %21s%n",
                    n + "x" + n, casas, posto, silenciosos,
                    silenciosos == 0 ? "todos" : "1 em " + (1L << silenciosos), toques);
        }
        System.out.println();
        System.out.println("a coluna dos silenciosos nao segue regra que se adivinhe: ela e");
        System.out.println("zero em dez dos dezesseis primeiros formatos, e quando nao e,");
        System.out.println("vale 4, 2, 8, 6, 4 e 8, nessa ordem, sem subir nem descer.");
        System.out.println();
    }

    /** A algebra e a perseguicao conferidas painel por painel pela forca bruta. */
    public static void forca() {
        int[][] formatos = {{2, 2}, {2, 3}, {3, 3}, {3, 4}, {4, 4}, {4, 5}, {3, 7}, {5, 5}};
        // montar a forca bruta do 5x5 custa 33 milhoes de tentativas: uma vez so
        Forca[] forcas = new Forca[formatos.length];
        for (int i = 0; i < formatos.length; i++) {
            forcas[i] = new Forca(formatos[i][0], formatos[i][1]);
        }

        System.out.println();
        System.out.println("FORCA  (todos os conjuntos de botoes, contra a algebra e a perseguicao)");
        System.out.printf(Locale.ROOT, "%-10s %12s %14s %14s %18s%n",
                "painel", "paineis", "com solucao", "pela algebra", "discordancias");
        for (int i = 0; i < formatos.length; i++) {
            Algebra algebra = new Algebra(formatos[i][0], formatos[i][1]);
            System.out.printf(Locale.ROOT, "%-10s %12d %14d %14d %18d%n",
                    formatos[i][0] + "x" + formatos[i][1], forcas[i].paineis(),
                    forcas[i].comSolucao(), algebra.paineisSoluveis(),
                    forcas[i].discordanciasCom(algebra));
        }
        System.out.println();
        System.out.println("e a perseguicao, que so experimenta 2^colunas primeiras linhas:");
        System.out.printf(Locale.ROOT, "%-10s %18s %14s %16s %16s%n",
                "painel", "primeiras linhas", "que servem", "toques minimos", "pela forca");
        for (int i = 0; i < formatos.length; i++) {
            Painel aceso = Painel.aceso(formatos[i][0], formatos[i][1]);
            Perseguicao perseguicao = new Perseguicao(formatos[i][0], formatos[i][1]);
            int servem = perseguicao.primeirasLinhasQueServem(aceso);
            System.out.printf(Locale.ROOT, "%-10s %18d %14s %16d %16d%n",
                    formatos[i][0] + "x" + formatos[i][1], 1 << formatos[i][1],
                    servem == 0 ? "nenhuma" : String.valueOf(servem),
                    perseguicao.toquesMinimos(aceso), forcas[i].toquesMinimos(aceso));
        }
        System.out.println();
    }

    /** Os conjuntos de botoes que nao mudam nada. */
    public static void silenciosos() {
        System.out.println();
        System.out.println("SILENCIOSOS  (apertar isso tudo nao muda uma luz)");
        for (int[] formato : new int[][]{{4, 4}, {5, 5}}) {
            Algebra algebra = new Algebra(formato[0], formato[1]);
            System.out.println();
            System.out.printf(Locale.ROOT, "painel %dx%d: %d padroes independentes, %d ao todo%n",
                    formato[0], formato[1], algebra.silenciosos(),
                    algebra.todosOsSilenciosos().size());
            for (long padrao : algebra.padroesSilenciosos()) {
                System.out.println();
                System.out.print(new Painel(formato[0], formato[1], padrao));
            }
        }
        System.out.println();
        System.out.println("e o que eles cobram: cada painel com solucao tem 2^k solucoes, e a");
        System.out.println("eliminacao devolve uma qualquer. Escolher a melhor economiza isto:");
        System.out.printf(Locale.ROOT, "%-10s %14s %16s %18s %16s%n",
                "painel", "com solucao", "solucoes cada", "economia media", "maior economia");
        for (int[] formato : new int[][]{{2, 3}, {4, 4}, {5, 5}}) {
            Algebra algebra = new Algebra(formato[0], formato[1]);
            int casas = formato[0] * formato[1];
            long soma = 0;
            int maior = 0;
            int quantos = 0;
            for (long luzes = 0; luzes < (1L << casas); luzes++) {
                Painel painel = new Painel(formato[0], formato[1], luzes);
                if (!algebra.temSolucaoPelaParidade(painel)) {
                    continue;
                }
                int qualquer = Long.bitCount(algebra.umaSolucao(painel).orElseThrow());
                int melhor = algebra.toquesMinimos(painel);
                soma += qualquer - melhor;
                maior = Math.max(maior, qualquer - melhor);
                quantos++;
            }
            System.out.printf(Locale.ROOT, "%-10s %14d %16d %18.2f %16d%n",
                    formato[0] + "x" + formato[1], quantos,
                    algebra.todosOsSilenciosos().size(), (double) soma / quantos, maior);
        }
        System.out.println();
        Algebra cinco = new Algebra(5, 5);
        Painel aceso = Painel.aceso(5, 5);
        System.out.println("no painel todo aceso 5x5, porem, as quatro solucoes tem o MESMO");
        System.out.printf(Locale.ROOT, "tamanho: %d toques cada uma.%n",
                cinco.toquesMinimos(aceso));
        System.out.println();
    }
}
