ALTER TABLE public.search_contact
  ALTER COLUMN explanation TYPE varchar(150),
  ALTER COLUMN explanation_cy TYPE varchar(150);

ALTER TABLE public.search_email
  ALTER COLUMN explanation TYPE varchar(150),
  ALTER COLUMN explanation_cy TYPE varchar(150);
