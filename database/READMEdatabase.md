# Database module (MySQL + JDBC): read this first

Hey team! This is the database part of our project. It lets our Java code **save complaints in MySQL** so they don't disappear when the program closes.

I've tested everything on my own PC and it works. You don't need to understand JDBC to use it, just call the methods below.

---

## What's in this PR (5 new files, nothing of yours is changed)

| File | What it is, in plain words |
|---|---|
| `database/schema.sql` | Creates the database and 3 tables. Run it once. |
| `src/DatabaseConnection.java` | Opens the connection to MySQL. You never need to touch it. |
| `src/RequestRecord.java` | A small box that holds one complaint's data when it travels to or from the database. |
| `src/MaintenanceRequestDAO.java` | **The important one.** All the save/load methods live here. |
| `src/DatabaseTest.java` | A test program that checks everything works. |

`Main.java` and all the other classes are untouched.

---

## Setup on your PC (about 10 minutes, do it once)

**1. Install MySQL** and remember your root password.

**2. Create the database and tables.** In the terminal:
```
mysql -u root -p
```
then at the `mysql>` prompt (change the path to wherever the file is on your PC, and use forward slashes):
```
source C:/path/to/database/schema.sql
```
Check it worked: `USE college_maintenance;` then `SHOW TABLES;` should list 3 tables.

**3. Download the MySQL driver.** Search "MySQL Connector/J download", choose **Platform Independent**, download the ZIP, and copy the `.jar` file into a `lib` folder in the project. (Jar files are not in the repo, so everyone downloads their own.)

**4. Save your MySQL password in a Windows environment variable** (so the password is never in the code or on GitHub):
```
setx DB_PASSWORD "your-mysql-password"
```
Then **close VS Code completely and open it again**. This is the step people forget.

**5. Compile and run the test** (replace the jar name with yours):
```
mkdir out
javac --release 21 -cp "lib\mysql-connector-j-XX.jar" -d out src\*.java
java -cp "out;lib\mysql-connector-j-XX.jar" DatabaseTest
```
If you see lots of `PASSED` lines, you're all set. Some `Could not...` lines in the middle are normal, they're tests that deliberately fail to check errors are handled.

Note: each time you run the test, it adds more test complaints to your database. That's fine.

---

## How to use it in your code

```java
MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

// Save a new complaint (1 = the user_id of whoever submitted it)
RequestRecord rec = new RequestRecord(1, "CSE", "204", "ELECTRICAL", "Fan is not working");
int newId = dao.insertRequest(rec);      // returns the new ID, or -1 if it failed

// Load it back
RequestRecord found = dao.getRequestById(newId);   // null if not found
System.out.println(found.getRoom() + " " + found.getStatus());

// HOD approves it (3 = the HOD's user_id)
dao.updateStatus(newId, "APPROVED", 3, "Approved by HOD");

// HOD assigns it to staff (4 = staff user_id, 3 = HOD's user_id)
dao.assignStaff(newId, 4, 3, "Assigned to Arun");
```

### All the methods

| Method | What it does |
|---|---|
| `int insertRequest(RequestRecord r)` | Saves a new complaint. Returns its new ID, or -1 on failure. |
| `RequestRecord getRequestById(int id)` | Gets one complaint. Returns `null` if it doesn't exist. |
| `ArrayList<RequestRecord> getAllRequests()` | Gets every complaint. |
| `ArrayList<RequestRecord> getRequestsBySubmitter(int userId)` | Complaints submitted by one person (for "track my complaints"). |
| `ArrayList<RequestRecord> getRequestsByStatus(String status)` | Complaints with one status, e.g. `"SUBMITTED"`. |
| `ArrayList<RequestRecord> getRequestsByAssignedStaff(int staffId)` | Complaints assigned to one staff member. |
| `boolean updateStatus(int id, String newStatus, int changedBy, String remarks)` | Changes the status and records it in the history. `true` = worked. |
| `boolean assignStaff(int id, int staffId, int changedBy, String remarks)` | Assigns staff, sets status to ASSIGNED, records history. |
| `ArrayList<String> getHistoryForRequest(int id)` | The history of one complaint, as text lines. |

**Lists never come back as `null`.** If nothing matches, you get an empty list, so you can loop over it safely.
**Failures never crash.** The method prints a message and returns `-1`, `null`, `false`, or an empty list.

---

## Important things to know

**Statuses and categories are written in CAPITALS in the database.**
- Statuses: `SUBMITTED`, `APPROVED`, `REJECTED`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`
- Categories: `ELECTRICAL`, `PLUMBING`, `FURNITURE`, `EQUIPMENT`, `OTHER`

MySQL will refuse anything else (like `"In Progress"`).

**People are stored by ID, not by name.** The database has a `users` table, and complaints store the user's ID number. I added 4 sample users for testing:

| user_id | Name | Role |
|---|---|---|
| 1 | Rahul | STUDENT |
| 2 | Sam | FACULTY |
| 3 | Meera | HOD |
| 4 | Arun | MAINTENANCE_STAFF |

**IDs are created by MySQL.** Don't make up request IDs like 101, `insertRequest` gives you the real one.

**The database only blocks invalid data.** It does **not** check workflow rules or permissions. For example, it will happily let you assign a REJECTED complaint. Rules like "only APPROVED complaints can be assigned" and "only the HOD can approve" must be checked in the service layer *before* calling the DAO.

---

## What I need from each of you

**Member 1 (OOP and business logic)**
1. **Status format:** your `MaintenanceRequest` uses `"In Progress"` and `"Approved"`, but the database needs `"IN_PROGRESS"` and `"APPROVED"`. We need to pick one way. Simplest idea: you convert when you call the DAO (for example, replace spaces with `_` and make it uppercase).
2. **Two classes for one complaint:** the DAO uses `RequestRecord`, not your `MaintenanceRequest` (I didn't want to change your class without asking). Someone needs to convert between them. Or, if you prefer, add getters to `MaintenanceRequest` and I'll change the DAO to use it.
3. **Workflow and permission rules** go in your layer, as explained above.

**Member 2 (Swing GUI)**
1. Don't call the DAO or write SQL in the GUI. Go through the service layer.
2. Complaints use user IDs, so when someone logs in, we need to keep their `user_id`.

**Member 4 (integration and testing)**
Please run the setup steps above on your PC and tell me if anything breaks or is unclear. That tells me if these instructions are good enough.

---

## What's NOT done yet
- **Login and passwords.** The sample users have the text `PLACEHOLDER` in the `password_hash` column, which is not a real password. When we build login, we'll store hashed passwords, never plain text.
- **No `UserDAO` yet** (for finding a user by email). Tell me if you need one.
- **Not connected to the service layer or GUI yet.** That's us working together next.
- **Status update and history are two separate steps** (not one all-or-nothing transaction). I check things first to make it safe, but it isn't perfect.
- I tested on JDK 25 (compiled with `--release 21`), not on a real JDK 21.

---

## Common problems

| Error message | What it means |
|---|---|
| `DB_PASSWORD environment variable is not set` | You haven't done step 4, or you didn't restart VS Code after `setx`. |
| `Access denied for user 'root'` | Wrong password in `DB_PASSWORD`. Run `setx` again and restart VS Code. |
| `Communications link failure` | MySQL isn't running. Open Windows "Services" and start MySQL. |
| `Unknown database 'college_maintenance'` | You haven't run `schema.sql` yet (step 2). |
| `cannot find symbol` when compiling | A file is missing or unsaved. Compile all of `src\*.java`. |

**The test cases are not removed from the tables in the database**

Ask me anything! If something is confusing, that's a problem with my explanation, not with you. 🙂
