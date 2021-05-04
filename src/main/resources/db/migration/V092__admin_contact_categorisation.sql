ALTER TABLE public.search_contact
ADD COLUMN contact_type_id integer NULL;

-- Set all contact type to be 'Enquiries' for now. Update when we have the confirmed list
UPDATE public.search_contact
SET contact_type_id = (SELECT id FROM public.admin_contacttype WHERE name = 'Enquiries');
