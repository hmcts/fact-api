ALTER TABLE ONLY public.search_servicearea
    ADD COLUMN catchment_method character varying(250);

UPDATE public.search_servicearea
SET catchment_method = 'proximity';

UPDATE public.search_servicearea
SET catchment_method = 'local-authority'
WHERE slug in ('childcare-arrangements', 'civil-partnership', 'divorce');

UPDATE public.search_servicearea
SET catchment_method = 'postcode'
WHERE slug in ('bankruptcy', 'housing', 'money-claims');
