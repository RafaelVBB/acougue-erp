-- Dados iniciais para o sistema de açougue
INSERT INTO produtos (nome, descricao, categoria, corte, preco_custo, preco_venda, percentual_perda, percentual_quebra, unidade_medida, estoque_atual, estoque_minimo, ativo, data_cadastro, data_atualizacao)
VALUES
('Picanha', 'Picanha bovina premium', 'BOVINA', 'Picanha', 45.00, 65.00, 5.00, 2.00, 'KG', 50, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alcatra', 'Alcatra bovina', 'BOVINA', 'Alcatra', 35.00, 52.00, 4.00, 1.50, 'KG', 30, 8, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Contra Filé', 'Contra filé bovino', 'BOVINA', 'Contra Filé', 38.00, 55.00, 3.00, 1.00, 'KG', 25, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lombo Suíno', 'Lombo suíno', 'SUINA', 'Lombo', 22.00, 35.00, 6.00, 2.50, 'KG', 40, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Peito de Frango', 'Peito de frango', 'FRANGO', 'Peito', 12.00, 18.00, 3.00, 1.00, 'KG', 60, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Linguiça Caseira', 'Linguiça caseira', 'LINGUICA', 'Tradicional', 18.00, 28.00, 2.00, 1.00, 'KG', 40, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);