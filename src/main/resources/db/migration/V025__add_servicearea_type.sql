ALTER TABLE public.search_servicearea
	RENAME COLUMN service_area_type TO type;

ALTER TABLE ONLY public.search_servicearea
	ADD CONSTRAINT con_catchment_type CHECK (type IN ('family', 'civil', 'other'));

UPDATE public.search_servicearea
SET type = 'family'
WHERE slug in ('probate', 'divorce', 'civil-partnership', 'forced-marriage', 'childcare-arrangements', 'adoption',
			   'domestic-abuse', 'female-genital-mutilation');

UPDATE public.search_servicearea
SET type = 'civil'
WHERE slug in ('money-claims', 'housing', 'bankruptcy');

UPDATE public.search_servicearea
SET type = 'other'
WHERE slug in ('benefits', 'claims-against-employers', 'tax', 'immigration', 'major-criminal-offences',
			   'minor-criminal-offences', 'high-court-district-registries');
