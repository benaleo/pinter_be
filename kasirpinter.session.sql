SELECT u.id, u.email, u.company_id, u.password
FROM users u
LEFT JOIN company c ON u.company_id = c.secure_id;

-- UPDATE users
-- SET company_id = NULL
-- WHERE users.id = 2