UPDATE public.search_openingtime
SET hours = 'Urgent enquiries only 09.00 am to 4.00pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bristol-crown-court'))
  AND type = 'Court counter open';

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 8.30am to 5pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bristol-crown-court'))
  AND type = 'Telephone enquiries answered';

-- FACT-392 content update

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10am to 2pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'yeovil-county-family-and-magistrates-court'))
  AND type = 'Counter open';

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10am to 3:30pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'yeovil-county-family-and-magistrates-court'))
  AND type = 'Telephone enquiries answered';
