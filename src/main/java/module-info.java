module com.hubick.utility_servlets {
  requires transitive org.eclipse.jdt.annotation;
  requires transitive java.servlet;
  requires transitive jakarta.activation;

  exports com.hubick.utility_servlets;
  exports com.hubick.utility_servlets.role;
  exports com.hubick.utility_servlets.session;
}
