package org.graalvm.buildtools.maven


import java.util.regex.Pattern

class JavaApplicationWithResourcesFunctionalTest extends AbstractGraalVMMavenFunctionalTest {

    def "can build an application which uses resources"() {

        given:
        withSample("java-application-with-resources")

        List<String> options = []
        if (inference) {
            options << '-Dresources.inference.enabled=true'
        }
        if (includedPatterns) {
            options << "-Dresources.includedPatterns=${joinForCliArg(includedPatterns)}".toString()
        }
        if (!restrictToModules) {
            options << '-Dresources.inference.restrictToModuleDependencies=false'
        }
        if (inferenceExclusionPatterns) {
            options << "-Dresources.inference.inferenceExclusionPatterns=${joinForCliArg(inferenceExclusionPatterns)}".toString()
        }

        when:
        mvn(['-Pnative', '-DskipTests', *options, 'package', 'exec:exec@native'])

        then:
        buildSucceeded
        outputContains "Hello, native!"

        and:
        file("target/native/generated/generateResourceConfig/resource-config.json").text == '''{
    "resources": {
        "includes": [
            {
                "pattern": "\\\\Qmessage.txt\\\\E"
            }
        ],
        "excludes": [
            
        ]
    },
    "bundles": [
        
    ]
}'''

        where:
        inference | includedPatterns               | restrictToModules | inferenceExclusionPatterns
        false     | [Pattern.quote("message.txt")] | false             | []
        true      | []                             | false             | ["META-INF/.*"]
        true      | []                             | true              | ["META-INF/.*"]
    }

    def "can test an application which uses test resources"() {
        given:
//        withDebug()
        withSample("java-application-with-resources")

        List<String> options = []
        if (inference) {
            options << '-Dresources.inference.enabled=true'
        }
        if (includedPatterns) {
            options << "-Dresources.includedPatterns=${joinForCliArg(includedPatterns)}".toString()
        }
        if (!restrictToModules) {
            options << '-Dresources.inference.restrictToModuleDependencies=false'
        }
        if (inferenceExclusionPatterns) {
            options << "-Dresources.inference.inferenceExclusionPatterns=${joinForCliArg(inferenceExclusionPatterns)}".toString()
        }

        when:
        mvn(['-Pnative', 'test', *options])

        then:
        buildSucceeded

        and:
        file("target/native/generated/generateTestResourceConfig/resource-config.json").text == '''{
    "resources": {
        "includes": [
            {
                "pattern": "\\\\Qmessage.txt\\\\E"
            },
            {
                "pattern": "\\\\Qorg/graalvm/demo/expected.txt\\\\E"
            }
        ],
        "excludes": [
            
        ]
    },
    "bundles": [
        
    ]
}'''

        where:
        inference | includedPatterns                                                               | restrictToModules | inferenceExclusionPatterns
        false     | [Pattern.quote("message.txt"), Pattern.quote("org/graalvm/demo/expected.txt")] | false             | []
        true      | []                                                                             | false             | ["META-INF/.*", "junit-platform-unique-ids.*"]
        true      | []                                                                             | true              | ["META-INF/.*", "junit-platform-unique-ids.*"]
    }

    private static String joinForCliArg(List<String> patterns) {
        patterns.join(",")
    }
}