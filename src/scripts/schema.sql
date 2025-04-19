
-- Criação das tabelas de Veículo

CREATE TABLE IF NOT EXISTS Tipo_Veiculo (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    padrao BOOLEAN NOT NULL -- Verifica se vem por padrão da aplicação ou não.
);

CREATE TABLE IF NOT EXISTS Veiculo (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    placa TEXT NOT NULL UNIQUE,
    modelo TEXT NOT NULL,
    tipo_id INT NOT NULL,
    altura_interna REAL,
    largura_interna REAL,
    comprimento_interno REAL,
    capacidade_peso REAL,
    observacoes TEXT,
    FOREIGN KEY (tipo_id) REFERENCES Tipo_Veiculo(id)
);

INSERT INTO Tipo_Veiculo (nome, padrao) VALUES
('Picape', true),
('Caminhão leve', true),
('Caminhão médio', true),
('Carreta', true),
('Bitrem', true);


-- Criação das tabelas de Produto

CREATE TABLE IF NOT EXISTS Tipo_Produto (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    padrao BOOLEAN NOT NULL -- Verifica se vem por padrão da aplicação ou não.
);

CREATE TABLE IF NOT EXISTS Produto (
    id INTEGER NOT NULL PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT,
    familia TEXT,
    tipo_id INT NOT NULL,
    lote TEXT,
    altura REAL,
    largura REAL,
    comprimento REAL,
    peso REAL,
    grau_fragilidade TEXT NOT NULL CHECK (grau_fragilidade in ('alta', 'média', 'baixa')),
    observacoes TEXT,
    FOREIGN KEY (tipo_id) REFERENCES Tipo_Produto(id)
);

INSERT INTO Tipo_Produto (nome, padrao) VALUES
('Tecnologia', true),
('Alimentação', true),
('Saúde e beleza', true),
('Roupas e moda', true);


-- Criação das tabelas de Embalagem

CREATE TABLE IF NOT EXISTS Tipo_Embalagem (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    padrao BOOLEAN NOT NULL -- Verifica se vem por padrão da aplicação ou não.
);

CREATE TABLE IF NOT EXISTS Embalagem (
    id INTEGER NOT NULL PRIMARY KEY,
    altura REAL NOT NULL,
    largura REAL NOT NULL,
    comprimento REAL NOT NULL,
    peso REAL NOT NULL,
    empilhavel BOOLEAN NOT NULL,
    observacoes TEXT,
    tipo_id INT NOT NULL,
    produto_id INT NOT NULL,
    FOREIGN KEY (tipo_id) REFERENCES Tipo_Embalagem(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id)
);

INSERT INTO Tipo_Embalagem (nome, padrao) VALUES
('Caixa', true),
('Tambor', true),
('Saco', true),
('Frasco', true),
('Galão', true);
