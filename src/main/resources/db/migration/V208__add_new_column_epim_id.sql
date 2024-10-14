-- FACT-1903
-- Add new column epim_id to court address table
ALTER TABLE search_courtaddress ADD COLUMN IF NOT EXISTS epim_id VARCHAR(255);
