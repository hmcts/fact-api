UPDATE public.search_dxcode
SET code = 'DX 331805, CANARY WHARF 9'
WHERE id IN (
    SELECT dx.id
    FROM public.search_dxcode dx
    JOIN public.search_courtdxcode cdx ON dx.id = cdx.dx_code_id
    JOIN public.search_court c ON c.id = cdx.court_id
    WHERE c.slug = 'london-collection-and-compliance-centre'
    AND dx.code = 'DX 141424, BLOOMSBURY 7'
);
