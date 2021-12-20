CREATE DATABASE "projeto-jsp-novo"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Portuguese_Brazil.1252'
    LC_CTYPE = 'Portuguese_Brazil.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
    
-- --------------------------------------
    
CREATE TABLE public.model_login
(
    login character varying(200) COLLATE pg_catalog."default",
    senha character varying(200) COLLATE pg_catalog."default",
    CONSTRAINT login_unique UNIQUE (login)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.model_login
    OWNER to postgres;
    
-- --------------------------------------
    
INSERT INTO public.model_login (login, senha) VALUES ('admin', 'admin');

-- --------------------------------------