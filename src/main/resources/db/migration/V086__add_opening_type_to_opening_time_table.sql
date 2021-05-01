INSERT INTO public.admin_openingtype(id, name, name_cy)
VALUES(DEFAULT, 'Telephone payments accepted', 'Derbynnir taliadau ffôn');

ALTER TABLE public.search_openingtime
    ADD COLUMN opening_type_id int;

SELECT DISTINCT(type)
FROM public.search_openingtime o;

UPDATE public.search_openingtime
SET opening_type_id = 17
WHERE type IN (
    'Building open',
    'Court building open',
    'Court open',
    'Magistrates'' Court open'
);

UPDATE public.search_openingtime
SET opening_type_id = 44
WHERE type IN (
	'Counter open',
	'Court counter open',
	'Court enquiries open',
	'Civil counter open',
	'Counter Services',
	'County Court enquiries counter'
);

UPDATE public.search_openingtime
SET opening_type_id = 13
WHERE type IN (
	'Telephone enquiries answered',
	'Telephone Enquiries from',
	'Telephones'
);

UPDATE public.search_openingtime
SET opening_type_id = 47
WHERE type IN (
	'Telephone Payments and enquiries',
	'Telephone Payments open'
);

UPDATE public.search_openingtime
SET opening_type_id = 43
WHERE type = 'Tribunal open';

UPDATE public.search_openingtime
SET opening_type_id = 5
WHERE type IN (
	'Bailiff counter open',
	'Bailiff office open',
	'Bailiffs Office'
);

UPDATE public.search_openingtime
SET opening_type_id = 39
WHERE type = 'Counter service by appointment only';

UPDATE public.search_openingtime
SET opening_type_id = 40
WHERE type = 'No counter service available';
