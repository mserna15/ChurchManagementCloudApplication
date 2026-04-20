# Church Community Management System (CCMS)

A Spring Boot web application for managing church members, ministry groups, events, and announcements.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Database Schema](#database-schema)
- [Running Locally](#running-locally)
- [Deployment](#deployment)
- [UptimeRobot Monitoring](#uptimerobot-monitoring)
- [Logging](#logging)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.1.5 |
| Templates | Thymeleaf |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL |
| Build Tool | Maven (wrapper included) |
| Monitoring | Spring Boot Actuator + UptimeRobot |
| Logging | Log4J2 + Loggly |

---

## Architecture Overview

The app follows a standard Spring Boot MVC layered architecture:

```
Browser
  │
  ▼
Controller Layer        (handles HTTP requests, routes to views)
  │
  ▼
Service Layer           (business logic)
  │
  ▼
Repository Layer        (Spring Data JPA interfaces)
  │
  ▼
MySQL Database
```

### Package Structure

```
src/main/java/com/ccms/
├── CcmsApplication.java          # Entry point
├── controller/
│   ├── HomeController.java       # Serves the home/landing page
│   ├── MemberController.java     # CRUD for church members
│   ├── MinistryGroupController.java  # CRUD for ministry groups
│   ├── AnnouncementController.java   # CRUD for announcements
│   └── EventController.java      # CRUD for events
├── model/
│   ├── Member.java
│   ├── MinistryGroup.java
│   ├── Announcement.java
│   └── Event.java
├── repository/
│   ├── MemberRepository.java
│   ├── MinistryGroupRepository.java
│   ├── AnnouncementRepository.java
│   └── EventRepository.java
└── service/
    ├── MemberService.java
    ├── MinistryGroupService.java
    ├── AnnouncementService.java
    └── EventService.java

src/main/resources/
├── application.properties        # App config, DB connection, Actuator settings
└── templates/
    ├── members/                  # list.html, form.html
    ├── groups/                   # list.html, form.html
    ├── announcements/            # list.html, form.html
    └── events/                   # list.html, form.html
```

### URL Routes

| URL | Description |
|---|---|
| `GET /` | Home page |
| `GET /members` | List all members |
| `GET /members/new` | New member form |
| `POST /members/save` | Save a member |
| `GET /members/edit/{id}` | Edit member form |
| `GET /members/delete/{id}` | Delete a member |
| `GET /groups` | List all ministry groups |
| `GET /groups/new` | New group form |
| `POST /groups/save` | Save a group |
| `GET /groups/edit/{id}` | Edit group form |
| `GET /groups/delete/{id}` | Delete a group |
| `GET /announcements` | List all announcements |
| `GET /events` | List all events |
| `GET /actuator/health` | Health check endpoint (UptimeRobot) |

---

## Database Schema

Database name: `churchcmsdb`

### `ministry_groups`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Primary key, auto-increment |
| group_name | VARCHAR(150) | Unique, not null |
| description | TEXT | Optional |

### `members`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Primary key, auto-increment |
| first_name | VARCHAR(100) | Not null |
| last_name | VARCHAR(100) | Not null |
| email | VARCHAR(150) | Unique, not null |
| phone | VARCHAR(20) | Optional |
| join_date | DATE | Optional |
| ministry_group_id | BIGINT | FK → ministry_groups(id), SET NULL on delete |

### `announcements`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Primary key, auto-increment |
| (fields defined by Announcement.java entity) | | Auto-created by Hibernate |

### `events`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Primary key, auto-increment |
| (fields defined by Event.java entity) | | Auto-created by Hibernate |

> **Note:** The `announcements` and `events` tables are not included in the SQL dump — they are created automatically by Hibernate on first startup because `ddl-auto=update` is set in `application.properties`.

### Loading the Schema

```sql
CREATE DATABASE churchcmsdb CHARACTER SET utf8;
USE churchcmsdb;
SOURCE /path/to/DB/churchcmsdb.sql;
```

---

## Running Locally

### Prerequisites

- Java 17 JDK — verify with `java -version`
- MySQL running locally (5.7+ or 8.x)
- No need to install Maven — the `mvnw` wrapper is included

### Step 1 — Create the database

```sql
CREATE DATABASE churchcmsdb CHARACTER SET utf8;
USE churchcmsdb;
SOURCE /full/path/to/DB/churchcmsdb.sql;
```

### Step 2 — Configure credentials

Open `src/main/resources/application.properties` and set your actual MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/churchcmsdb
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

> **Security note:** Do not commit real credentials to GitHub. Consider using environment variables or an `application-local.properties` file added to `.gitignore` for any shared/public repos.

### Step 3 — Run

```bash
./mvnw spring-boot:run
```

The app will be available at **http://localhost:8080**

### Troubleshooting

| Error | Cause | Fix |
|---|---|---|
| `Driver claims to not accept jdbcUrl, ${DATASOURCE_URL}` | Environment variables not set | Set credentials directly in `application.properties` |
| `Access denied for user 'root'@'localhost'` | Wrong MySQL password | Verify password with `mysql -u root -p` in terminal |
| `Schema-validation: missing table [announcements]` | `ddl-auto=validate` with missing tables | Change to `ddl-auto=update` in `application.properties` |

---

## Deployment

The app is designed to be deployed to a cloud platform (e.g. Railway, Render, AWS, etc.) using environment variables for database credentials rather than hardcoding them.

### Environment Variables Required

| Variable | Example Value |
|---|---|
| `DATASOURCE_URL` | `jdbc:mysql://your-db-host:3306/churchcmsdb` |
| `DATASOURCE_USERNAME` | `ccmsuser` |
| `DATASOURCE_PASSWORD` | `yourpassword` |

### Building a JAR for Deployment

```bash
./mvnw clean package -DskipTests
```

This produces a runnable JAR at `target/church-community-management-0.0.1-SNAPSHOT.jar`.

Run it on the server with:

```bash
java -jar target/church-community-management-0.0.1-SNAPSHOT.jar
```

Or with environment variables inline:

```bash
DATASOURCE_URL=jdbc:mysql://host:3306/churchcmsdb \
DATASOURCE_USERNAME=ccmsuser \
DATASOURCE_PASSWORD=yourpassword \
java -jar target/church-community-management-0.0.1-SNAPSHOT.jar
```

The app runs on **port 8080** by default. Most cloud platforms handle SSL termination in front of it so the app itself does not need HTTPS configured.

---

## UptimeRobot Monitoring

The app exposes a health check endpoint powered by **Spring Boot Actuator** that UptimeRobot pings on a schedule to detect downtime.

### Health Endpoint

```
GET /actuator/health
```

Sample response when healthy:
```json
{
  "status": "UP"
}
```

The endpoint also checks the database connection automatically. If MySQL goes down, the status will return `DOWN` and UptimeRobot will fire an alert.

### What Was Added to Enable This

**`pom.xml`** — Actuator dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**`application.properties`** — Expose only the health endpoint publicly:
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
```

### Configuring UptimeRobot

1. Log into [uptimerobot.com](https://uptimerobot.com)
2. Click **Add New Monitor**
3. Set **Monitor Type** → `HTTP(s)`
4. Set **URL** → `https://yourdomain.com/actuator/health`
5. Set **Monitoring Interval** → 5 minutes
6. Add an **Alert Contact** (email or SMS) to be notified on downtime
7. Save — UptimeRobot will begin pinging immediately

> No API keys, tokens, or code changes are needed on the app side. UptimeRobot is fully external and works by simply checking that the endpoint returns an HTTP 200 response.

---

## Logging

The app uses **Log4J2** as the logging framework and ships logs to **Loggly** as the cloud-based log aggregation platform.

### How It Works

```
App Code (controllers, services)
  │
  ▼
Log4J2                  (formats and routes log events)
  │
  ▼
Loggly Syslog Appender  (ships logs over the network)
  │
  ▼
Loggly Dashboard        (search, filter, and monitor logs in real time)
```

### Log Levels in Use

| Level | Where | Example |
|---|---|---|
| `INFO` | App startup | `Started CcmsApplication in 100.7 seconds` |
| `DEBUG` | Controllers | `MemberController.listMembers() - Request received` |
| `DEBUG` | Services | `MemberService.getAllMembers() - Fetching all members` |
| `DEBUG` | Services | `MemberService.getAllMembers() - Found 12 members` |

Logging is applied at both the **controller layer** (request received) and the **service layer** (what was fetched and what was returned), giving full traceability of a request through the stack.

### Loggly Dashboard

Logs are viewable in real time at [loggly.com](https://loggly.com) under the `ccms` tag. Each log event includes:

- Timestamp
- Client IP (`clientHost`)
- Content type
- Full log message with class and method name
