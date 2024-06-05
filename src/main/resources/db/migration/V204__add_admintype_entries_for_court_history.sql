-- FACT-1500
-- adds admintype entries for court history to admin_audittype table

INSERT INTO public.admin_audittype(name)
VALUES('Create court history'), ('Update court history'), ('Delete court history');

