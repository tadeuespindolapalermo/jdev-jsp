CREATE DATABASE "projeto-jsp-novo"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Portuguese_Brazil.1252'
    LC_CTYPE = 'Portuguese_Brazil.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
    
-- --------------------------------------

CREATE SEQUENCE public.model_login_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE public.model_login_id_seq
    OWNER TO postgres;

-- --------------------------------------
    
CREATE TABLE public.model_login
(
    login character varying(200) COLLATE pg_catalog."default" NOT NULL,
    senha character varying(200) COLLATE pg_catalog."default" NOT NULL,
    id integer NOT NULL DEFAULT nextval('model_login_id_seq'::regclass),
    nome character varying(300) COLLATE pg_catalog."default" NOT NULL,
    email character varying(300) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT model_login_pkey PRIMARY KEY (id),
    CONSTRAINT login_unique UNIQUE (login)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.model_login
    OWNER to postgres;
    
-- --------------------------------------

-- COMANDOS UTILIZADOS PARA ALTERAR A TABELA v1 E GERAR A v2

ALTER TABLE model_login ADD id serial PRIMARY KEY;
ALTER TABLE model_login ADD nome CHARACTER VARYING(300);
ALTER TABLE model_login ADD email CHARACTER VARYING(300);

UPDATE model_login SET nome = 'Adminstrador';
ALTER TABLE model_login ALTER COLUMN nome SET NOT NULL;

UPDATE model_login SET email = 'admin@admin.com.br';
ALTER TABLE model_login ALTER COLUMN email SET NOT NULL;

ALTER TABLE model_login ALTER COLUMN id SET NOT NULL;
ALTER TABLE model_login ALTER COLUMN login SET NOT NULL;
ALTER TABLE model_login ALTER COLUMN senha SET NOT NULL;