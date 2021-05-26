DROP TABLE IF EXISTS temp_criminal_email_type_with_court_type;

-- Create a temporary table containing records with 'Criminal' email type and their court type
SELECT * INTO TEMP TABLE temp_criminal_email_type_with_court_type
FROM (SELECT se.id AS email_id, se.description AS email_description, ct.name AS court_type FROM public.search_email AS se
    INNER JOIN public.search_courtemail AS sce
    ON se.id = sce.email_id
    INNER JOIN public.search_court AS c
    ON sce.court_id = c.id
    INNER JOIN public.search_courtcourttype AS cct
    ON c.id = cct.court_id
    INNER JOIN search_courttype AS ct
    ON cct.court_type_id = ct.id
    WHERE se.description ILIKE 'Criminal%'
    AND (ct.name = 'Crown Court' OR ct.name = 'Magistrates'' Court')) AS A;

-- Add a new column to the temporary table with the email type id corresponding to the court type
-- Criminal email type in a Magistrates' court will be assigned a Magistrates court email type
-- Criminal email type in a Crown court will be assigned a Crown court email type
ALTER TABLE temp_criminal_email_type_with_court_type
    ADD COLUMN email_type_id INTEGER;

UPDATE temp_criminal_email_type_with_court_type AS t
SET email_type_id = aet.id
    FROM public.admin_emailtype AS aet
WHERE aet.id =
    CASE
    WHEN t.court_type = 'Crown Court'
    THEN (SELECT id FROM admin_emailtype WHERE description = 'Crown court')

    WHEN t.court_type = 'Magistrates'' Court'
    THEN (SELECT id FROM admin_emailtype WHERE description = 'Magistrates court')
END;

-- Update the email type id in the search_email table with the same email type id in the temporary table
UPDATE public.search_email AS se
SET admin_email_type_id = t.email_type_id
    FROM temp_criminal_email_type_with_court_type AS t
WHERE se.description ILIKE 'Criminal%'
  AND se.id = t.email_id;

DROP TABLE IF EXISTS temp_criminal_email_type_with_court_type;
DROP TABLE IF EXISTS temp_criminal_contact_type_with_court_type;

-- Create a temporary table containing records with 'Criminal' contact type and their court type
SELECT * INTO TEMP TABLE temp_criminal_contact_type_with_court_type
FROM (SELECT sc.id AS contact_id, sc.name AS contact_description, ct.name AS court_type FROM public.search_contact AS sc
    INNER JOIN public.search_courtcontact AS scc
    ON sc.id = scc.contact_id
    INNER JOIN public.search_court AS c
    ON scc.court_id = c.id
    INNER JOIN public.search_courtcourttype AS cct
    ON c.id = cct.court_id
    INNER JOIN search_courttype AS ct
    ON cct.court_type_id = ct.id
    WHERE sc.name ILIKE 'Criminal%'
    AND (ct.name = 'Crown Court' OR ct.name = 'Magistrates'' Court')) AS A;

-- Add a new column to the temporary table with the contact type id corresponding to the court type
-- Criminal contact type in a Magistrates' court will be assigned a Magistrates court contact type
-- Criminal contact type in a Crown court will be assigned a Crown court contact type
ALTER TABLE temp_criminal_contact_type_with_court_type
    ADD COLUMN contact_type_id INTEGER;

UPDATE temp_criminal_contact_type_with_court_type AS t
SET contact_type_id = act.id
    FROM public.admin_contacttype AS act
WHERE act.id =
    CASE
    WHEN t.court_type = 'Crown Court'
    THEN (SELECT id FROM admin_contacttype WHERE name = 'Crown court')

    WHEN t.court_type = 'Magistrates'' Court'
    THEN (SELECT id FROM admin_contacttype WHERE name = 'Magistrates court')
END;

-- Update the contact type id in the search_contact table with the same contact type id in the temporary table
UPDATE public.search_contact AS sc
SET contact_type_id = t.contact_type_id
    FROM temp_criminal_contact_type_with_court_type AS t
WHERE sc.name ILIKE 'Criminal%'
  AND sc.id = t.contact_id;

DROP TABLE IF EXISTS temp_criminal_contact_type_with_court_type;
