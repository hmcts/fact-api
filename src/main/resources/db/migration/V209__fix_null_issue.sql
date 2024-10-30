-- FACT-2050
-- Set common platform to false where currently null to fix bug
UPDATE search_inperson
SET common_platform = false
WHERE common_platform IS NULL;