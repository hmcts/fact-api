-- FACT_1202
ALTER TABLE public.search_courtaddress
  ADD COLUMN sort_order Integer NOT NULL DEFAULT 0;
