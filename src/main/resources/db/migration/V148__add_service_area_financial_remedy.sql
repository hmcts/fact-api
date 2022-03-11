--- FACT-880 ---
UPDATE public.search_servicearea SET sort_order = 12 WHERE sort_order = 11;
UPDATE public.search_servicearea SET sort_order = 13 WHERE sort_order = 12;
UPDATE public.search_servicearea SET sort_order = 14 WHERE sort_order = 13;
UPDATE public.search_servicearea SET sort_order = 15 WHERE sort_order = 14;
UPDATE public.search_servicearea SET sort_order = 16 WHERE sort_order = 15;
UPDATE public.search_servicearea SET sort_order = 17 WHERE sort_order = 16;
UPDATE public.search_servicearea SET sort_order = 18 WHERE sort_order = 17;
UPDATE public.search_servicearea SET sort_order = 19 WHERE sort_order = 18;

INSERT INTO public.search_servicearea(name, description, name_cy, description_cy, slug, type,
                                         online_url, area_of_law_id, sort_order)
VALUES('Financial remedy', 'Making an application to settle your finances following a divorce.',
       'Rhwymedi Ariannol', 'Gwneud cais i setlo materion ariannol yn dilyn ysgariad neu ddiweddu partneriaeth sifil.',
       'financial-remedy', 'family', 'https://www.gov.uk/money-property-when-relationship-ends', 34269, 11);

INSERT INTO public.search_serviceservicearea(service_id, servicearea_id)
VALUES(2, 19);
