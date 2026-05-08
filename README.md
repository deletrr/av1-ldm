
## Video de Teste de Funcionalidades android/web
https://youtu.be/iEx-M_rvBKw

# LDM - Avaliação 1

## Integrantes do Grupo

| Nome | RA | CRUD Responsável | Entidade |
|------|-----|-------------------|----------|
| **Amanda Ianes da Fonseca** | 2571392322023 | CRUD de Artistas | `Artista` (nome, gênero, data início, ativo) |
| **Bianca Soares Bomfim** | 2571392322013 | CRUD de Álbuns | `Album` (título, artista, lançamento, músicas, disponível) |
| **Daniel Teixeira da Silva** | 2571392312027 | CRUD de Clipes | `Clipe` (título, artista, duração, lançamento, disponível) |
| **Danilo da Silva Paulino** | 2571392322037 | CRUD de Playlists | `Playlist` (nome, descrição, data criação, pública, álbuns/músicas) |

## Sobre o Projeto

Aplicativo **Kotlin Multiplatform (KMP)** com **Compose Multiplatform**, simulando um sistema de gerenciamento de músicas inspirado no **Spotify**. O app suporta três plataformas: **Android**, **Desktop (JVM)** e **Web (WasmJs)**.

A camada de dados utiliza a **API REST do Firebase** via **Ktor Client**, permitindo que o mesmo código de rede (`RepositorioRemoto`) funcione em todas as plataformas sem depender da SDK nativa do Firebase.

> ### ⚠️ Nota para Correção — Requisito F (Repositório Remoto)
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
av1-ldm/
├── androidApp/                         # Módulo Android (Activity + Compose)
├── shared/                             # Lógica de negócio compartilhada (KMP)
│   └── src/
│       ├── commonMain/kotlin/com/fatec/av1/
│       │   ├── data/
│       │   │   └── RepositorioRemoto.kt    # Firebase REST via Ktor Client
│       │   ├── model/
│       │   │   └── Models.kt               # Data classes serializáveis
│       │   └── ui/
│       │       ├── App.kt                  # Navegação + Bottom Navigation
│       │       ├── AuthViewModel.kt        # ViewModel de autenticação
│       │       ├── ArtistaViewModel.kt     # ViewModel de Artistas (Amanda)
│       │       ├── AlbumViewModel.kt       # ViewModel de Álbuns (Bianca)
│       │       ├── ClipeViewModel.kt       # ViewModel de Clipes (Daniel)
│       │       ├── PlaylistViewModel.kt    # ViewModel de Playlists (Danilo)
│       │       ├── components/Components.kt # Componentes reutilizáveis
│       │       ├── screens/
│       │       │   ├── auth/AuthScreens.kt     # Login e Registro
│       │       │   ├── amanda/AmandaScreens.kt # CRUD Artistas
│       │       │   ├── bianca/BiancaScreens.kt # CRUD Álbuns
│       │       │   ├── daniel/DanielScreens.kt # CRUD Clipes
│       │       │   ├── danilo/DaniloScreens.kt # CRUD Playlists
│       │       │   └── home/HomeScreen.kt      # Tela Home
│       │       └── theme/Theme.kt              # Tema Spotify
│       ├── androidMain/
│       └── jvmMain/
├── gradle/
│   └── libs.versions.toml              # Catálogo de versões centralizado
├── kotlin-js-store/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew / gradlew.bat
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

### CRUDs (itens c–k)
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
- ✅ **Seleção de álbuns/músicas** nas playlists
- ✅ **Logout** na TopAppBar

### Multiplataforma (KMP)
- ✅ **Target Android** (`androidTarget()`) com Ktor OkHttp engine
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

### 🔥 Configuração do Firebase (Obrigatório)

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

> 💡 **Dica:** O Project ID e a API Key também podem ser extraídos do arquivo `google-services.json`.

#### Passo 3 — Obter a Web API Key

1. Na mesma página de **Configurações do projeto** → aba **"Geral"**
2. Copie a **"Chave da API da Web"** (Web API Key) (ex: `AIzaSyAbCdEfGhIjKlMnOpQrSt28j1Yz`)

> 💡 **Dica:** A Web API Key também aparece no `google-services.json` no campo `"api_key"` → `"current_key"`.

#### Passo 4 — Inserir no código

Abra o arquivo:
```
shared/src/commonMain/kotlin/com/fatec/av1/data/RepositorioRemoto.kt
```

Localize as constantes no topo da classe e substitua pelos seus valores:

```kotlin
object RepositorioRemoto {
    private const val PROJECT_ID = "SEU_PROJECT_ID_AQUI"   // ← Cole o Project ID
    private const val API_KEY    = "SUA_WEB_API_KEY_AQUI"  // ← Cole a Web API Key

    // As URLs são montadas automaticamente a partir dessas constantes:
    // Auth: https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$API_KEY
    // Firestore: https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/
}
```

#### Passo 5 — (Android) Colocar o google-services.json

1. No Firebase Console → **Configurações do projeto** → **"Seus apps"**
2. Adicione um app Android com package `com.fatec.av1`
3. Baixe o `google-services.json`
4. Coloque o arquivo na raiz do módulo Android

> ⚠️ O `google-services.json` é necessário apenas para o build Android. Desktop e Web usam apenas o `PROJECT_ID` e `API_KEY` do `RepositorioRemoto.kt`.

### ▶️ Executar no Android

```bash
./gradlew :androidApp:installDebug
```
Ou abra no Android Studio, selecione a configuração **androidApp** e clique **Run ▶**

### 🌐 Executar na Web (WasmJs)

```bash
./gradlew :shared:wasmJsBrowserDevelopmentRun
```
Abrirá automaticamente no navegador em `http://localhost:8080`.
