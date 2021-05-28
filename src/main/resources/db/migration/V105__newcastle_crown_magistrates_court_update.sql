--- FACT-549 ---
UPDATE public.search_facility
SET description = '<p>A children''s room is located on the first floor.</p>'
WHERE id IN (
    SELECT facility_id FROM public.search_courtfacility
    WHERE court_id IN (
        SELECT id from public.search_court
        WHERE slug = 'newcastle-upon-tyne-combined-court-centre')
)
  AND name LIKE 'Children%';
