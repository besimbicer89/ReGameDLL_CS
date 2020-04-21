package regamedll.testdemo

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.IOUtils

class RegamedllTestParser {
    static RegamedllTestInfo parseTestInfo(String testRootName, String testName) {
        File manifestFile = new File(testRootName + testName + '.xml')
        if (!manifestFile.exists()) {
            throw new RuntimeException("Unable to open "+testName+".xml in "+testRootName)
        }

        GPathResult metaInfo = new XmlSlurper().parse(manifestFile)
        RegamedllTestInfo testInfo = new RegamedllTestInfo(
            testName: metaInfo.name.text(),
            hldsArgs: metaInfo.runArgs.arg.list().collect { it.text().trim() },
            timeoutSeconds: metaInfo.timeout.text() as int
        )

        testInfo.testBinFile = new File(testRootName + testName + '.bin')

        // validate testInfo
        if (!testInfo.testName) {
            throw new RuntimeException("Error parsing ${testInfo.testBinFile.absolutePath}: test name is not specified")
        }

        if (!testInfo.hldsArgs) {
            throw new RuntimeException("Error parsing ${testInfo.testBinFile.absolutePath}: run arguments are not specified")
        }

        if (testInfo.timeoutSeconds <= 0) {
            throw new RuntimeException("Error parsing ${testInfo.testBinFile.absolutePath}: bad timeout")
        }

        return testInfo
    }
}
