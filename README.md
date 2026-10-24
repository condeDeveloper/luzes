# luzes

O jogo das luzes do zero em Java 21: um painel de luzes acesas, e apertar
qualquer casa inverte a luz dela e a dos quatro vizinhos. O objetivo é apagar
tudo.

Duas observações desmontam o jogo: apertar o mesmo botão duas vezes não faz
nada, e a ordem dos toques não importa. Então uma jogada não é uma sequência, é
um **conjunto** de botões, e o painel final é a soma dos efeitos. O jogo vira um
sistema linear sobre dois valores, e "tem solução?" vira "esse painel está no
alcance?".

Três métodos resolvem o mesmo jogo aqui, de propósito: a **álgebra** (eliminação
sobre dois valores), a **perseguição** (o jeito que se aprende jogando) e a
**força bruta** (tentar todos os conjuntos de botões). Os três são comparados
painel por painel, inclusive nos 33.554.432 painéis do 5x5.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="tamanhos"   # (ou forca, silenciosos)
```

## O formato decide quantos painéis têm solução

| painel | casas | posto | silenciosos | com solução | tudo aceso |
|---|---:|---:|---:|---:|---:|
| 1x1 | 1 | 1 | 0 | todos | 1 toque |
| 2x2 | 4 | 4 | 0 | todos | 4 toques |
| 3x3 | 9 | 9 | 0 | todos | 5 toques |
| **4x4** | 16 | 12 | 4 | **1 em 16** | **4 toques** |
| **5x5** | 25 | 23 | 2 | **1 em 4** | 15 toques |
| 6x6 | 36 | 36 | 0 | todos | 28 toques |
| 7x7 | 49 | 49 | 0 | todos | 33 toques |
| 8x8 | 64 | 64 | 0 | todos | - |
| 9x9 | 81 | 73 | 8 | 1 em 256 | - |
| 10x10 | 100 | 100 | 0 | todos | - |
| 11x11 | 121 | 115 | 6 | 1 em 64 | - |
| 12x12 | 144 | 144 | 0 | todos | - |
| 13x13 | 169 | 169 | 0 | todos | - |
| 14x14 | 196 | 192 | 4 | 1 em 16 | - |
| 15x15 | 225 | 225 | 0 | todos | - |
| 16x16 | 256 | 248 | 8 | 1 em 256 | - |

A coluna dos silenciosos é zero em **dez dos dezesseis** primeiros formatos, e
quando não é, vale 4, 2, 8, 6, 4 e 8 nessa ordem: não sobe, não desce e não se
adivinha. No 4x4, **quinze de cada dezesseis painéis não têm solução nenhuma**:
não é o jogador que está demorando, é o painel que não fecha.

E a última coluna não acompanha o tamanho. O painel 4x4 todo aceso se apaga em
**4 toques**, menos que o 3x3, que precisa de 5. O 5x5 pula para 15 e o 6x6 para
28.

Na faixa de uma linha, por outro lado, existe regra: o padrão silencioso aparece
em `1xN` exatamente quando `N` deixa resto 2 na divisão por 3, conferido até 30
colunas. Em duas linhas, o período é 4 (1, 0, 2, 0). É no quadrado que a conta
some.

## Três métodos, painel por painel

| painel | painéis | com solução | pela álgebra | discordâncias |
|---|---:|---:|---:|---:|
| 2x2 | 16 | 16 | 16 | 0 |
| 2x3 | 64 | 16 | 16 | 0 |
| 3x3 | 512 | 512 | 512 | 0 |
| 3x4 | 4.096 | 4.096 | 4.096 | 0 |
| 4x4 | 65.536 | 4.096 | 4.096 | 0 |
| 4x5 | 1.048.576 | 1.048.576 | 1.048.576 | 0 |
| 3x7 | 2.097.152 | 2.097.152 | 2.097.152 | 0 |
| **5x5** | **33.554.432** | **8.388.608** | **8.388.608** | **0** |

A coluna "com solução" vem de tentar os 33 milhões de conjuntos de botões e
anotar o que cada um produz. A coluna "pela álgebra" vem de contar o posto de
uma matriz. São 36,7 milhões de painéis comparados, um por um, e zero
discordâncias, incluindo o número mínimo de toques de cada painel.

A perseguição é o terceiro caminho, e o mais barato: escolhida a primeira linha
de toques, todo o resto fica decidido, porque em cada linha seguinte só resta
apertar o botão embaixo de cada luz ainda acesa. Isso troca 2^25 tentativas por
2^5:

| painel | primeiras linhas | que servem | toques mínimos | pela força |
|---|---:|---:|---:|---:|
| 2x2 | 4 | 1 | 4 | 4 |
| 2x3 | 8 | 4 | 2 | 2 |
| 3x3 | 8 | 1 | 5 | 5 |
| 3x4 | 16 | 1 | 10 | 10 |
| 4x4 | 16 | 16 | 4 | 4 |
| 4x5 | 32 | 1 | 10 | 10 |
| 3x7 | 128 | 1 | 7 | 7 |
| **5x5** | **32** | **4** | **15** | **15** |

Quantas primeiras linhas servem não é um detalhe da perseguição: é exatamente
`2^silenciosos`, o mesmo número que a álgebra calcula sem jogar.

## Os padrões silenciosos

São conjuntos de botões que, apertados todos, **não mudam uma luz sequer**. Os
dois do 5x5:

```
. # # # .      # . # . #
# . # . #      # . # . #
# # . # #      . . . . .
# . # . #      # . # . #
. # # # .      # . # . #
```

São eles que fazem o jogo ter mais de uma solução: somar um padrão silencioso a
uma solução dá outra solução. E é por isso que pegar a primeira resposta que a
eliminação devolve custa caro:

| painel | com solução | soluções cada | toques desperdiçados (média) | pior caso |
|---|---:|---:|---:|---:|
| 2x3 | 16 | 4 | 0,38 | 2 |
| 4x4 | 4.096 | 16 | 1,57 | 8 |
| **5x5** | **8.388.608** | **4** | **1,60** | **14** |

No 5x5, escolher a melhor das quatro soluções economiza **1,60 toque em média**
e até **14** no pior painel. A eliminação não erra, mas também não escolhe: ela
devolve uma solução qualquer da família.

O painel 5x5 todo aceso, que é o caso famoso, não mostra nada disso: as quatro
soluções dele têm **15 toques cada uma**.

## O que as medidas me corrigiram

1. **"Painel maior é mais difícil de apagar."** O 4x4 todo aceso custa 4 toques
   e o 3x3 custa 5. O custo não acompanha o tamanho; ele acompanha o formato.
2. **"Se não apagou, é porque faltou tentar."** No 4x4, quinze de cada dezesseis
   painéis não têm solução nenhuma, e nenhuma sequência de toques os apaga. No
   6x6 todos têm.
3. **"A quantidade de soluções deve crescer com o painel."** Ela é zero em dez
   dos dezesseis primeiros quadrados, e quando não é, vale 4, 2, 8, 6, 4 e 8
   nessa ordem. A faixa de uma linha tem regra limpa e o quadrado não tem.
4. **"A eliminação resolve o problema."** Ela devolve *uma* solução, não a
   melhor: no 5x5 isso desperdiça 1,60 toque em média e até 14 em um painel.
   Faltava somar os padrões silenciosos e escolher.
5. **"O caso famoso serve de teste."** O 5x5 todo aceso tem quatro soluções com
   exatamente 15 toques cada, então ele não detecta o erro de cima. Foi preciso
   varrer os 8.388.608 painéis com solução para o desperdício aparecer.

## Como está organizado

- `Painel`: o estado num long, um bit por luz, e o efeito de cada botão.
- `Algebra`: a eliminação sobre dois valores, de onde saem o posto, os padrões
  silenciosos, uma solução e a menor delas.
- `Perseguicao`: o jeito de jogar, que experimenta só as primeiras linhas.
- `Forca`: o juiz que não sabe nada: todos os conjuntos de botões, até 25 casas.
- `Posto`: só a conta do posto, com as linhas em vetores de bits, para olhar
  formatos que não cabem num long.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

79 testes.
