ALTER TABLE public.search_servicearea
	ADD COLUMN text CHARACTER VARYING(500);

ALTER TABLE public.search_servicearea
	ADD COLUMN text_cy CHARACTER VARYING(500);

UPDATE public.search_servicearea
SET text = 'We manage applications to dissolve a civil partnership at our central service centre.'
WHERE slug = 'civil-partnership';

UPDATE public.search_servicearea
SET text = 'We manage benefits appeals at our central service centre.'
WHERE slug = 'benefits';

UPDATE public.search_servicearea
SET text = 'We manage major criminal offences at our central service centre.'
WHERE slug = 'major-criminal-offences';

UPDATE public.search_servicearea
SET text = 'We manage your money claims applications at our central service centre.'
WHERE slug = 'money-claims';
