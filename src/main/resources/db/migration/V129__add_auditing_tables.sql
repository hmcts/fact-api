ALTER TABLE public.search_courtareaoflaw
ADD COLUMN created_date timestamp,
ADD COLUMN last_modified_date timestamp,
ADD COLUMN created_by character varying(255),
ADD COLUMN last_modified_by character varying(255);

ALTER TABLE public.search_areaoflaw
ADD COLUMN created_date timestamp,
ADD COLUMN last_modified_date timestamp,
ADD COLUMN created_by character varying(255),
ADD COLUMN last_modified_by character varying(255);
