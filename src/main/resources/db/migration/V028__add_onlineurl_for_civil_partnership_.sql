UPDATE public.search_servicearea
SET online_text = 'Apply to end a civil partnership online'
WHERE slug = 'civil-partnership';

UPDATE public.search_servicearea
SET online_url = 'https://www.gov.uk/end-civil-partnership'
WHERE slug = 'civil-partnership';

UPDATE public.search_servicearea
SET online_text = null
WHERE slug in ('childcare-arrangements');
