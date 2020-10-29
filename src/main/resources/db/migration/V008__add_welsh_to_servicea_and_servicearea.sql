ALTER TABLE public.search_service
	ADD COLUMN name_cy CHARACTER VARYING(250);

ALTER TABLE public.search_service
	ADD COLUMN description_cy CHARACTER VARYING(500);


ALTER TABLE public.search_servicearea
	ADD COLUMN name_cy CHARACTER VARYING(250);

ALTER TABLE public.search_servicearea
	ADD COLUMN description_cy CHARACTER VARYING(500);
