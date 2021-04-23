--- FACT-95 ---
--- Adding new table for admin_email_description
CREATE TABLE public.admin_email_description
(
    id              integer PRIMARY KEY    NOT NULL,
    description_en  character varying(250) NOT NULL,
    description_cy  character varying(500) NOT NULL
);

CREATE SEQUENCE public.admin_email_description_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.admin_email_description_id_seq OWNED BY public.admin_email_description.id;

ALTER TABLE ONLY public.admin_email_description
ALTER COLUMN id SET DEFAULT nextval('public.admin_email_description_id_seq'::regclass);

-- Amending the existing search_email table to include a foreign key to the one above
ALTER TABLE public.search_email
    ADD COLUMN admin_description CHARACTER VARYING(500) NOT NULL;

-- Inserting the data for the new table
INSERT INTO public.admin_email_description(description_en, description_cy)
VALUES
('Case progression', 'Case progression cy'), ('Chancery', 'Chancery cy'),
('Civil court', 'Civil court cy'), ('County court', 'County court cy'),
('Crown court', 'Crown court cy'), ('Enquiries', 'Enquiries cy'),
('Admin', 'Admin cy'), ('Finances', 'Finances cy'),
('Family', 'Family cy'), ('Immigration', 'Immigration cy'),
('Listing', 'Listing cy'), ('Magistrates court', 'Magistrates court cy'),
('Post and pre court', 'Post and pre court cy'), ('Witness', 'Witness cy');
