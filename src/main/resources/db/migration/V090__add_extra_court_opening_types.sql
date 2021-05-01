INSERT INTO public.admin_openingtype(id, name, name_cy)
VALUES(DEFAULT, 'Magistrates'' Court open', 'Llys Ynadon ar agor');

INSERT INTO public.admin_openingtype(id, name, name_cy)
VALUES(DEFAULT, 'Crown Court open', 'Llys y Goron ar agor');

INSERT INTO public.admin_openingtype(id, name, name_cy)
VALUES(DEFAULT, 'Family Court open', 'Llys Teulu ar agor');

INSERT INTO public.admin_openingtype(id, name, name_cy)
VALUES(DEFAULT, 'County Court open', 'Llys Sirol ar agor');

UPDATE public.search_openingtime
SET opening_type_id = (
    SELECT id FROM public.admin_openingtype
    WHERE name = 'Magistrates'' Court open')
WHERE type = 'Magistrates'' Court open';

UPDATE public.search_openingtime
SET opening_type_id = (
    SELECT id FROM public.admin_openingtype
    WHERE name = 'Crown Court open')
WHERE type = 'Crown Court open';
