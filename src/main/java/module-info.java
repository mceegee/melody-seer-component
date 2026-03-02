
// Had to create a module-info document to include java.net.http because it was conflicting with Jackson when creating Javadocs
// As I had a required class, all packages had to be included here as well :(
// https://stackoverflow.com/questions/65273632/the-package-java-sql-is-not-accessible-in-eclipse
// https://stackoverflow.com/questions/51503140/the-import-java-awt-cannot-be-resolved

module MelodySeerComponent {
    requires java.net.http;
    requires java.desktop;
    requires tools.jackson.databind;
    requires com.fasterxml.jackson.annotation;
}