# Infracom UDP

Funciona assim:

1. O cliente abre uma conexão com o servidor e envia um número pra ele. Esse número é o tamanho da janela.
2. O servidor cria uma janela do tamanho especificado e começa a enviar pacotes de dados do arquivo `teste.zip` pro cliente. Cada pacote possui, no **máximo**, 256 bits (incluindo os headers).
3. O cliente valida os pacotes, envia Ack para cada pacote correto e escreve o dado desse pacote num `ByteArrayOutputStream`.
4. Ao finalizar o arquivo, o servidor envia um byte nulo pra sinalizar o término.
5. Ao receber o byte nulo, o cliente escreve o `ByteArrayOutputStream` no disco com o nome `recebido.zip`.
6. Acabou (:

### Resumão das classes:

#### Ack
Métodos lá pra criar e validar Acks

### Packet
Métodos para criar e validar pacotes do arquivo.

### Window
É a janela usada pelo algoritmo. Se algum pacote der `TIMEOUT` o método `unsetSentAt` deve ser chamado. Dessa forma o pacote será reenviado.

### util
Coisas úteis como abrir arquivos, transformar inteiros para array de bytes e afins.

### Server
Na `main`, o server espera por um cliente. Assim que algum cliente manda o tamanho da janela, ele chama o método `sendFile`. Esse método é o que contém toda a lógica do Selective Repeat. O `sendFile` usa o `waitAck` para ler `Acks` do cliente.

# Client
Envia um tamanho de janela pro servidor, depois pega e valida os pacotes recebidos enviando ou não Acks e, no fim, escreve os dados recebidos no disco com o nome `recebido.zip`.


### Bugs conhecidos

Em Issues.