ALTER TABLE public.search_servicearea
    ADD COLUMN sort_order integer;

UPDATE public.search_servicearea
SET sort_order = id;

UPDATE public.search_servicearea
SET sort_order = 4
WHERE id = 3;

UPDATE public.search_servicearea
SET sort_order = 3
WHERE id = 4;
