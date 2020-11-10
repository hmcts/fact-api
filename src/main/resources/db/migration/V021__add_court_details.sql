COPY public.search_courtaddress (address, postcode, address_type_id, court_id, town_name, address_cy,
								 town_name_cy) FROM stdin;
HMCTS Divorce\nPO Box 12625	CM20 9QE 	5881	1480147	Harlow	\N	\N
HMCTS Probate\nPO Box 12625	CM20 9QE 	5881	1480146	Harlow	\N	\N
HMCTS HMCTS Crime Service Centre \nPO Box 12625	B1 WS44	5881	1480143	Birmingham 	\N	\N
\.

COPY public.search_email (description, address, explanation, explanation_cy) FROM stdin;
Enquiries	enquiries.divorce@justice.gov.uk	\N	\N
Enquiries	enquiries.probate@justice.gov.uk	\N	\N
Enquiries	enquiries.social-security-and-child-support-appeals-service-centre@justice.gov.uk	\N	\N
Enquiries	enquiries.crime@justice.gov.uk	\N	\N
Enquiries	enquiries.minor-criminal-offences-service-centre@justice.gov.uk	\N	\N
Enquiries	enquiries.immigration-and-asylum-appeals-service-centre@justice.gov.uk	\N	\N
\.

COPY public.search_courtemail (court_id, email_id, "order") FROM stdin;
1480147	2290025	0
1480146	2290026	0
1480140	2290027	0
1480143	2290028	0
1480148	2290029	0
1480144	2290029	0
\.

COPY public.search_contact (name, number, sort_order, explanation, in_leaflet, explanation_cy) FROM stdin;
DX	702634 Harlow 5	0		t	\N
Enquiries	01234 567 890 	1	Monday to Friday, 8:30am to 5pm 	t	\N
Fax	01264 347 985 	2	\N	t	\N
DX	702634 Birmingham 4	0		t	\N
DX	702634 Stoke 5	0		t	\N
Welsh	0300 303 5174	3	Monday to Friday, 9:00am to 5pm	t	\N
\.

COPY public.search_courtcontact (contact_id, court_id) FROM stdin;
5558556	1480147
5558557	1480147
5558558	1480147
5558561	1480147
5558556	1480146
5558557	1480146
5558558	1480146
5558561	1480146
5558556	1480140
5558557	1480140
5558558	1480140
5558561	1480140
5558559	1480143
5558557	1480143
5558558	1480143
5558561	1480143
5558560	1480148
5558557	1480148
5558558	1480148
5558561	1480148
5558556	1480144
5558557	1480144
5558558	1480144
5558561	1480144
\.

COPY public.search_courtareaoflaw (area_of_law_id, court_id, single_point_of_entry) FROM stdin;
34250	1480147	f
34261	1480146	f
34259	1480144	f
\.
