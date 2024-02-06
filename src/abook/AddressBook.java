
package abook;

import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
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
                        displayAdressBook();
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
                String myAdress = "1 rue Raymond Barre";
                String myCountry = "France";
                String myNickname = "Moncoifsonne";

                // b. Add a description of my Vcard
                Resource myCard = createVcardResource(
                                myURI, myFirstName, myFamilyName, myAdress, myCountry, myNickname);

                // Add the card to the dataset
                dataset.getDefaultModel().add(myCard.getModel());

                // d. Add some friends into a Dataset
                Resource camilleCard = createVcardResource(
                                "https://something/camilleJavel", "Camille", "Javel", "CROUS", "France", "Marcel");
                // Add the card to the dataset
                dataset.getDefaultModel().add(camilleCard.getModel());

                Resource alexandreCard = createVcardResource(
                                "https://something/alexTamhui", "Alexandre", "Tamhui", "CROUS", "France", "Alex");
                // Add the card to the dataset
                dataset.getDefaultModel().add(alexandreCard.getModel());
        }

        public void displayAdressBook() {
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

                        // Assuming the URI itself represents the contact
                        stringWriter.write("URI: " + resource.getURI());
                        stringWriter.write(System.lineSeparator());

                        // Check for vCard properties on the resource
                        if (resource.hasProperty(VCARD.FN)) {
                                stringWriter.write("Full Name: " + resource.getProperty(VCARD.FN).getString());
                                stringWriter.write(System.lineSeparator());
                        }
                        if (resource.hasProperty(VCARD.N)) {
                                RDFNode nNode = resource.getProperty(VCARD.N).getObject();
                                if (nNode.isResource()) {
                                        Resource nResource = nNode.asResource();
                                        if (nResource.hasProperty(VCARD.Given)) {
                                                stringWriter.write("Given Name: "
                                                                + nResource.getProperty(VCARD.Given).getString());
                                                stringWriter.write(System.lineSeparator());
                                        }
                                        if (nResource.hasProperty(VCARD.Family)) {
                                                stringWriter.write("Family Name: "
                                                                + nResource.getProperty(VCARD.Family).getString());
                                                stringWriter.write(System.lineSeparator());
                                        }
                                        if (nResource.hasProperty(VCARD.ADR)) {
                                                stringWriter.write("Address: "
                                                                + nResource.getProperty(VCARD.ADR).getString());
                                                stringWriter.write(System.lineSeparator());
                                        }
                                        if (nResource.hasProperty(VCARD.Country)) {
                                                stringWriter.write("Country: "
                                                                + nResource.getProperty(VCARD.Country).getString());
                                                stringWriter.write(System.lineSeparator());
                                        }
                                        if (nResource.hasProperty(VCARD.NICKNAME)) {
                                                stringWriter.write("Nickname: "
                                                                + nResource.getProperty(VCARD.NICKNAME).getString());
                                                stringWriter.write(System.lineSeparator());
                                        }
                                }
                        }
                }
                // Return the string
                return stringWriter.toString();
        }

        public static void main(String args[]) {
                AddressBook addressBook = new AddressBook(); // Create an instance to access non-static methods
                addressBook.setFakeData();

                // Print the RDF content of the dataset
                System.out.println("");
                System.out.println("RDF/XML-ABBREV");
                System.out.println("");
                addressBook.dataset.getDefaultModel().write(System.out, "RDF/XML-ABBREV");

                System.out.println("");
                System.out.println("RDF/XML");
                System.out.println("");
                addressBook.dataset.getDefaultModel().write(System.out);
        }

}
