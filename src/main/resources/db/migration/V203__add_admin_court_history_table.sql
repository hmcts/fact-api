-- FACT-1500
-- create new admin_court_history table

CREATE TABLE public.admin_court_history (
  id integer PRIMARY KEY NOT NULL,
  search_court_id integer NOT NULL,
  court_name character varying(255) NOT NULL,
  updated_at timestamp NOT NULL,
  created_at timestamp NOT NULL
);

CREATE SEQUENCE public.admin_court_history_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_court_history_id_seq OWNED BY public.admin_court_history.id;
ALTER TABLE ONLY public.admin_court_history ALTER COLUMN id SET DEFAULT nextval('public.admin_court_history_id_seq'::regclass);
ALTER TABLE ONLY public.admin_court_history ADD CONSTRAINT fk_admin_court_history_search_court_id FOREIGN KEY (search_court_id) REFERENCES public.search_court(id);
