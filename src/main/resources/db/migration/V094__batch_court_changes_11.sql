--- FACT-501 ---
UPDATE public.search_court
SET alert = ''
WHERE slug = 'blackpool-magistrates-and-civil-court';

--- FACT-484 ---
UPDATE public.search_court
SET cci_code = '270'
WHERE slug = 'teesside-combined-court-centre';

--- FACT-481 ---
UPDATE public.search_court
SET cci_code = '303'
WHERE slug = 'preston-crown-court-and-family-court-sessions-house';

--- FACT-508 ---
UPDATE public.search_court
SET gbs = 'Y459'
WHERE slug = 'dudley-county-court-and-family-court';

--- FACT-509 ---
UPDATE public.search_court
SET gbs = 'Y448'
WHERE slug = 'clerkenwell-and-shoreditch-county-court-and-family-court';
