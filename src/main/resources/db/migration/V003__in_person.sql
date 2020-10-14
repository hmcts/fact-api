CREATE TABLE public.search_inperson (
    id integer PRIMARY KEY NOT NULL,
    in_person boolean
);

CREATE TABLE public.search_courtinperson (
    id integer NOT NULL,
    court_id integer NOT NULL,
    in_person_id integer NOT NULL
);


CREATE SEQUENCE public.search_courtinperson_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_courtinperson_id_seq OWNED BY public.search_courtinperson.id;


COPY public.search_inperson (id, in_person) FROM stdin;
1	true
2	false
\.

COPY public.search_courtinperson (id, court_id, in_person_id) FROM stdin;
1	1479885	1
2	1479472	1
3	1479473	2
4	1479784	2
\.
