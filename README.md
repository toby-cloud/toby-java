[![Build Status](https://travis-ci.org/toby-cloud/toby-java.svg?branch=master)](https://travis-ci.org/toby-cloud/toby-java)

# Installing

toby-java uses Gradle. The jars *are not* yet available from a public repo, but will be soon.

To compile it yourself:

```bash
git clone https://github.com/toby-cloud/toby-node
cd toby-node
# see usage below
```

## Install gradle

`curl -s https://get.sdkman.io | bash`

Open new terminal

`sdk install gradle 3.1`


## Usage

```bash
gradle tasks
gradle assemble
gradle build
java -jar build/libs/toby-java-0.1.jar
```
Unit tests are run automatically with every build. You can also run the tests
with the command `gradle check`.


## Dependencies

- [fusesource/mqtt-client](https://github.com/fusesource/mqtt-client)
- [google/gson](https://github.com/google/gson)
