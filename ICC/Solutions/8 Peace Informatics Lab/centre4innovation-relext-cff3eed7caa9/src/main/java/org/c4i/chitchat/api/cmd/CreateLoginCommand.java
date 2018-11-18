package org.c4i.chitchat.api.cmd;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.c4i.util.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create entry for settings.yml auth section.
 * @author Arvid Halma
 * @version 1-10-2017 - 10:11
 */
public class CreateLoginCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(CreateLoginCommand.class);

    public CreateLoginCommand() {
        super("createlogin", "Create entry for settings.yml auth section.");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-u", "--user-name")
                .dest("user-name")
                .type(String.class)
                .required(true)
                .help("The new user name");
        subparser.addArgument("-p", "--password")
                .dest("password")
                .type(String.class)
                .required(true)
                .help("The new password");
    }


    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        String user = namespace.getString("user-name");
        String pass = namespace.getString("password");
        String salt = Hash.randomString256bit();
        System.out.println("- user:");
        System.out.println("    name: " + user);
        System.out.println("    roles: [BASIC_GUY]" );
        System.out.println("  salt: " + salt);
        System.out.println("  hashedPassword: " + Hash.sha256Hex(salt + pass));
    }
}