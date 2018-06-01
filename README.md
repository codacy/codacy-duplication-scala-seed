# Codacy Duplication Scala Seed

[![Codacy Badge](https://api.codacy.com/project/badge/grade/bc3a79d1b12649158a1eb4758e872141)](https://www.codacy.com/app/Codacy/codacy-duplication-scala-seed)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/b85f7d351cd44a96ad95dbbff1305ccc)](https://www.codacy.com/app/Codacy/codacy-duplication-scala-seed?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-duplication-scala-seed&utm_campaign=Badge_Coverage)
[![Build Status](https://circleci.com/gh/codacy/codacy-duplication-scala-seed.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-duplication-scala-seed)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-duplication-scala-seed_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-duplication-scala-seed_2.11)

We use external tools at Codacy, this is the library we use across the multiple external tools integrations.
For more details and examples of tools that use this project, you can check
`TODO`.

## Usage

Add to your SBT dependencies:

```scala
"com.codacy" %% "codacy-duplication-scala-seed" % "1.0.0-SNAPSHOT"
```

You shouldn't worry about the library itself, we use it as a core in our tools,
and everything is well explained in our Docs section.

## Docs

### How to integrate an external duplication tool on Codacy

By creating a docker and writing code to handle the tool invocation and output,
you can integrate the tool of your choice on Codacy!

> To know more about dockers, and how to write a docker file please refer to [https://docs.docker.com/reference/builder/](https://docs.docker.com/reference/builder/)

We use external tools at Codacy, in this tutorial, we explain how you can integrate the duplication tool of your choice in Codacy.
You can check the code of an already implemented tool and if you wish fork it to start yours.
You are free to modify it and use it for your integration.

## Requirements

* Docker definition with the tool you want to integrate

## Assumptions and Behaviour

* To run the tool we provide the configuration file, `/src/.codacyrc`, with the language to run and optional parameters your tool might need.
* The files to analyse are located in `/src`, meaning that when provided in the configuration, the paths are relative to `/src.

* **.codacyrc**
  * **duplication:**
    * **language:** Language to run the tool
    * **params:** Object with key/value parameters

```json
{
  "duplication": {
    "language": "Scala",
    "params": {
      "maxTokens": 5
    }
  }
}
```

## Output

You are free to write this code in the language you want.
After you have your results from the tool, you should print them to the standard output in our **Result** format, one result per line.

```json
{
  "cloneLines": "case class Foo(bar: Int)",
  "nrTokens": 2,
  "nrLines": 1,
  "files": [
    { "filePath": "path/to/my/file1.scala", "startLine": 1, "endLine": 2 },
    { "filePath": "path/to/my/file2.scala", "startLine": 5, "endLine": 6 }
  ]
}
```

> The filename should not include the prefix `/src/`
> Example:
> * absolute path: `/src/folder/file.js`
> * filename path: `folder/file.js`

## Submit the Docker

### Running the docker

```sh
docker run -t \
    --net=none \
    --privileged=false \
    --cap-drop=ALL \
    --user=docker \
    --rm=true \
    -v <PATH-TO-FOLDER-WITH-FILES-TO-CHECK>:/src \
    -v <PATH-TO-CODACY-CONFIG-RC>:/src/.codacyrc \
    <YOUR-DOCKER-NAME>:<YOUR-DOCKER-VERSION>
```

### Docker restrictions

* Docker image size should not exceed 500MB
* Docker should contain a non-root user named docker with UID/GID 2004
* All the source code of the docker must be public
* The docker base must officially supported on DockerHub
* Your docker must be provided in a repository through a public git host (ex: GitHub, Bitbucket, ...)

### Docker submission

* To submit the docker you should send an email to `team [at] codacy [dot] com` with the link to the git repository with your docker definition.
* The docker will then be subjected to a review by our team and you will then be contacted with more details

If you have any question or suggestion regarding this guide let us know.

## Test

> TODO

## What is Codacy

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features

* Identify new Static Analysis issues
* Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
* Auto-comments on Commits and Pull Requests
* Integrations with Slack, HipChat, Jira, YouTrack
* Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
