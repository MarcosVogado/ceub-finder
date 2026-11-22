# CEUB Finder

Aplicativo Android acadêmico desenvolvido em Kotlin com Jetpack Compose para gerenciamento e consulta de **Achados e Perdidos** no âmbito do CEUB.

---

## 📌 Contexto e Objetivo
Estudantes que perdem um pertence no campus muitas vezes precisam se deslocar até a Central de Achados e Perdidos apenas para consultar se o objeto foi entregue. O **CEUB Finder** resolve esse problema oferecendo um catálogo digital consultável dos objetos recolhidos pela central, permitindo triagem remota e contato direto.

---

## 🚀 Funcionalidades Principais

### Área Pública (Estudante - Sem Login)
- **Tela Início:** Mensagem institucional, busca rápida, atalhos por categoria e listagem dos objetos recentes disponíveis.
- **Busca Avançada:** Filtros combinados por texto, categoria e local específico (ex: bosque, biblioteca, quadra, estacionamento).
- **Detalhes do Objeto:** Exibição de fotografia, categoria, descrição, local, data em que foi encontrado e status.
- **Contato com a Central:** Botão integrado com **`ACTION_DIAL`** para abrir o discador telefônico.
- **Área de Informações:** Orientações sobre o funcionamento e o aviso de que a retirada é presencial.

### Área Administrativa (Funcionário)
- **Login Demonstrativo:** Tela de acesso institucional simulada para funcionários.
- **Dashboard Dinâmico:** Indicadores em tempo real baseados em consultas SQLite (total de cadastros, disponíveis e devolvidos).
- **Cadastro e Edição:** Formulário completo com seleção de fotografias via **Android Photo Picker** e persistência no armazenamento interno.
- **Gerenciamento:** Listagem com abas/filtros (Disponíveis e Devolvidos), marcação de devolução, edição e exclusão com confirmação.
- **Histórico:** Consulta de registros devolvidos.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **Interface Gráfica:** Jetpack Compose & Material 3 (com paleta institucional do CEUB e cores dinâmicas desativadas)
- **Persistência:** SQLite puro (`SQLiteOpenHelper`)
- **Mídia:** AndroidX Photo Picker (`PickVisualMedia`) & Coil (carregamento de imagens)
- **Navegação:** Jetpack Navigation Compose

---

## 🗄️ Estrutura do Banco de Dados e Relacionamento

O banco de dados local (`ceub_finder.db`) possui duas tabelas principais relacionadas em **1:N (Um para Muitos)**:

1. **`categorias`**: Armazena as categorias dos objetos (ex: Eletrônicos, Chaves, Carteiras e acessórios, Vestuário, etc.).
2. **`objetos`**: Armazena os itens encontrados, contendo uma chave estrangeira (`categoria_id`) referenciando a tabela `categorias`.

### 🔗 Consulta Obrigatória com INNER JOIN
Para exibir a listagem pública de objetos disponíveis junto ao nome de sua respectiva categoria, o aplicativo executa uma consulta SQL com `INNER JOIN`:

```sql
SELECT o.*, c.nome AS categoria_nome 
FROM objetos o 
INNER JOIN categorias c ON c.id = o.categoria_id 
WHERE o.status = 'DISPONIVEL' 
ORDER BY o.data_cadastro DESC;
```

---

## ⚠️ Avisos Importantes
- **Login Demonstrativo:** A autenticação da área administrativa é puramente simulada para fins acadêmicos e de prototipagem.
- **Telefone Demonstrativo:** O número de telefone configurado para contato com a central é um número demonstrativo fictício.
- **Retirada Presencial:** O aplicativo não realiza transações online, confirmação de propriedade ou liberação de objetos; todo o processo de verificação e retirada é presencial na central física do CEUB.

---

## 💻 Instruções para Executar o Projeto
1. Clone este repositório ou baixe o ZIP.
2. Abra o **Android Studio** (versão Jellyfish ou superior recomendada).
3. Selecione **Open** e a pasta raiz do projeto `CEUBFinder`.
4. Aguarde a sincronização do Gradle.
5. Execute o aplicativo em um emulador Android (API 26 ou superior).
