-- Make sure the temporary tables do not exist before we start
DROP TABLE IF EXISTS temp_court_with_email_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_new_email_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_old_email_type;
DROP TABLE IF EXISTS temp_email_to_update;
DROP TABLE IF EXISTS temp_court_with_contact_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_new_contact_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_old_contact_type;
DROP TABLE IF EXISTS temp_contact_to_update;


---- EMAIL ----
-- Create a temporary table for all active courts with their old and new email descriptions, and explanation where:
-- 1. The description has been updated (case insensitive)
-- 2. The explanation field is blank
SELECT * INTO TEMP TABLE temp_court_with_email_type
FROM (SELECT a.id AS court_id, a.name AS court_name, c.id AS email_id, c.description AS old_description, d.description AS new_description, c.explanation
    FROM search_court AS a
    INNER JOIN search_courtemail AS b  ON a.id = b.court_id
    INNER JOIN search_email AS c ON b.email_id = c.id
    INNER JOIN admin_emailtype AS d ON c.admin_email_type_id = d.id
    WHERE a.displayed = true
    AND lower(c.description) != lower(d.description)
    AND (explanation IS NULL OR explanation = '')) AS t;

-- Create a temporary table for all active courts with duplicated new email description
SELECT * INTO TEMP TABLE temp_court_with_duplicated_new_email_type
FROM (SELECT court_id, court_name, new_description FROM temp_court_with_email_type
    GROUP BY court_id, court_name, new_description
    HAVING count(*) > 1) as t;

-- Create a temporary table for all active courts with duplicated old email description
SELECT * INTO TEMP TABLE temp_court_with_duplicated_old_email_type
FROM (SELECT court_id, court_name, old_description FROM temp_court_with_email_type
    GROUP BY court_id, court_name, old_description
    HAVING count(*) > 1) as t;

-- Create a temporary table for emails to be updated where:
-- 1. The new description is duplicated but not the old description
-- 2. The old description not the same as the new description
SELECT * INTO TEMP TABLE temp_email_to_update
FROM (SELECT t1.* from temp_court_with_email_type AS t1
    INNER JOIN temp_court_with_duplicated_new_email_type as t2
    ON t1.court_id = t2.court_id
    AND t1.new_description = t2.new_description
    WHERE t1.court_id NOT IN (SELECT court_id FROM temp_court_with_duplicated_old_email_type)
    AND t1.old_description NOT IN (SELECT old_description FROM temp_court_with_duplicated_old_email_type)
    AND lower(t1.old_description) != lower(t1.new_description)) as t;

-- Update the explanation fields (English and Welsh) in the search_email table with then old description for all the email from the previous table with duplicates
UPDATE public.search_email AS a
SET explanation = description,
    explanation_cy = description_cy
WHERE id IN (SELECT email_id FROM temp_email_to_update);


---- PHONE NUMBER ----
-- Create a temporary table for all active courts with their old and new contact descriptions, and explanation where:
-- 1. The description has been updated (case insensitive)
-- 2. The explanation field is blank
-- 3. Ignore the word 'fax' at the end of description for comparison
SELECT * INTO TEMP TABLE temp_court_with_contact_type
FROM (SELECT a.id AS court_id, a.name AS court_name, c.id AS contact_id, rtrim(c.name, ' fax') AS old_description, d.name AS new_description, c.explanation
    FROM search_court AS a
    INNER JOIN search_courtcontact AS b  ON a.id = b.court_id
    INNER JOIN search_contact AS c ON b.contact_id = c.id
    INNER JOIN admin_contacttype AS d ON c.contact_type_id = d.id
    WHERE a.displayed = true
    AND lower(c.name) != lower(d.name)
    AND (explanation IS NULL OR explanation = '')) AS t;

-- Create a temporary table for all active courts with duplicated new contact description
SELECT * INTO TEMP TABLE temp_court_with_duplicated_new_contact_type
FROM (SELECT court_id, court_name, new_description FROM temp_court_with_contact_type
    GROUP BY court_id, court_name, new_description
    HAVING count(*) > 1) as t;

-- Create a temporary table for all active courts with duplicated old contact description
SELECT * INTO TEMP TABLE temp_court_with_duplicated_old_contact_type
FROM (SELECT court_id, court_name, old_description FROM temp_court_with_contact_type
    GROUP BY court_id, court_name, old_description
    HAVING count(*) > 1) as t;

-- Create a temporary table for contacts to be updated where:
-- 1. The new description is duplicated but not the old description
-- 2. The old description not the same as the new description
SELECT * INTO TEMP TABLE temp_contact_to_update
FROM (SELECT t1.* from temp_court_with_contact_type AS t1
    INNER JOIN temp_court_with_duplicated_new_contact_type as t2
    ON t1.court_id = t2.court_id
    AND t1.new_description = t2.new_description
    WHERE t1.court_id NOT IN (SELECT court_id FROM temp_court_with_duplicated_old_contact_type)
    AND t1.old_description NOT IN (SELECT old_description FROM temp_court_with_duplicated_old_contact_type)
    AND lower(t1.old_description) != lower(t1.new_description)) as t;

-- Update the explanation fields (English and Welsh) in the search_contact table with then old description for all the contact from the previous table with duplicates
UPDATE public.search_contact
SET explanation = name,
    explanation_cy = name_cy
WHERE id IN (SELECT contact_id FROM temp_contact_to_update);


-- Tidy up
DROP TABLE IF EXISTS temp_court_with_email_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_new_email_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_old_email_type;
DROP TABLE IF EXISTS temp_email_to_update;
DROP TABLE IF EXISTS temp_court_with_contact_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_new_contact_type;
DROP TABLE IF EXISTS temp_court_with_duplicated_old_contact_type;
DROP TABLE IF EXISTS temp_contact_to_update;
