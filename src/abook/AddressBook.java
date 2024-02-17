package abook;

import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class AddressBook {

        // Create an (empty RDF) model
        private final Model model;
        // Add a Dataset as an instance variable
        private final Dataset dataset;

        public AddressBook() {
                this.model = ModelFactory.createDefaultModel();
                this.dataset = DatasetFactory.create();
                setFakeData();
        }

        // Add the new Vcard to the model
        public void newContact(String URI, String firstName, String familyName, String address,
                        String country, String nickname) {
                try {
                        createVcardResource(URI, firstName, familyName, address, country, nickname);
                        // displayAddressBook();
                } catch (Exception e) {
                        // Handle exceptions appropriately
                        JOptionPane.showMessageDialog(null, "Error creating contact: " + e.getMessage());
                }
        }

        public Resource createVcardResource(String URI, String firstName, String familyName, String address,
                        String country, String nickname) {
                // Check if a contact with the given details already exists in the model
                ResIterator existingContacts = model.listSubjectsWithProperty(VCARD.FN, firstName + " " + familyName);
                if (existingContacts.hasNext()) {
                        // Return the existing contact
                        return existingContacts.nextResource();
                }

                // Create the resource and add properties
                Resource card = model.createResource(URI)
                                .addProperty(VCARD.FN, firstName + " " + familyName)
                                .addProperty(VCARD.N,
                                                model.createResource()
                                                                .addProperty(VCARD.Given, firstName)
                                                                .addProperty(VCARD.Family, familyName)
                                                                .addProperty(VCARD.ADR, address)
                                                                .addProperty(VCARD.Country, country)
                                                                .addProperty(VCARD.NICKNAME, nickname));
                // Add the card to the dataset
                dataset.getDefaultModel().add(card.getModel());
                return card;
        }

        public void setFakeData() {
                // Definitions for my resource
                String myURI = "https://paulinebrisset.tech/";
                String myFirstName = "Pauline";
                String myFamilyName = "Moncoiffé-Brisset";
                String myAddress = "1 rue Raymond Barre";
                String myCountry = "France";
                String myNickname = "Moncoifsonne";

                // Add a description of my Vcard
                Resource myCard = createVcardResource(
                                myURI, myFirstName, myFamilyName, myAddress, myCountry, myNickname);

                // Add the card to the dataset
                dataset.getDefaultModel().add(myCard.getModel());

                // Add some friends into a Dataset
                Resource camilleCard = createVcardResource(
                                "https://something/camilleJavel", "Camille", "Javel", "CROUS", "France", "Marcel");
                // Add the card to the dataset
                dataset.getDefaultModel().add(camilleCard.getModel());

                Resource alexandreCard = createVcardResource(
                                "https://something/alexTamhui", "Alexandre", "Tamhui", "CROUS", "France", "Alex");
                // Add the card to the dataset
                dataset.getDefaultModel().add(alexandreCard.getModel());
        }

        public void displayAddressBook() {
                System.out.println("");
                System.out.println("Address Book");
                System.out.println("");
                model.write(System.out, "TURTLE");
        }

        public String getModelAsString() {
                StringWriter stringWriter = new StringWriter();
                int contactCounter = 0;

                // Loop through all the resources from the model
                ResIterator resIterator = model.listSubjects();
                while (resIterator.hasNext()) {
                        Resource resource = resIterator.nextResource();
                        contactCounter++; // Increment the contact counter for each resource
                        stringWriter.write(System.lineSeparator());
                        stringWriter.write("Contact n°" + contactCounter + ": ");
                        stringWriter.write(System.lineSeparator());

                        // Check for vCard properties on the resource
                        StmtIterator iterator = resource.listProperties();
                        while (iterator.hasNext()) {
                                // if iterator.listProperties() has something, then loop throught the properties

                                Statement stmt = iterator.nextStatement();
                                System.out.println(stmt.getPredicate().getLocalName());
                                stringWriter.write(stmt.getPredicate().getLocalName() + ": ");
                                // Check if the object of the statement is a literal or a resource
                                if (stmt.getObject().isLiteral()) {
                                        stringWriter.write(stmt.getLiteral().getLexicalForm());
                                } else {
                                        stringWriter.write(stmt.getObject().toString());
                                }
                                stringWriter.write("\n ");
                        }
                }
                // Return the string
                return stringWriter.toString();
        }

        // Method to query a SPARQL endpoint
        public String querySPARQLEndpoint(String queryString) {
                try {
                        Query query = QueryFactory.create(queryString);
                        try (QueryExecution qExec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql",
                                        query)) {
                                ResultSet rs = qExec.execSelect();
                                return ResultSetFormatter.asText(rs);
                        }
                } catch (Exception e) {
                        return "Error executing SPARQL query: " + e.getMessage();
                }
        }

        public void testSomeRequests() {

                System.out.println("");
                System.out.println("Exercice 2 : Testez quelques requêtes");
                System.out.println("");

                // 2.1 Récupérer tous les triplets
                String queryTriplets = "SELECT ?s ?p ?o WHERE {?s ?p ?o}";
                Query qTriplets = QueryFactory.create(queryTriplets);
                try (QueryExecution qeTriplets = QueryExecutionFactory.create(qTriplets, this.dataset)) {
                        ResultSet rsTriplets = qeTriplets.execSelect();
                        ResultSetFormatter.out(System.out, rsTriplets, qTriplets);
                } catch (QueryParseException e) {
                        System.err.println("Error parsing triplets query: " + e.getMessage());
                }
                // 2.2 Récupérer l'uri de ceux qui ont pour la propriété vCard:FN la valeur de
                // votre nom.
                String queryURI = "PREFIX vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>\n" +
                                "SELECT ?uri WHERE {?uri vcard:FN \"Pauline Moncoiffé-Brisset\"}";
                Query qURI = QueryFactory.create(queryURI);
                try (QueryExecution qeURI = QueryExecutionFactory.create(qURI, this.dataset)) {
                        ResultSet rsURI = qeURI.execSelect();
                        ResultSetFormatter.out(System.out, rsURI, qURI);
                } catch (QueryParseException e) {
                        System.err.println("Error parsing URI query: " + e.getMessage());
                }
                // Une requête de mon choix
                String queryString = "PREFIX vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>\n" +
                                "SELECT ?givenName ?familyName ?nickname WHERE {?person vcard:N [ vcard:Given ?givenName; vcard:Family ?familyName; vcard:NICKNAME ?nickname ]. }";
                Query query = QueryFactory.create(queryString);
                try (QueryExecution qe = QueryExecutionFactory.create(query, this.dataset)) {
                        ResultSet rs = qe.execSelect();
                        ResultSetFormatter.out(System.out, rs, query);
                } catch (QueryParseException e) {
                        System.err.println("Error parsing query: " + e.getMessage());
                }
        }

        public static void main(String args[]) {
                AddressBook addressBook = new AddressBook(); // Create an instance to access non-static methods
                addressBook.setFakeData();

                // // Print the RDF content of the dataset
                // System.out.println("");
                // System.out.println("RDF/XML-ABBREV");
                // System.out.println("");
                // addressBook.dataset.getDefaultModel().write(System.out, "RDF/XML-ABBREV");

                // System.out.println("");
                // System.out.println("RDF/XML");
                // System.out.println("");
                // addressBook.dataset.getDefaultModel().write(System.out);

                System.out.println("================== SPARQL ==================");

                System.out.println("SPARQL Query Results:");
                addressBook.testSomeRequests();
                // Query 1: Count the number of concepts in DBpedia
                System.out.println("");
                System.out.println("Query 1: Count the number of concepts in DBpedia ");
                System.out.println("");
                String query1 = "SELECT (COUNT(?s) AS ?count) WHERE { ?s ?p ?o }";
                String answer = addressBook.querySPARQLEndpoint(query1);
                System.out.println(answer);

                // Query 2: Actors or actresses born in the 1950s
                System.out.println("");
                System.out.println("Query 2: Actors or actresses born in the 1950s ");
                System.out.println("");
                String query2 = "SELECT ?actorOrActress"
                                + "                WHERE {"
                                + "                  ?actorOrActress a <http://dbpedia.org/ontology/Actor> ."
                                + "                ?actorOrActress <http://dbpedia.org/ontology/birthDate> ?date ."
                                + "               FILTER (YEAR(?date) >= 1950 && YEAR(?date) <= 1959)"
                                +
                                "}";
                answer = addressBook.querySPARQLEndpoint(query2);
                System.out.println(answer);

                // Query 3: Actors or actresses who acted in the movie "Armageddon"
                System.out.println("");
                System.out.println("Query 3: Actors or actresses who acted in the movie \"Armageddon\"");
                System.out.println("");
                String query3 = "SELECT ?actorOrActress" +
                                "WHERE {" +
                                "    ?actorOrActress a <http://dbpedia.org/ontology/Actor> ." +
                                "    ?actorOrActress <http://dbpedia.org/ontology/starring> <http://dbpedia.org/resource/Armageddon>"
                                +
                                "}";
                answer = addressBook.querySPARQLEndpoint(query3);
                System.out.println(answer);

                // Query 4: Actors or actresses who acted with Bruce Willis (movies in common)
                System.out.println("");
                System.out.println("Query 4: Actors or actresses who acted with Bruce Willis (movies in common)");
                System.out.println("");
                String query4 = "SELECT DISTINCT ?movieName"
                                + "  WHERE {"
                                + "<http://dbpedia.org/resource/Bruce_Willis> <http://dbpedia.org/ontology/starring> ?movie ."
                                + "?actorOrActress <http://dbpedia.org/ontology/starring> ?movie ."
                                + "?movie <http://purl.org/dc/terms/label> ?movieName ."
                                + "FILTER (?actorOrActress != <http://dbpedia.org/resource/Bruce_Willis>)"
                                + "}";
                answer = addressBook.querySPARQLEndpoint(query4);
                System.out.println(answer);
        }
}
