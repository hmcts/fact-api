ALTER TABLE public.search_courtareaoflaw
ADD COLUMN created_date timestamp,
ADD COLUMN last_modified_date timestamp,
ADD COLUMN created_by character varying(255),
ADD COLUMN last_modified_by character varying(255);

ALTER TABLE public.search_areaoflaw
ADD COLUMN created_date timestamp,
ADD COLUMN last_modified_date timestamp,
ADD COLUMN created_by character varying(255),
ADD COLUMN last_modified_by character varying(255);

CREATE TABLE public.audit (
  id integer PRIMARY KEY NOT NULL,
  user_email character varying(255) NOT NULL,
  action character varying(255) NOT NULL,
  action_data character varying,
  location character varying(255),
  creation_time timestamp);
);

CREATE SEQUENCE public.audit_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.audit_id_seq OWNED BY public.audit.id;
ALTER TABLE ONLY public.audit ALTER COLUMN id SET DEFAULT nextval('public.audit_id_seq'::regclass);
