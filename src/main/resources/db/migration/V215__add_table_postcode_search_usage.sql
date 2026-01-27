CREATE TABLE public.postcode_search_usage (
  id integer PRIMARY KEY NOT NULL,
  full_postcode VARCHAR(10) NOT NULL,
  cache_postcode VARCHAR(10) NOT NULL,
  search_timestamp timestamp NOT NULL
);

CREATE SEQUENCE public.postcode_search_usage_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

ALTER SEQUENCE public.postcode_search_usage_id_seq OWNED BY public.postcode_search_usage.id;
ALTER TABLE ONLY public.postcode_search_usage ALTER COLUMN id SET DEFAULT nextval('public.postcode_search_usage_id_seq'::regclass);
