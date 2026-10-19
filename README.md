# College Maintenance System

A Java-based desktop application designed to make reporting and managing college maintenance complaints easier. Students and faculty will be able to submit maintenance requests, HODs/administrators will review and assign them, and maintenance staff will update each request until the issue is resolved.

> **Project status:** Planning / initial development  
> This repository is intended for a four-member college project team. Features described below are planned and should be marked as completed only after implementation and testing.

## Project Objectives

- Provide one centralized place to submit and track maintenance complaints.
- Help HODs/administrators review, approve, reject, and assign requests.
- Allow maintenance staff to update the progress of assigned work.
- Store complaint information in MySQL so it remains available after the application closes.
- Demonstrate Java OOP, JDBC database integration, and software design patterns.

## Technology Stack

- **Language:** Java (JDK 21)
- **Desktop GUI:** Java Swing
- **Database:** MySQL
- **Database connectivity:** JDBC
- **Database driver:** MySQL Connector/J
- **IDE:** Visual Studio Code
- **Version control and collaboration:** Git and GitHub

This is a desktop application. The planned core implementation does not use HTML, CSS, JavaScript, Spring Boot, or a web frontend.

## Planned User Roles

### Student
- Log in to the application.
- Submit a maintenance complaint.
- Choose a department, room number, and complaint category.
- Add an optional description.
- View and track their own complaints.

### Faculty
- Log in to the application.
- Submit complaints about classrooms, laboratories, offices, and equipment.
- View and track their own complaints.

### HOD / Administrator
- View complaints submitted by students and faculty.
- Approve or reject submitted complaints.
- Assign approved complaints to maintenance staff.
- Filter complaints by department, category, or status.
- Monitor the complaint workflow and status history.

### Maintenance Staff
- View complaints assigned to them.
- Review the complaint details.
- Mark assigned work as in progress.
- Mark work as completed.
- View assigned and completed requests.

Role permissions are intended to be checked in the service layer, not only by hiding GUI buttons.

## Complaint Categories

The planned categories are:

- Electrical
- Plumbing
- Furniture
- Equipment
- Other

Each request is intended to store a unique request ID, submitter, department, room number, category, optional description, submission and update timestamps, current status, and assigned staff member when applicable.

## Complaint Workflow

```text
SUBMITTED ──> APPROVED ──> ASSIGNED ──> IN_PROGRESS ──> COMPLETED
     |
     └──────> REJECTED
```

The application will enforce valid status transitions. For example, a rejected complaint cannot be assigned, and work cannot start before a complaint has been assigned. Status changes and relevant history will be stored in the database.

## Planned Architecture

The application will use a layered architecture:

```text
Java Swing GUI
      |
      v
Service Layer (validation, permissions, workflow rules)
      |
      v
DAO Layer (JDBC database operations)
      |
      v
MySQL Database
```

- **Presentation layer:** Displays login screens, dashboards, complaint forms, and tracking information.
- **Service layer:** Applies business rules, validates requests, checks permissions, and coordinates operations.
- **DAO layer:** Reads and writes database records using JDBC and prepared statements.
- **Database layer:** Stores users, complaints, assignments, and complaint status history.

The GUI should not execute SQL queries directly.

## Planned Design Patterns

### Singleton
The `DatabaseConnection` component will centralize database configuration and connection creation. It should provide fresh JDBC connections as needed rather than keeping one connection open indefinitely.

### Adapter
The notification component will use a `NotificationSender` interface and a `NotificationAdapter` to translate calls into the interface expected by a legacy notifier. For the initial prototype, notifications may be displayed or recorded inside the application; external email or SMS is not required.

### Optional patterns
- **Factory:** A `RequestFactory` may centralize the creation and validation of maintenance requests.
- **Observer:** Observers may be notified when a complaint's status changes.

Singleton and Adapter are the required patterns in the current plan. Optional patterns can be added if they are useful or required by the evaluation rubric.

## Planned Project Structure

The project structure will be built gradually as each component is implemented.

```text
CollegeMaintenanceSystem/
├── src/
│   └── college/
│       └── maintenance/
│           ├── Main.java
│           ├── model/
│           ├── service/
│           ├── dao/
│           ├── database/
│           ├── factory/
│           ├── adapter/
│           ├── observer/
│           └── ui/
├── database/
│   └── schema.sql
├── lib/
│   └── mysql-connector-j.jar
├── README.md
└── .gitignore
```

The package structure and database setup instructions may be updated as implementation progresses. Do not create every class at once; add files when they are needed.

## Database Plan

The initial database is planned to be named `college_maintenance`, with these main tables:

- **`users`** — user details, email, password hash, role, department, and creation timestamp.
- **`maintenance_requests`** — complaint details, submitter, status, assigned staff, and timestamps.
- **`request_history`** — status changes, the user who made each change, timestamp, and remarks.

The schema should use primary keys, foreign keys, appropriate constraints, and prepared statements. Passwords should never be stored as plain text.

## Team Responsibilities

The team has four members. Responsibilities can be adjusted together before implementation begins.

| Team role | Main responsibilities |
|---|---|
| Member 1 — Core System Development | Develop core classes and the object-oriented structure; implement core system functionality; apply encapsulation, inheritance, and polymorphism |
| Member 2 — Database Management | Design database tables; implement JDBC connectivity; develop database operations for storing, retrieving, and updating maintenance requests; ensure data persistence across logins and application restarts |
| Member 3 — User Interface Development | Develop the Java Swing login interface and role selection; create Student and Faculty dashboards, HOD review interface, and Maintenance Staff interface; implement forms, tables, buttons, and navigation |
| Member 4 — Complaint Management and Business Logic | Implement complaint validation and business rules; manage status transitions from pending to approval, rejection, in-progress, and completion; implement complaint filtering, tracking, statistics, and reporting; integrate business logic with the database and UI; test the complete complaint management workflow |

All members should agree on class names and method signatures before implementing separate modules. Everyone should understand the overall system and be able to explain their contributions.

## Development Roadmap

1. **Environment and repository setup:** Check Java, Git, VS Code, and MySQL; create the repository and initial database schema.
2. **Core OOP model:** Create user and request models, the `RequestStatus` enum, and valid status transitions.
3. **Database integration:** Implement the connection manager and DAOs; test saving and retrieving complaints.
4. **Business services:** Implement authentication, role checks, complaint submission, approval, rejection, assignment, and completion.
5. **Swing GUI:** Build the login, complaint form, dashboards, and tracking screen.
6. **Integration and testing:** Test the complete complaint lifecycle, invalid transitions, permissions, validation, and persistence after restart.
7. **Final preparation:** Complete and explain the required design patterns, update documentation, prepare screenshots and presentation materials, and practise a demonstration.

The initial target is approximately one week for a working prototype. Prioritize one complete end-to-end workflow before adding optional features.

## Initial Test Checklist

As features are implemented, test that:

- [ ] Students and faculty can submit complaints.
- [ ] New complaints start with `SUBMITTED`.
- [ ] Complaints are saved in MySQL and remain after restarting the application.
- [ ] Only an authorized HOD/administrator can approve or reject complaints.
- [ ] Rejected complaints cannot be assigned.
- [ ] Only approved complaints can be assigned.
- [ ] Only assigned work can move to `IN_PROGRESS`.
- [ ] Only in-progress work can move to `COMPLETED`.
- [ ] Students can view their own complaints.
- [ ] Maintenance staff can view their assigned complaints.
- [ ] Unauthorized actions are rejected by the service layer.
- [ ] The notification Adapter translates calls to the notifier correctly.

## Getting Started

The project is at the planning stage, so the exact build and run commands will be added after the initial Java project structure and database configuration are established.

To begin contributing:

1. Install Git and VS Code.
2. Install JDK 21 and MySQL.
3. Accept the GitHub repository invitation from the repository owner.
4. Clone the repository to your computer.
5. Read this README and coordinate with the team before creating or changing shared classes.

Clone command (replace the URL with your repository URL):

```bash
git clone https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
```

## Collaboration Guidelines

- Pull the latest shared changes before starting work.
- Use a separate branch for each feature or task.
- Commit small, meaningful changes with clear messages.
- Push your branch and open a pull request for review before merging into `main`.
- Communicate before changing shared class interfaces or database schema.
- Do not commit passwords, database credentials, API keys, or other secrets.
- Keep this README updated as planned features become implemented and tested.

## Project Scope

The initial goal is a working Java Swing desktop application with MySQL persistence, role-based complaint handling, and the required Singleton and Adapter patterns. Optional notifications, Factory, and Observer features should not delay the core workflow.

---

**Project name:** College Maintenance System  
**Project type:** Java OOP desktop application  
**Team size:** 4 members  
**Initial prototype target:** Approximately one week
