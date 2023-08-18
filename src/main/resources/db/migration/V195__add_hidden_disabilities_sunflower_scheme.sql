-- Reset broken id_seq
SELECT setval('admin_facilitytype_id_seq', COALESCE((SELECT MAX(id) FROM admin_facilitytype), 0));

-- Add facilities only for in-person courts
INSERT INTO search_facility (name, description, image, image_description, image_file_path, name_cy, description_cy)
SELECT
  'Hidden Disabilities Sunflower network',
  'Lanyards available on request.',
  'disabled',
  'Disabled access icon',
  'uploads/facility/image_file/49/disabled.png',
  'Rhwydwaith Blodau''r Haul Anableddau Cudd',
  'Mae cortynnau gwddf ar gael ar gais.'
FROM (
       SELECT s.id
       FROM search_court s
              JOIN search_inperson si ON s.id = si.court_id
       WHERE si.is_in_person = TRUE
     ) AS in_person_courts;


WITH CourtRows AS (
  SELECT s.id as court_id, ROW_NUMBER() OVER (ORDER BY s.id) as rn
  FROM search_court s
         JOIN search_inperson si ON s.id = si.court_id
  WHERE si.is_in_person = TRUE
),
     FacilityRows AS (
       SELECT id as facility_id, ROW_NUMBER() OVER (ORDER BY id DESC) as rn FROM search_facility
       WHERE name = 'Hidden Disabilities Sunflower network'
     )

-- Linking facilities to courts
INSERT INTO search_courtfacility (court_id, facility_id)
SELECT c.court_id, f.facility_id
FROM CourtRows c
       JOIN FacilityRows f ON c.rn = f.rn;

-- Insert Hidden Disability Sunflower network facility
WITH HiddenDisabilitySunflowerFacilityType AS (
  INSERT INTO admin_facilitytype
    ("name", "image", "image_description", "image_file_path", "order", "image_description_cy", "name_cy")
    VALUES
      (
        'Hidden Disabilities Sunflower network',
        'disabled',
        'Disabled access icon',
        'uploads/facility/image_file/49/disabled.png',
        23,
        'Eicon mynediad i bobl anabl',
        'Rhwydwaith Blodau''r Haul Anableddau Cudd'
      )
    RETURNING id AS facility_type_id
)

-- Link each new search_facility record to the new admin facility type.
INSERT INTO search_facilityfacilitytype (facility_id, facility_type_id)
SELECT
  sf.id,
  hdsft.facility_type_id
FROM search_facility sf
       CROSS JOIN HiddenDisabilitySunflowerFacilityType hdsft
WHERE sf.name = 'Hidden Disabilities Sunflower network';
