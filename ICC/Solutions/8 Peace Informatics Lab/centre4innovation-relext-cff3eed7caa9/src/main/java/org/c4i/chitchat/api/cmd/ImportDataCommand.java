package org.c4i.chitchat.api.cmd;

import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.c4i.chitchat.api.App;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.db.TextDocDao;
import org.c4i.chitchat.api.model.TextDoc;
import org.jdbi.v3.core.Jdbi;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Import data
 * @author Arvid Halma
 */
public class ImportDataCommand extends EnvironmentCommand<Config> {

    public ImportDataCommand() {
        super( new App(),"import", "Import data files");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-s", "--src")
                .dest("src")
                .required(true)
                .type(String.class)
                .help("The file or directory from which the text should be imported");
        super.configure(subparser);
    }

    @Override
    protected void run(Environment environment,
                       Namespace namespace,
                       Config config) throws Exception
    {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config.getDataSourceFactory(), "database");

        // DAOs
        final TextDocDao textDocDao = jdbi.onDemand(TextDocDao.class);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        final File file = new File(namespace.getString("src"));
        File[] ls = new File[]{file};
        if (file.isDirectory()) {
            ls = file.listFiles(File::isFile);
        }

        for (File f : ls) {
            try (InputStream stream = new FileInputStream(f)) {
                BodyContentHandler handler = new BodyContentHandler(-1);
                parser.parse(stream, handler, metadata);
                System.out.println(f);
                //System.out.println(handler.toString().substring(0, 200));

                final String body = handler.toString();

                System.out.println("body = " + body.substring(0, 20)+"...");
//                System.out.println("body = " + body);q

                textDocDao.upsert(new TextDoc()
                        .setId(f.getName())
                        .setName(f.getName())
                        .setType("ardoc")
                        .setBody(body));
            } catch (TikaException | SAXException e) {
                System.err.println(e.getMessage());
            }
        }

        System.exit(0);


    }


}
