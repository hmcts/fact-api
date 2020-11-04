TRUNCATE TABLE public.search_service CASCADE;
ALTER SEQUENCE public.search_service_id_seq RESTART;
ALTER SEQUENCE public.search_servicearea_id_seq RESTART;

ALTER TABLE public.search_service
	ADD COLUMN slug character varying(255) UNIQUE NOT NULL;

COPY public.search_service (name, description, name_cy, description_cy, slug) FROM stdin;
Money	Money claims, rent or mortgage disputes, bankruptcy, job disputes related to pay, appealing a tax or benefits decision.			money
Probate, divorce or ending civil partnerships	Probate application and disputes, divorce, ending a civil partnership.			probate-divorce-or-ending-civil-partnerships
Childcare and parenting	Arrangements for looking after your children if you separate from your partner or making an adoption legal.			childcare-and-parenting
Harm and abuse	Applying for an injunction against someone who is harassing or abusing you, being made to marry against your will or preventing Female Genital Mutilation (FGM).			harm-and-abuse
Immigration and asylum	Seeking asylum, right to live in the UK, and appealing deportation.			immigration-and-asylum
Crime	Minor criminal fines for TV licenses or speeding, major criminal cases at a Crown or Magistrates’ Court.			crime
High Court district registries	Courts that deal with the most serious and high profile cases in criminal and civil law.			high-court-district-registries
\.

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


ALTER TABLE ONLY public.search_courtservicearea
	ALTER COLUMN id SET DEFAULT nextval('public.search_courtservicearea_id_seq'::regclass);

ALTER SEQUENCE public.search_courtservicearea_id_seq RESTART;

COPY public.search_courtservicearea (court_id, servicearea_id) FROM stdin;
1479885	10
1479472	10
1479925	10
1479978	10
\.

