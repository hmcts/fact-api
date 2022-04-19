--- FACT-1150 ---
INSERT INTO public.search_serviceareacourt (servicearea_id, court_id, catchment_type)
SELECT servicearea.id , court.id, 'national'
FROM (select * from search_servicearea where slug = 'money-claims') servicearea,
     (select * from search_court where slug='small-claims-mediation-service-scms') court
WHERE EXISTS (SELECT 1
              FROM search_court
              WHERE slug = 'small-claims-mediation-service-scms' );

