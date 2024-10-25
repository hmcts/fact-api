-- FACT-1903
-- Add new column epim_id to court address table
-- Add check constraint to ensure epim_id length is less than or equal to 30
ALTER TABLE search_courtaddress ADD COLUMN IF NOT EXISTS epim_id VARCHAR(30);
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.constraint_column_usage
        WHERE table_name = 'search_courtaddress'
          AND constraint_name = 'check_epim_id_length'
    )
    THEN
      ALTER TABLE search_courtaddress
      ADD CONSTRAINT check_epim_id_length
        CHECK (char_length(epim_id) <= 30);
  END IF;
END $$;
