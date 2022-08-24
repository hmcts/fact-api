CREATE TABLE public.search_region
(
  id             integer PRIMARY KEY NOT NULL,
  name           character varying(250),
  country        character varying(250)
);

CREATE SEQUENCE public.search_region_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

ALTER SEQUENCE public.search_region_id_seq OWNED BY public.search_region.id;

ALTER TABLE ONLY public.search_region
ALTER COLUMN id SET DEFAULT nextval('public.search_region_id_seq'::regclass);

COPY public.search_region (name, country) FROM stdin;
Eastern England
North West England
North East England
South East England
South West England
East Midlands England
London England
West Midlands England
Yorkshire and the Humber England
South Wales West Wales
South Wales Central Wales
South Wales East Wales
Mid and West Wales Wales
North Wales Wales
\.
