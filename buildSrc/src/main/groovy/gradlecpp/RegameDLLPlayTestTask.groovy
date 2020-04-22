package gradlecpp

import org.apache.commons.lang.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.nativeplatform.NativeBinarySpec
import regamedll.testdemo.RegamedllDemoRunner
import regamedll.testdemo.RegamedllTestParser

class RegamedllPlayTestTask extends DefaultTask {

    def Set<String> testDemos
    def Closure postExtractAction
    def File regamedllImageRoot
    def File regamedllTestLogs

    @TaskAction
    def doPlay() {
        if (!SystemUtils.IS_OS_WINDOWS) {
            return
        }

        if (!testDemos) {
            println 'RegamedllPlayTestTask: no demos attached to the testDemos property'
        }

        regamedllImageRoot.mkdirs()
        regamedllTestLogs.mkdirs()

        def demoRunner = new RegamedllDemoRunner(regamedllImageRoot, postExtractAction)

        println "Preparing engine..."
        demoRunner.prepareEngine()

        println "Running ${testDemos.size()} ReGameDLL_CS test demos..."

        int failCount = 0;
        testDemos.each { s ->

            def testInfo = RegamedllTestParser.parseTestInfo(regamedllImageRoot.absolutePath + '/testdemos/', s)

            println "Running ReGameDLL_CS test demo ${testInfo.testName} "

            def testRes = demoRunner.runTest(testInfo, regamedllTestLogs)
            if (testRes.success) {
                println ' OK'
            } else {
                println ' Failed'
                println "ReGameDLL_CS testdemo ${testInfo.testName} playback failed. Exit status is ${testRes.returnCode}."
                println "Dumping console output:"
                println testRes.hldsConsoleOutput

                failCount++
            }
        }

        if (failCount) {
            throw new RuntimeException("ReGameDLL_CS testdemos: failed ${failCount} tests")
        }
    }
}
