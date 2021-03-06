CREATE TABLE public.search_inperson (
    id integer PRIMARY KEY NOT NULL,
    is_in_person boolean NOT NULL,
    court_id integer NOT NULL
);

CREATE SEQUENCE public.search_inperson_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_inperson_id_seq OWNED BY public.search_inperson.id;

ALTER TABLE ONLY public.search_inperson
    ADD CONSTRAINT fk_search_court_id FOREIGN KEY (court_id) REFERENCES public.search_court(id);

COPY public.search_inperson (id, is_in_person, court_id) FROM stdin;
1	t	1479885
2	t	1479358
3	f	1479925
4	t	1479978
\.
