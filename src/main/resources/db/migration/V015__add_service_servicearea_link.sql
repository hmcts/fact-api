CREATE TABLE public.search_serviceservicearea
(
    id             integer PRIMARY KEY    NOT NULL,
    service_id     integer                NOT NULL,
    servicearea_id integer                NOT NULL
);

CREATE SEQUENCE public.search_serviceservicearea_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_serviceservicearea_id_seq OWNED BY public.search_serviceservicearea.id;

ALTER TABLE ONLY public.search_serviceservicearea
    ALTER COLUMN id SET DEFAULT nextval('public.search_serviceservicearea'::regclass);

ALTER TABLE ONLY public.search_serviceservicearea
    ADD CONSTRAINT fk_search_service_id FOREIGN KEY (service_id) REFERENCES public.search_service (id);

ALTER TABLE ONLY public.search_serviceservicearea
    ADD CONSTRAINT fk_search_servicearea_id FOREIGN KEY (servicearea_id) REFERENCES public.search_servicearea (id);

COPY public.search_serviceservicearea (id, service_id, servicearea_id) FROM stdin;
1	1	1
2	1	2
3	1	3
4	1	4
5	1	5
6	1	6
7	1	7
8	1	8
9	2	2
10	2	9
11	2	10
12	2	15
13	3	11
14	3	12
15	4	13
16	4	14
17	4	15
18	6	8
19	6	16
20	5	17
21	7	18
\.

COPY public.search_serviceareacourt (id, servicearea_id, court_id, catchment_type) FROM stdin;
1	1	1479943	national
2	2	1479430	national
3	5	1479426	national
4	9	1479448	national
5	10	1479448	national
6	8	1479978	national
7	16	1479831	national
8	17	1479450	national
\.
