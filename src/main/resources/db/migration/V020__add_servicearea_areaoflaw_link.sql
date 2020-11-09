COPY public.search_areaoflaw (id, name) FROM stdin;
34264	Minor crime
\.

ALTER TABLE public.search_servicearea
    ADD COLUMN area_of_law_id	integer;

ALTER TABLE ONLY public.search_servicearea
    ADD CONSTRAINT fk_area_of_law_id FOREIGN KEY (area_of_law_id) REFERENCES public.search_areaoflaw (id);

UPDATE public.search_servicearea
SET area_of_law_id			= 34254
WHERE id = 1;

UPDATE public.search_servicearea
SET area_of_law_id			= 34261
WHERE id = 2;

UPDATE public.search_servicearea
SET area_of_law_id			= 34255
WHERE id = 3;

UPDATE public.search_servicearea
SET area_of_law_id			= 34253
WHERE id = 4;

UPDATE public.search_servicearea
SET area_of_law_id			= 34252
WHERE id = 5;

UPDATE public.search_servicearea
SET area_of_law_id			= 34260
WHERE id = 6;

UPDATE public.search_servicearea
SET area_of_law_id			= 34263
WHERE id = 7;

UPDATE public.search_servicearea
SET area_of_law_id			= 34264
WHERE id = 8;

UPDATE public.search_servicearea
SET area_of_law_id			= 34250
WHERE id = 9;

UPDATE public.search_servicearea
SET area_of_law_id			= 34257
WHERE id = 10;

UPDATE public.search_servicearea
SET area_of_law_id			= 34249
WHERE id = 11;

UPDATE public.search_servicearea
SET area_of_law_id			= 34248
WHERE id = 12;

UPDATE public.search_servicearea
SET area_of_law_id			= 34251
WHERE id = 13;

UPDATE public.search_servicearea
SET area_of_law_id			= 34258
WHERE id = 14;

UPDATE public.search_servicearea
SET area_of_law_id			= 34258
WHERE id = 15;

UPDATE public.search_servicearea
SET area_of_law_id			= 34247
WHERE id = 16;

UPDATE public.search_servicearea
SET area_of_law_id			= 34259,
    name					= 'Immigration and asylum'
WHERE id = 17;

UPDATE public.search_servicearea
SET area_of_law_id			= 34256,
    name					= 'High Court district registry'
WHERE id = 18;

ALTER TABLE public.search_servicearea ALTER COLUMN area_of_law_id SET NOT NULL;
