--to fix data issue for fact frontend failing functional tests--
DELETE FROM public.search_serviceareacourt
  WHERE court_id = 1479504;

