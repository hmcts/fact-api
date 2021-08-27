CREATE TABLE public.admin_audit (
  id integer PRIMARY KEY NOT NULL,
  user_email character varying(255) NOT NULL,
  action_id integer NOT NULL,
  action_data_before character varying,
  action_data_after character varying,
  location character varying(255),
  creation_time timestamp NOT NULL);

CREATE TABLE public.admin_audittype (
    id integer PRIMARY KEY NOT NULL,
    name character varying(255) NOT NULL);

CREATE SEQUENCE public.admin_audit_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE public.admin_audittype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_audit_id_seq OWNED BY public.admin_audit.id;
ALTER TABLE ONLY public.admin_audit ALTER COLUMN id SET DEFAULT nextval('public.admin_audit_id_seq'::regclass);

ALTER SEQUENCE public.admin_audittype_id_seq OWNED BY public.admin_audittype.id;
ALTER TABLE ONLY public.admin_audittype ALTER COLUMN id SET DEFAULT nextval('public.admin_audittype_id_seq'::regclass);

INSERT INTO public.admin_audittype(name)
VALUES('Update area of law'), ('Update local authority'), ('Update court areas of law'), ('Create area of law'),
       ('Update court additional links'), ('Update court addresses and coordinates'),
       ('Update court contacts'), ('Update court email list'), ('Update court facilities'), ('Update court general info'),
       ('Update court local authorities'), ('Update court opening times'), ('Create court postcodes'), ('Delete court postcodes'),
       ('Move court postcodes'), ('Update court court types'), ('Update court details');
