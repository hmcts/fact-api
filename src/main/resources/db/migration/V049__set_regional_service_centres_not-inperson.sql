ALTER TABLE ONLY public.search_inperson
    ADD UNIQUE (court_id);

COPY public.search_inperson (id, is_in_person, court_id, access_scheme) FROM stdin;
16	FALSE	1479430	FALSE
17	FALSE	1479995	FALSE
18	FALSE	1479413	FALSE
\.
