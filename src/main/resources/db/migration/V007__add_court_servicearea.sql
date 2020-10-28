CREATE TABLE public.search_courtservicearea
(
	id             integer PRIMARY KEY NOT NULL,
	court_id       integer             NOT NULL,
	servicearea_id integer             NOT NULL
);

CREATE SEQUENCE public.search_courtservicearea_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_courtservicearea_id_seq OWNED BY public.search_courtservicearea.id;

ALTER TABLE ONLY public.search_courtservicearea
	ALTER COLUMN id SET DEFAULT nextval('public.search_courtservicearea'::regclass);


ALTER TABLE ONLY public.search_courtservicearea
	ADD CONSTRAINT fk_search_court_id FOREIGN KEY (court_id) REFERENCES public.search_court (id);

ALTER TABLE ONLY public.search_courtservicearea
	ADD CONSTRAINT fk_search_servicearea_id FOREIGN KEY (servicearea_id) REFERENCES public.search_servicearea (id);

COPY public.search_courtservicearea (id, court_id, servicearea_id) FROM stdin;
1	1479885	10
2	1479472	10
3	1479925	10
4	1479978	10
\.
