# Conferece talk survey (rest app)
>Epic Link : https://jira2.cerner.com/browse/ACADEM-35864

### Table of content
- [Description](#description)
- [Technologies Used](#technologies-used)
- [How to install](#how-to-install)


## Description
As a Conference Talk volunteer, we would like a system that encourages and captures survey comments from Conference talks rapidly so that we are not manually entering in 100 cards per hour as it is very boring. We know, we are done when we have a system that will automatically enter 60-100% of the comments without sacrificing response rate.

We need a way to gather comments from participants. We've found that online surveys have a very low response rate, so we've been favoring paper cards. The paper cards are labor-intensive. We either need a solution that scans most of the comments from the paper cards or that incentivizes participants to enter them digitally.

## Technologies Used
- Backend : Springboot
- Frontend : React
- Database : Postgresql

## How to install
#### Installation
1.Clone the repository to your machine by running the command git clone (clone link) using cmd/git bash/terminal.

2.Install [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

3.Set your JAVA_HOME and JRE_HOME environement variable set up based on your Operaing System environment variable setup.

4.Install PostgreSQL(https://www.postgresql.org/download/)

	a.Make sure to install pgAdmin (checked default) which helps manage database connection.
	b. 
4.The command should give you a version for both.

5.Navigate to the web app directory from the Root directory of the project.

6.Run the following command on the to download all the dependencies for the web app.
```
	npm install
```

7.Run the following command to get the web app up and running while being inside the same directory.
```
	npm start
```
