ALTER TABLE public.search_servicearea
    ADD COLUMN application_type	character varying(255);

UPDATE public.search_servicearea
SET application_type = 'your money claims applications'
WHERE id = 1;

UPDATE public.search_servicearea
SET application_type = 'your probate applications '
WHERE id = 2;

UPDATE public.search_servicearea
SET application_type = 'benefits appeals'
WHERE id = 5;

UPDATE public.search_servicearea
SET application_type = 'minor criminal offences'
WHERE id = 8;

UPDATE public.search_servicearea
SET application_type = 'divorce applications'
WHERE id = 9;

UPDATE public.search_servicearea
SET application_type = 'applications to dissolve a civil partnership'
WHERE id = 10;

UPDATE public.search_servicearea
SET application_type = 'major criminal offences'
WHERE id = 16;

UPDATE public.search_servicearea
SET application_type = 'immigration and asylum applications'
WHERE id = 17;
