package org.c4i.chitchat.api.cmd;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.c4i.chitchat.api.Config;

import java.io.IOException;
import java.io.InputStream;

/**
 * Kill process by port.
 * @author Arvid Halma
 */
public class KillServer extends ConfiguredCommand<Config> {

    public KillServer() {
        super("killserver", "Ends the server process by port number");
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
    }

    @Override
    protected void run(Bootstrap<Config> bootstrap,
                       Namespace namespace,
                       Config config) throws Exception
    {
        kill("5678");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        kill(args[0]);
    }

    public static void kill(String port) throws InterruptedException, IOException {
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
            Runtime.getRuntime().exec("cmd /c for /f \"tokens=5\" %a in ('netstat -aon ^| find \":" + port + "\" ^| find \"LISTENING\"') do taskkill /f /pid %a").waitFor();
        } else {
            Runtime.getRuntime().exec("kill -9 $(lsof -t -i:"+port+" -sTCP:LISTEN)").waitFor();
        }
    }


}
