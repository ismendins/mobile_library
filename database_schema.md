# Estrutura do Banco de Dados Supabase

## aprovacao_emprestimos
- id (int8) - PK
- usuario_id (int4) - FK
- livro_id (int4) - FK
- data_solicitacao (timestamp)
- data_inicio (timestamp)
- data_fim (timestamp)
- status (varchar)
- observacoes (text)

## convidados_eventos
- id (int8) - PK
- evento_id (int8) - FK
- nome (varchar)
- descricao (varchar)
- created_at (timestamptz)

## eventos
- id (int8) - PK
- nome (varchar)
- tipo (varchar)
- local (varchar)
- data_hora (timestamp)
- descricao (varchar)
- created_at (timestamptz)
- imagem_url (varchar)

## avaliacoes
- id (int8) - PK
- usuario_id (int4) - FK
- livro_id (int4) - FK
- nota (int4)
- comentario (text)
- created_at (timestamptz)

## emprestimos
- id (int8) - PK
- usuario_id (int4) - FK
- data_inicio (timestamp)
- data_prevista_fim (timestamp)
- data_devolucao (timestamp)
- status (varchar)

## exemplares
- id (int8) - PK
- livro_id (int4) - FK
- registro (varchar)
- isbn (varchar)
- editora (varchar)
- edicao (varchar)
- ano (int4)
- suporte (varchar)
- localizacao (varchar)
- situacao (varchar)
- sinopse (text)
- criado_em (timestamptz)
- atualizado_em (timestamptz)
(aguardando estrutura)

## livros
- id (int4) - PK
- titulo (varchar)
- autores (varchar)
- idiomas (varchar)
- capa_url (varchar)
- criado_em (timestamptz)
- atualizado_em (timestamptz)
- titulo
- autores
- idiomas
- capa_url
- criado_em
- atualizado_em
(aguardando estrutura)

## usuarios
- id (int4) - PK
- nome_completo (varchar)
- email (varchar)
- matricula (varchar)
- senha_hash (varchar)
- data_cadastro (timestamptz)
- total_lidos (int4)
- tipo_usuario (varchar)
- nome_completo
- email
- matricula
- senha_hash
- data_cadastro
- total_lidos
- tipo_usuario
(aguardando estrutura)
