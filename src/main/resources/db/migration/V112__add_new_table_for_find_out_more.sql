CREATE TABLE public.search_additionallink (
    id integer PRIMARY KEY NOT NULL,
    url character varying(255),
    description character varying(255),
    description_cy character varying(255)
);

CREATE SEQUENCE public.search_additionallink_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_additionallink_id_seq OWNED BY public.search_additionallink.id;
ALTER TABLE ONLY public.search_additionallink ALTER COLUMN id SET DEFAULT nextval('public.search_additionallink_id_seq'::regclass);

CREATE TABLE public.search_courtadditionallink (
    id integer PRIMARY KEY NOT NULL,
    court_id integer NOT NULL,
    additional_link_id integer NOT NULL,
    sort integer NOT NULL
);

CREATE SEQUENCE public.search_courtadditionallink_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_courtadditionallink_id_seq OWNED BY public.search_courtadditionallink.id;
ALTER TABLE ONLY public.search_courtadditionallink ALTER COLUMN id SET DEFAULT nextval('public.search_courtadditionallink_id_seq'::regclass);


INSERT INTO public.search_additionallink(url, description, description_cy)
VALUES ('https://www.supportthroughcourt.org',
        'Support through court (Independent charity)',
        'Support through court (Independent charity)'
       );

INSERT INTO public.search_courtadditionallink(court_id, additional_link_id, sort)
VALUES ((SELECT id FROM public.search_court WHERE slug = 'west-london-family-court'),
        (SELECT id FROM public.search_additionallink ORDER BY id DESC LIMIT 1),
        0
       );
