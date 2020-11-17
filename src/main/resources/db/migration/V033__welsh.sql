UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli ceisiadau i ddiddymu partneriaeth sifil yn ein canolfan wasanaeth ganolog'
WHERE slug = 'civil-partnership';

UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli apeliadau budd-daliadau yn ein canolfan wasanaeth ganolog'
WHERE slug = 'benefits';

UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli troseddau mawr yn ein canolfan wasanaeth ganolog'
WHERE slug = 'major-criminal-offences';

UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli mân droseddau yn ein canolfan wasanaeth ganolog'
WHERE slug = 'minor-criminal-offences';

UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli eich ceisiadau am hawliadau am arian yn ein canolfan wasanaeth ganolog'
WHERE slug = 'money-claims';

UPDATE public.search_servicearea
SET text_cy = 'Rydym yn rheoli eich ceisiadau profiant yn ein canolfan wasanaeth ganolog'
WHERE slug = 'probate';

UPDATE public.search_servicearea
SET online_text_cy = 'Gwneud neu ymateb i hawliad meddiant ar-lein'
WHERE slug = 'housing';

UPDATE public.search_servicearea
SET online_text_cy = 'Gwneud cais am ysgariad ar-lein'
WHERE slug = 'divorce';

UPDATE public.search_servicearea
SET online_text_cy = 'Apelio yn erbyn penderfyniad budd-daliadau ar-lein'
WHERE slug = 'benefits';

UPDATE public.search_servicearea
SET online_text_cy = 'Gwneud hawliad am arian ar-lein'
WHERE slug = 'money-claims';

UPDATE public.search_servicearea
SET online_text_cy = 'Gwneud cais am brofiant ar-lein'
WHERE slug = 'probate';

UPDATE public.search_servicearea
SET online_text_cy = 'Apelio yn erbyn penderfyniad visa neu fewnfudo'
WHERE slug = 'immigration';

UPDATE public.search_servicearea
SET online_text_cy = 'Gwneud cais ar-lein i ddod â phartneriaeth sifil i ben'
WHERE slug = 'civil-partnership';
