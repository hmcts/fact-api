ALTER TABLE public.search_areaoflaw
	ADD COLUMN display_name CHARACTER VARYING(500);

ALTER TABLE public.search_areaoflaw
	ADD COLUMN display_name_cy CHARACTER VARYING(500);

UPDATE public.search_areaoflaw
SET display_name = 'Domestic abuse'
WHERE name = 'Domestic violence';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Cam-drin domestig'
WHERE name = 'Domestic violence';

UPDATE public.search_areaoflaw
SET display_name = 'Claims against employers'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Honiadau yn erbyn cyflogwyr'
WHERE name = 'Employment';

UPDATE public.search_areaoflaw
SET display_name = 'High Court cases – serious or high profile criminal or civil law cases'
WHERE name = 'High Court District Registry';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Achosion yn yr Uchel Lys – achosion cyfraith droseddol neu gyfraith sifil difrifol neu sy’n cael cryn amlwgrwydd'
WHERE name = 'High Court District Registry';

UPDATE public.search_areaoflaw
SET display_name = 'Childcare arrangements if you separate from your partner'
WHERE name = 'Children';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Trefniadau gofal plant os ydych yn gwahanu oddi wrth eich partner'
WHERE name = 'Children';

UPDATE public.search_areaoflaw
SET display_name = 'Housing'
WHERE name = 'Housing possession';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Tai'
WHERE name = 'Housing possession';

UPDATE public.search_areaoflaw
SET display_name = 'Immigration and asylum'
WHERE name = 'Immigration';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Mewnfudo'
WHERE name = 'Immigration';

UPDATE public.search_areaoflaw
SET display_name = 'Benefits'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Budd-daliadau'
WHERE name = 'Social security';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Mabwysiadu'
WHERE name = 'Adoption';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Methdaliad'
WHERE name = 'Bankruptcy';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Partneriaeth sifil'
WHERE name = 'Civil partnership';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Ysgariad'
WHERE name = 'Divorce';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Hawliadau am arian'
WHERE name = 'Money claims';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Profiant'
WHERE name = 'Probate';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Treth'
WHERE name = 'Tax';

UPDATE public.search_areaoflaw
SET display_name_cy = 'Troseddu'
WHERE name = 'Crime';
