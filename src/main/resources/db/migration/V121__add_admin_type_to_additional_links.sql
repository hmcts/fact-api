ALTER TABLE public.search_additionallink
ADD COLUMN type CHARACTER VARYING(500);

UPDATE public.search_additionallink
SET type = (CASE
                WHEN url = 'https://www.gov.uk/money-property-when-relationship-ends' THEN 'Financial Remedy'
                ELSE description
            END);
