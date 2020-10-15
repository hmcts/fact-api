CREATE TABLE public.search_servicearea (
    id integer PRIMARY KEY NOT NULL,
    service_area CHARACTER VARYING(250)  NOT NULL,
    court_id integer NOT NULL
);

CREATE SEQUENCE public.search_servicearea_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY public.search_servicearea
    ADD CONSTRAINT fk_search_court_id FOREIGN KEY (court_id) REFERENCES public.search_court(id);

COPY public.search_servicearea (id, service_area, court_id) FROM stdin;
1	divorce	1479885
2	dicorce	1479472
3	divorce	1479925
4	divorce	1479978
\.
