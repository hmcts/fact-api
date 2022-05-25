UPDATE search_courtaddress SET address = REPLACE(REPLACE(REPLACE(address, ' ', '<>'), '><', ''), '<>', ' ');
