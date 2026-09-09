```markdown
# Boekenbeheer Systeem

Een REST API voor het beheren van boeken, klanten en uitleningen in een bibliotheeksysteem.  
Gebouwd als eindopdracht voor de Leerlijn Backend 2.0 aan NOVI Hogeschool.

## Beschrijving

De Boekenbeheer API biedt de volgende functionaliteit:

- **Auteurs beheren** — toevoegen, opvragen, bijwerken en verwijderen
- **Boeken beheren** — toevoegen, opvragen per categorie, bijwerken en verwijderen
- **Boekexemplaren beheren** — fysieke exemplaren koppelen aan boeken
- **Klanten beheren** — klantregistratie en profielbeheer
- **Bibliotheekpassen beheren** — uitgeven en beheren van bibliotheekpassen
- **Uitleningen registreren** — boeken uitlenen en retourneren
- **Boekomslagen uploaden en downloaden** — bestandsbeheer per boek
- **Authenticatie en autorisatie** — via Keycloak OAuth2 met de rollen `BEHEERDER` en `KLANT`

## Technologieën

| Technologie | Versie |
|---|---|
| Java | 21 (Temurin) |
| Spring Boot | 3.4.3 |
| PostgreSQL | 17 |
| Keycloak | 26.3 |
| Maven | 3.9 |
| Docker | Docker Desktop |

## Vereisten

Voor het lokaal uitvoeren van het project zijn de volgende onderdelen nodig:

- Java 21
- Docker Desktop

Het project bevat een Maven Wrapper (`mvnw`), waardoor Maven niet afzonderlijk hoeft te worden geïnstalleerd.

## Projectstructuur

```text
library-api/
├── src/
│   ├── main/
│   │   ├── java/nl/novi/boekenbeheer/
│   │   │   ├── config/          # OpenAPI configuratie
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/
│   │   │   │   ├── request/     # Request DTO's
│   │   │   │   └── response/    # Response DTO's
│   │   │   ├── entity/          # JPA entiteiten
│   │   │   ├── enums/           # Enumeraties
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── mapper/          # Entity ↔ DTO mappers
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # Spring Security configuratie
│   │   │   ├── service/         # Business logica
│   │   │   ├── util/            # Hulpklassen voor bestandsopslag
│   │   │   ├── validation/      # Validatie
│   │   │   └── LibraryApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application.properties.example
│   │       └── data.sql         # Testdata
│   └── test/
│       ├── java/nl/novi/boekenbeheer/
│       │   ├── service/         # Unit- en integratietests
│       │   └── LibraryApiApplicationTests.java
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

## Installatie

### Stap 1 — Repository klonen

```bash
git clone https://github.com/I-Tosun/library-api.git
cd library-api
```

### Stap 2 — PostgreSQL starten via Docker

```bash
docker run -d \
  --name postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -e POSTGRES_DB=boekenbeheerdb \
  -p 5434:5432 \
  postgres:17
```

### Stap 3 — Keycloak starten via Docker

```bash
docker run -d \
  --name keycloak \
  -p 8180:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.3 \
  start-dev
```

### Stap 4 — Keycloak realm importeren

1. Ga naar http://localhost:8180
2. Log in met gebruikersnaam `admin` en wachtwoord `admin`.
3. Klik linksboven op het dropdownmenu naast **Keycloak**.
4. Kies **Create realm**.
5. Klik op **Browse** en selecteer het meegeleverde Keycloak-exportbestand `boekenbeheer-realm.json`.
6. Klik op **Create**.

De realm `boekenbeheer` wordt aangemaakt met de geconfigureerde rollen, client en testgebruikers.

### Stap 5 — Applicatie starten

```bash
./mvnw spring-boot:run
```

De applicatie start op poort **8080**.

Bij het starten van de applicatie worden de database en de beschikbare testdata geïnitialiseerd.

## Als Docker al eerder is gebruikt

Wanneer de PostgreSQL- en Keycloak-containers al bestaan, kunnen deze opnieuw worden gestart met:

```bash
docker start postgres keycloak
```

## Tests uitvoeren

Voer alle tests uit met:

```bash
./mvnw clean test
```

De huidige testset bestaat uit:

- 40 tests totaal
- 0 failures
- `LoanService`: 100% line coverage
- `BookService`: 100% line coverage

### Coverage rapport bekijken

Na het uitvoeren van de tests kan het JaCoCo-rapport worden geopend met:

```bash
open target/site/jacoco/index.html
```

## API-documentatie

Na het starten van de applicatie is de Swagger UI beschikbaar via:

```text
http://localhost:8080/swagger-ui.html
```

Swagger kan worden gebruikt om de beschikbare endpoints te bekijken en de API rechtstreeks te testen.

Voor uitgebreide endpointdocumentatie, request- en responsevoorbeelden en de autorisatiematrix wordt verwezen naar de afzonderlijke API-documentatie.

## Postman

De API kan worden getest met de meegeleverde Postman-collectie.

Importeer het bestand:

```text
Boekenbeheer-API.postman_collection.json
```

in Postman.

### Postman environment

Gebruik een Postman environment met de volgende variabelen:

| Variabele | Waarde |
|---|---|
| `baseUrl` | `http://localhost:8080` |
| `keycloakUrl` | `http://localhost:8180` |
| `clientSecret` | Zie Keycloak → Clients → `boekenbeheer-client` → Credentials |
| `username` | `beheerder` |
| `password` | `beheerder123` |
| `token` | Wordt automatisch gevuld via het Get Token request |

De Postman-collectie gebruikt het verkregen JWT-token voor authenticatie van de API-requests.

## Testgebruikers

De volgende accounts zijn bedoeld voor lokaal testen:

| Gebruikersnaam | Wachtwoord | Rol |
|---|---|---|
| `beheerder` | `beheerder123` | `BEHEERDER` |
| `klant` | `klant123` | `KLANT` |

## Rollen en rechten

| Endpoint | Methode | BEHEERDER | KLANT |
|---|---|---|---|
| `/api/authors` | GET | Toegestaan | Toegestaan |
| `/api/authors` | POST / PUT / DELETE | Toegestaan | Niet toegestaan |
| `/api/books` | GET | Toegestaan | Toegestaan |
| `/api/books` | POST / PUT / DELETE | Toegestaan | Niet toegestaan |
| `/api/books/{id}/cover` | GET | Toegestaan | Toegestaan |
| `/api/books/{id}/cover` | POST | Toegestaan | Niet toegestaan |
| `/api/book-copies` | GET | Toegestaan | Toegestaan |
| `/api/book-copies` | POST / DELETE | Toegestaan | Niet toegestaan |
| `/api/customers` | GET / POST / PUT / DELETE | Toegestaan | Niet toegestaan |
| `/api/library-cards` | GET / POST / PUT / DELETE | Toegestaan | Niet toegestaan |
| `/api/loans` | GET | Toegestaan | Toegestaan |
| `/api/loans` | POST | Toegestaan | Toegestaan |
| `/api/loans/{id}/return` | PUT | Toegestaan | Niet toegestaan |
| `/api/loans` | DELETE | Toegestaan | Niet toegestaan |

De volledige autorisatie en alle beschikbare endpoints zijn uitgewerkt in de afzonderlijke API-documentatie.

## GitHub

De broncode van het project is beschikbaar via:

https://github.com/I-Tosun/library-api
```
```