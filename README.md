# OOP Restaurant Management System (OOP\_RMS)

A Java-based desktop application for managing restaurant operations, built as an Object-Oriented Programming school project.

---

## Project Description

OOP\_RMS is a Restaurant Management System that allows staff to manage the restaurant menu and generate reports. It demonstrates core OOP principles such as **encapsulation**, **inheritance**, and **polymorphism**, combined with a JavaFX graphical user interface and a SQLite database for persistent data storage.

---

## Features

-  **Add** new menu items with name and price  
-  **Update** existing menu items  
-  **Delete** menu items with confirmation dialog  
-  **View** all menu items in a table  
-  **Menu Report** — generates a PDF list of all menu items  
-  **Orders Report** — generates a PDF summary of all orders  
-  **Billing Report** — generates a customer receipt PDF with grand total

---

##  Technologies Used

| Technology | Purpose |
| :---- | :---- |
| Java | Core programming language |
| JavaFX | Graphical User Interface (GUI) |
| SQLite | Local database for storing menu and orders |
| JasperReports 6.21.0 | PDF report generation |
| OpenPDF | PDF rendering library |
| Eclipse IDE | Development environment |

---

##  How to Run

### Requirements

- Java JDK 11 or higher  
- Eclipse IDE  
- JavaFX SDK  
- JasperReports JAR files (see below)

### Required JAR Files

Add these to your Eclipse **Classpath** (not Modulepath):

- `jasperreports-6.21.0.jar`  
- `openpdf-x.x.x.jar`  
- `commons-collections4-x.x.x.jar`  
- `commons-logging-x.x.x.jar`  
- `commons-digester-x.x.x.jar`  
- `commons-beanutils-x.x.x.jar`  
- `jasperreports-fonts-x.x.x.jar`  
- `sqlite-jdbc-x.x.x.jar`

### Steps

1. Clone the repository:  
     
   git clone https://github.com/YOUR\_USERNAME/OOP\_RMS.git  
     
2. Open Eclipse and import the project: `File` → `Import` → `Existing Projects into Workspace`  
3. Add all required JAR files to the **Classpath**  
4. Add JavaFX to the **Modulepath** and VM arguments  
5. Run `Main.java` as a Java Application

### Output

Generated PDF reports are saved to:

reports/output/

---

##  Project Structure

OOP\_RMS/

├── src/

│   └── com/OOPRms/

│       ├── Main.java              \# JavaFX UI entry point

│       ├── DatabaseConnection.java \# SQLite connection

│       ├── AddMenu.java           \# Add menu item

│       ├── UpdateMenu.java        \# Update menu item

│       ├── DeleteMenu.java        \# Delete menu item

│       ├── ViewMenu.java          \# Fetch menu items

│       └── ReportGenerator.java   \# PDF report generation

├── reports/

│   ├── menu\_report.jrxml          \# Menu report template

│   ├── orders\_report.jrxml        \# Orders report template

│   ├── billing\_report.jrxml       \# Billing report template

│   └── output/                    \# Generated PDFs saved here

└── README.md

---

##  Author

**Roland Renz D. Acido** OOP School Project PUPT — 2026

**Collaborators:**   
**Lhuise Gahbrielle Valila**  
**JM Bigtas**  
**Jed Libay**  
