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
('Admin', 'Gweinyddol'),
('Adoption', 'Mabwysiadu'),
('Applications', 'Ceisiadau'),
('Breathing space enquiries', 'Ymholiadau lle i anadlu'),
('Business and property courts', 'Llysoedd busnes ac eiddo'),
('CAPS', 'CAPS'),
('Case progression', 'Hwyluso achosion'),
('Chancery', 'Siawnsri'),
('Citizens advice', 'Cyngor ar bopeth'),
('Civil court', 'Llys sifil'),
('Counter appointments', 'apwyntiadau cownter'),
('County court', 'Llys sirol'),
('Criminal', 'Troseddol'),
('Crown court', 'Llys y goron'),
('Enforcement', 'Gorfodaeth'),
('Enquiries', 'Ymholidau'),
('FGM and forced marriage', 'Anffurfio organau cenhedlu benywod a phriodas dan orfod'),
('Family court', 'Llys teulu'),
('Family public law (children in care)', 'Cyfraith gyhoeddus - teulu (plant mewn gofal)'),
('Finances', 'Cyllid'),
('High court', 'Uchel lys'),
('Immigration', 'Mewnfudo'),
('Jury service', 'Gwasanaeth Rheithgor'),
('Legal aid', 'Cymorth cyfreithiol'),
('Listing', 'Rhestru'),
('Magistrates court', 'Llys ynadon'),
('Mediation', 'Cyfryngu'),
('Payments', 'Taliadau'),
('Possession enquiries', 'Ymholiadau meddiannu'),
('Pre and post court', 'Cyn ac ar ôl y llys'),
('Queen''s bench', 'Mainc y frenhines'),
('Social security and child support', 'Nawdd cymdeithasol a chynnal plant'),
('Support through court', 'Support through court'),
('Technology or construction', 'Technoleg ac adeiladwaith'),
('Transcripts', 'Trawsgrifiadau'),
('Tribunal', 'Tribiwnlys'),
('Witness service', 'Gwasanaeth i dystion');

-- Assigning email categories to existing free text entries (for the desc field)
UPDATE public.search_email se
SET admin_email_type_id = aet.id
    FROM (SELECT inner_aet.id, inner_aet.description FROM admin_emailtype AS inner_aet) AS aet
WHERE aet.id =
CASE
    -- Cater for previous free text entries
    WHEN LOWER(se.description) IN ('orders and accounts', 'filing and records', 'send documents', 'accounts',
                                   'admin', 'admin court', 'clerks')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Admin')

    WHEN LOWER(se.description) IN ('breathing space enquiries', 'breathing space notifications')
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Breathing space enquiries')

    WHEN se.description ILIKE 'Centralised Attachment of Earnings (CAPS)'
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'CAPS')

    WHEN LOWER(se.description) IN ('case progression', 'care cases', 'children cases')
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Case progression')

    WHEN LOWER(se.description) IN ('bankruptcy', 'chancery issue', 'chancery judges listing', 'chancery masters appointments')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Chancery')

    WHEN se.description ILIKE 'Citizens Advice Witness Service'
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Citizens advice')

    WHEN LOWER(se.description) IN ('civil queries', 'civil and family', 'civil enquiries', 'civil listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Civil court')

    WHEN LOWER(se.description) IN ('counter appointments', 'appointments')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Counter appointments')

    WHEN LOWER(se.description) IN ('county court', 'county court money claims centre', 'county court listing', 'court of protection')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'County court')

    WHEN se.description ILIKE 'Criminal%'
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Criminal')

    WHEN LOWER(se.description) IN ('crown court', 'crown court enquiries', 'crown court listing', 'crown court witness service enquiries')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Crown court')

    WHEN LOWER(se.description) IN ('enforcement', 'bailiffs')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Enforcement')

    WHEN LOWER(se.description) IN ('enquiries', 'scottish enquiries', 'urgent queries', 'issue')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Enquiries')

    WHEN LOWER(se.description) IN ('family queries', 'family listing', 'divorce')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Family court')

    WHEN LOWER(se.description) IN ('attachment of earnings', 'charging orders', 'claims issue', 'small claims', 'fixed penalties')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Finances')

    WHEN LOWER(se.description) IN ('immigration and asylum', 'immigration bail applications')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Immigration')

    WHEN LOWER(se.description) IN ('listing', 'listing (circuit judges)', 'listing (district judges)')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Listing')

    WHEN LOWER(se.description) IN ('magistrates court', 'magistrates'' court',
                                   'magistrates court enquiries', 'magistrates'' court enquiries',
                                   'magistrates court listing', 'magistrates'' court listing',
                                   'magistrates fine queries', 'magistrates listing')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Magistrates court')

    WHEN LOWER(se.description) IN ('fine queries')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Payments')

    WHEN LOWER(se.description) IN ('post court', 'pre and post court', 'pre court')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Pre and post court')

    WHEN se.description ILIKE 'Personal support unit'
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Support through court')

    WHEN se.description ILIKE 'Technology and construction'
    THEN (SELECT aet.id FROM admin_emailtype AS aet WHERE aet.description = 'Technology or construction')

    WHEN LOWER(se.description) IN ('transcript requests')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Transcripts')

    WHEN LOWER(se.description) IN ('tribunals', 'employment tribunal')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Tribunal')

    WHEN LOWER(se.description) IN ('witness service', 'witness care unit')
    THEN (SELECT aet.id FROM admin_emailtype as aet WHERE aet.description = 'Witness service')

    ELSE (SELECT aet.id FROM admin_emailtype as aet WHERE LOWER(aet.description) = LOWER(se.description))
END
-- Prevent empty expensive updates and dead rows FROM being produced
AND   se.admin_email_type_id IS DISTINCT FROM aet.id;
