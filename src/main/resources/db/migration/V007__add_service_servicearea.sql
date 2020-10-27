CREATE TABLE public.search_serviceservicearea
(
	id             integer NOT NULL,
	service_id     integer NOT NULL,
	servicearea_id integer NOT NULL
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

COPY public.search_serviceservicearea (id, service_id, servicearea_id) FROM stdin;
1	1	1
2	1	2
3	1	3
4	1	4
5	1	5
6	1	6
7	1	7
8	1	8
9	2	9
10	2	10
11	2	11
12	2	12
13	3	13
14	3	14
15	4	15
16	4	16
17	4	17
18	5	18
19	5	19
\.
