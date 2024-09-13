-- FACT-1962
-- add court of protection email type to admin_emailtype table if it does not exist
-- welsh translation checked with welsh language unit

INSERT INTO
  admin_emailtype
(description, description_cy)
SELECT
  'Court of Protection', 'Y Llys Gwarchod'
WHERE NOT EXISTS (
	SELECT 1
	FROM admin_emailtype
  WHERE description = 'Court of Protection'
);
