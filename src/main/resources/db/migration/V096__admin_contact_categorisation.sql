ALTER TABLE public.search_contact
ADD COLUMN contact_type_id integer NULL,
ADD COLUMN fax BOOLEAN DEFAULT FALSE;

UPDATE public.search_contact
SET fax = TRUE
WHERE name ILIKE '%fax%';

-- Set all contact type to be 'Enquiries' for now. Update when we have the confirmed list
UPDATE public.search_contact
SET contact_type_id = (SELECT id FROM public.admin_contacttype WHERE name = 'Enquiries');
