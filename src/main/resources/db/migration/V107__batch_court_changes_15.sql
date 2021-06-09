--- FACT-581 ---
UPDATE public.search_court
SET alert = 'Please note that the lift is temporarily out of service – engineers have ordered a new part and we await its arrival. If you need assistance with access arrangements please telephone 01253 757 015. We apologise for any inconvenience.'
WHERE slug = 'blackpool-magistrates-and-civil-court';

--- FACT-512 ---
INSERT INTO search_courtareaoflaw(id, area_of_law_id, court_id, single_point_of_entry)
VALUES (
   DEFAULT,
   (SELECT id FROM public.search_areaoflaw WHERE name = 'Domestic violence'),
   (SELECT id FROM public.search_court WHERE slug = 'newcastle-upon-tyne-combined-court-centre'),
   FALSE
);
