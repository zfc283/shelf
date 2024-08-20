## Description
KnowledgeShelf is a full-stack application designed to enhance access to curated knowledge resources. The frontend is built with Vue 3, leveraging the Ant Design Vue component library for a modern-look and user-friendly interface. On the server side, the application runs on Spring Boot 3, ensuring a robust backend architecture. 

&nbsp;

- Project Demo: https://knowledgeshelf.site
- Test account:
  - username: admin
  - password: test123

&nbsp;

## Screenshots
Before login, users can browse on different topics and articles

&nbsp;

<img src="https://zfc283-wiki.s3.ca-central-1.amazonaws.com/github-readme-images/Screenshot+(402).png" alt="Alt text" >

&nbsp;

&nbsp;

After login, admin users can manage (add/edit/delete) categories, topics and articles

&nbsp;

<img src="https://zfc283-wiki.s3.ca-central-1.amazonaws.com/github-readme-images/Screenshot+(403).png" alt="Alt text" >

&nbsp;

&nbsp;

Note that the docs admin panel is entered from the topic admin panel

&nbsp;

<img src="https://zfc283-wiki.s3.ca-central-1.amazonaws.com/github-readme-images/Screenshot+(406).png" alt="Alt text" >

&nbsp;

&nbsp;

Docs admin panel

&nbsp;

<img src="https://zfc283-wiki.s3.ca-central-1.amazonaws.com/github-readme-images/Screenshot+(408).png" alt="Alt text" >

&nbsp;

&nbsp;

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
1. Install project dependencies (Java SE 21, Redis7, MySQL8.0, NodeJS 20.9.0) on your local machine
2. Clone the repository to the local environment using: `git clone <repository-url>`
3. Open the project in IntelliJ IDEA; Maven dependencies should begin downloading automatically 
4. Create a new database and use the database cloning script in `doc/all.sql` to initialize and populate the dabase with data records
5. Configure your MySQL settings in `application.yml` located under the `resources/mapper` folder
6. This project features rich text editing functionality. Images are saved to a local directory and can be accessed via `http://localhost:8080/path_to_local_directory`. Choose a directory on your local computer and update the `application-dev.yml` file accordingly.
7. Navigate to the `web` folder of the project and run `npm install` to download the frontend dependencies
8. Run Redis 7 and MySQL80
9. In IntelliJ IDEA, navigate to `src/main/java/com.project.shelf/config/ShelfApplication` and run `ShelfApplication`. After the backend application has started, navigate to the `web` folder. Right-click on `package.json` and select `Show npm scripts`. In the popup window, select `serve-dev`. The frontend app should now be running, and the fully operational app can be accessed at `http://localhost:8081`. 
