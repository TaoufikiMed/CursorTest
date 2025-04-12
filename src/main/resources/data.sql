-- Insert permissions
INSERT INTO permissions (name, description)
    VALUES ('users.read', 'Permission to read user details')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO permissions (name, description)
    VALUES ('users.update', 'Permission to update user details')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO permissions (name, description)
    VALUES ('users.delete', 'Permission to delete users')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

-- Insert roles
INSERT INTO roles (name, description)
    VALUES ('ROLE_USER', 'Standard user role with basic permissions')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO roles (name, description)
    VALUES ('ROLE_ADMIN', 'Administrative role with full access')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r 
CROSS JOIN permissions p 
WHERE r.name = 'ROLE_ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- Assign read and update permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r 
CROSS JOIN permissions p 
WHERE r.name = 'ROLE_USER' 
AND p.name IN ('users.read', 'users.update')
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
); 