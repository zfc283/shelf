## Dependencies
* Java SE 21
* Redis 7
* MySQL 8.0
* NodeJS 20.9.0
* IntelliJ IDEA 2023.2.4
* Additional dependencies are listed in `pom.xml` and `package.json`

&nbsp;

## Setup
To get started, follow the following steps:
* Install project dependencies (Java SE 21, Redis7, MySQL8.0, NodeJS 20.9.0) on your local machine
* Clone the repository to the local environment using: `git clone <repository-url>`
* Open the project in IntelliJ IDEA; Maven dependencies should begin downloading automatically 
* Create a new database and use the database cloning script in `doc/all.sql` to initialize and populate the dabase with data records
* Configure your MySQL settings in `application.yml` located under the `resources/mapper` folder
* This project features rich text editing functionality. Images are saved to a local directory and can be accessed via `http://localhost:8080/path_to_local_directory`. Choose a directory on your local computer and update the `application-dev.yml` file accordingly.
* Navigate to the `web` folder of the project and run `npm install` to download the frontend dependencies
* Run Redis 7 and MySQL80
* In IntelliJ IDEA, navigate to `src/main/java/com.project.shelf/config/ShelfApplication` and run `ShelfApplication`. After the backend application has started, navigate to the `web` folder. Right-click on `package.json` and select `Show npm scripts`. In the popup window, select `serve-dev`. The frontend app should now be running, and the fully operational app can be accessed at `http://localhost:8081`. 
