DELETE FROM public.search_courtapplicationupdate WHERE id IN (
    SELECT cau.id FROM public.search_applicationupdate au
    RIGHT OUTER JOIN public.search_courtapplicationupdate cau
    ON au.id = cau.application_update_id
    WHERE au.id IS null
);
