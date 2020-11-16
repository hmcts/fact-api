ALTER TABLE ONLY public.search_servicearea
    ADD CONSTRAINT con_catchment_method CHECK (catchment_method IN ('postcode', 'proximity', 'local-authority'));
