INSERT INTO search_serviceareacourt(servicearea_id, court_id, catchment_type)
SELECT
	( SELECT id FROM public.search_servicearea WHERE slug = 'money-claims' ) as servicearea_id,
    court.id,
	'national'
FROM public.search_court court
WHERE slug IN
      ('civil-money-claims-service-centre',
       'county-court-business-centre-ccbc');
