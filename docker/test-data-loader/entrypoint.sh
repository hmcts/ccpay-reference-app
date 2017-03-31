#!/bin/bash

/usr/local/bin/wait-for-command.sh -c 'nc -z idam-database 5432' && echo "DB ready"

psql -U idam -d idam -h idam-database -c "DELETE FROM idam.registered_user WHERE email LIKE '%binkmail.com'"
psql -U idam -d idam -h idam-database -c "INSERT INTO idam.registered_user (email, forename, surname, password, activation_date, role, level_of_access) VALUES ('idam-test-user-citizen@binkmail.com', 'Test', 'User Citizen', '\$2a\$06\$JVrpCRLGaTtukQugZz5gMO1of9aiopm8QwgfGjhjPJ.ZqaT6W.Uke', '2016-01-01 00:00:00', 'citizen', 1)"
psql -U idam -d idam -h idam-database -c "INSERT INTO idam.registered_user (email, forename, surname, password, activation_date, role, level_of_access) VALUES ('idam-test-user-unknown@binkmail.com', 'Test', 'User Unknown', '\$2a\$06\$JVrpCRLGaTtukQugZz5gMO1of9aiopm8QwgfGjhjPJ.ZqaT6W.Uke', '2016-01-01 00:00:00', 'unknown', 1)"