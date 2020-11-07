CREATE TABLE public.search_serviceareacourt
(
	id             integer PRIMARY KEY    NOT NULL,
	servicearea_id integer                NOT NULL,
	court_id       integer                NOT NULL,
	catchment_type character varying(250) NOT NULL
);

CREATE SEQUENCE public.search_serviceareacourt_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_serviceareacourt_id_seq OWNED BY public.search_serviceareacourt.id;

ALTER TABLE ONLY public.search_serviceareacourt
	ALTER COLUMN id SET DEFAULT nextval('public.search_serviceareacourt_id_seq'::regclass);


ALTER TABLE ONLY public.search_serviceareacourt
	ADD CONSTRAINT fk_search_court_id FOREIGN KEY (court_id) REFERENCES public.search_court (id);

ALTER TABLE ONLY public.search_serviceareacourt
	ADD CONSTRAINT fk_search_servicearea_id FOREIGN KEY (servicearea_id) REFERENCES public.search_servicearea (id);

ALTER TABLE ONLY public.search_serviceareacourt
	ADD CONSTRAINT con_catchment_type CHECK (catchment_type IN ('national', 'regional', 'local'));
