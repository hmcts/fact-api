ALTER TABLE public.search_inperson
	ADD COLUMN access_scheme boolean NOT NULL default false
		CONSTRAINT search_inperson_check CHECK (NOT (is_in_person = false AND access_scheme = true));

COPY public.search_inperson (id, is_in_person, court_id, access_scheme) FROM stdin;
5	t	1479949	t
6	t	1479970	t
7	t	1479729	f
\.
