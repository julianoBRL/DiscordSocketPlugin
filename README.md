É uma ótima ideia documentar seu plugin\! Aqui está um `README.md` formatado, pronto para ser copiado e colado no seu projeto.


# 🔌 Discord Socket Plugin

Este plugin fornece uma **integração via Unix Domain Socket (UDS)** para o seu bot Discord, permitindo comunicação e controle de forma eficiente e segura através de mensagens JSON.

## 🚀 Funcionalidade

O objetivo principal deste plugin é atuar como uma interface de comunicação, possibilitando que aplicações externas (clientes) enviem comandos ao bot e recebam respostas em **tempo real** através de um *socket*.

## 💬 Comunicação (UDS)

A comunicação é feita através da troca de mensagens JSON.

### Estrutura da Mensagem de Envio

Todas as mensagens enviadas ao socket devem seguir o formato JSON abaixo:

```json
{
  "command": "<NOME DO COMANDO>",
  "data": "<DADOS ADICIONAIS OU null>"
}
```

- **`command`**: O nome do comando a ser executado pelo bot.
- **`data`**: Dados adicionais necessários para o comando. Pode ser `null` se o comando não exigir dados extras.

### Estrutura da Mensagem de Resposta

As respostas do bot também são retornadas em formato JSON.

### Comandos Disponíveis

| Comando | Descrição                                                           | Formato do `data` de Envio | Exemplo de Uso |
| :--- |:--------------------------------------------------------------------| :--- | :--- |
| **`SELF`** | Retorna informações básicas sobre o bot logado (o usuário cliente). | `null` | `{"command":"SELF","data":null}` |
| **`GET_STATUS`** | Retorna o status atual do bot.                                      | `null` | `{"command":"GET_STATUS","data":null}` |
| **`GET_GUILDS`** | Retorna a lista de servidores (guilds) onde o bot está presente.    | `null` | `{"command":"GET_GUILDS","data":null}` |
| **`SET_ACTIVITY`** | Define a atividade e o status do bot.                               | Objeto JSON com `type` e `name`. | `{"command":"SET_ACTIVITY","data":{"type":"PLAYING","name":"Desenvolvendo o Bot"}}` |

-----

### Detalhes do Comando `SET_ACTIVITY`

O comando `SET_ACTIVITY` requer um objeto JSON no campo `data` com as seguintes chaves:

- **`type`** (String): O tipo de atividade.
- **`name`** (String): O texto de exibição da atividade.

#### Tipos de Atividade Válidos:

Os valores permitidos para o campo `type` são:

- `PLAYING`
- `STREAMING`
- `LISTENING`
- `WATCHING`
- `CUSTOM_STATUS`
- `COMPETING`

#### Exemplo de `data` para `SET_ACTIVITY`:

```json
{
  "type": "LISTENING",
  "name": "Música Lofi"
}
```
