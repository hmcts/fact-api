INSERT INTO public.admin_contacttype (name, name_cy)
SELECT et.description, et.description_cy FROM public.admin_contacttype ct -- ones in contact
RIGHT JOIN public.admin_emailtype et
ON et.description = ct.name
WHERE et.description NOT IN (
    SELECT ct.name
    FROM public.admin_contacttype ct
);

INSERT INTO public.admin_emailtype (description, description_cy)
SELECT ct.name, ct.name_cy
FROM public.admin_emailtype et
         RIGHT JOIN public.admin_contacttype ct
                    on et.description = ct.name
WHERE ct.name NOT IN (
    SELECT et.description
    FROM public.admin_emailtype et
);


CREATE SEQUENCE public.search_email_type_id_seq
	START WITH 100
	INCREMENT BY 1
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER SEQUENCE public.search_email_type_id_seq OWNED BY public.admin_emailtype.id;

ALTER TABLE ONLY public.admin_emailtype
	ALTER COLUMN id SET DEFAULT nextval('public.search_email_type_id_seq'::regclass);
