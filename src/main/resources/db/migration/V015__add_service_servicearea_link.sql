CREATE TABLE public.search_serviceservicearea
(
	id             integer PRIMARY KEY NOT NULL,
	service_id     integer             NOT NULL,
	servicearea_id integer             NOT NULL
);

CREATE SEQUENCE public.search_serviceservicearea_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_serviceservicearea_id_seq OWNED BY public.search_serviceservicearea.id;

ALTER TABLE ONLY public.search_serviceservicearea
	ALTER COLUMN id SET DEFAULT nextval('public.search_serviceservicearea_id_seq'::regclass);

ALTER TABLE ONLY public.search_serviceservicearea
	ADD CONSTRAINT fk_search_service_id FOREIGN KEY (service_id) REFERENCES public.search_service (id);

ALTER TABLE ONLY public.search_serviceservicearea
	ADD CONSTRAINT fk_search_servicearea_id FOREIGN KEY (servicearea_id) REFERENCES public.search_servicearea (id);

COPY public.search_serviceservicearea (service_id, servicearea_id) FROM stdin;
1	1
1	2
1	3
1	4
1	5
1	6
1	7
1	8
2	2
2	9
2	10
2	15
3	11
3	12
4	13
4	14
4	15
6	8
6	16
5	17
7	18
\.

COPY public.search_serviceareacourt (servicearea_id, court_id, catchment_type) FROM stdin;
12	1479373	local
4	1479373	local
5	1479627	local
11	1479373	local
10	1479978	local
6	1479429	local
16	1479367	local
8	1479373	local
9	1479373	local
13	1479373	local
15	1479373	local
14	1479373	local
3	1479373	local
18	1479373	local
17	1479444	local
1	1479373	local
2	1479651	regional
\.
