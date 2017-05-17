# Testing HyGene
Current project status:
[![Build Status](https://travis-ci.com/nielsdebruin/dna.svg?token=MPR2aq1yzRi2MdAzgtdk&branch=master)](https://travis-ci.com/nielsdebruin/dna)
[![CodeCov](https://codecov.io/gh/nielsdebruin/dna/branch/master/graph/badge.svg?token=iCcqwI3I98)](https://codecov.io/gh/nielsdebruin/dna)

## Abstract
This document describes special test cases and the test progress. The special test cases are usually cases that could not be well tested due to various reasons. We explain them to show that they were not 'forgotten', but untested for a reason. Also, we give more insight in our testing process in the chapter about testing progress. We gave a per sprint overview of the testing related activities and possible difficulties that we encountered during that sprint.

## Special Components

### JavaFX UI
For our UI we have set up end-to-end/acceptance tests that verify the working of the functionality in our UI. There are some exception when the testing tool, `TestFX`, cannot control the UI, for instance opening a file through the file opener. Tests for these parts of the application are handled differently.


## Testing Tools
Automated testing is considered vital to writing a successful application by our team. We are using modern test tools such as JUnit 5, AssertJ and Mockito to write intuitive tests. Whenever possible, we try to write tests at multiple levels (unit, integration, end-to-end, acceptance). To this end we are using TestFX to test our UI on a mix between end-to-end and acceptance level.

We are using Jacoco in our Gradle builds to generate code coverage reports. These reports include line coverage, which will be used for grading purposes. However, internally we like to use Codecov's metric, which takes both line and branch coverage into account.

## Coverage Metric
The [Codecov metric]((http://docs.codecov.io/docs/frequently-asked-questions)) is defined as follows: `round((hits / (hits + partials + misses)) * 100, 5)`. This based on both line and branch coverage.

## Progress
In this chapter we will the describe our testing progress throughout the sprints. The test coverage will be expressed in the percentage of lines that are covered by tests.

### Sprint 1
In the first sprint we started testing immediately from the start of the project. In fact, JUnit 5 and AssertJ were one of the first tools we set up. Without GraphStream we reached a total amount of line coverage of 87% (CodeCov 78%). Since GraphStream is only present temporarily to show a first prototype, and will be taken out of our code base, we have not paid too much attention to testing our GraphStream integration (it will be taken out of the code base before the end of the next sprint).  Therefore, with GraphStream included we reached a total amount of line coverage of 76% (CodeCov 70%).

## Sprint 2
As mentioned in the section about 'Sprint 1', during this sprint we have removed our GraphStream integration and replaced this with drawing primitives ourselves. Drawing primitives cannot be 'well tested' in the sense that we cannot test if something is actually displayed. The other features such as the new data structure and recent files are well tested.
We reached a total amount of line coverage of 83% (CodeCov 80%).
