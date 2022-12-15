-- FACT_1194
INSERT INTO public.search_serviceareacourt(
  id, servicearea_id, court_id, catchment_type)
VALUES (DEFAULT, 1, ( SELECT id FROM public.search_court
                      WHERE slug = 'online-civil-money-claims-service-centre' or slug = 'civil-money-claims-service-centre'), 'local');
