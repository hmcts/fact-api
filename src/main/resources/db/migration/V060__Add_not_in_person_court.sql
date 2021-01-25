INSERT INTO public.search_inperson (is_in_person, court_id, access_scheme)
SELECT false, id, false
FROM public.search_court
WHERE name = 'Civil Money Claims Service Centre';
