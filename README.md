# BoekenbeheerSysteem

Een REST API voor het beheren van boeken, klanten en uitleningen in een bibliotheeksysteem.
Gebouwd als eindopdracht voor de Leerlijn Backend 2.0 aan NOVI Hogeschool.

## Inhoudsopgave

1. [Inleiding](#inleiding)
2. [Functionaliteiten](#functionaliteiten)
3. [Technologieën en frameworks](#technologieën-en-frameworks)
4. [Vereisten](#vereisten)
5. [Installatie](#installatie)
6. [Applicatie starten](#applicatie-starten)
7. [Tests uitvoeren](#tests-uitvoeren)
8. [API-documentatie](#api-documentatie)
9. [Postman](#postman)
10. [Testgebruikers](#testgebruikers)
11. [Rollen en rechten](#rollen-en-rechten)
12. [Projectstructuur](#projectstructuur)
13. [GitHub](#github)

---

## Inleiding

Het Boekenbeheer Systeem is een REST API waarmee een bibliotheek haar boekencatalogus, klanten, bibliotheekpassen en uitleningen digitaal beheert. De API biedt beveiligde endpoints via OAuth2 met JWT-authenticatie via Keycloak, waarbij twee rollen zijn gedefinieerd: een beheerder met volledige toegang en een klant met beperkte leesrechten.

De applicatie is ontwikkeld als eindopdracht voor de Leerlijn Backend 2.0 bij NOVI Hogeschool.

---

## Functionaliteiten

- Auteurs beheren — toevoegen, opvragen, bijwerken en verwijderen
- Boeken beheren — toevoegen, opvragen per categorie, bijwerken en verwijderen
- Boekexemplaren beheren — fysieke exemplaren koppelen aan boeken
- Klanten beheren — klantregistratie en profielbeheer
- Bibliotheekpassen beheren — uitgeven en beheren van bibliotheekpassen
- Uitleningen registreren — boeken uitlenen en retourneren
- Boekomslagen uploaden en downloaden — bestandsbeheer per boek
- Authenticatie en autorisatie — via Keycloak OAuth2 met rollen `BEHEERDER` en `KLANT`
- Automatische testdata — via `data.sql` bij het starten van de applicatie
- API-documentatie — via Swagger UI

---

## Technologieën en frameworks

| Technologie | Versie | Doel |
|---|---|---|
| Java | 21 (Temurin) | Programmeertaal |
| Spring Boot | 3.4.3 | Applicatieframework |
| Spring Security | 6 | Authenticatie en autorisatie |
| Spring Data JPA | 3 | Databasecommunicatie |
| Hibernate | 6 | ORM framework |
| PostgreSQL | 17 | Relationele database |
| Keycloak | 26.3 | Identity provider (OAuth2/JWT) |
| Docker | Desktop | Containerisatie |
| Maven | 3.9 | Build tool |
| Lombok | 1.18 | Boilerplate code reductie |
| SpringDoc OpenAPI | 2.8 | Swagger UI generatie |
| JaCoCo | 0.8.12 | Code coverage rapportage |
| JUnit 5 | 5 | Testframework |
| Mockito | 5 | Mock framework voor unit tests |

---

## Vereisten

Zorg dat de volgende software geïnstalleerd is:

- Java 21 (Temurin aanbevolen)
- Docker Desktop

Controleer de installatie:

```bash
java -version
docker --version
```

---

## Installatie

Volg onderstaande stappen om het project lokaal op te zetten.

### Stap 1
### Optie A — Repository klonen

```bash
Optie A —  git clone https://github.com/I-Tosun/library-api.git
cd library-api
```
### Optie B — ZIP-bestand
```bash
Optie B — Project ontvangen als ZIP-bestand
1. Download het aangeleverde ZIP-bestand.
2. Pak het ZIP-bestand uit op de gewenste locatie.
3. Open de uitgepakte map library-api in IntelliJ IDEA.
4. Kies Open en selecteer de map library-api.
5. IntelliJ IDEA herkent het project als Maven-project via pom.xml.
6. Wacht totdat Maven alle dependencies heeft ingeladen.
7. Controleer of de projectstructuur overeenkomt met de structuur in deze README.
8. Controleer vervolgens de configuratie in:
```
```bash
src/main/resources/application.properties
Het project is daarna klaar om de benodigde PostgreSQL- en Keycloak-containers op te zetten.
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

1. Ga naar `http://localhost:8180`
2. Log in met gebruikersnaam `admin` en wachtwoord `admin`
3. Klik linksboven op het dropdownmenu naast **Keycloak**
4. Kies **Create realm**
5. Klik op **Browse** en selecteer het meegeleverde exportbestand `realm-export.json`
6. Klik op **Create**

De realm `boekenbeheer` wordt aangemaakt inclusief de geconfigureerde rollen, client en testgebruikers.

> **Let op:** De UUID's van de testgebruikers in Keycloak moeten overeenkomen met de waarden in `data.sql`. Bij gebruik van de meegeleverde `realm-export.json` worden de juiste UUID's automatisch aangemaakt.

### Stap 5 — application.properties controleren

Controleer dat `src/main/resources/application.properties` de volgende waarden bevat:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/boekenbeheerdb
spring.datasource.username=postgres
spring.datasource.password=1234
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/boekenbeheer
spring.security.oauth2.resourceserver.jwt.audiences=account
```

---

## Applicatie starten

```bash
./mvnw spring-boot:run
```

De applicatie start op poort **8080**.

Bij het starten wordt de benodigde databasestructuur bijgewerkt door Hibernate en wordt de beschikbare testdata geïnitialiseerd via `data.sql`.

### Als Docker al eerder is gebruikt

Wanneer de PostgreSQL- en Keycloak-containers al bestaan, kunnen deze opnieuw worden gestart met:

```bash
docker start postgres keycloak
```

---

## Tests uitvoeren

Voer alle tests uit met:

```bash
./mvnw clean test
```

De huidige testset bestaat uit:

| Testklasse | Aantal tests | Type |
|---|---|---|
| `LoanServiceTest` | 13 | Unit test |
| `BookServiceTest` | 17 | Unit test |
| `AuthorControllerIntegrationTest` | 4 | Integratietest |
| `BookControllerIntegrationTest` | 5 | Integratietest |
| `LibraryApiApplicationTests` | 1 | Context test |
| **Totaal** | **40** | |

- 0 failures
- `LoanService`: 100% line coverage
- `BookService`: 100% line coverage

### Coverage rapport bekijken

Na het uitvoeren van de tests kan het JaCoCo-rapport worden geopend met:

```bash
open target/site/jacoco/index.html
```

---

## API-documentatie

Na het starten van de applicatie is de Swagger UI beschikbaar via:

```
http://localhost:8080/swagger-ui.html
```

Swagger kan worden gebruikt om de beschikbare endpoints te bekijken, request- en responsevoorbeelden te zien en de API rechtstreeks te testen.

De OpenAPI specificatie is beschikbaar via:

```
http://localhost:8080/api-docs
```

---

## Postman

De API kan worden getest met de meegeleverde Postman-collectie.

Importeer het bestand `Boekenbeheer-API.postman_collection.json` in Postman.

### Postman environment

Maak een environment aan met de naam `Boekenbeheer Local` en voeg de volgende variabelen toe:

| Variable | Waarde |
|---|---|
| `baseUrl` | `http://localhost:8080` |
| `keycloakUrl` | `http://localhost:8180` |
| `clientSecret` | Zie Keycloak → Clients → `boekenbeheer-client` → Credentials |
| `username` | `beheerder` |
| `password` | `beheerder123` |
| `token` | Wordt automatisch gevuld via het Get Token request |

De collectie bevat een **Get Token** request die automatisch het JWT-token opslaat in `{{token}}`. Alle overige requests gebruiken dit token voor authenticatie.

---

## Testgebruikers

| Gebruikersnaam | Wachtwoord | Rol |
|---|---|---|
| `beheerder` | `beheerder123` | `BEHEERDER` |
| `klant` | `klant123` | `KLANT` |

---

## Rollen en rechten

| Endpoint | Methode | BEHEERDER | KLANT    |
|---|---|--|----------|
| `/api/authors` | GET | Toegestaan | Toegestaan |
| `/api/authors` | POST / PUT / DELETE | Toegestaan | Niet toegestaan         |
| `/api/books` | GET | Toegestaan | Toegestaan |
| `/api/books` | POST / PUT / DELETE | Toegestaan | Niet toegestaan         |
| `/api/books/{id}/cover` | GET | Toegestaan | Toegestaan |
| `/api/books/{id}/cover` | POST | Toegestaan | Niet toegestaan         |
| `/api/book-copies` | GET | Toegestaan | Toegestaan |
| `/api/book-copies` | POST / DELETE | Toegestaan | Niet toegestaan         |
| `/api/customers` | GET / POST / PUT / DELETE | Toegestaan | Niet toegestaan         |
| `/api/library-cards` | GET / POST / PUT / DELETE | Toegestaan | Niet toegestaan         |
| `/api/loans` | GET | Toegestaan | Toegestaan |
| `/api/loans` | POST | Toegestaan | Toegestaan |
| `/api/loans/{id}/return` | PUT | Toegestaan | Niet toegestaan         |
| `/api/loans` | DELETE | Toegestaan | Niet toegestaan |

---

## Projectstructuur

```
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
│   │   │   ├── enums/           # Enumeraties (BookCopyStatus)
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
│   │       └── data.sql         # Testdata
│   └── test/
│       ├── java/nl/novi/boekenbeheer/
│       │   ├── service/         # Unit- en integratietests
│       │   └── LibraryApiApplicationTests.java
│       └── resources/
│           └── application.properties
├── docs/
│   └── uml/
│       ├── class-diagram.puml
│       ├── sequence-loan.puml
│       └── sequence-return.puml
├── pom.xml
└── README.md
```

---

## GitHub

De broncode van het project is beschikbaar via:

**https://github.com/I-Tosun/library-api**
