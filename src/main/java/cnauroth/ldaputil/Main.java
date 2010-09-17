/*
 * Main.java
 */

package cnauroth.ldaputil;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * <p>
 * This class is the main entry point into java-ldap-util.
 * </p>
 * 
 * <p>
 * The following command-line arguments are required:
 * 
 * <ol>
 * <li>url - LDAP URL for connection</li>
 * <li>principal - security principal to use for authentication to the LDAP</li>
 * <li>password - security principal's password</li>
 * </ol>
 * 
 * </p>
 * 
 * <p>
 * Any remaining arguments are interpreted as a list of the principal's attributes to query after
 * successful authentication.  The attribute values are printed to stdout.
 * </p>
 */
public final class Main {

    /**
     * Empty list of arguments.
     */
    private static final List<String> EMPTY_ARGS = emptyList();

    /**
     * This method is the main entry point.
     * 
     * @param args String[] command-line arguments.
     * @throws Exception Thrown if any exception occurs.
     */
    public static void main(String[] args) throws Exception {
        // Parse command-line arguments.
        // TODO: Input validation.
        List<String> argsList = asList(args);
        String url = argsList.get(0);
        String principal = argsList.get(1);
        String password = argsList.get(2);
        List<String> attributeNames = (argsList.size() > 3 ? argsList.subList(3, argsList.size()) : EMPTY_ARGS);

        System.out.println("url = " + url);
        System.out.println("principal = " + principal);
        System.out.println("password = " + password);
        System.out.println("attributeNames = " + attributeNames);

        // Configure LDAP connection.
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, password);

        // Set a cusom SSL socket factory that blindly trusts any SSL certificate.
        env.put("java.naming.ldap.factory.socket", "cnauroth.ldaputil.CustomSSLSocketFactory");

        DirContext ctx = null;

        try {
            ctx = new InitialDirContext(env);

            // At this point, we have successful authentication, because no exception was thrown.
            System.out.println("Successfully authenticated.");

            // Optionally, print requested attribute values.
            if (!attributeNames.isEmpty()) {
                Attributes attributes = ctx.getAttributes(principal,
                    attributeNames.toArray(new String[attributeNames.size()]));

                for (String attributeName : attributeNames) {
                    Attribute attribute = attributes.get(attributeName);

                    if (null != attribute) {
                        NamingEnumeration<?> values = attribute.getAll();

                        if (null != values) {
                            if (values.hasMore()) {
                                System.out.println(attributeName);

                                while (values.hasMore())
                                    System.out.println("    " + values.next());
                            }
                        }
                    }
                }
            }
        }
        finally {
            // Guarantee the connection gets closed.
            if (null != ctx) {
                try {
                    ctx.close();
                }
                catch (NamingException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}

