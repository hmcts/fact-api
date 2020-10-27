TRUNCATE table public.search_servicearea;

ALTER SEQUENCE public.search_servicearea_id_seq RESTART;

ALTER TABLE ONLY public.search_servicearea
	ALTER COLUMN id SET DEFAULT nextval('public.search_servicearea_id_seq'::regclass);

ALTER TABLE public.search_servicearea
	DROP COLUMN court_id;

ALTER TABLE public.search_servicearea
	RENAME COLUMN service TO name;

ALTER TABLE public.search_servicearea
	ADD COLUMN description CHARACTER VARYING(500) NOT NULL;

ALTER TABLE public.search_servicearea
	ADD COLUMN service_id integer NOT NULL;

ALTER TABLE ONLY public.search_servicearea
	ADD CONSTRAINT fk_search_service_id FOREIGN KEY (service_id) REFERENCES public.search_service (id);

COPY public.search_servicearea (name, description, service_id) FROM stdin;
Money claims	Claims for when you are owed money or responding to money claims against you.	1
Probate	Settlement and disputes over a deceased person's property, money and possessions, storing a will.	1
Housing	Tenant evictions and rent or mortgage disputes.	1
Bankruptcy	Opposing a bankruptcy petition, support for a person at risk of violence order, canceling a bankruptcy.	1
Benefits	Appealing entitlement to benefits such as Personal Independence Payment (PIP), Employment and Support Allowance (ESA) or Universal Credit.	1
Claims against employers	Appealing a decision about employment disputes related to pay, unfair dismissal and discrimination.	1
Tax	Appealing a tax decision.	1
Minor criminal offences	TV licence fines, speeding or parking fines.	1
Probate	Applying for probate, storing a will, settlement and disputes over a deceased person's property, money and possessions	2
Divorce	Ending a marriage, getting a legal separation or help with money, property and children during a divorce.	2
Civil partnership	Ending a civil partnership.	2
Forced marriage	Being made to marry against your will.	2
Childcare arrangements if you separate from your partner	Making child arrangements if you divorce or separate.	3
Adoption	Support for a child adoption application.	3
Domestic abuse	Support for an injunction if you've been the victim of domestic abuse.	4
Female Genital Mutilation	Support for legal protection for victims of female genital mutilation.	4
Forced marriage	Being made to marry against your will.	4
Minor criminal offences	TV licence fines, speeding or parking fines.	6
Major criminal offences	Major criminal cases at a Crown or Magistrates’ Court.	6
\.
