COPY public.search_serviceareacourt (servicearea_id, court_id, catchment_type) FROM stdin;
9	1479995	regional
9	1479925	regional
9	1479413	regional
10	1479995	regional
10	1479925	regional
10	1479413	regional
\.

UPDATE public.search_servicearea
SET catchment_method = 'local-authority'
WHERE slug = 'adoption';
