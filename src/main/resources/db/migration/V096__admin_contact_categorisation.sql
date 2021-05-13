-- Add contact type ID and fax fields to the search-contact table
ALTER TABLE public.search_contact
ADD COLUMN contact_type_id integer NULL,
ADD COLUMN fax BOOLEAN DEFAULT FALSE;

-- Update the fax field on the search_contact table if the description column contains the word 'fax'
UPDATE public.search_contact
SET fax = TRUE
WHERE name ILIKE '%fax%';

-- Remove the existing content of the admin_contacttype table
DELETE from public.admin_contacttype;

-- Insert the new list of contact types into the admin_contacttype table
INSERT INTO public.admin_contacttype(name, name_cy)
VALUES
('Admin', 'Gweinyddol'),
('Adoption', 'Mabwysiadu'),
('Appeals', 'Apeliadau'),
('Applications', 'Ceisiadau'),
('Associates', 'Cymdeithion'),
('Business and property courts', 'Llysoedd busnes ac eiddo'),
('Case progression', 'Hwyluso achosion'),
('Chancery', 'Siawnsri'),
('Citizens advice', 'Cyngor ar bopeth'),
('Civil court', 'Llys sifil'),
('Counter appointments', 'apwyntiadau cownter'),
('County court', 'Llys sirol'),
('Criminal', 'Troseddol'),
('Crown court', 'Llys y goron'),
('DX', 'DX'),
('Enforcement', 'Gorfodaeth'),
('Enquiries', 'Ymholidau'),
('FGM and forced marriage', 'Anffurfio organau cenhedlu benywod a phriodas dan orfod'),
('Facilities', 'Cyfleusterau'),
('Family court', 'Llys teulu'),
('Family public law (children in care)', 'Cyfraith gyhoeddus - teulu (plant mewn gofal)'),
('Finances', 'Cyllid'),
('High court', 'Uchel lys'),
('Immigration', 'Mewnfudo'),
('Jury service', 'Gwasanaeth rheithgor'),
('Legal aid', '	Cymorth cyfreithiol'),
('Listing', 'Rhestru'),
('Magistrates court', 'Llys ynadon'),
('Masters', 'Meistri'),
('Mediation', 'Cyfryngu'),
('Minicom', 'Minicom'),
('Office', 'Swyddfa'),
('Payments', 'Taliadau'),
('Pre and post court', 'Cyn ac ar ôl y llys'),
('Queen''s bench', 'Mainc y frenhines'),
('Registry', 'Cofrestrfa'),
('Social security and child support', 'Nawdd cymdeithasol a chynnal plant'),
('Security', 'Diogelwch'),
('Support through court', 'Support through court'),
('Tribunal', 'Tribiwnlys'),
('Welsh language helpline', 'Llinell gymorth Gymraeg'),
('Witness service', 'Gwasanaeth i dystion');

-- Assigning contact categories to existing free text entries (for the desc field)
UPDATE public.search_contact sc
SET contact_type_id = act.id
    FROM (SELECT inner_act.id, inner_act.name FROM admin_contacttype AS inner_act) AS act
WHERE act.id =
CASE
-- Cater for previous free text entries
    WHEN LOWER(sc.name) IN ('orders and accounts', 'accounts fax', 'admin', 'admin court', 'clerks', 'clerk fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Admin')

    WHEN LOWER(sc.name) IN ('court of appeal civil division', 'rcj high court appeals')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Appeals')

    WHEN LOWER(sc.name) IN ('case progression', 'case progression (a)', 'case progression (b)', 'case progression (c)', 'case progression fax', 'care cases', 'care cases fax', 'children cases')
    THEN (SELECT act.id FROM admin_contacttype AS act WHERE act.name = 'Case progression')

    WHEN LOWER(sc.name) IN ('bankruptcy')
    THEN (SELECT act.id FROM admin_contacttype AS act WHERE act.name = 'Chancery')

    WHEN sc.name ILIKE 'Citizens Advice Witness Service'
    THEN (SELECT act.id FROM admin_contacttype AS act WHERE act.name = 'Citizens advice')

    WHEN LOWER(sc.name) IN ('civil fax', 'civil queries', 'civil and family', 'appointments - civil', 'civil listing')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Civil court')

    WHEN LOWER(sc.name) IN ('counter appointments', 'appointments')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Counter appointments')

    WHEN LOWER(sc.name) IN ('county court', 'county court fax', 'county court listing')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'County court')

    WHEN sc.name ILIKE 'Criminal%'
    THEN (SELECT act.id FROM admin_contacttype AS act WHERE act.name = 'Criminal')

    WHEN LOWER(sc.name) IN ('crown court', 'crown court enquiries', 'crown court listing', 'crown court fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Crown court')

    WHEN LOWER(sc.name) IN ('enforcement', 'bailiffs', 'bailiffs fax', 'confiscation', 'warrant of control south east')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Enforcement')

    WHEN LOWER(sc.name) IN ('enquiries', 'probate helpline', 'issue', 'issue fax', 'company issue', 'company winding up', 'general enquiries')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Enquiries')

    WHEN LOWER(sc.name) IN ('disabled access', 'phone conferencing')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Facilities')

    WHEN LOWER(sc.name) IN ('family queries', 'family listing', 'family fax', 'divorce', 'divorce fax', 'appointments - family')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Family court')

    WHEN LOWER(sc.name) IN ('attachment of earnings', 'charging orders', 'claims issue', 'fees', 'graduated fees', 'small claims',
                            'fixed penalties', 'regional taxing', 'senior courts costs')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Finances')

    WHEN LOWER(sc.name) IN ('immigration and asylum')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Immigration')

    WHEN LOWER(sc.name) IN ('jury service', 'jury service fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Jury service')

    WHEN LOWER(sc.name) IN ('legal aid', 'legal aid fax', 'legal representation fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Legal aid')

    WHEN LOWER(sc.name) IN ('listing', 'listing fax', 'listing (circuit judges)', 'listing (district judges)')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Listing')

    WHEN LOWER(sc.name) IN ('magistrates'' court', 'magistrates'' court fax', 'magistrates court enquiries', 'magistrates court listing', 'magistrates listing')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Magistrates court')

    WHEN LOWER(sc.name) IN ('masters', 'masters fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Masters')

    WHEN LOWER(sc.name) IN ('small claims mediation', 'mediation')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Mediation')

    WHEN sc.name ILIKE '%office'
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Office')

    WHEN LOWER(sc.name) IN ('pay a fine', 'payments', 'fine queries', 'fines fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Payments')

    WHEN LOWER(sc.name) IN ('post court', 'post court fax', 'pre court', 'magistrates'' post court fax', 'magistrates'' pre court fax')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Pre and post court')

    WHEN LOWER(sc.name) IN ('queen''s bench', 'queen''s bench foreign process', 'queen''s bench judges listing')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Queen''s bench')

    WHEN LOWER(sc.name) IN ('registry', 'registrar''s hearings')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Registry')

    WHEN LOWER(sc.name) IN ('social security and child support', 'social security and child support fax', 'social security and child support minicom',
                            'social security and child support tribunals', 'child maintenance')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Social security and child support')

    WHEN sc.name ILIKE 'Personal support unit'
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Support through court')

    WHEN LOWER(sc.name) IN ('tribunals', 'tribunals fax', 'employment tribunal')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Tribunal')

    WHEN LOWER(sc.name) IN ('witness service', 'witness care unit')
    THEN (SELECT act.id FROM admin_contacttype as act WHERE act.name = 'Witness service')

    ELSE (SELECT act.id FROM admin_contacttype as act WHERE LOWER(act.name) = LOWER(sc.name))
END
-- Prevent empty expensive updates and dead rows FROM being produced
AND   sc.contact_type_id IS DISTINCT FROM act.id;

