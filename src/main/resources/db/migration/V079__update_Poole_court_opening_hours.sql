UPDATE public.search_openingtime
SET hours = 'This court does not operate a counter service. Civil enquiries should be addressed to Bournemouth and Poole County Court'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'poole-magistrates-court'))
  AND type = 'No counter service available';
