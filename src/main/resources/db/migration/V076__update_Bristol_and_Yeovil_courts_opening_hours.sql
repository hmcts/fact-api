-- Bristol Crown Court (FACT-395)

UPDATE public.search_openingtime
SET hours = 'Urgent enquiries only 09.00 am to 4.00pm'
WHERE id = 1083617;

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 8.30am to 5pm'
WHERE id = 1084203;

-- Yeovil County, Family and Magistrates' Court update (FACT-392)

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10am to 2pm'
WHERE id = 1084074;

UPDATE public.search_openingtime
SET hours = 'Monday to Friday 10am to 3:30pm'
WHERE id = 1084196;
