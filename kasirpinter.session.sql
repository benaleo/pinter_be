SELECT u.id, u.email, u.company_id, u.password
FROM um_users u
LEFT JOIN ms_companies c ON u.company_id = c.secure_id;

-- UPDATE users
-- SET company_id = NULL
-- WHERE users.id = 2