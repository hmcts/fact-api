-- FACT-1228
CREATE TABLE public.search_courtsecondaryaddresstype
(
    id             integer PRIMARY KEY NOT NULL,
    court_id       integer             NOT NULL,
    area_of_law_id integer NULLABLE,
    court_type_id  integer NULLABLE
);

CREATE SEQUENCE public.search_courtsecondaryaddresstype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.search_courtsecondaryaddresstype_id_seq OWNED BY public.search_courtsecondaryaddresstype.id;
ALTER TABLE ONLY public.search_courtsecondaryaddresstype ALTER COLUMN id SET DEFAULT nextval('public.search_courtsecondaryaddresstype_id_seq'::regclass);

--
INSERT INTO public.search_courtsecondaryaddresstype(court_id, area_of_law_id, court_type_id)
VALUES (1479948,
        (SELECT id FROM public.search_areaoflaw WHERE name = 'Money claims'),
        null),
       (1479948,
        null,
        (SELECT id FROM public.search_courttype WHERE name = 'Magistrates'' Court'));
