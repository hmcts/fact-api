-- FACT-1523
-- make Maintenance Enforcement Business Centre to not in person court

INSERT INTO public.search_inperson
(id, is_in_person, court_id, access_scheme, common_platform)
VALUES
  (
    DEFAULT,
   false,
    (
      SELECT id
      FROM public.search_court
      WHERE slug = 'maintenance-enforcement-business-centre-england' or slug = 'maintenance-enforcement-business-centre'
    ),
    false,
    false
  );


