```toml
name = 'login'
method = 'POST'
url = '{{url}}/api/auth/login'
sortWeight = 1000000
id = 'c7ebb6db-c182-498f-9ed8-c632e6c03d5e'

[[queryParams]]
key = 'username'
value = 'admin@pinter.id'
disabled = true

[[queryParams]]
key = 'password'
value = 'adminberi'
disabled = true

[auth]
type = 'NO_AUTH'

[body]
type = 'JSON'
raw = '''
//{
//  email: "admin@pinter.id",
//  password: "adminberi"
//}

{
  email: "ari@pinter.id",
  password: "adminberi"
}
'''
```
