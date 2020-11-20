DELETE
FROM search_inperson
WHERE id > 9;

COPY public.search_inperson (id, is_in_person, court_id, access_scheme) FROM stdin;
10	f	1480142	f
11	f	1480140	f
12	f	1480136	f
13	f	1480138	f
14	f	1480141	f
\.

DELETE
FROM search_serviceareacourt
WHERE id = 2;
