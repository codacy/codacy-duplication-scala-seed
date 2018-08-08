# Codacy Duplication Scala Seed

[![Codacy Badge](https://api.codacy.com/project/badge/grade/bc3a79d1b12649158a1eb4758e872141)](https://www.codacy.com/app/Codacy/codacy-duplication-scala-seed)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/b85f7d351cd44a96ad95dbbff1305ccc)](https://www.codacy.com/app/Codacy/codacy-duplication-scala-seed?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-duplication-scala-seed&utm_campaign=Badge_Coverage)
[![Build Status](https://circleci.com/gh/codacy/codacy-duplication-scala-seed.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-duplication-scala-seed)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-duplication-scala-seed_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-duplication-scala-seed_2.12)

We use external tools at Codacy, this is the library we use across the multiple external tools integrations.
For more details and examples of tools that use this project, you can check
`TODO`.

## Usage

Add to your SBT dependencies:

```scala
"com.codacy" %% "codacy-duplication-scala-seed" % "<VERSION>"
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
[//]: # (TODO: review these types)
You are free to write this code in the language you want.
After you have your results from the tool, you should print them to the standard output in our **DuplicationResult** format, one result per line.
You should be careful to always set a $type attribute in two situations: 
* In the root of the DuplicationResult, indicating whether the result should be parsed as a Clone or a Problem. The allowed values for this field are:
    * For a clone use ``com.codacy.plugins.api.docker.v2.DuplicationResult$Clone``
    * For an error/warning use ``com.codacy.plugins.api.docker.v2.DuplicationResult$Problem``
* In the root of the reason for an error/warning (you can find a detailed description of the different types of reason after the example for this case below)

In the case you want to output an instance of duplication you should comply to the following format:
```json
{
  "$type": "com.codacy.plugins.api.docker.v2.DuplicationResult$Clone",
  "cloneLines": "case class Foo(bar: Int)",
  "nrTokens": 2,
  "nrLines": 1,
  "files": [
    { "filePath": "path/to/my/file1.scala", "startLine": 1, "endLine": 2 },
    { "filePath": "path/to/my/file2.scala", "startLine": 5, "endLine": 6 }
  ]
}
```

In the case you want to output an error/warning with the execution, you should format your response according to:
```json
{
  "$type": "com.codacy.plugins.api.docker.v2.DuplicationResult$Problem",
  "file": "path/to/my/file1.scala",
  "reason": {
    "$type": "com.codacy.plugins.api.docker.v2.Problem$Reason$FileError"
  }
}
```

For the latter situation, the expected reasons for an error/warning outputed by the tool should comply to one of the 
* Missing Configuration - when the configuration file for the tool cannot be found (if it is needed by the tool):
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$MissingConfiguration``
    * ``supportedFilename`` - an array of strings containing the file names that are supported for the configuration file
* Invalid Configuration - when the configuration file for the tool does not comply to the expected format 
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$InvalidConfiguration``
    * ``unsupportedPatterns`` - an array of strings that encode patterns that were selected in the configuration but are not supported
    * ``unsupportedValues`` - an array of objects that encodes invalid parameter values. These should use the following fields:
        * ``patternId`` - the identifier of a pattern, encoded as a string
        * ``parameterName`` - the name of the parameter that was set with an invalid value, encoded as a string
        * ``badValue`` - the value used in the configuration (invalid value), encoded as a string
        * ``supportedValues`` - an array of strings, the values that are supported for this parameter
* Missing Options - when a subset of the options for the tool were not provided
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$MissingOptions``
    * ``missingKeys`` - an array of strings encoding option keys that are required and were not set
* Invalid Options - when a subset of the options for the tool are not allowed or the values for some allowed options are invalid
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$InvalidOptions``
    * ``options`` - an array of objects that encode invalid option values. These should use the following fields:
        * ``optionName`` - the name of the option that was set with an invalid value, encoded as a string
        * ``badValue`` - the invalid value that was set for the option
        * ``supportedValues`` - an array of strings, the values that are supported for the option
* File Error - when a file cannot be found, opened or analysed
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$FileError``
* Missing Artifacts - when the compilation artifacts cannot be found (only for tools that need these)
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$MissingArtifacts``
    * ``supported`` - an array of strings containing the supported locations for the artifacts
* Invalid Artifacts - when the compilation artifacts are invalid
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$InvalidArtifacts``
    * ``paths`` - an array of strings containing paths to invalid artifacts
* Timed Out - when the tool execution failed due to a timeout
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$TimedOut``
    * ``timeout`` - an object encoding the timeout value. This object should support the following fields:
       * ``length`` - an integer encoded value for the length of the timeout
       * ``unit`` - a string encoded value for the unit of time of the timeout. Must be one of "NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS"
* Other Reason - to encode other reasons that caused some error/warning
    * ``$type`` - ``com.codacy.plugins.api.docker.v2.Problem$Reason$OtherReason``
    * ``message`` - a string encoded, human-readable description of the problem
    * ``output`` - optional property, intended to encode a generic error output
    * ``stacktrace`` - optional property, intended to encode a stacktrace 


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
