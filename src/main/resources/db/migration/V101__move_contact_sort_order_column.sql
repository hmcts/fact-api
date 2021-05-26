ALTER TABLE public.search_courtcontact
ADD COLUMN sort_order integer NULL;

UPDATE public.search_courtcontact cc
SET sort_order = c.sort_order
    FROM (SELECT id, sort_order FROM search_contact) as c
WHERE cc.contact_id = c.id;

ALTER TABLE public.search_contact
DROP COLUMN IF EXISTS sort_order;
