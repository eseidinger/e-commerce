# E-Commerce JSF

## Initial Application

Let Copilot generate:

- a data model for an arbitrary e-commerce application
- a Jakarta 10 application with JSF frontend and JPA entities to perform basic CRUD operations that runs on Payara Micro
- a Docker environment with PostgreSQL

What needed fixing:

- Dependency versions
- Package names
- Data source configuration
- Column references
- Transaction handling
- JSF forms

## Configuration

https://download.eclipse.org/microprofile/microprofile-config-3.1/microprofile-config-spec-3.1.html#default_configsources

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/Payara%20Server%20Documentation/General%20Administration/Configuration%20Variables%20Reference.html

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/MicroProfile/Config/Overview.html

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/MicroProfile/Config/Directory.html

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/Payara%20Micro%20Documentation/Payara%20Micro%20Configuration%20and%20Management/Micro%20Management/Command%20Line%20Options/Command%20Line%20Options.html

## Clustering

https://docs.payara.fish/community/docs/Technical%20Documentation/Payara%20Micro%20Documentation/Payara%20Micro%20Configuration%20and%20Management/Micro%20Management/Clustering.html

## Data Source

https://docs.payara.fish/cloud/docs/user-guides/common/application/data-source.html

## Authentication

### Keycloak Realm Config

- Create groups
- Assign users to groups
- Create custom mapping of group membership to ID token groups attribute in e-commerce client config scope e-commerce-dedicated

### Payara OIDC

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/Public%20API/OpenID%20Connect%20Support.html

Does not work for clustered application. Auth info not in shared session.

### Payara JWT

https://docs.payara.fish/community/docs/6.2025.9/Technical%20Documentation/MicroProfile/JWT.html

https://download.eclipse.org/microprofile/microprofile-jwt-auth-2.1/microprofile-jwt-auth-spec-2.1.html

Create JWT cookie in callback servlet. Overwrite auth header with JWT from cookie in web filter to make MP-JWT kick in. Did not work. Auth header modification not allowed.

### Custom JWT

Create JWT cookie in callback servlet. Parse JWT in web filter and wrap request. Override `getUserPrincipal` and `isUserInRole`.
