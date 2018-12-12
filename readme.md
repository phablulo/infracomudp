# Infracom UDP

## Características
- Tamanho de janela configurável
- Taxa de perda de arquivos configurável
- Selective Repeat
- Output bonito (fica feio no Windows!!)
- Apenas um cliente por vez

## Uso
pra compilar:

- `javac Client.java`
- `javac Server.java`

Pra abrir (nessa ordem):

- `java Server`
- `java Client (tamanho da janela) (taxa de perda) (endereço do servidor)`

Por exemplo para ter uma janela de tamanho 40, e uma taxa de perda de 2% com o servidor em 127.0.0.1:
```
java Client 50 2 127.0.0.1
```

**O arquivo enviado será o chamado "teste.zip" localizado _dentro_ da pasta raiz do projeto. Ao receber, o cliente irá escrever o resultado em "recebido.zip" localizado _dentro_ da pasta raiz do projeto.**

#### Observação sobre taxa de perda:
Em nossos testes, uma taxa de perda muito alta pode levar a transferência a nunca ser completada. No Linux (Ubuntu) a taxa de perda limite foi 10%. Acima disso, os mesmos pacotes ficam sendo descartados pelo módulo descarte e daí ele nunca completa a transferência. No Windows, o limite parece ser 5%.

Para informações de como o módulo de descarte foi implementado, veja o arquivo Client.java, linha 84.

#### Observação sobre o uso no Windows:
**Se você estiver usando Windows**, em cerca de **10%** das vezes, a transferência pode "travar" no meio do caminho. O Cliente informa que enviou Ack; o servidor informa que Ack foi recebido, e nada acontece depois disso. Por falta de tempo (culpe a prova final de Hardware), não foi possível encontrar a causa do problema (é bastante difícil procurá-la já que o problema acontece em raras ocasiões). Se isso acontecer, simplesmente reinicie o Server e o Client.

No entanto, para evitar tal anomalia, é altamente recomendado o uso de um sistema Unix-Like.

---

Visão geral:

1. O cliente abre uma conexão com o servidor e envia um número pra ele. Esse número é o tamanho da janela.
2. O servidor cria uma janela do tamanho especificado e começa a enviar pacotes de dados do arquivo `teste.zip` pro cliente. Cada pacote possui, no **máximo**, 256 bits (incluindo os headers).
3. O cliente valida os pacotes, envia Ack para cada pacote correto e escreve o dado desse pacote num `ByteArrayOutputStream`.
4. Ao finalizar o arquivo, o servidor envia um byte nulo pra sinalizar o término.
5. Ao receber o byte nulo, o cliente escreve o `ByteArrayOutputStream` no disco com o nome `recebido.zip`.
6. Acabou (:

### Resumão das classes:

#### Ack
Métodos pra criar e validar Acks

### Packet
Métodos para criar e validar pacotes do arquivo.

### Window
É a janela usada pelo algoritmo do Selective Repeat.

### util
Coisas úteis como abrir arquivos, transformar inteiros para array de bytes e afins.

### Server
Na `main`, o server espera por um cliente. Assim que algum cliente manda o tamanho da janela, ele chama o método `sendFile`. Esse método é o que contém toda a lógica do Selective Repeat. O `sendFile` usa o `waitAck` para ler `Acks` do cliente.

### Client
Envia um tamanho de janela pro servidor, depois pega e valida os pacotes recebidos enviando ou não Acks e, no fim, escreve os dados recebidos no disco com o nome `recebido.zip`.
