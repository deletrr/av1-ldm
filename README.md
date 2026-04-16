# Laboratório de Desenvolvimento Multiplataforma - Avaliação 1

## Integrantes do Grupo

| Nome                        | RA                | CRUD Responsável      | Entidade |
|-----------------------------|-------------------|-----------------------|----------|
| **Amanda Ianes da Fonseca** | RA: 2571392322023 | CRUD de Artistas      | `Artista` (nome, gênero, data início, ativo) |
| **Bianca Soares Bomfim**    | RA: 2571392322013 | CRUD de Álbuns        | `Album` (título, artista, lançamento, músicas, disponível) |
| **Daniel Teixeira da Silva**| RA: 2571392312027 | CRUD de Clipes        | `Clipe` (título, artista, duração, lançamento, disponível) |
| **Danilo da Silva Paulino** | RA: 2571392322037 | CRUD de Playlists     | `Playlist` (nome, descrição, data criação, pública, álbuns/músicas) |

## Sobre o Projeto

Aplicativo **Kotlin Multiplatform (KMP)** com **Compose Multiplatform**, simulando um sistema de gerenciamento de músicas inspirado no **Spotify**. O app suporta três plataformas: **Android**, **Desktop (JVM)** e **Web (WasmJs)**.

A camada de dados foi refatorada para utilizar a **API REST do Firebase** via **Ktor Client**, permitindo que o mesmo código de rede (`RepositorioRemoto`) funcione em todas as plataformas sem depender da SDK nativa do Firebase.

> ### ⚠️ Nota para Correção - Requisito F (Repositório Remoto) ⚠️
> A classe `commonMain/.../data/RepositorioRemoto.kt` implementa **toda a comunicação REST** com o Firebase:
>
> * **POST** → `signInWithPassword`, `signUp` (Firebase Auth REST API)
> * **GET** → `listarDocumentos` (Firestore REST API)
> * **PATCH** → `salvarDocumento` (Firestore REST API — create/update)
> * **DELETE** → `deletarDocumento` (Firestore REST API)
>
> Utiliza **Ktor Client** com `ContentNegotiation` + `KotlinX Serialization`, garantindo compatibilidade multiplataforma.

## Arquitetura KMP

```
LDM-AV1-KMP/
├── androidApp/                              # Módulo Android (Activity + Compose)
├── composeApp/
│   ├── build.gradle.kts                     # Plugin KMP, targets android + desktop + wasmJs
│   ├── google-services.json                 # Config Firebase (Android)
│   └── src/
│       ├── commonMain/kotlin/com/fatec/av1/ # Código compartilhado (todas as plataformas)
│       │   ├── data/
│       │   │   └── RepositorioRemoto.kt     # Firebase REST via Ktor Client
│       │   ├── model/
│       │   │   └── Models.kt                # Data classes serializáveis
│       │   └── ui/
│       │       ├── App.kt                   # Navegação + Bottom Navigation
│       │       ├── AuthViewModel.kt         # ViewModel de autenticação
│       │       ├── ArtistaViewModel.kt      # ViewModel de Artistas (Amanda)
│       │       ├── AlbumViewModel.kt        # ViewModel de Álbuns (Bianca)
│       │       ├── ClipeViewModel.kt        # ViewModel de Clipes (Daniel)
│       │       ├── PlaylistViewModel.kt     # ViewModel de Playlists (Danilo)
│       │       ├── components/Components.kt # Componentes reutilizáveis
│       │       ├── screens/
│       │       │   ├── auth/AuthScreens.kt  # Login e Registro
│       │       │   ├── amanda/AmandaScreens.kt # CRUD Artistas
│       │       │   ├── bianca/BiancaScreens.kt # CRUD Álbuns
│       │       │   ├── daniel/DanielScreens.kt # CRUD Clipes
│       │       │   ├── danilo/DaniloScreens.kt # CRUD Playlists
│       │       │   └── home/HomeScreen.kt   # Tela Home
│       │       └── theme/Theme.kt           # Tema Spotify
│       │
│       ├── androidMain/                     # Código específico Android
│       │   ├── AndroidManifest.xml
│       │   └── kotlin/com/fatec/av1/
│       │       ├── MainActivity.kt          # Activity principal
│       │       └── data/
│       │           ├── currentTimeMillis.android.kt
│       │           └── FirebaseService.kt   # SDK nativa (referência/fallback)
│       │
│       ├── desktopMain/                     # Código específico Desktop (JVM)
│       │   └── kotlin/com/fatec/av1/
│       │       └── Main.kt                  # Window entry point
│       │
│       └── wasmJsMain/                      # Código específico Web (Wasm)
│           └── kotlin/com/fatec/av1/
│               └── Main.kt                  # ComposeViewport entry point
│
├── shared/                                  # Lógica de negócio compartilhada (KMP)
│   └── src/
│       ├── commonMain/
│       ├── androidMain/
│       └── jvmMain/
│
├── gradle/
│   └── libs.versions.toml                   # Catálogo de versões centralizado
└── settings.gradle.kts
```

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| **Kotlin 2.1.20** | Linguagem base do projeto |
| **Kotlin Multiplatform** | Compartilhamento de código Android + Desktop + Web |
| **Compose Multiplatform** | UI declarativa multiplataforma |
| **Ktor Client 3.0.3** | HTTP client multiplataforma (OkHttp no Android, Fetch no Wasm) |
| **KotlinX Serialization** | Serialização JSON multiplataforma |
| **Firebase Auth REST API** | Autenticação via HTTP (sem SDK nativa) |
| **Firebase Firestore REST API** | CRUD via HTTP (sem SDK nativa) |
| **Material 3** | Design system |
| **Coroutines** | Programação assíncrona |
| **Gradle 9.0** | Build system com Version Catalog |

## Requisitos Implementados

### Autenticação (itens a, b)
- ✅ **Tela de Login** com campos de email e senha, botão logar e ir para registro
- ✅ **Tela de Registro** com nome, email, senha e confirmação

### CRUDs (itens c-k)
Cada CRUD possui:
- ✅ **Listagem** com cards estilizados
- ✅ **Formulário** de criação/edição
- ✅ **DatePicker** para campos de data
- ✅ **Switch** para campos booleanos
- ✅ **Botões** Gravar e Limpar Campos
- ✅ **Ícones** de Editar e Apagar
- ✅ **Navegação** entre lista e formulário

### Funcionalidades Extras
- ✅ **Tela Home** com visão geral
- ✅ **Busca global** na Home e Clipes
- ✅ **Bottom Navigation** com 5 abas
- ✅ **Tema escuro** Spotify (#121212, #1DB954)
- ✅ **Músicas embutidas** nos álbuns
- ✅ **Seleção de álbuns/músicas** nas playlists
- ✅ **Logout** na TopAppBar

### Multiplataforma (KMP)
- ✅ **Target Android** (`androidTarget()`) com Ktor OkHttp engine
- ✅ **Target Desktop** (`jvm("desktop")`) com Ktor CIO engine
- ✅ **Target Web/Wasm** (`wasmJs()`) com Ktor engine + `ComposeViewport`
- ✅ **`expect`/`actual`** para `currentTimeMillis()` em todas as plataformas
- ✅ **`RepositorioRemoto`** em `commonMain` usando Ktor + Firebase REST API

## Como Executar

### Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| **Android Studio** | Ladybug (2024.2.1) ou superior |
| **JDK** | 21+ |
| **Android SDK** | API 35 (compileSdk), minSdk 24 |
| **Gradle** | 9.0 (wrapper incluso, não precisa instalar) |

---

### 🔥 Configuração do Firebase (OBRIGATÓRIO)

O projeto usa a **API REST do Firebase** diretamente. Você precisa de duas informações do seu projeto Firebase: o **Project ID** e a **Web API Key**.



#### Passo 1 — Criar/Acessar o projeto no Firebase

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Crie um novo projeto ou use um existente
3. Ative **Authentication** → método **Email/Senha**
4. Ative **Cloud Firestore** → inicie no **modo de teste**

#### Passo 2 — Obter o Project ID

1. No Firebase Console, clique na **engrenagem ⚙️** ao lado de "Visão geral do projeto"
2. Clique em **"Configurações do projeto"**
3. Na aba **"Geral"**, copie o **ID do projeto** (ex: `meu-projeto-12345`)
4. É POSSIVEL EXTRAIR O ID E A APIKEY DO ARQUIVO google-services.JSON

#### Passo 3 — Obter a Web API Key

1. Na mesma página de **Configurações do projeto** → aba **"Geral"**
2. Copie a **"Chave da API da Web"** (Web API Key) (ex: `AIzaSyAbCdEfGhIjKlMnOpQrSt28j1Yz`)

> 💡 **Dica:** A Web API Key também aparece no `google-services.json` no campo `"api_key"` → `"current_key"`.

#### Passo 4 — Inserir no código

Abra o arquivo:
```
composeApp/src/commonMain/kotlin/com/fatec/av1/data/RepositorioRemoto.kt
```

Localize as constantes no topo da classe e substitua pelos seus valores:

```kotlin
object RepositorioRemoto {
    private const val PROJECT_ID = "SEU_PROJECT_ID_AQUI"      // ← Cole o Project ID
    private const val API_KEY    = "SUA_WEB_API_KEY_AQUI"      // ← Cole a Web API Key

    // As URLs são montadas automaticamente a partir dessas constantes:
    // Auth:      https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$API_KEY
    // Firestore: https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/
    // ...
}
```

#### Passo 5 — (Android) Colocar o google-services.json

1. No Firebase Console → **Configurações do projeto** → **"Seus apps"**
2. Adicione um app Android com package `com.fatec.av1`
3. Baixe o `google-services.json`
4. Coloque o arquivo em `composeApp/google-services.json`

> ⚠️ O `google-services.json` é necessário apenas para o build Android. Desktop e Web usam apenas o `PROJECT_ID` e `API_KEY` do `RepositorioRemoto.kt`.

---

### ▶️ Executar no Android

```bash
./gradlew :androidApp:installDebug
```
Ou abra no Android Studio, selecione a configuração **androidApp** e clique **Run ▶**

### 🖥️ Executar no Desktop (JVM)

```bash
./gradlew :composeApp:run
```

### 🌐 Executar na Web (WasmJs)

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
Abrirá automaticamente no navegador em `http://localhost:8080`.

---

## 🐛 Solução de Problemas

| Erro | Solução |
|---|---|
| `Could not find org.nodejs:node` | Verifique se o repositório Ivy do Node.js está em `settings.gradle.kts` |
| `Could not find com.yarnpkg:yarn` | Verifique se o repositório Ivy do Yarn está em `settings.gradle.kts` |
| `plugin already on classpath` | Não declare `version` no `plugins {}` do `shared/build.gradle.kts` |
| JDK incompatível | Use JDK 21+ configurado em File → Project Structure → SDK |
| `repositoriesMode FAIL_ON_PROJECT_REPOS` | Use `PREFER_SETTINGS` em `settings.gradle.kts` |
