--- Fix data for fact frontend failing tests---
INSERT INTO public.search_serviceareacourt (servicearea_id, court_id, catchment_type)
SELECT servicearea.id , court.id, 'national'
FROM (select * from search_servicearea where slug = 'money-claims') servicearea,
     (select * from search_court where slug='county-court-money-claims-centre-ccmcc') court
WHERE NOT EXISTS (select 1 from  public.search_serviceareacourt where court_id = (SELECT id
              FROM public.search_court
              WHERE slug = 'county-court-money-claims-centre-ccmcc' ) and servicearea_id = 1 and catchment_type = 'national');
