--- FACT-1150 ---
ALTER TABLE public.search_servicecentre DROP COLUMN id;
ALTER TABLE public.search_servicecentre  ADD COLUMN id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY;

-- FACT-1167 --
UPDATE search_court SET lat = 56.45768760869469, lon = -2.97030760165751 WHERE id = 1479934;