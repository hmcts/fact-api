-- Create new search_dxcode table
CREATE TABLE public.search_dxcode (
    id integer PRIMARY KEY NOT NULL,
    code character varying(255) NOT NULL,
    explanation character varying(255),
    explanation_cy character varying(255),
    in_leaflet boolean NOT NULL
);

CREATE SEQUENCE public.search_dxcode_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_dxcode_id_seq OWNED BY public.search_dxcode.id;
ALTER TABLE ONLY public.search_dxcode ALTER COLUMN id SET DEFAULT nextval('public.search_dxcode_id_seq'::regclass);

-- Create new joined table search_courtdxcode for linking the court table to the DX code table
CREATE TABLE public.search_courtdxcode (
    id integer PRIMARY KEY NOT NULL,
    court_id integer NOT NULL,
    dx_code_id integer NOT NULL,
    sort integer
);

CREATE SEQUENCE public.search_courtdxcode_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.search_courtdxcode_id_seq OWNED BY public.search_courtdxcode.id;
ALTER TABLE ONLY public.search_courtdxcode ALTER COLUMN id SET DEFAULT nextval('public.search_courtdxcode_id_seq'::regclass);

-- Create a temp table with both contact table id and the new dx code ID for all DX entries so they can be easily linked back
-- to the joined table (ignore any spurious DX records which are not linked to any court)
DROP TABLE IF EXISTS temp_dx_code;
SELECT * INTO TEMP TABLE temp_dx_code
FROM (SELECT id, number, explanation, explanation_cy, in_leaflet, row_number() OVER () as dx_code_id
      FROM public.search_contact
      WHERE name = 'DX' AND id IN (SELECT contact_id FROM search_courtcontact)) AS t;

-- Populate the search_dxcode table with the temp table DX entries
INSERT INTO public.search_dxcode(id, code, explanation, explanation_cy, in_leaflet)
SELECT dx_code_id, number, explanation, explanation_cy, in_leaflet
FROM temp_dx_code;

-- Populate the search_courtdxcode joined table
INSERT INTO public.search_courtdxcode(court_id, dx_code_id, sort)
SELECT court_id, dx_code_id, sort_order
FROM public.search_courtcontact cc
JOIN temp_dx_code t
    ON cc.contact_id = t.id;

-- Remove all DX entries from the contact table and its associated joined table
DELETE FROM public.search_courtcontact
WHERE contact_id IN (
    SELECT id FROM public.search_contact
    WHERE name = 'DX');

DELETE FROM public.search_contact
WHERE name = 'DX';

-- Clean up temp tables
DROP TABLE IF EXISTS temp_dx_code;
