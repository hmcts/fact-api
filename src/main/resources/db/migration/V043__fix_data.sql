COPY public.search_serviceareacourt (servicearea_id, court_id, catchment_type) FROM stdin;
2	1480143	national
\.

COPY public.search_inperson (id, is_in_person, court_id, access_scheme) FROM stdin;
15	FALSE	1480143	FALSE
\.
