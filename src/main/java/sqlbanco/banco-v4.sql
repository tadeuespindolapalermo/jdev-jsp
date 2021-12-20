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
    useradmin boolean NOT NULL DEFAULT false,
    usuario_id bigint NOT NULL DEFAULT 1,
    CONSTRAINT model_login_pkey PRIMARY KEY (id),
    CONSTRAINT login_unique UNIQUE (login),
    CONSTRAINT usuario_fk FOREIGN KEY (usuario_id)
        REFERENCES public.model_login (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.model_login
    OWNER to postgres;
    
-- --------------------------------------

-- COMANDOS UTILIZADOS PARA ALTERAR A TABELA v3 E GERAR A v4

ALTER TABLE model_login ADD COLUMN usuario_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE model_login ADD CONSTRAINT usuario_fk FOREIGN KEY (usuario_id) REFERENCES model_login (id);