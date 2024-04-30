#  SERVICO FÁCIL

# Objetivo:

* Esta aplicação tem como objetivo a criação de um sistema de orquestração de serviços, onde teremos dois atores principais, o cliente, e o prestador. O cliente é responsável por solicitar um serviço, e o prestador é responsável por aceitar ou recusar o serviço solicitado.
Dado que o prestador aceite o serviço, o cliente é notificado e o prestador é direcionado para a realização do serviço.

# Funcionalidades:

1- Prestador:

    * Cadastro de prestador.
    * Autenticação de prestador.
    * Aceitar serviço.
    * Recusar serviço.
    * Realizar serviço.
    * Flexibilidade de perfil (Nesta feature o prestador pode alterar seu perfil de prestador para cliente e vice-versa).

2- Cliente:

    * Cadastro de cliente.
    * Autenticação de cliente.
    * Solicitar serviço.
    * Buscar serviço dinamicamente (Nesta feature o cliente terá a flexibilidade de buscar o serviço que deseja realizar por meio de nome, categoria, profissão, localidade, etc..).
    * Cancelar serviço.
    * Flexibilidade de perfil (Nesta feature o cliente pode alterar seu perfil de cliente para prestador e vice-versa).

3- Autenticação de CNPJ
    
        * Nesta feature o prestador terá a possibilidade de autenticar seu CNPJ no ato de seu cadastro (caso houver), para que o cliente tenha a segurança de que o prestador é uma empresa legalizada.
        * Esse mesmo CNPJ será utlizado para aumentar a representatividade do prestador no sistema. Podendo ser mais facilmente encontrado por clientes e mais bem avaliado.

4- Avaliação de serviço

        * Nesta feature o cliente terá a possibilidade de avaliar o serviço prestado pelo prestador, e o prestador terá a possibilidade de avaliar o cliente.
        * Essa avaliação será utilizada para aumentar a representatividade do prestador no sistema, e para aumentar a confiabilidade do cliente no sistema.

5- Notificação de serviço

        * Nesta feature o cliente será notificado quando o prestador aceitar ou recusar o serviço solicitado.
        * O prestador será notificado quando o cliente cancelar o serviço solicitado.
        * O prestador será notificado quando o cliente avaliar o serviço prestado.
        * O cliente será notificado quando o prestador avaliar o serviço solicitado.

6- Favoritar prestador

        * Nesta feature o cliente terá a possibilidade de favoritar um prestador, para que possa solicitar serviços futuros com mais facilidade.

# Clique a baixo para ver o fluxo de funcionalidade:
[Servico Fácil](https://miro.com/welcomeonboard/ZEhPSFNpU1JTMDNCTk85b0lNdDdsUWk0Ym84cXBNUUJmWVZycFR6Rlp1M1NVTFhDRW9iZU9rUlFUTTBOWUdYb3wzNDU4NzY0NTUzOTUwNTcyMTk0fDI=?share_link_id=931661708177)

# OBSERVAÇÕES:
1. Todo o fluxo de variáveis de ambiente está composto dentro do application.yml do projeto. Onde posteriormente será realizado a sua migração para um confi-repo, onde centralizaremos todas as variáveis de ambiente.
2. O projeto está sendo desenvolvido em java 21, com o framework spring boot 3.2.3.
3. Como banco de dados utilizamos o mongo, do qual é utlizado o atlas para a hospedagem do banco.
4. Também utilizamos a biblioteca feign para a comunicação com o serviço de verificação de CNPJ.

# Testes:

* A aplicação possui fácil testabilidade, desde que seu ambiente de desenvolvimento esteja preparado para rodar o java 21. Por nossas variáveis de ambiente estarem ainda em abiente local, apenas a estabilidade da versão do jdk é o suficiente para rodar a aplicação, e realizar seus testes com postman, por exemplo.

