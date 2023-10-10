-- FACT-1507
-- Update CCBC to CNBC to match production data changes
UPDATE public.search_court
SET
  "name" = 'Civil National Business Centre (CNBC)',
  "slug" = 'civil-national-business-centre-cnbc'
WHERE slug = 'county-court-business-centre-ccbc';

-- Add CNBC to service-area-courts list for money-claims
INSERT INTO public.search_serviceareacourt
(id, servicearea_id, court_id, catchment_type)
VALUES
  (
    DEFAULT,
    1,
    (
      SELECT id
      FROM public.search_court
      WHERE slug = 'civil-national-business-centre-cnbc'
    ),
    'national'
  );

-- Set all non CNBC courts on money-claims service area list to have local catchment type
UPDATE public.search_serviceareacourt
SET catchment_type = 'local'
WHERE servicearea_id = '1'
  AND court_id != (
  SELECT id
  FROM public.search_court
  WHERE slug = 'civil-national-business-centre-cnbc'
);
