--to fix data issue for fact frontend failing functional tests--
DELETE FROM public.search_serviceareacourt
  WHERE court_id = (SELECT id
                         FROM search_court
                         WHERE slug = 'pocock-street-tribunal-hearing-centre')

