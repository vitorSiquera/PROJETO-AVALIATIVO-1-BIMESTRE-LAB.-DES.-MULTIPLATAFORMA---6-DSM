-- =====================================================================
-- To-Do (CRUD) - Lista de Tarefas
-- Script de criacao do banco de dados (PostgreSQL)
--
-- Como executar:
--   1) Crie o banco (conectado no banco "postgres"):
--        CREATE DATABASE todo_db;
--   2) Conecte no banco todo_db e execute o restante deste script:
--        psql -U postgres -d todo_db -f database/script.sql
-- =====================================================================

-- CREATE DATABASE todo_db;

DROP TABLE IF EXISTS tarefa;

CREATE TABLE tarefa (
    id               BIGSERIAL    PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    descricao        VARCHAR(500),
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    observacoes      VARCHAR(500),
    data_criacao     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_tarefa_status CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA'))
);

-- Dados de exemplo (opcional)
INSERT INTO tarefa (nome, descricao, status, observacoes) VALUES
    ('Estudar Spring Boot', 'Revisar os conceitos de JPA e REST', 'EM_ANDAMENTO', 'Foco em testes'),
    ('Fazer compras', 'Comprar itens da semana', 'PENDENTE', NULL),
    ('Entregar o projeto', 'Enviar o link do GitHub no Teams', 'PENDENTE', 'Prazo: 04/10/2026');
