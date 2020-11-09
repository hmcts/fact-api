COPY public.search_court (id, admin_id, name, slug, displayed, lat, lon, number, alert, directions, image_file,
						  cci_code, updated_at, created_at, parking_id, info, hide_aols, defence_leaflet, info_leaflet,
						  juror_leaflet, prosecution_leaflet, magistrate_code, welsh_enabled, alert_cy, directions_cy,
						  info_cy, defence_leaflet_cy, info_leaflet_cy, prosecution_leaflet_cy, name_cy,
						  gbs) FROM stdin;
1480147	\N	Divorce Service Centre	divorce-service-centre	t	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N
1480148	\N	Minor Criminal Offences Service Centre	minor-criminal-offences-service-centre	t	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	f	\N	\N	\N	\N	\N	\N	\N	\N
\.

TRUNCATE TABLE public.search_serviceareacourt;

ALTER SEQUENCE public.search_serviceareacourt_id_seq RESTART;

COPY public.search_serviceareacourt (servicearea_id, court_id, catchment_type) FROM stdin;
1	1479943	national
5	1480140	national
9	1480142	regional
10	1480142	regional
16	1480143	national
17	1480144	national
2	1480146	national
9	1480147	national
8	1480148	national
\.
