-- FACT-3113
-- Extend character limit on warning notice
ALTER TABLE public.search_court ALTER COLUMN alert TYPE varchar(1000);
ALTER TABLE public.search_court ALTER COLUMN alert_cy TYPE varchar(1000);
