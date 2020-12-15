COPY public.search_areaoflaw (name, external_link, external_link_desc, external_link_cy, external_link_desc_cy,
							  display_name) FROM stdin;
Forced marriage	https%3A//www.gov.uk/apply-forced-marriage-protection-order	Information about forced marriage protection orders	https://www.gov.uk/apply-forced-marriage-protection-order	Gwybodaeth ynglŷn â gorchmynion amddiffyn priodas dan orfod	\N
FGM	https://www.gov.uk/government/collections/female-genital-mutilation	\N	https://www.gov.uk/government/collections/female-genital-mutilation	\N	Female Genital Mutilation
Single justice procedure	\N	\N	\N	\N	\N
\.

COPY public.search_courtareaoflaw (court_id, area_of_law_id, single_point_of_entry) FROM stdin;
1479530	34264	FALSE
1479547	34264	FALSE
1479781	34264	FALSE
1479789	34264	FALSE
1479799	34264	FALSE
1479822	34264	FALSE
1479835	34264	FALSE
1479861	34264	FALSE
1479936	34264	FALSE
1479938	34264	FALSE
1479939	34264	FALSE
1479941	34264	FALSE
1479948	34264	FALSE
1480133	34264	FALSE
1479970	34264	FALSE
1479976	34264	FALSE
1479978	34264	FALSE
1480005	34264	FALSE
1479373	34264	FALSE
1479382	34264	FALSE
1479401	34264	FALSE
1479409	34264	FALSE
1479437	34264	FALSE
1479463	34264	FALSE
1480135	34264	FALSE
1480134	34264	FALSE
1479530	34265	FALSE
1479547	34265	FALSE
1479781	34265	FALSE
1479789	34265	FALSE
1479799	34265	FALSE
1479822	34265	FALSE
1479835	34265	FALSE
1479861	34265	FALSE
1479936	34265	FALSE
1479938	34265	FALSE
1479939	34265	FALSE
1479941	34265	FALSE
1479948	34265	FALSE
1480133	34265	FALSE
1479970	34265	FALSE
1479976	34265	FALSE
1479978	34265	FALSE
1480005	34265	FALSE
1479373	34265	FALSE
1479382	34265	FALSE
1479401	34265	FALSE
1479409	34265	FALSE
1479437	34265	FALSE
1479463	34265	FALSE
1480135	34265	FALSE
1480134	34265	FALSE
\.

UPDATE public.search_servicearea
SET area_of_law_id = 34264
WHERE name = 'Forced marriage';

UPDATE public.search_servicearea
SET area_of_law_id = 34265
WHERE name = 'Female Genital Mutilation';

UPDATE public.search_servicearea
SET area_of_law_id = 34266
WHERE name = 'Minor criminal offences';

DELETE
from search_courtareaoflaw
where area_of_law_id = 34258;

DELETE
from search_areaoflaw
where name = 'Forced marriage and FGM';
