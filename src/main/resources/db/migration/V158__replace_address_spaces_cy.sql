UPDATE search_courtaddress SET address_cy = REPLACE(REPLACE(REPLACE(address_cy, ' ', '<>'), '><', ''), '<>', ' ');
