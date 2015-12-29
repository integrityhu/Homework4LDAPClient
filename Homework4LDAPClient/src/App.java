import java.util.Hashtable;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.sun.jndi.ldap.LdapCtx;

public class App {
	// Java client call to 389 Directory (Fedora 20)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws NamingException {

		Hashtable env = new Hashtable(11);

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localfedora.infokristaly.hu:389");

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,
				"uid=pzoli, ou=People, dc=infokristaly, dc=hu");
		env.put(Context.SECURITY_CREDENTIALS, "myldap-1X");
		// env.put("com.sun.jndi.ldap.trace.ber", System.err);

		LdapContext ctx = new InitialLdapContext(env, null);
		ctx.setRequestControls(null);

		NamingEnumeration<?> namingEnum = ctx.search(
				"ou=Groups,dc=infokristaly,dc=hu", "(objectClass=*)",
				getSimpleSearchControls());
		printList(namingEnum);
		namingEnum.close();

		namingEnum = ctx.search("cn=developer,ou=Groups,dc=infokristaly,dc=hu",
				"(objectClass=*)", getSimpleSearchControls());
		printList(namingEnum);

		namingEnum.close();

		String searchFilter = "(objectClass=*)";
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String returnedAtts[]={"uniquemember"};
		searchControls.setReturningAttributes(returnedAtts);
		namingEnum = ctx.search("cn=developer,ou=Groups,dc=infokristaly,dc=hu", searchFilter,
				searchControls);
		printList(namingEnum);
		namingEnum.close();

		searchFilter = "(objectClass=*)";
		searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);		
		ModificationItem mods[] = new ModificationItem[1];
		mods[0]= new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("description", "developers group")); //ADD_ATTRIBUTE
		ctx.modifyAttributes("cn=Developer,ou=Groups,dc=infokristaly,dc=hu", mods);
		printList(namingEnum);
		namingEnum.close();
		
		namingEnum = ctx.search("ou=People,dc=infokristaly,dc=hu",
				"(objectClass=*)", getSimpleSearchControls());
		printList(namingEnum);
		namingEnum.close();

		LdapCtx dev = (LdapCtx) ctx
				.lookup("uid=pzoli,ou=People,dc=infokristaly,dc=hu");
		System.out.println(dev);

		ctx.close();

	}

	private static void printList(NamingEnumeration<?> namingEnum)
			throws NamingException {
		while (namingEnum.hasMore()) {
			SearchResult result = (SearchResult) namingEnum.next();
			Attributes attrs = result.getAttributes();
			Attribute attr = attrs.get("ou");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("title");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("cn");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("uid");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("mail");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("telephonenumber");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("memberof");
			if (attr != null) {
				System.out.println(attr);
			}
			attr = attrs.get("uniquemember");
			if (attr != null) {
				System.out.println(attr);
			}
		}

	}

	private static SearchControls getSimpleSearchControls() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		// String[] attrIDs = {"objectGUID"};
		// searchControls.setReturningAttributes(attrIDs);
		return searchControls;
	}
}
