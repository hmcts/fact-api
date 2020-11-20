drop table search_courtservicearea;

UPDATE public.search_serviceareacourt
SET court_id = 1479430
WHERE id = 3;

UPDATE public.search_serviceareacourt
SET court_id = 1479430
WHERE id = 4;

UPDATE public.search_serviceareacourt
SET court_id = '1480142'
WHERE id = 5;

UPDATE public.search_serviceareacourt
SET court_id = 1480140
WHERE id = 6;

UPDATE public.search_serviceareacourt
SET court_id = 1480136
WHERE id = 7;

UPDATE public.search_serviceareacourt
SET court_id = 1480138
WHERE id = 8;

UPDATE public.search_serviceareacourt
SET court_id = 1480141
WHERE id = 9;

UPDATE public.search_serviceareacourt
SET court_id = 1480138
WHERE id = 10;

UPDATE public.search_servicearea
SET area_of_law_id = 34247
WHERE id = 8;

ALTER TABLE ONLY public.search_servicearea
    ADD CONSTRAINT fk_search_areaoflaw_id FOREIGN KEY (area_of_law_id) REFERENCES public.search_areaoflaw (id);

