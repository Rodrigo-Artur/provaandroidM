# Task Flow — provaandroidM

**Português** | [English](README.en.md)

Aplicativo Android para organizar tarefas por **categoria, prioridade e prazo**, com persistência local e um dashboard de acompanhamento. Desenvolvido em **Kotlin**, utiliza interfaces XML, Fragments, View Binding e uma estrutura MVVM com ViewModel, repositórios e Room.

O dashboard apresenta o nome **Task Flow**; o aplicativo instalado é identificado como **Prova**. A interface está em português; a versão inglesa deste README traduz a documentação.

> **Estado atual:** os recursos abaixo foram identificados no código. A tentativa de compilação desta revisão foi interrompida pela ausência do SDK Android no ambiente; a execução em dispositivo não foi validada. A opção “Repete diariamente” apenas identifica tarefas diárias; não implementa recorrência automática.

## Funcionalidades

| Área | Recursos implementados |
| --- | --- |
| **Categorias** | Criar, listar, editar e excluir categorias com nome e cor. Seleção em uma paleta de 20 cores. |
| **Tarefas** | Criar, editar e excluir tarefas com título, descrição, categoria, prioridade, prazo opcional e marcação de tarefa diária. |
| **Prioridades** | Opções `Baixa`, `Média` e `Alta`; novas tarefas iniciam com prioridade média selecionada no formulário. |
| **Conclusão** | Pressionar uma tarefa por alguns instantes a marca como `Concluída`, tanto na lista quanto no dashboard. |
| **Organização por prazo** | Lista dividida em atrasadas, hoje, próximos 7 dias, próximos 8–30 dias, sem prazo/futuro e concluídas. |
| **Dashboard** | Percentual geral de conclusão, barras de progresso por grupo e grupos de pendências que podem ser expandidos ou recolhidos. |
| **Identificação visual** | Indicador da cor da categoria, status, prioridade e prazo nos cards. Em Android 9 ou superior, o adapter também configura sombras com a cor da categoria. |
| **Persistência** | Banco Room `taskflow_database`, versão de esquema `3`, para manter categorias e tarefas entre execuções. |

As três áreas são acessadas pelo menu inferior: **Dashboard**, **Tarefas** e **Categorias**. O aplicativo inicia no dashboard, sem categorias ou tarefas de exemplo.

## Como usar

Após configurar e compilar o projeto:

1. Abra **Categorias**, toque em **+**, informe um nome e escolha uma cor.
2. Abra **Tarefas** e toque em **+**. É necessário ter pelo menos uma categoria cadastrada.
3. Informe o título, selecione categoria e prioridade e, se desejar, adicione descrição e prazo pelo seletor de data.
4. Marque **Repete diariamente** para identificar a tarefa como diária. Essa opção não cria novas ocorrências nem reabre tarefas concluídas.
5. Salve. Toque em uma tarefa na aba **Tarefas** para editar; o diálogo de edição também oferece **Excluir**.
6. Pressione uma tarefa por alguns instantes para concluí-la. Não existe ação de reabertura na interface atual.
7. Consulte o **Dashboard** e toque nos títulos dos grupos para expandir ou recolher suas pendências.
8. Toque em uma categoria para editar seu nome ou cor.

**Exclusão de categoria:** pressionar uma categoria por alguns instantes a exclui imediatamente, sem confirmação. O relacionamento no banco usa `ON DELETE CASCADE`, portanto **todas as tarefas vinculadas a essa categoria também são apagadas**.

## Organização e indicadores

### Lista de tarefas

Os agrupamentos usam a data atual como referência:

| Grupo exibido | Regra aplicada |
| --- | --- |
| `Atrasadas` | Tarefas não concluídas com prazo anterior a hoje. |
| `Hoje` | Tarefas não concluídas com prazo para hoje. |
| `Esta Semana` | Tarefas não concluídas com prazo nos próximos 1 a 7 dias. |
| `Este Mês` | Tarefas não concluídas com prazo nos próximos 8 a 30 dias. |
| `Sem Prazo / Futuro` | Tarefas não concluídas sem prazo ou com prazo além de 30 dias; erros de leitura da data também podem cair neste grupo. |
| `Concluídas` | Todas as tarefas com status `Concluída`, independentemente do prazo. |

“Esta Semana” e “Este Mês” representam intervalos móveis de dias, e não a semana e o mês do calendário.

### Dashboard

Cada tarefa é classificada em **um único grupo**, seguindo esta precedência: **atrasadas → diárias → prioridade alta → hoje → próximo dia → próximos 2 a 7 dias**. Uma tarefa diária atrasada, por exemplo, aparece em “Atrasadas”. Tarefas que não atendem a esses critérios continuam disponíveis na aba **Tarefas**.

A lista do dashboard exibe somente as pendências desses grupos. As barras de progresso consideram também as tarefas concluídas classificadas em cada grupo.

O indicador principal calcula:

```text
percentual = tarefas concluídas / total de tarefas × 100
```

O resultado é exibido sem casas decimais; sem tarefas, o valor é `0%`. Apesar do título **Progresso de Hoje**, o cálculo atual considera **todas as tarefas cadastradas**, sem filtrar pela data de hoje.

## Tecnologias e configuração

| Item | Configuração encontrada |
| --- | --- |
| Linguagem / plugin Kotlin | Kotlin `1.9.22` |
| Interface | XML, Fragments, RecyclerView e View Binding |
| Componentes visuais | Material Components `1.13.0`, AppCompat `1.7.1` e ConstraintLayout `2.2.1` |
| Estado e arquitetura | MVVM, AndroidViewModel, LiveData e repositórios |
| Operações assíncronas | Coroutines com `viewModelScope` e `Dispatchers.IO` |
| Persistência | Room `2.6.1` |
| Processamento de código | KSP `1.9.22-1.0.17` |
| Lifecycle | `2.7.0` |
| Navigation Component | `2.7.7` |
| Android Gradle Plugin | `8.5.0` |
| Gradle Wrapper | `9.3.1` |
| JVM solicitada pelo daemon | Java `21`, fornecedor `JETBRAINS` |
| Alvo do código Java/Kotlin | Java/JVM `17` |
| SDK de compilação / alvo | API `34` / API `34` |
| Android mínimo | Android 7.0 — API `24` |
| Identificador / versão | `com.example.prova` / `1.0` — código `1` |

As versões são definidas em `gradle/libs.versions.toml`, nos arquivos `build.gradle.kts` e nas configurações do Gradle. A interface deste projeto usa Views Android; não há Jetpack Compose na implementação.

## Compilação e execução

### Resultado da verificação e compatibilidade

A tentativa de executar `:app:assembleDebug` com os arquivos originais avançou até a resolução das dependências da tarefa de compilação Java e parou com **`SDK location not found`**. Nenhum APK foi gerado e os testes não foram executados. Esse resultado identifica uma limitação do ambiente de revisão, sem comprovar uma falha de compilação do código do aplicativo.

O repositório combina **Gradle 9.3.1, AGP 8.5.0 e Kotlin 1.9.22**. Essa combinação está fora da faixa de suporte completo documentada para Kotlin 1.9.22 e merece validação em um ambiente Android configurado.

Como referências, o [AGP 8.5 documenta Gradle 8.7, JDK 17 e SDK Build-Tools 34.0.0](https://developer.android.com/build/releases/agp-8-5-0-release-notes#compatibility), enquanto a [matriz do Kotlin](https://kotlinlang.org/docs/gradle-configure-project.html) informa a faixa compatível de cada versão do plugin. Se forem necessários ajustes, considere Gradle, AGP, Kotlin e KSP em conjunto; a simples troca do Java não valida a compatibilidade entre eles.

### Ambiente necessário

- Git e Android Studio compatível com a versão de AGP adotada.
- Android SDK Platform **34**, Platform-Tools e Build-Tools **34.0.0** para a configuração Android declarada atualmente.
- JVM de acordo com a configuração escolhida: o arquivo `gradle/gradle-daemon-jvm.properties` atual solicita **JetBrains JDK 21**, enquanto o código gera bytecode Java/JVM 17.
- Emulador ou dispositivo com Android **7.0/API 24** ou superior.
- Internet para baixar ferramentas e dependências na configuração inicial.

O aplicativo não exige login, servidor, chave de API ou permissões de localização. O manifesto não declara permissões adicionais; os dados são mantidos no banco local.

### Android Studio

1. Clone o repositório:

   ```bash
   git clone https://github.com/Rodrigo-Artur/provaandroidM.git
   cd provaandroidM
   ```

2. Abra a pasta que contém `settings.gradle.kts` no Android Studio.
3. Instale o SDK e as ferramentas correspondentes pelo **SDK Manager** e configure a JVM do Gradle.
4. Sincronize o Gradle. Em caso de erros de versão, revise o conjunto de ferramentas conforme a seção de compatibilidade.
5. Inicie um emulador ou conecte um dispositivo com depuração USB.
6. Selecione a configuração `app` e clique em **Run**.

### Terminal

Execute os comandos abaixo na raiz do repositório, com a JVM configurada e o caminho do SDK definido por `ANDROID_HOME` ou `sdk.dir` em `local.properties`. A conclusão da compilação permanece pendente de validação.

**Windows — PowerShell:**

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

**Linux/macOS:**

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

`installDebug` exige dispositivo ou emulador conectado. Após uma compilação bem-sucedida, o APK será gerado em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Organização do código

```text
provaandroidM/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/prova/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── dao/          # Consultas e operações Room
│       │   │   │   ├── db/           # AppDatabase
│       │   │   │   ├── entity/       # Categoria e Tarefa
│       │   │   │   └── repository/   # Repositórios dos dois modelos
│       │   │   └── ui/
│       │   │       ├── adapter/      # Cards e cabeçalhos das listas
│       │   │       ├── view/         # Dashboard, tarefas e categorias
│       │   │       └── viewmodel/    # TarefaViewModel compartilhado
│       │   └── res/
│       │       ├── layout/           # Interfaces XML
│       │       ├── navigation/       # Grafo de navegação
│       │       ├── menu/             # Menu inferior
│       │       └── values/           # Cores, textos e temas
│       ├── test/
│       └── androidTest/
├── gradle/
│   ├── libs.versions.toml
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

`MainActivity` conecta o menu inferior ao `NavHostFragment`. Os três Fragments compartilham `TarefaViewModel` com `activityViewModels()`, observam `LiveData` e enviam alterações aos repositórios. Estes acessam os DAOs do Room; as gravações são iniciadas pelo ViewModel em `Dispatchers.IO`.

`Categoria` guarda `id`, `nome` e `colorHex`. `Tarefa` guarda `id`, `titulo`, `descricao`, `categoriaID`, `prioridade`, `status`, `limitDate`, `isDaily` e `createdAt`. A categoria é referenciada por chave estrangeira, com índice em `categoriaID` e exclusão em cascata.

## Limitações conhecidas

- **Recorrência incompleta:** `isDaily` afeta o rótulo e o agrupamento; não existem agendamento, nova ocorrência, redefinição diária ou lembretes.
- **Conclusão sem reversão:** a interface marca tarefas como concluídas, mas não permite retornar ao estado pendente.
- **Exclusão sem confirmação:** categorias são removidas por toque prolongado, junto com suas tarefas. A exclusão de tarefas pelo formulário também não solicita uma segunda confirmação.
- **Migrações destrutivas:** `fallbackToDestructiveMigration()` permite recriar o banco e perder dados quando a versão do esquema muda sem uma migração disponível.
- **Datas como texto:** `limitDate` usa `dd/MM/yyyy` ou `Sem prazo`. A consulta ordena esse texto, portanto a ordem dentro dos grupos não é necessariamente cronológica entre meses e anos.
- **Status inconsistente no DAO:** `getTarefasConcluidas()` procura `Concluida`, sem acento, enquanto a interface grava `Concluída`. As telas atuais usam `allTarefas` e fazem sua própria classificação; a consulta específica não representa corretamente essas conclusões.
- **Indicador com rótulo impreciso:** “Progresso de Hoje” exibe o percentual global de conclusão, não a produtividade do dia.
- **Validação básica:** título e nome de categoria são verificados apenas como não vazios; espaços em branco e duplicidade não recebem tratamento específico.
- **Escopo local:** não há busca na lista de tarefas, sincronização entre dispositivos, autenticação ou exportação de dados na interface.

## Testes

O repositório contém dois testes de exemplo: `ExampleUnitTest`, que verifica uma soma, e `ExampleInstrumentedTest`, que verifica o identificador do aplicativo. Eles não cobrem categorias, conclusão, exclusão em cascata, agrupamento por datas ou migrações.

Com o ambiente Android configurado:

**Windows — PowerShell:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

**Linux/macOS:**

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

O teste instrumentado exige um dispositivo ou emulador conectado. Os comandos são instruções de execução, e não uma declaração de testes aprovados.

## Prioridades sugeridas para evolução

As propostas abaixo ainda não estão implementadas:

1. Validar a compilação com o SDK configurado e alinhar as versões das ferramentas para obter uma base com compatibilidade documentada.
2. Proteger a exclusão de categorias e substituir migrações destrutivas por migrações que preservem os dados.
3. Padronizar status e datas, corrigir o indicador diário e definir o comportamento de recorrência e reabertura.
4. Criar testes para as regras do dashboard, persistência, conclusão e exclusão em cascata.

## Repositório e licença

Código disponível em [Rodrigo-Artur/provaandroidM](https://github.com/Rodrigo-Artur/provaandroidM). O repositório analisado não contém arquivo `LICENSE` próprio para o projeto.

Documentação baseada na leitura do código do commit [`207f914`](https://github.com/Rodrigo-Artur/provaandroidM/tree/207f9144a58e99cedf122115d0012ba43da7e7af).
