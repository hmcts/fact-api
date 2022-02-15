CREATE TABLE public.search_servicecentre
(
    id             integer PRIMARY KEY    NOT NULL,
    court_id       integer                NOT NULL,
    intro_paragraph character varying(300),
    intro_paragraph_cy character varying(300)
);

CREATE SEQUENCE public.search_servicecentre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
