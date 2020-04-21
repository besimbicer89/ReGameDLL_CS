package regamedll.testdemo

import org.doomedsociety.gradlecpp.GradleCppUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.FileUtils

class RegamedllDemoRunner {
    File rootDir
    Closure postExtract

    static class TestResult
    {
        boolean success
        int returnCode
        String hldsConsoleOutput
        long duration
    }

    RegamedllDemoRunner(File rootDir, Closure postExtract)
    {
        this.rootDir = rootDir
        this.postExtract = postExtract
    }

    void prepareEngine()
    {
        FileUtils.copyDirectory(new File(rootDir.toString() + '/deps/regamedll'), rootDir);

        if (postExtract != null) {
            postExtract.run()
        }
    }

    TestResult runTest(RegamedllTestInfo info, File testLogDir)
    {
        long startTime = System.currentTimeMillis()

        def outPath = new File(testLogDir, "${info.testName}_run.log")

        def cmdParams = []
        cmdParams << new File(rootDir, 'hlds.exe').absolutePath
        cmdParams.addAll(info.hldsArgs)
        if (info.regamedllExtraArgs) {
            cmdParams.addAll(info.regamedllExtraArgs)
        }
        cmdParams << '--rehlds-test-play' << info.testBinFile.absolutePath

        def pb = new ProcessBuilder(cmdParams).redirectErrorStream(true).directory(rootDir)
        def sout = new StringBuffer()

        def p = pb.start()
        p.consumeProcessOutput(sout, sout)
        p.waitForOrKill(info.timeoutSeconds * 1000)

        int exitVal = p.exitValue()

        outPath.withWriter('UTF-8') { writer ->
            writer.write(sout.toString())
        }

        long endTime = System.currentTimeMillis()

        return new TestResult(
                success: (exitVal == 777),
                returnCode: exitVal,
                hldsConsoleOutput: sout.toString(),
                duration: endTime - startTime
        )
    }
}
