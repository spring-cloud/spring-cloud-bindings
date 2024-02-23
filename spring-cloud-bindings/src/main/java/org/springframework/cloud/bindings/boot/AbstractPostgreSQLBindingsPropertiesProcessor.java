package org.springframework.cloud.bindings.boot;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.bindings.Binding;

public abstract class AbstractPostgreSQLBindingsPropertiesProcessor implements BindingsPropertiesProcessor {
	
    /**
     * sslmode determines whether or with what priority a secure SSL TCP/IP connection will be negotiated with the server.
     */
    public static final String SSL_MODE = "sslmode";
    /**
     * sslrootcert specifies the name of a file containing SSL certificate authority (CA) certificate(s).
     */
    public static final String SSL_ROOT_CERT = "sslrootcert";
    /**
     * options Specifies command-line options to send to the server at connection start.
     * CockroachDB uses this to pass in cluster routing id
     */
    public static final String OPTIONS = "options";
    
    /**
     * Returns a concatenated list of options parameters defined in the bound file `options` in the format specified in
     * <a href="https://www.postgresql.org/docs/14/libpq-connect.html">PostgreSQL Doc</a>.
     * <p>
     * CockroachDB, which shares the same 'postgresql://' protocol as PostgreSQL, has customized options to meet its
     * distributed database nature.
     * Refer to <a href="https://www.cockroachlabs.com/docs/v21.2/connection-parameters#additional-connection-parameters">Client Connection Parameters</a>.
     */
    protected String buildDbOptions(Binding binding) {
        String options = binding.getSecret().getOrDefault(OPTIONS, "");
        String crdbOption = "";
        List<String> dbOptions = new ArrayList<>();
        if (!options.equals("")) {
            String[] allOpts = options.split("&");
            for (String o : allOpts) {
                String[] keyval = o.split("=");
                if (keyval.length != 2 || keyval[0].length() == 0 || keyval[1].length() == 0) {
                    continue;
                }
                if (keyval[0].equals("--cluster")) {
                    crdbOption = keyval[0] + "=" + keyval[1];
                } else {
                    dbOptions.add("-c " + keyval[0] + "=" + keyval[1]);
                }
            }
        }
        String combinedOptions = crdbOption;
        if (dbOptions.size() > 0) {
            String otherOpts = String.join(" ", dbOptions);
            if (!combinedOptions.equals("")) {
                combinedOptions = combinedOptions + " " + otherOpts;
            } else {
                combinedOptions = otherOpts;
            }
        }
        if (!"".equals(combinedOptions)) {
            combinedOptions = "options=" + combinedOptions;
        }
        return combinedOptions;
    }

    /**
     * Returns a concatenated string of all ssl parameters for enabling one-way TLS (PostgreSQL certifies itself)
     * Refer to <a href="https://www.postgresql.org/docs/14/libpq-connect.html">PostgreSQL Doc</a>
     */
    protected String buildSslModeParam(Binding binding) {
        //process ssl params
        //https://www.postgresql.org/docs/14/libpq-connect.html
        String sslmode = binding.getSecret().getOrDefault(SSL_MODE, "");
        String sslRootCert = binding.getSecret().getOrDefault(SSL_ROOT_CERT, "");
        StringBuilder sslparam = new StringBuilder();
        if (!"".equals(sslmode)) {
            sslparam.append(SSL_MODE).append("=").append(sslmode);
        }
        if (!"".equals(sslRootCert)) {
            if (!"".equals(sslmode)) {
                sslparam.append("&");
            }
            sslparam.append(SSL_ROOT_CERT).append("=")
                    .append(binding.getPath()).append(FileSystems.getDefault().getSeparator())
                    .append(sslRootCert);
        }
        return sslparam.toString();
    }
}
