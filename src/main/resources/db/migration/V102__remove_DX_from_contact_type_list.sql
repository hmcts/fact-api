UPDATE public.search_contact
SET contact_type_id = NULL
WHERE contact_type_id = (
    SELECT id FROM public.admin_contacttype
    WHERE name = 'DX'
);

-- Remove DX and Criminal contact and email types
DELETE FROM public.admin_contacttype
WHERE name = 'DX'
   OR name = 'Criminal';

DELETE FROM public.admin_emailtype
WHERE description = 'Criminal';

