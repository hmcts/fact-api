--- FACT-95 ---
--- Adding new table for admin_email_description
CREATE TABLE public.admin_emailtype
(
    id              integer PRIMARY KEY    NOT NULL,
    description     character varying(250) NOT NULL,
    description_cy  character varying(500) NOT NULL
);

CREATE SEQUENCE public.admin_emailtype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_emailtype_id_seq OWNED BY public.admin_emailtype.id;

ALTER TABLE ONLY public.admin_emailtype
ALTER COLUMN id SET DEFAULT nextval('public.admin_emailtype_id_seq'::regclass);

-- Amending the existing search_email table to include a foreign key to the one above
ALTER TABLE public.search_email
ADD COLUMN admin_email_type_id integer NULL;

-- Inserting the data for the new table
INSERT INTO public.admin_emailtype(description, description_cy)
VALUES
('Case progression', 'Case progression cy'), ('Chancery', 'Chancery cy'),
('Civil court', 'Civil court cy'), ('County court', 'County court cy'),
('Crown court', 'Crown court cy'), ('Enquiries', 'Enquiries cy'),
('Admin', 'Admin cy'), ('Finances', 'Finances cy'),
('Family', 'Family cy'), ('Immigration', 'Immigration cy'),
('Listing', 'Listing cy'), ('Magistrates court', 'Magistrates court cy'),
('Post and pre court', 'Post and pre court cy'), ('Witness', 'Witness cy');

-- Assigning email categories to existing free text entries (for the desc field)
UPDATE search_email se
SET admin_email_type_id = aet.id
    FROM (SELECT inner_aet.id, inner_aet.description FROM admin_emailtype AS inner_aet) AS aet
WHERE aet.id =
    CASE
    -- Cater for previous free text entries
    WHEN LOWER(se.description) IN ('case progression', 'care cases', 'children cases')
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE LOWER(aet.description) = 'case progression')

    WHEN LOWER(se.description) IN ('counter appointments', 'bankruptcy', 'appointments', 'chancery issue',
                                   'chancery judges listing', 'chancery masters appointments')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'chancery')

    WHEN LOWER(se.description) IN ('civil queries', 'civil and family', 'civil enquiries', 'civil listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'civil court')

    WHEN LOWER(se.description) IN ('county court', 'county court money claims centre', 'county court listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'county court')

    WHEN LOWER(se.description) IN ('crown court', 'crown court listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'crown court')

    WHEN LOWER(se.description) IN ('enquries', 'scottish enquiries', 'urgent queries', 'issue')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'enquiries')

    WHEN LOWER(se.description) IN ('orders and accounts', 'filing and records', 'send documents',
                                   'accounts', 'admin', 'admin court', 'clerks')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'admin')

    WHEN LOWER(se.description) IN ('family queries', 'family listing',
                                   'family public law (children in care)',
                                   'divorce', 'adoption', 'social security and child support')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'finances')

    WHEN LOWER(se.description) IN ('fine queries', 'small claims', 'fixed penalties')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'family')

    WHEN LOWER(se.description) IN ('immigration and asylum', 'immigration bail applications')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'immigration')

    WHEN LOWER(se.description) IN ('listing', 'listing (circuit judges)', 'listing (district judges)')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'listing')

    WHEN LOWER(se.description) IN ('magistrates court', 'magistrate''s court',
                                   'magistrates listing', 'magistrate''s listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'magistrates court')

    WHEN LOWER(se.description) IN ('witness service', 'witness care unit', 'citizens advice witness service')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = 'witness')
END
-- Prevent empty expensive updates and dead rows FROM being produced
AND   se.admin_email_type_id IS DISTINCT FROM aet.id;
