-- FACT-519
UPDATE public.search_email
SET admin_email_type_id = (SELECT id FROM admin_emailtype WHERE description = 'Listing'),
    explanation = 'Crown court',
    explanation_cy = 'Llys y goron'
WHERE address = 'listing.carlisle.crowncourt@justice.gov.uk'
  AND description = 'Crown Court listing'
  AND id IN (
    SELECT email_id FROM public.search_courtemail
    WHERE court_id = (
        SELECT id FROM search_court
        WHERE slug = 'carlisle-combined-court'
));


-- FACT-520
UPDATE public.search_contact
SET contact_type_id = (SELECT id FROM admin_contacttype WHERE name = 'Listing'),
    explanation = 'Crown court',
    explanation_cy = 'Llys y goron'
WHERE number = '02392 893 060'
  AND name = 'Criminal listing'
  AND id IN (
    SELECT contact_id FROM public.search_courtcontact
    WHERE court_id = (
        SELECT id FROM search_court
        WHERE slug = 'portsmouth-combined-court-centre'
));


-- FACT-521
UPDATE public.search_contact
SET contact_type_id = (SELECT id FROM admin_contacttype WHERE name = 'Listing'),
    explanation = 'Crown court',
    explanation_cy = 'Llys y goron'
WHERE number = '01642 343042'
  AND name = 'Criminal listing'
  AND id IN (
      SELECT contact_id FROM public.search_courtcontact
      WHERE court_id = (
          SELECT id FROM search_court
          WHERE slug = 'teesside-combined-court-centre'
));
