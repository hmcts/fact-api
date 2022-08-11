-- FACT-1228
CREATE TABLE public.search_courtsecondaryaddresstype
(
    id             integer PRIMARY KEY NOT NULL,
    address_id     integer             NOT NULL,
    area_of_law_id integer,
    court_type_id  integer
);

CREATE SEQUENCE public.search_courtsecondaryaddresstype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.search_courtsecondaryaddresstype_id_seq OWNED BY public.search_courtsecondaryaddresstype.id;
ALTER TABLE ONLY public.search_courtsecondaryaddresstype ALTER COLUMN id SET DEFAULT nextval('public.search_courtsecondaryaddresstype_id_seq'::regclass);

-- Remove description and description cy columns from address table
ALTER TABLE ONLY public.search_courtaddress DROP COLUMN description;
ALTER TABLE ONLY public.search_courtaddress DROP COLUMN description_cy;
