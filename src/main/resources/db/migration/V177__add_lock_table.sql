CREATE TABLE public.admin_courtlock (
  id integer PRIMARY KEY NOT NULL,
  lock_acquired timestamp NOT NULL,
  user_email character varying,
  court_slug character varying);

CREATE SEQUENCE public.admin_courtlock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_courtlock_id_seq OWNED BY public.admin_courtlock.id;
ALTER TABLE ONLY public.admin_courtlock ALTER COLUMN id SET DEFAULT nextval('public.admin_courtlock_id_seq'::regclass);
