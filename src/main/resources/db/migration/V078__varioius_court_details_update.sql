-- FACT-394 content changes

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 09.00am to 4.30pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bath-law-courts-civil-family-and-magistrates'))
  AND type = 'Court open';

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10.00am to 2.00pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bath-law-courts-civil-family-and-magistrates'))
  AND type = 'Counter open';

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10.00am to 3.30pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bath-law-courts-civil-family-and-magistrates'))
  AND type = 'Telephone enquiries answered'
  AND hours = 'Monday to Friday 10am to 3:30pm';

DO $$
DECLARE temp_id integer;
BEGIN
    SELECT id into temp_id
    FROM public.search_openingtime
    WHERE id IN (
        SELECT opening_time_id FROM public.search_courtopeningtime
        WHERE court_id = (
            SELECT id FROM public.search_court
            WHERE slug = 'bath-law-courts-civil-family-and-magistrates'))
      AND type = 'Telephone enquiries answered'
      AND hours = '9am to 1pm and 2pm to 4pm';

    DELETE FROM public.search_courtopeningtime
    WHERE opening_time_id = temp_id;

    DELETE FROM public.search_openingtime
    WHERE id = temp_id;
END $$;


-- FACT-396 content changes

UPDATE public.search_contact
SET name = 'Civil queries'
WHERE id IN (
    SELECT contact_id FROM public.search_courtcontact
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'exeter-combined-court-centre'))
  AND name = 'County Court';


-- FACT-397 content changes

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 9am to 5pm'
WHERE id IN (
    SELECT opening_time_id FROM public.search_courtopeningtime
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'west-hampshire-magistrates-court'))
  AND type = 'Telephone enquiries answered';

UPDATE public.search_facility
SET description = '<p>The Flying Aubergine Caf&eacute; on the ground floor is open from 9am until 3pm</p>'
WHERE id IN (
    SELECT facility_id FROM public.search_courtfacility
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'southampton-combined-court-centre'))
  AND name = 'Refreshments';


-- FACT-399 content changes

UPDATE public.search_email
SET description = 'Civil enquiries'
WHERE id IN (
    SELECT email_id FROM public.search_courtemail
    WHERE court_id = (
    SELECT id FROM public.search_court
    WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'))
  AND description = 'Enquiries';

UPDATE public.search_contact
SET explanation = 'General switchboard'
WHERE id IN (
    SELECT contact_id FROM public.search_courtcontact
    WHERE court_id = (
    SELECT id FROM public.search_court
    WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'))
  AND name = 'Enquiries'
  AND explanation IS NULL;

UPDATE public.search_contact
SET number = '01202 502 813'
WHERE id IN (
    SELECT contact_id FROM public.search_courtcontact
    WHERE court_id = (
    SELECT id FROM public.search_court
    WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'))
  AND name = 'Family public law (children in care)'
  AND explanation = 'Paper process';

UPDATE public.search_contact
SET name = 'Social Security and Child Support Tribunals'
WHERE id IN (
    SELECT contact_id FROM public.search_courtcontact
    WHERE court_id = (
    SELECT id FROM public.search_court
    WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'))
  AND name = 'Social security and child support';

UPDATE public.search_facility
SET description = '<p><a href="https://www.thepsu.org/">The Justice Advice Project</a> is available on site to provide assistance.</p>'
WHERE id IN (
    SELECT facility_id FROM public.search_courtfacility
    WHERE court_id = (
        SELECT id FROM public.search_court
        WHERE slug = 'bournemouth-and-poole-county-court-and-family-court'))
  AND name = 'Witness service';
