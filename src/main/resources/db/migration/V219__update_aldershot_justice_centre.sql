-- FACT-3067
-- Update Aldershot Justice Centre
UPDATE public.search_court
SET alert = 'The site currently do not have a working lift so the only way to enter the Court building is to use the stairs'
where slug = 'aldershot-justice-centre';
