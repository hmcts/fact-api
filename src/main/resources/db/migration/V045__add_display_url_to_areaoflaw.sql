ALTER TABLE public.search_areaoflaw
	ADD COLUMN display_external_link CHARACTER VARYING(500);

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/child-adoption/applying-for-an-adoption-court-order'
WHERE name = 'Adoption';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/bankruptcy'
WHERE name = 'Bankruptcy';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/employment-tribunals'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/looking-after-children-divorce'
WHERE name = 'Children';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/applying-for-probate'
WHERE name = 'Probate';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/appeal-benefit-decision'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/injunction-domestic-violence'
WHERE name = 'Domestic violence';

UPDATE public.search_areaoflaw
SET display_external_link = 'https://www.gov.uk/immigration-asylum-tribunal'
WHERE name = 'Immigration';
