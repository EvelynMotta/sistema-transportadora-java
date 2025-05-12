
<p align="center"><img align="center" width="300" src="./.github/text-logo.png#gh-dark-mode-only"/></p>
<p align="center"><img align="center" width="300" src="./.github/text-logo-light.png#gh-light-mode-only"/></p>

<p align="center">
    <img alt="GitHub Tag"
    src="https://img.shields.io/github/v/tag/pedroanuda/sistema-transportadora-java?label=Vers%C3%A3o">
    <img alt="Github License" 
    src="https://img.shields.io/github/license/pedroanuda/sistema-transportadora-java?label=Licen%C3%A7a">
    <img alt="Status"
    src="https://img.shields.io/badge/Status-Finalizado-purple">
</p>

<p align="center" style="font-size: 1rem">
    Uma solução simples de controle para transportadoras feita em Java.
</p>
<hr>

> [!NOTE]
> 📚 Projeto com fins puramente educativos!

## Recursos
O que esse projeto oferece é uma forma de cadastrar e obter resumos sobre entidades que existem em
uma transportadora, sendo elas **Veículo**, **Produto** e **Embalagem**.

O projeto usa SQLite como sistema de banco de dados, o que elimina a necessidade de servidores e facilita
ainda mais o uso para o usuário final.

Em termos mais técnicos, este projeto utiliza de uma organização de modelo, repositório e serviço. Com isso,
diferentes camadas são feitas para abstrair e simplificar ainda mais a conexão com o banco de dados. 

E para a comunicação com o banco de dados, se usa de pooling com o **HikariCP** para evitar que conexões
sejam abertas e fechadas constantemente, fazendo um gerenciamento das conexões de forma eficiente.

## Como configurar
Antes de pôr o projeto em uso para desenvolvimento, é necessário primeiro instalar as bibliotecas necessárias
e as colocar no diretório `lib/`. É nesse diretório que o Java com Ant, normalmente, trabalha com
bibliotecas. As necessárias para instalação são:
* [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc/releases/tag/3.49.1.0);
* [HikariCP](https://repo1.maven.org/maven2/com/zaxxer/HikariCP/6.3.0/);
* [slf4j-api](https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/);
* [slf4j-simple](https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/).

Contexto de pastas do projeto:
```
SistemaTransportadora/
├── nbproject/...
├── src/...
└── lib/
    ├── HikariCP-6.3.0.jar
    ├── slf4j-api-2.0.9.jar
    ├── slf4j-simple-2.0.9.jar
    └── sqlite-jdbc-3.49.1.0.jar
```
