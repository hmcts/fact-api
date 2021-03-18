DELETE FROM public.search_serviceareacourt
WHERE court_id IN (
    SELECT id
    FROM public.search_court
    WHERE slug IN
          ('civil-money-claims-service-centre',
           'county-court-business-centre-ccbc'
          )
	);
