-- FACT-1903
-- drop constraint on epim id
-- change epim id to varchar(30)
-- readd the constraint to ensure epim id length is less than or equal to 30
DO $$
  BEGIN
      ALTER TABLE search_courtaddress
      DROP CONSTRAINT IF EXISTS check_epim_id_length;

      IF EXISTS (SELECT 1 FROM information_schema.columns
                 WHERE table_name = 'search_courtaddress'
                 AND column_name = 'epim_id') THEN
      ALTER TABLE search_courtaddress
      ALTER COLUMN epim_id
              TYPE VARCHAR(30)
          USING LEFT(epim_id, 30);
      END IF;

      IF NOT EXISTS (
          SELECT 1
          FROM information_schema.constraint_column_usage
          WHERE table_name = 'search_courtaddress'
            AND constraint_name = 'check_epim_id_length'
      ) THEN
      ALTER TABLE search_courtaddress
      ADD CONSTRAINT check_epim_id_length
      CHECK (char_length(epim_id) <= 30);
  END IF;
END $$;
