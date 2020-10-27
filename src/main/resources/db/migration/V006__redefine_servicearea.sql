TRUNCATE table public.search_servicearea;

ALTER SEQUENCE public.search_servicearea_id_seq RESTART;

ALTER TABLE ONLY public.search_servicearea
	ALTER COLUMN id SET DEFAULT nextval('public.search_servicearea_id_seq'::regclass);

ALTER TABLE public.search_servicearea
	DROP COLUMN court_id;

ALTER TABLE public.search_servicearea
	RENAME COLUMN service TO servicearea;

ALTER TABLE public.search_servicearea
	ADD COLUMN description CHARACTER VARYING(500) NOT NULL;

COPY public.search_servicearea (servicearea, description) FROM stdin;
Money claims	Claims for when you are owed money or responding to money claims against you.
Probate	Settlement and disputes over a deceased person's property, money and possessions, storing a will.
Housing	Tenant evictions and rent or mortgage disputes.
Bankruptcy	Opposing a bankruptcy petition, support for a person at risk of violence order, canceling a bankruptcy.
Benefits	Appealing entitlement to benefits such as Personal Independence Payment (PIP), Employment and Support Allowance (ESA) or Universal Credit.
Claims against employers	Appealing a decision about employment disputes related to pay, unfair dismissal and discrimination.
Tax	Appealing a tax decision.
Minor criminal offences	TV licence fines, speeding or parking fines.
Probate	Applying for probate, storing a will, settlement and disputes over a deceased person's property, money and possessions
Divorce	Ending a marriage, getting a legal separation or help with money, property and children during a divorce.
Civil partnership	Ending a civil partnership.
Forced marriage	Being made to marry against your will.
Childcare arrangements if you separate from your partner	Making child arrangements if you divorce or separate.
Adoption	Support for a child adoption application.
Domestic abuse	Support for an injunction if you've been the victim of domestic abuse.
Female Genital Mutilation	Support for legal protection for victims of female genital mutilation.
Forced marriage	Being made to marry against your will.
Minor criminal offences	TV licence fines, speeding or parking fines.
Major criminal offences	Major criminal cases at a Crown or Magistrates’ Court.
\.
