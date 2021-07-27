CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;

CREATE INDEX IF NOT EXISTS trgm_idx_name ON public.search_court USING GIN (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS trgm_idx_name_cy ON public.search_court USING GIN (name_cy gin_trgm_ops);
CREATE INDEX IF NOT EXISTS trgm_idx_address ON public.search_courtaddress USING GIN (address gin_trgm_ops);
CREATE INDEX IF NOT EXISTS trgm_idx_address_cy ON public.search_courtaddress USING GIN (address_cy gin_trgm_ops);
