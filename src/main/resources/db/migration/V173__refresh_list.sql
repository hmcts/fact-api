DELETE FROM public.search_email
WHERE admin_email_type_id NOT IN (
  SELECT id
  FROM public.admin_emailtype
);
