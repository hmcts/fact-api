DROP TABLE public.search_courtservicearea;

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
    ALTER COLUMN id SET DEFAULT nextval('public.search_serviceareacourt'::regclass);


ALTER TABLE ONLY public.search_serviceareacourt
    ADD CONSTRAINT fk_search_court_id FOREIGN KEY (court_id) REFERENCES public.search_court (id);

ALTER TABLE ONLY public.search_serviceareacourt
    ADD CONSTRAINT fk_search_servicearea_id FOREIGN KEY (servicearea_id) REFERENCES public.search_servicearea (id);

ALTER TABLE ONLY public.search_serviceareacourt
	ADD CONSTRAINT con_catchment_type CHECK (catchment_type IN ('national', 'regional'));

COPY public.search_servicearea (name, description, service_id) FROM stdin;
Immigration	Seeking asylum, right to live in the UK, and appealing deportation.	5
\.

COPY public.search_serviceareacourt (id, servicearea_id, court_id, catchment_type) FROM stdin;
1	1	1479943	national
2	2	1479430	national
3	5	1479426	national
4	10	1479448	national
5	11	1479448	national
6	18	1479978	national
7	19	1479831	national
8	20	1479450	national
\.
