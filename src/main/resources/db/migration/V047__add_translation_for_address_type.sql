ALTER TABLE public.search_addresstype
  ADD COLUMN name_cy CHARACTER VARYING(255);

UPDATE public.search_addresstype
SET name = 'Visit us', name_cy = 'Ymweld â ni'
WHERE id = 5880;

UPDATE public.search_addresstype
SET name = 'Write to us', name_cy = 'Ysgrifennwch atom'
WHERE id = 5881;

UPDATE public.search_addresstype
SET name = 'Visit or contact us', name_cy = 'Cysylltu â ni'
WHERE id = 5882;

