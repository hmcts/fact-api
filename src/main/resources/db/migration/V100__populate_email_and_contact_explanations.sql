-- Make sure the temporary tables do not exist before we start
DROP TABLE IF EXISTS temp_court_with_email_descriptions;
DROP TABLE IF EXISTS temp_new_email_description_duplicates;
DROP TABLE IF EXISTS temp_old_email_description_duplicates;
DROP TABLE IF EXISTS temp_court_with_contact_descriptions;
DROP TABLE IF EXISTS temp_new_contact_description_duplicates;
DROP TABLE IF EXISTS temp_old_contact_description_duplicates;


---- EMAIL ----
-- Create a temporary table for all active courts with their old and new email descriptions where the explanation field is blank
SELECT * INTO TEMP TABLE temp_court_with_email_descriptions
FROM (
    SELECT c.id AS email_id, c.description AS old_description, ct.description AS new_description, sc.name AS court_name FROM search_email c
    INNER JOIN admin_emailtype ct ON c.admin_email_type_id = ct.id
    INNER JOIN search_courtemail cc ON c.id = cc.email_id
    INNER JOIN search_court sc ON cc.court_id = sc.id
    WHERE sc.displayed = TRUE
    AND (explanation IS NULL or explanation = '')
    ) AS t;

-- Create a temporary table for all emails with duplicated new description
SELECT * INTO TEMP TABLE temp_new_email_description_duplicates
FROM (
    SELECT new_description, court_name FROM temp_court_with_email_descriptions
    GROUP BY new_description, court_name
    HAVING count(*) > 1
    ) AS t;

-- Create a temporary table for all emails with duplicated old description
SELECT * INTO TEMP TABLE temp_old_email_description_duplicates
FROM (
    SELECT old_description, court_name FROM temp_court_with_email_descriptions
    GROUP BY old_description, court_name
    HAVING count(*) > 1
    ) AS t;

-- Update the explanation fields (English and Welsh) in the search_email table with the old description when:
-- 1. The old description not the same as the new description
-- 2. The new description is duplicated
-- 3. The old description is not duplicated
UPDATE public.search_email
SET explanation = description,
    explanation_cy = description_cy
WHERE id IN (
    SELECT t1.email_id FROM temp_court_with_email_descriptions AS t1
                                INNER JOIN temp_new_email_description_duplicates AS t2
                                           ON t1.court_name = t2.court_name AND t1.new_description = t2.new_description
    WHERE lower(t1.old_description) != lower(t1.new_description)
  AND NOT EXISTS (SELECT * FROM temp_old_email_description_duplicates t3
    WHERE t1.court_name = t3.court_name
  AND t1.old_description = t3.old_description)
    );


---- PHONE NUMBER ----
-- Create a temporary table for all active courts with their old and new contact descriptions where the explanation field is blank
SELECT * INTO TEMP TABLE temp_court_with_contact_descriptions
FROM (
    SELECT c.id AS contact_id, c.name AS old_description, ct.name AS new_description, c.fax, sc.name AS court_name FROM search_contact c
    INNER JOIN admin_contacttype ct ON c.contact_type_id = ct.id
    INNER JOIN search_courtcontact cc ON c.id = cc.contact_id
    INNER JOIN search_court sc ON cc.court_id = sc.id
    WHERE sc.displayed = TRUE
    AND (explanation IS NULL or explanation = '')
    ) AS t;

-- Create a temporary table for all contacts with duplicated new description
SELECT * INTO TEMP TABLE temp_new_contact_description_duplicates
FROM (
    SELECT new_description, fax, court_name FROM temp_court_with_contact_descriptions
    GROUP BY new_description, fax, court_name
    HAVING count(*) > 1
    ) AS t;

-- Create a temporary table for all contacts with duplicated old description
SELECT * INTO TEMP TABLE temp_old_contact_description_duplicates
FROM (
    SELECT max(contact_id) AS contact_id, old_description, court_name FROM temp_court_with_contact_descriptions
    GROUP BY old_description, court_name
    HAVING count(*) > 1
    ) AS t;

-- Update the explanation fields (English and Welsh) in the search_contact table with the old description when:
-- 1. The old description not the same as the new description
-- 2. The new description is duplicated
-- 3. The old description is not duplicated
UPDATE public.search_contact
SET explanation = name,
    explanation_cy = name_cy
WHERE id IN (
    SELECT t1.contact_id FROM temp_court_with_contact_descriptions AS t1
                                  INNER JOIN temp_new_contact_description_duplicates AS t2
                                             ON t1.court_name = t2.court_name AND t1.new_description = t2.new_description AND t1.fax = t2.fax
    WHERE lower(t1.old_description) != lower(t1.new_description)
  AND NOT EXISTS (SELECT * FROM temp_old_contact_description_duplicates t3
    WHERE t1.court_name = t3.court_name
  AND t1.old_description = t3.old_description)
    );


-- Tidy up
DROP TABLE IF EXISTS temp_court_with_email_descriptions;
DROP TABLE IF EXISTS temp_new_email_description_duplicates;
DROP TABLE IF EXISTS temp_old_email_description_duplicates;
DROP TABLE IF EXISTS temp_court_with_contact_descriptions;
DROP TABLE IF EXISTS temp_new_contact_description_duplicates;
DROP TABLE IF EXISTS temp_old_contact_description_duplicates;
