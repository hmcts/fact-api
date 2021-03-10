TRUNCATE TABLE public.search_servicearea CASCADE;
ALTER SEQUENCE public.search_servicearea_id_seq RESTART;

ALTER TABLE public.search_servicearea
	ADD COLUMN slug              character varying(255) UNIQUE NOT NULL,
	ADD COLUMN service_area_type character varying(250),
	ADD COLUMN online_url        character varying(250),
	ADD COLUMN online_text       character varying(250),
	ADD COLUMN online_text_cy    character varying(250);

ALTER TABLE public.search_servicearea
	DROP service_id;

COPY public.search_servicearea (slug, name, name_cy, description, description_cy, online_url, online_text) FROM stdin;
money-claims	Money claims	Hawliadau am arian	Claims for when you are owed money or responding to money claims against you.	Hawliadau pan mae arian yn ddyledus ichi neu ymateb i hawliadau am arian yn eich erbyn.	https://www.gov.uk/make-money-claim	Make a money claim online
probate	Probate	Profiant	Applying for probate, storing a will, settlement and disputes over a deceased person's property, money and possessions	Gwneud cais am brofiant, storio ewyllys, setliad ac anghydfod ynghylch eiddo, arian a meddiannau unigolyn ymadawedig	https://www.gov.uk/applying-for-probate/apply-for-probate	Apply for probate online
bankruptcy	Bankruptcy	Methdaliad	Opposing a bankruptcy petition, support for a person at risk of violence order, cancelling a bankruptcy.	Gwrthwynebu deiseb methdaliad, gorchymyn i gefnogi unigolyn sydd mewn perygl o drais, canslo methdaliad	https://www.gov.uk/apply-for-bankruptcy	\N
housing	Housing	Tai	Tenant evictions and rent or mortgage disputes.	Dadfeddiannu tenantiaid ac anghydfod rhent neu forgais.	https://www.gov.uk/possession-claim-online-recover-property	Make or respond to a possession claim online
benefits	Benefits	Budd-daliadau	Appealing entitlement to benefits such as Personal Independence Payment (PIP), Employment and Support Allowance (ESA) or Universal Credit.	Apelio yn erbyn hawl i gael budd-daliadau fel Taliad Annibyniaeth Personol (PIP), Lwfans Cyflogaeth a Chymorth (ESA) neu Gredyd Cynhwysol	https://www.gov.uk/appeal-benefit-decision/submit-appeal	Appeal a benefits decision online
claims-against-employers	Claims against employers	Honiadau yn erbyn cyflogwyr	Appealing a decision about employment disputes related to pay, unfair dismissal and discrimination.	Apelio yn erbyn penderfyniad am anghydfod cyflogaeth sy'n ymwneud â thâl, diswyddo annheg a gwahaniaethu.		\N
tax	Tax	Treth	Appealing a tax decision.	Apelio yn erbyn penderfyniad treth.		\N
minor-criminal-offences	Minor criminal offences	Mân droseddau	TV licence fines, speeding or parking fines.	Dirwyon trwydded deledu, dirwyon goryrru neu barcio.		\N
divorce	Divorce	Ysgariad	Ending a marriage, getting a legal separation or help with money, property and children during a divorce.	Dod â phriodas i ben, gwahanu’n gyfreithiol neu gael help gydag arian, eiddo a phlant yn ystod ysgariad.	https://www.gov.uk/apply-for-divorce	Apply for a divorce online
civil-partnership	Civil partnership	Partneriaeth sifil	Ending a civil partnership.	Dod â phartneriaeth sifil i ben.		\N
childcare-arrangements	Childcare arrangements if you separate from your partner	Trefniadau gofal plant os ydych yn gwahanu oddi wrth eich partner.	Making child arrangements if you divorce or separate.	Gwneud trefniadau plant os ydych yn ysgaru neu’n gwahanu.	https://apply-to-court-about-child-arrangements.service.justice.gov.uk/	null
adoption	Adoption	Mabwysiadu	Support for a child adoption application.	Cymorth ar gyfer cais i fabwysiadu plentyn.		\N
domestic-abuse	Domestic abuse	Cam-drin domestig	Support for an injunction if you've been the victim of domestic abuse.	Cymorth ar gyfer gwaharddeb os ydych wedi dioddef cam-drin domestig.		\N
female-genital-mutilation	Female Genital Mutilation	Anffurfio Organau Cenhedlu Benywod	Support for legal protection for victims of female genital mutilation.	Cymorth i ddiogelu'n gyfreithiol dioddefwyr anffurfio organau cenhedlu benywod.		\N
forced-marriage	Forced marriage	Priodas dan orfod	Being made to marry against your will.	Cael eich gorfodi i briodi yn erbyn eich ewyllys.		\N
major-criminal-offences	Major criminal offences	Troseddau mawr	Major criminal cases at a Crown or Magistrates’ Court.	Achosion troseddol mawr yn Llys y Goron neu mewn Llys Ynadon.		\N
immigration	Immigration	Mewnfudo	Seeking asylum, right to live in the UK, and appealing deportation.	Ceisio lloches, hawl i fyw yn y DU, ac apelio yn erbyn allgludiad.	https://www.gov.uk/immigration-asylum-tribunal	Appeal against a visa or immigration decision
high-court-district-registries	High Court district registries	cofrestrfa dosbarth yr Uchel Lys	Courts that deal with the most serious and high profile cases in criminal and civil law.	Llysoedd sy'n delio â'r achosion mwyaf difrifol a phroffil uchel mewn cyfraith droseddol a chyfraith sifil.		\N
\.
