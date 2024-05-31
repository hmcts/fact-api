-- FACT-1500
-- adds court_name_cy column to admin_court_history table

ALTER TABLE ONLY public.admin_court_history ADD COLUMN IF NOT EXISTS court_name_cy character varying(255);
