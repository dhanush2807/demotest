# Selenium CI/CD Starter (Java + TestNG + Maven)

A small but realistic test framework you can push to GitHub and run in CI on day one.
Tests run against the public demo site <https://the-internet.herokuapp.com>.

## What's inside

```
selenium-cicd-starter/
├── pom.xml                         Maven build: Selenium 4, TestNG, Surefire
├── testng.xml                      Full regression suite (parallel, 3 threads)
├── smoke.xml                       Quick suite: only tests in group "smoke"
├── Jenkinsfile                     Jenkins declarative pipeline
├── docker-compose.yml              Local Selenium Grid (hub + Chrome + Firefox)
├── .github/workflows/selenium.yml  GitHub Actions pipeline
└── src/test/
    ├── resources/config.properties Default settings (browser, URL, timeouts...)
    └── java/com/example/
        ├── base/       DriverFactory (ThreadLocal driver), BaseTest
        ├── pages/      Page Object Model: Login, Checkbox, Dropdown
        ├── tests/      The actual tests
        ├── listeners/  Screenshot on failure
        └── utils/      Config reader (system prop > env var > properties file)
```

## Prerequisites

- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -v`)
- Chrome (Firefox / Edge optional)
- Git and a GitHub account

No driver downloads needed: Selenium Manager fetches the right driver automatically.

## Run locally

```bash
mvn clean test                          # all tests, Chrome, visible browser
mvn clean test -Dheadless=true          # no browser window (how CI runs)
mvn clean test -Dsuite=smoke.xml        # smoke tests only
mvn clean test -Dbrowser=firefox        # another browser
```

Results: `target/surefire-reports/index.html` (open in a browser).
Failure screenshots: `target/screenshots/`.

Every setting in `config.properties` can be overridden with `-Dkey=value` or an
environment variable with the key in upper case (e.g. `PASSWORD`).

## Run on GitHub Actions

1. Create an empty repo on GitHub.
2. Push this folder:
   ```bash
   git init
   git add .
   git commit -m "Selenium CI/CD starter"
   git branch -M main
   git remote add origin https://github.com/<your-username>/selenium-cicd-starter.git
   git push -u origin main
   ```
3. Open the **Actions** tab. The `smoke` job starts automatically.
4. Download reports and screenshots from the run's **Artifacts** section.
5. To run full regression (Chrome + Firefox) or the Grid job: **Actions > Selenium Tests > Run workflow**.

What triggers what:

| Event                     | Job                                    |
|---------------------------|----------------------------------------|
| push / pull request       | `smoke` (fast check)                   |
| nightly 2:00 AM IST       | `regression` on Chrome and Firefox     |
| manual "Run workflow"     | `regression` + `grid` (Docker Grid)    |

## Run on Jenkins

1. Run Jenkins locally with Docker:
   ```bash
   docker run -d -p 8080:8080 -v jenkins_home:/var/jenkins_home --name jenkins jenkins/jenkins:lts-jdk17
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
   Open <http://localhost:8080>, paste the password, install suggested plugins.
2. **Manage Jenkins > Tools**: add a Maven install named `Maven3` and a JDK named `JDK17`.
3. **New Item > Pipeline**. Under Pipeline choose **Pipeline script from SCM**, Git, your repo URL, script path `Jenkinsfile`.
4. **Build with Parameters**. Pick browser and suite.

Note: the Jenkins container has no browser. Either install Chrome on the agent,
or start the Grid (below) and add `-DgridUrl=http://<host-ip>:4444` to the `mvn` command.

## Run on Selenium Grid (Docker)

```bash
docker compose up -d                                   # start hub + nodes
mvn clean test -DgridUrl=http://localhost:4444         # run on Grid (Chrome)
mvn clean test -DgridUrl=http://localhost:4444 -Dbrowser=firefox
docker compose down                                    # stop
```

Watch tests live: <http://localhost:4444> (click the camera icon, password `secret`).

---

## 6-week learning plan (about 1 hour a day)

### Week 1: Understand this framework
- Run every command in "Run locally".
- Read `DriverFactory`, `BaseTest`, `LoginPage`, `LoginTest`. Explain to yourself why each exists.
- Learn: Maven lifecycle (`clean`, `compile`, `test`), TestNG annotations, groups, DataProvider.
- **Task:** add a test for <https://the-internet.herokuapp.com/add_remove_elements/> with a new page object.

### Week 2: Git and GitHub
- Learn: `clone`, `branch`, `commit`, `push`, pull requests, merge conflicts.
- Push this project to GitHub.
- **Task:** make a branch, add a test, open a pull request, and merge it.

### Week 3: GitHub Actions
- Read `.github/workflows/selenium.yml` line by line.
- Learn: triggers (`on`), jobs, steps, `runs-on`, matrix, artifacts, secrets, cron syntax.
- **Task:** break a test on purpose. Watch the pipeline fail, download the screenshot, fix it, and watch it pass.
- **Task:** add an `edge` entry to the matrix.

### Week 4: Jenkins
- Install Jenkins with Docker (see above). Create the pipeline job.
- Learn: declarative syntax, stages, `post`, parameters, triggers, credentials, JUnit plugin.
- **Task:** turn on `pollSCM` so Jenkins builds on every push. Then try a GitHub webhook instead.

### Week 5: Docker and Selenium Grid
- Learn: images, containers, `docker compose`, ports, volumes.
- Run tests on the local Grid. Watch them in the noVNC viewer.
- **Task:** raise `thread-count` in `testng.xml` and `SE_NODE_MAX_SESSIONS`. Compare run time.

### Week 6: Reporting and polish
- Add Allure or ExtentReports and publish the report as a CI artifact.
- Add a Slack or email notification on failure.
- Add retry for flaky tests (TestNG `IRetryAnalyzer`).
- **Task:** write a short README section on your design choices. Put the repo link on your resume.

## Interview questions to practise

1. What is CI/CD, and where does test automation fit?
2. Why run tests headless in CI?
3. How do you run tests in parallel safely? (Hint: `ThreadLocal<WebDriver>`)
4. How do you pass browser or environment to a pipeline? (Hint: `-D` properties, parameters)
5. How do you keep passwords out of code?
6. Tests pass locally but fail in CI. How do you debug that?
7. What is Selenium Grid, and when do you need it?
8. Difference between a Jenkins freestyle job and a pipeline?

## Common problems

| Problem | Fix |
|---|---|
| `SessionNotCreatedException` in CI | Add `--no-sandbox --disable-dev-shm-usage` (already done in `DriverFactory`) |
| Passes locally, fails in CI | Use explicit waits, set a window size, check the failure screenshot |
| Chrome crashes in Docker | Increase `shm_size` to `2gb` |
| Tests interfere in parallel | Never share a driver. Use `DriverFactory.getDriver()` |
