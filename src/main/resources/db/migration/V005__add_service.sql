CREATE TABLE public.search_service
(
	id          integer PRIMARY KEY    NOT NULL,
	name        character varying(250) NOT NULL,
	description character varying(500) NOT NULL
);

CREATE SEQUENCE public.search_service_id_seq
	AS integer
	START WITH 1
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_service_id_seq OWNED BY public.search_service.id;

ALTER TABLE ONLY public.search_service
	ALTER COLUMN id SET DEFAULT nextval('public.search_service_id_seq'::regclass);

COPY public.search_service (name, description) FROM stdin;
Money	Money claims, rent or mortgage disputes, bankruptcy, job disputes related to pay, appealing a tax or benefits decision.
Probate, divorce or ending civil partnerships	Probate application and disputes, divorce, ending a civil partnership.
Childcare and parenting	Arrangements for looking after your children if you separate from your partner or making an adoption legal.
Harm and abuse	Applying for an injunction against someone who is harassing or abusing you, being made to marry against your will or preventing Female Genital Mutilation (FGM).
Immigration and asylum	Seeking asylum, right to live in the UK, and appealing deportation.
Crime	Minor criminal fines for TV licenses or speeding, major criminal cases at a Crown or Magistrates’ Court.
High Court district registries	Courts that deal with the most serious and high profile cases in criminal and civil law.
\.
