# Kahoot — Jogo de perguntas multijogador (cliente/servidor)

Implementação de um jogo de perguntas e respostas ao estilo Kahoot, com um **servidor** que gere vários jogos em simultâneo e **clientes** com interface gráfica que se ligam por sockets TCP. O foco do projeto é a **programação concorrente e distribuída**: threads, sincronização e coordenação entre jogadores.

> Projeto desenvolvido no âmbito da unidade curricular **Programação Concorrente e Distribuída**, 2025/2026.

## Funcionalidades

- Vários jogos em simultâneo, cada um com um ID, número de equipas, jogadores por equipa e número de perguntas configuráveis.
- Comunicação cliente/servidor por sockets, com troca de objetos serializados (`ObjectInputStream` / `ObjectOutputStream`).
- Perguntas lidas de um ficheiro JSON (`perguntas.json`) e sorteadas aleatoriamente em cada jogo.
- Dois tipos de pergunta, alternados:
  - **Individual:** responde cada jogador; quanto mais depressa se responde, maior o bónus de pontos.
  - **Em equipa:** a equipa só é avaliada quando todos os elementos respondem (ou o tempo acaba). Se todos acertarem, a equipa ganha **pontos a dobrar**; caso contrário ganha a pontuação mais alta entre as respostas certas.
- Temporizador por pergunta, enviado em tempo real aos clientes.
- Placar com a pontuação da ronda e a classificação das equipas.
- Validação de erros: nome de jogador já em uso, jogo inexistente, equipa cheia.

## Conceitos de concorrência utilizados

| Classe | Papel |
|---|---|
| `DealWithClient` | Uma thread por cliente ligado ao servidor |
| `Temporizador` | Thread de contagem decrescente com ação de *timeout* |
| `Barreira` | Barreira que sincroniza as respostas dos elementos de uma equipa |
| `CountDownLatchModified` | *CountDownLatch* modificado que devolve o bónus de rapidez e permite libertar por *timeout* |
| `GameState` | Estado do jogo com métodos `synchronized` |

## Tecnologias

- Java 21
- [Gson 2.13.2](https://github.com/google/gson) (leitura do JSON de perguntas)
- Eclipse (projeto Eclipse incluído)

## Estrutura do projeto

```
Projeto/
├── perguntas.json          # banco de perguntas
└── src/kahoot/
    ├── servidor/           # Servidor, Jogo, GameState, Equipa, Barreira, Temporizador...
    ├── cliente/            # Cliente e GUI
    └── mensagens/          # objetos trocados entre cliente e servidor
```

## Como executar

### 1. Pré-requisitos

- JDK 21 ou superior
- Descarrega o `gson-2.13.2.jar` (por exemplo do [Maven Central](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.13.2/)) e coloca-o numa pasta `lib/`.

> **Eclipse:** o `.classpath` aponta para um caminho local (`C:/Users/.../gson-2.13.2.jar`). Ao importar o projeto, remove essa entrada e adiciona o `.jar` em *Build Path > Add External JARs*.

### 2. Compilar (a partir da pasta do projeto)

```bash
# Linux / macOS
javac -cp lib/gson-2.13.2.jar -d bin $(find src -name "*.java")

# Windows (PowerShell)
javac -cp lib/gson-2.13.2.jar -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
```

### 3. Iniciar o servidor

Corre sempre na pasta do projeto, para que o `perguntas.json` seja encontrado:

```bash
java -cp "bin:lib/gson-2.13.2.jar" kahoot.servidor.Servidor      # no Windows usa ; em vez de :
```

O servidor escuta no porto **8080**. No terminal do servidor, cria um jogo:

```
new <numEquipas> <jogadoresPorEquipa> <numPerguntas>
```

Exemplo: `new 2 2 5` cria um jogo com 2 equipas de 2 jogadores e 5 perguntas, e imprime o **ID do jogo**. Para listar os jogos ativos: `mostrarJogos`.

### 4. Iniciar os clientes

Um terminal por jogador:

```bash
java -cp "bin:lib/gson-2.13.2.jar" kahoot.cliente.Cliente
```

Depois escreve na linha de comandos:

```
<endereço-do-servidor> <porto> <IDdoJogo> <IDdaEquipa> <nomeDoJogador>
```

Exemplo: `localhost 8080 1234 1 Ana`

O jogo começa automaticamente quando todos os jogadores estiverem ligados.

## Formato do ficheiro de perguntas

```json
{
  "name": "PCD-1",
  "questions": [
    {
      "question": "O que é uma thread?",
      "correct": 3,
      "points": 5,
      "options": ["Processo", "Aplicação", "Programa", "Processo Ligeiro"]
    }
  ]
}
```

`correct` é o índice (a começar em 0) da opção certa.

## Autores

- **Vicu Mindru** (n.º 99328)
- **Marin Mindru** (n.º 99845)

## Licença

Projeto académico, sem fins comerciais.
