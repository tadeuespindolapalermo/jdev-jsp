CREATE SEQUENCE public.versionadorbanco_seq
	INCREMENT 1
	START 1
	MINVALUE 1
	MAXVALUE 2147483647
	CACHE 1;
-- --------------------------------------
CREATE TABLE public.versionadorbanco
(
	id integer NOT NULL DEFAULT nextval('versionadorbanco_seq'::regclass),
	arquivo_sql character varying(50) COLLATE pg_catalog."default" NOT NULL,
	CONSTRAINT versionadorbanco_pkey PRIMARY KEY (id)
);  
-- --------------------------------------